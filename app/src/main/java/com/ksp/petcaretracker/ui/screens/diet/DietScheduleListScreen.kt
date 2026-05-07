package com.ksp.petcaretracker.ui.screens.diet

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ksp.petcaretracker.domain.model.DietSchedule
import com.ksp.petcaretracker.domain.model.MealTime
import com.ksp.petcaretracker.ui.components.ConfirmationDialog
import com.ksp.petcaretracker.ui.components.EmptyState
import com.ksp.petcaretracker.ui.components.LoadingState
import com.ksp.petcaretracker.ui.components.PetCareFab
import com.ksp.petcaretracker.ui.components.PetCareTopBar

@Composable
fun DietScheduleListScreen(onNavigateBack: () -> Unit, onNavigateToAdd: (Long) -> Unit, onNavigateToEdit: (Long, Long) -> Unit, viewModel: DietScheduleViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    var pendingDeleteId by remember { mutableStateOf<Long?>(null) }
    Scaffold(topBar = { PetCareTopBar(title = "${state.pet?.name ?: ""} Diet Plan", onBackClick = onNavigateBack) }, floatingActionButton = { PetCareFab(onClick = { onNavigateToAdd(viewModel.petId) }) }) { padding ->
        if (state.isLoading) { LoadingState(modifier = Modifier.padding(padding)) }
        else if (state.schedules.isEmpty()) {
            EmptyState(
                icon = Icons.Default.Restaurant,
                title = "No diet schedule set",
                subtitle = "Plan ${state.pet?.name ?: "your pet"}'s meals to keep them on a healthy routine",
                modifier = Modifier.padding(padding),
                actionLabel = "Add Meal Plan",
                onAction = { onNavigateToAdd(viewModel.petId) }
            )
        }
        else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                itemsIndexed(items = state.schedules, key = { _, d -> d.id }) { index, schedule ->
                    AnimatedVisibility(visible = true, enter = fadeIn() + slideInVertically(initialOffsetY = { it * (index + 1) })) {
                        DietCard(schedule = schedule, onEdit = { onNavigateToEdit(viewModel.petId, schedule.id) }, onDelete = { pendingDeleteId = schedule.id })
                    }
                }
            }
        }
    }

    pendingDeleteId?.let { id ->
        ConfirmationDialog(
            title = "Delete meal plan?",
            message = "This meal plan will be permanently removed. This action cannot be undone.",
            onConfirm = { viewModel.deleteDietSchedule(id) },
            onDismiss = { pendingDeleteId = null }
        )
    }
}

@Composable
fun DietCard(schedule: DietSchedule, onEdit: () -> Unit, onDelete: () -> Unit, modifier: Modifier = Modifier) {
    val mealIcon = when (schedule.mealTime) { MealTime.MORNING -> Icons.Default.LunchDining; MealTime.AFTERNOON -> Icons.Default.Restaurant; MealTime.EVENING -> Icons.Default.LunchDining }
    Card(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(MaterialTheme.colorScheme.tertiaryContainer), contentAlignment = Alignment.Center) { Icon(imageVector = mealIcon, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(24.dp)) }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) { Text(text = schedule.mealTime.displayName, style = MaterialTheme.typography.titleMedium); Text(text = "${schedule.foodType} - ${schedule.quantity}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)) }
            Icon(imageVector = if (schedule.reminderEnabled) Icons.Default.Notifications else Icons.Default.NotificationsOff, contentDescription = null, tint = if (schedule.reminderEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline, modifier = Modifier.size(20.dp))
            IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary) }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error) }
        }
    }
}
