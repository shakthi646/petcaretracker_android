package com.ksp.petcaretracker.ui.screens.vaccination

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ksp.petcaretracker.domain.model.Vaccination
import com.ksp.petcaretracker.ui.components.ConfirmationDialog
import com.ksp.petcaretracker.ui.components.EmptyState
import com.ksp.petcaretracker.ui.components.LoadingState
import com.ksp.petcaretracker.ui.components.PetCareFab
import com.ksp.petcaretracker.ui.components.PetCareTopBar
import com.ksp.petcaretracker.ui.components.StatusChip
import com.ksp.petcaretracker.ui.theme.OverdueRed
import com.ksp.petcaretracker.ui.theme.SuccessGreen
import com.ksp.petcaretracker.ui.theme.WarningOrange
import com.ksp.petcaretracker.utils.DateUtils

@Composable
fun VaccinationListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAdd: (Long) -> Unit,
    onNavigateToEdit: (Long, Long) -> Unit,
    viewModel: VaccinationListViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var pendingDeleteId by remember { mutableStateOf<Long?>(null) }
    Scaffold(
        topBar = { PetCareTopBar(title = "${state.pet?.name ?: ""} Vaccinations", onBackClick = onNavigateBack) },
        floatingActionButton = { PetCareFab(onClick = { onNavigateToAdd(viewModel.petId) }) }
    ) { padding ->
        if (state.isLoading) {
            LoadingState(modifier = Modifier.padding(padding))
        } else if (state.vaccinations.isEmpty()) {
            EmptyState(
                icon = Icons.Default.Healing,
                title = "No vaccinations recorded",
                subtitle = "Add a vaccination record to keep ${state.pet?.name ?: "your pet"} protected",
                modifier = Modifier.padding(padding),
                actionLabel = "Add Vaccination",
                onAction = { onNavigateToAdd(viewModel.petId) }
            )
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                itemsIndexed(items = state.vaccinations, key = { _, v -> v.id }) { index, vaccination ->
                    AnimatedVisibility(visible = true, enter = fadeIn() + slideInVertically(initialOffsetY = { it * (index + 1) })) {
                        VaccinationCard(vaccination = vaccination, onEdit = { onNavigateToEdit(viewModel.petId, vaccination.id) }, onDelete = { pendingDeleteId = vaccination.id })
                    }
                }
            }
        }
    }

    pendingDeleteId?.let { id ->
        ConfirmationDialog(
            title = "Delete vaccination?",
            message = "This vaccination record will be permanently removed. This action cannot be undone.",
            onConfirm = { viewModel.deleteVaccination(id) },
            onDismiss = { pendingDeleteId = null }
        )
    }
}

@Composable
fun VaccinationCard(vaccination: Vaccination, onEdit: () -> Unit, onDelete: () -> Unit, modifier: Modifier = Modifier) {
    val daysUntil = DateUtils.daysUntil(vaccination.nextDueDate)
    val statusColor = when { daysUntil < 0 -> OverdueRed; daysUntil <= 7 -> WarningOrange; else -> SuccessGreen }
    val statusText = when { daysUntil < 0 -> "Overdue"; daysUntil == 0L -> "Due Today"; daysUntil == 1L -> "Due Tomorrow"; else -> "Due in $daysUntil days" }

    Card(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
                    Icon(imageVector = Icons.Default.Healing, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = vaccination.vaccineName, style = MaterialTheme.typography.titleMedium)
                    StatusChip(text = statusText, color = statusColor)
                }
                Icon(imageVector = if (vaccination.reminderEnabled) Icons.Default.Notifications else Icons.Default.NotificationsOff, contentDescription = null, tint = if (vaccination.reminderEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(text = "Last Date", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    Text(text = DateUtils.formatDate(vaccination.lastDate), style = MaterialTheme.typography.bodyMedium)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Next Due", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    Text(text = DateUtils.formatDate(vaccination.nextDueDate), style = MaterialTheme.typography.bodyMedium)
                }
            }
            if (vaccination.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = vaccination.notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = onEdit) { Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary) }
                IconButton(onClick = onDelete) { Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error) }
            }
        }
    }
}
