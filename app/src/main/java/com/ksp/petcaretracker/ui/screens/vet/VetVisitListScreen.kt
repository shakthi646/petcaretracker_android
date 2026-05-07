package com.ksp.petcaretracker.ui.screens.vet

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
import androidx.compose.material.icons.filled.LocalHospital
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
import com.ksp.petcaretracker.domain.model.VetVisit
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
fun VetVisitListScreen(onNavigateBack: () -> Unit, onNavigateToAdd: (Long) -> Unit, onNavigateToEdit: (Long, Long) -> Unit, viewModel: VetVisitListViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    var pendingDeleteId by remember { mutableStateOf<Long?>(null) }
    Scaffold(topBar = { PetCareTopBar(title = "${state.pet?.name ?: ""} Vet Visits", onBackClick = onNavigateBack) }, floatingActionButton = { PetCareFab(onClick = { onNavigateToAdd(viewModel.petId) }) }) { padding ->
        if (state.isLoading) { LoadingState(modifier = Modifier.padding(padding)) }
        else if (state.vetVisits.isEmpty()) {
            EmptyState(
                icon = Icons.Default.LocalHospital,
                title = "No vet visits recorded",
                subtitle = "Log a vet visit to track ${state.pet?.name ?: "your pet"}'s health history",
                modifier = Modifier.padding(padding),
                actionLabel = "Add Vet Visit",
                onAction = { onNavigateToAdd(viewModel.petId) }
            )
        }
        else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                itemsIndexed(items = state.vetVisits, key = { _, v -> v.id }) { index, visit ->
                    AnimatedVisibility(visible = true, enter = fadeIn() + slideInVertically(initialOffsetY = { it * (index + 1) })) {
                        VetVisitCard(vetVisit = visit, onEdit = { onNavigateToEdit(viewModel.petId, visit.id) }, onDelete = { pendingDeleteId = visit.id })
                    }
                }
            }
        }
    }

    pendingDeleteId?.let { id ->
        ConfirmationDialog(
            title = "Delete vet visit?",
            message = "This vet visit record will be permanently removed. This action cannot be undone.",
            onConfirm = { viewModel.deleteVetVisit(id) },
            onDismiss = { pendingDeleteId = null }
        )
    }
}

@Composable
fun VetVisitCard(vetVisit: VetVisit, onEdit: () -> Unit, onDelete: () -> Unit, modifier: Modifier = Modifier) {
    val daysUntil = DateUtils.daysUntil(vetVisit.date)
    val statusColor = when { daysUntil < 0 -> OverdueRed; daysUntil <= 3 -> WarningOrange; else -> SuccessGreen }
    val statusText = when { daysUntil < 0 -> "Completed"; daysUntil == 0L -> "Today"; daysUntil == 1L -> "Tomorrow"; else -> "In $daysUntil days" }
    Card(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(MaterialTheme.colorScheme.secondaryContainer), contentAlignment = Alignment.Center) { Icon(imageVector = Icons.Default.LocalHospital, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(24.dp)) }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) { Text(text = vetVisit.clinicName, style = MaterialTheme.typography.titleMedium); Text(text = "Dr. ${vetVisit.doctorName}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)) }
                StatusChip(text = statusText, color = statusColor)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "Date: ${DateUtils.formatDate(vetVisit.date)}", style = MaterialTheme.typography.bodyMedium)
            if (vetVisit.notes.isNotBlank()) { Spacer(modifier = Modifier.height(4.dp)); Text(text = vetVisit.notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)) }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary) }
                IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error) }
            }
        }
    }
}
