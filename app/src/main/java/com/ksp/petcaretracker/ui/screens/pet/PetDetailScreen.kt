package com.ksp.petcaretracker.ui.screens.pet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.ksp.petcaretracker.ui.components.ConfirmationDialog
import com.ksp.petcaretracker.ui.components.LoadingState
import com.ksp.petcaretracker.ui.components.PetCareTopBar
import com.ksp.petcaretracker.utils.DateUtils

@Composable
fun PetDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEditPet: (Long) -> Unit,
    onNavigateToVaccinations: (Long) -> Unit,
    onNavigateToVetVisits: (Long) -> Unit,
    onNavigateToDiet: (Long) -> Unit,
    onNavigateToGrowthLog: (Long) -> Unit,
    onNavigateToMemories: (Long) -> Unit,
    viewModel: PetDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            PetCareTopBar(
                title = state.pet?.name ?: "Pet Details",
                onBackClick = onNavigateBack,
                actions = {
                    state.pet?.let { pet ->
                        IconButton(onClick = { onNavigateToEditPet(pet.id) }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit pet"
                            )
                        }
                        IconButton(onClick = { showDeleteConfirmation = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete pet",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        val pet = state.pet
        if (pet == null) { LoadingState(modifier = Modifier.padding(padding)) } else {
            Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            if (pet.imageUri != null) {
                                AsyncImage(
                                    model = pet.imageUri,
                                    contentDescription = pet.name,
                                    modifier = Modifier
                                        .size(88.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Text(text = pet.type.emoji, fontSize = 46.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = pet.name,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${pet.type.displayName} - ${pet.breed}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "DOB: ${pet.dateOfBirth}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        InfoChip(
                            label = "Age",
                            value = DateUtils.calculateAge(pet.dateOfBirth),
                            modifier = Modifier.weight(1f)
                        )
                        InfoChip(
                            label = "Weight",
                            value = "${pet.weight} kg",
                            modifier = Modifier.weight(1f)
                        )
                        InfoChip(
                            label = "Gender",
                            value = pet.gender.displayName,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        FeatureCard(title = "Vaccinations", icon = Icons.Default.Healing, color = MaterialTheme.colorScheme.primary, onClick = { onNavigateToVaccinations(pet.id) }, modifier = Modifier.weight(1f))
                        FeatureCard(title = "Vet Visits", icon = Icons.Default.LocalHospital, color = MaterialTheme.colorScheme.secondary, onClick = { onNavigateToVetVisits(pet.id) }, modifier = Modifier.weight(1f))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        FeatureCard(title = "Diet Plan", icon = Icons.Default.Restaurant, color = MaterialTheme.colorScheme.tertiary, onClick = { onNavigateToDiet(pet.id) }, modifier = Modifier.weight(1f))
                        FeatureCard(title = "Growth Log", icon = Icons.Default.MonitorWeight, color = MaterialTheme.colorScheme.primary, onClick = { onNavigateToGrowthLog(pet.id) }, modifier = Modifier.weight(1f))
                    }
                    FeatureCard(title = "Photo Memories", icon = Icons.Default.PhotoLibrary, color = MaterialTheme.colorScheme.secondary, onClick = { onNavigateToMemories(pet.id) }, modifier = Modifier.fillMaxWidth())
                }
            }

            if (showDeleteConfirmation) {
                ConfirmationDialog(
                    title = "Delete pet?",
                    message = "This will permanently remove this pet and all its records. This action cannot be undone.",
                    onConfirm = {
                        viewModel.deletePet(pet.id)
                        onNavigateBack()
                    },
                    onDismiss = { showDeleteConfirmation = false }
                )
            }
        }
    }
}

@Composable
fun InfoChip(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(text = value, style = MaterialTheme.typography.titleSmall)
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        }
    }
}

@Composable
fun FeatureCard(title: String, icon: ImageVector, color: androidx.compose.ui.graphics.Color, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(onClick = onClick, modifier = modifier.height(100.dp), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, style = MaterialTheme.typography.labelLarge, color = color)
        }
    }
}
