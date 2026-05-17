package com.ksp.petcaretracker.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.ksp.corelibrary.util.PreferenceAccessor
import com.ksp.petcaretracker.domain.model.Pet
import com.ksp.petcaretracker.domain.model.Vaccination
import com.ksp.petcaretracker.domain.model.VetVisit
import com.ksp.petcaretracker.ui.components.EmptyState
import com.ksp.petcaretracker.ui.components.LoadingState
import com.ksp.petcaretracker.ui.components.StatusChip
import com.ksp.petcaretracker.ui.theme.OverdueRed
import com.ksp.petcaretracker.ui.theme.SuccessGreen
import com.ksp.petcaretracker.ui.theme.WarningOrange
import com.ksp.petcaretracker.utils.DateUtils

private const val FREE_PET_LIMIT = 1

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToPetDetail: (Long) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAddPet: () -> Unit,
    onNavigateToSubscription: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val tryAddPet: () -> Unit = {
        if (state.pets.size >= FREE_PET_LIMIT && !PreferenceAccessor.isPremiumUser(context)) {
            onNavigateToSubscription()
        } else {
            onNavigateToAddPet()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "PetCare+",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Care for your furry friends",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
//                    IconButton(onClick = onNavigateToSettings) {
//                        Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
//                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        if (state.isLoading) {
            LoadingState(modifier = Modifier.padding(padding))
        } else if (state.pets.isEmpty()) {
            EmptyState(
                icon = Icons.Default.Pets,
                title = "No pets yet",
                subtitle = "Add your first furry friend to start tracking their care",
                modifier = Modifier.padding(padding),
                actionLabel = "Add Pet",
                onAction = tryAddPet
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
            ) {
                item { HeroSummaryCard(state = state) }
                item { Spacer(modifier = Modifier.height(20.dp)) }

                item {
                    SectionTitle(
                        title = "Your Pets",
                        actionLabel = "Add",
                        actionIcon = Icons.Default.Add,
                        onActionClick = tryAddPet
                    )
                }
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.pets, key = { it.id }) { pet ->
                            PetAvatarCard(pet = pet, onClick = { onNavigateToPetDetail(pet.id) })
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }

                if (state.upcomingVaccinations.isNotEmpty()) {
                    item { SectionTitle(title = "Upcoming Vaccinations") }
                    items(state.upcomingVaccinations, key = { "vaccination_${it.id}" }) { vaccination ->
                        VaccinationReminderCard(vaccination = vaccination, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
                    }
                    item { Spacer(modifier = Modifier.height(12.dp)) }
                }
                if (state.upcomingVetVisits.isNotEmpty()) {
                    item { SectionTitle(title = "Upcoming Vet Visits") }
                    items(state.upcomingVetVisits, key = { "vet_visit_${it.id}" }) { visit ->
                        VetVisitReminderCard(vetVisit = visit, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroSummaryCard(state: HomeUiState) {
    val primary = MaterialTheme.colorScheme.primary
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(colors = listOf(primary, primary.copy(alpha = 0.75f))),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "Today at a glance",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.85f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Stay on top of pet care",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    HeroStat(value = state.pets.size.toString(), label = "Pets", modifier = Modifier.weight(1f))
                    HeroStat(value = state.upcomingVaccinations.size.toString(), label = "Vaccines", modifier = Modifier.weight(1f))
                    HeroStat(value = state.upcomingVetVisits.size.toString(), label = "Vet Visits", modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun HeroStat(value: String, label: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = Color.White.copy(alpha = 0.18f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = value, style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.85f))
        }
    }
}

@Composable
private fun SectionTitle(
    title: String,
    actionLabel: String? = null,
    actionIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    onActionClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f)
        )
        if (actionLabel != null && onActionClick != null) {
            Surface(
                onClick = onActionClick,
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (actionIcon != null) {
                        Icon(imageVector = actionIcon, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(16.dp))
                    }
                    Text(text = actionLabel, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        }
    }
}

@Composable
fun PetAvatarCard(pet: Pet, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        onClick = onClick,
        modifier = modifier.width(104.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                if (pet.imageUri != null) {
                    AsyncImage(model = pet.imageUri, contentDescription = pet.name, modifier = Modifier.size(64.dp).clip(CircleShape), contentScale = ContentScale.Crop)
                } else {
                    Text(text = pet.type.emoji, fontSize = 32.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = pet.name, style = MaterialTheme.typography.labelLarge, maxLines = 1, fontWeight = FontWeight.SemiBold)
            Text(text = pet.type.displayName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun VaccinationReminderCard(vaccination: Vaccination, modifier: Modifier = Modifier) {
    val daysUntil = DateUtils.daysUntil(vaccination.nextDueDate)
    val chipColor = when { daysUntil < 0 -> OverdueRed; daysUntil <= 7 -> WarningOrange; else -> SuccessGreen }
    val chipText = when { daysUntil < 0 -> "Overdue"; daysUntil == 0L -> "Today"; daysUntil == 1L -> "Tomorrow"; else -> "In $daysUntil days" }

    Card(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
                Icon(imageVector = Icons.Default.Healing, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = vaccination.vaccineName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(text = "Due: ${DateUtils.formatDate(vaccination.nextDueDate)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            StatusChip(text = chipText, color = chipColor)
        }
    }
}

@Composable
fun VetVisitReminderCard(vetVisit: VetVisit, modifier: Modifier = Modifier) {
    val daysUntil = DateUtils.daysUntil(vetVisit.date)
    val chipColor = when { daysUntil < 0 -> OverdueRed; daysUntil <= 3 -> WarningOrange; else -> SuccessGreen }
    val chipText = when { daysUntil < 0 -> "Passed"; daysUntil == 0L -> "Today"; daysUntil == 1L -> "Tomorrow"; else -> "In $daysUntil days" }

    Card(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.secondaryContainer), contentAlignment = Alignment.Center) {
                Icon(imageVector = Icons.Default.LocalHospital, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = vetVisit.clinicName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(text = "Dr. ${vetVisit.doctorName} - ${DateUtils.formatDate(vetVisit.date)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            StatusChip(text = chipText, color = chipColor)
        }
    }
}
