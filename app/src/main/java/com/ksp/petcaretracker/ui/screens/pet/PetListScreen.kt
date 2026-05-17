package com.ksp.petcaretracker.ui.screens.pet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Card
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.ksp.corelibrary.util.PreferenceAccessor
import com.ksp.petcaretracker.domain.model.Pet
import com.ksp.petcaretracker.ui.components.EmptyState
import com.ksp.petcaretracker.ui.components.LoadingState
import com.ksp.petcaretracker.ui.components.PetCareFab
import com.ksp.petcaretracker.ui.components.PetCareTopBar
import com.ksp.petcaretracker.utils.DateUtils

private const val FREE_PET_LIMIT = 1

@Composable
fun PetListScreen(
    onNavigateToAddPet: () -> Unit,
    onNavigateToPetDetail: (Long) -> Unit,
    onNavigateToSubscription: () -> Unit,
    viewModel: PetListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val tryAddPet: () -> Unit = {
        val currentCount = (uiState as? PetListUiState.Success)?.pets?.size ?: 0
        if (currentCount >= FREE_PET_LIMIT && !PreferenceAccessor.isPremiumUser(context)) {
            onNavigateToSubscription()
        } else {
            onNavigateToAddPet()
        }
    }
    Scaffold(
        topBar = { PetCareTopBar(title = "My Pets") },
        floatingActionButton = { PetCareFab(onClick = tryAddPet) }
    ) { padding ->
        when (val state = uiState) {
            is PetListUiState.Loading -> LoadingState(modifier = Modifier.padding(padding))
            is PetListUiState.Error -> Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { Text(text = state.message, color = MaterialTheme.colorScheme.error) }
            is PetListUiState.Success -> {
                if (state.pets.isEmpty()) {
                    EmptyState(
                        icon = Icons.Default.Pets,
                        title = "No pets added yet",
                        subtitle = "Add your first pet to start tracking their care",
                        modifier = Modifier.padding(padding),
                        actionLabel = "Add Pet",
                        onAction = tryAddPet
                    )
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        itemsIndexed(items = state.pets, key = { _, pet -> pet.id }) { index, pet ->
                            AnimatedVisibility(visible = true, enter = fadeIn() + slideInVertically(initialOffsetY = { it * (index + 1) })) {
                                PetCard(pet = pet, onClick = { onNavigateToPetDetail(pet.id) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PetCard(pet: Pet, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(onClick = onClick, modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(60.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
                if (pet.imageUri != null) {
                    AsyncImage(model = pet.imageUri, contentDescription = pet.name, modifier = Modifier.size(60.dp).clip(CircleShape), contentScale = ContentScale.Crop)
                } else {
                    Text(text = pet.type.emoji, fontSize = 28.sp)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = pet.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${pet.type.displayName} - ${pet.breed}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = "Age: ${DateUtils.calculateAge(pet.dateOfBirth)} | ${pet.weight} kg", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
        }
    }
}
