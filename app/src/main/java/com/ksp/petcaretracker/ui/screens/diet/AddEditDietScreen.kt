package com.ksp.petcaretracker.ui.screens.diet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ksp.petcaretracker.domain.model.MealTime
import com.ksp.petcaretracker.ui.components.BottomActionBar
import com.ksp.petcaretracker.ui.components.PetCareTopBar

@Composable
fun AddEditDietScreen(onNavigateBack: () -> Unit, viewModel: AddEditDietViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(state.isSaved) { if (state.isSaved) onNavigateBack() }

    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = { PetCareTopBar(title = if (state.isEditing) "Edit Diet" else "Add Diet", onBackClick = onNavigateBack) },
        bottomBar = { BottomActionBar(label = if (state.isEditing) "Update" else "Save", onClick = viewModel::save) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp)) {
            Text(text = "Meal Time", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(bottom = 8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { MealTime.entries.forEach { time -> FilterChip(selected = state.mealTime == time, onClick = { viewModel.onMealTimeChange(time) }, label = { Text(time.displayName) }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer)) } }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = state.foodType, onValueChange = viewModel::onFoodTypeChange, label = { Text("Food Type") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), singleLine = true, isError = state.error != null)
            AnimatedVisibility(visible = state.error != null) { Text(text = state.error ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 16.dp, top = 4.dp)) }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = state.quantity, onValueChange = viewModel::onQuantityChange, label = { Text("Quantity") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), singleLine = true)
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) { Text(text = "Enable Reminder", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f)); Switch(checked = state.reminderEnabled, onCheckedChange = viewModel::onReminderToggle, colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary, checkedTrackColor = MaterialTheme.colorScheme.primaryContainer)) }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
