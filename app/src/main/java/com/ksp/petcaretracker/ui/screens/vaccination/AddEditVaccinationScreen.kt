package com.ksp.petcaretracker.ui.screens.vaccination

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ksp.petcaretracker.ui.components.BottomActionBar
import com.ksp.petcaretracker.ui.components.PetCareTopBar
import com.ksp.petcaretracker.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditVaccinationScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddEditVaccinationViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showDatePickerFor by remember { mutableIntStateOf(0) }

    LaunchedEffect(state.isSaved) { if (state.isSaved) onNavigateBack() }

    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = { PetCareTopBar(title = if (state.isEditing) "Edit Vaccination" else "Add Vaccination", onBackClick = onNavigateBack) },
        bottomBar = { BottomActionBar(label = if (state.isEditing) "Update" else "Save", onClick = viewModel::save) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp)) {
            OutlinedTextField(value = state.vaccineName, onValueChange = viewModel::onVaccineNameChange, label = { Text("Vaccine Name") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), singleLine = true, isError = state.error != null)
            AnimatedVisibility(visible = state.error != null) { Text(text = state.error ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 16.dp, top = 4.dp)) }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = DateUtils.formatDate(state.lastDate), onValueChange = {}, label = { Text("Last Vaccination Date") }, modifier = Modifier.fillMaxWidth().clickable { showDatePickerFor = 1 }, shape = RoundedCornerShape(16.dp), readOnly = true, enabled = false, colors = OutlinedTextFieldDefaults.colors(disabledTextColor = MaterialTheme.colorScheme.onSurface, disabledBorderColor = MaterialTheme.colorScheme.outline, disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant))
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = DateUtils.formatDate(state.nextDueDate), onValueChange = {}, label = { Text("Next Due Date") }, modifier = Modifier.fillMaxWidth().clickable { showDatePickerFor = 2 }, shape = RoundedCornerShape(16.dp), readOnly = true, enabled = false, colors = OutlinedTextFieldDefaults.colors(disabledTextColor = MaterialTheme.colorScheme.onSurface, disabledBorderColor = MaterialTheme.colorScheme.outline, disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant))
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Enable Reminder", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                Switch(checked = state.reminderEnabled, onCheckedChange = viewModel::onReminderToggle, colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary, checkedTrackColor = MaterialTheme.colorScheme.primaryContainer))
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = state.notes, onValueChange = viewModel::onNotesChange, label = { Text("Notes") }, modifier = Modifier.fillMaxWidth().height(120.dp), shape = RoundedCornerShape(16.dp), maxLines = 5)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showDatePickerFor > 0) {
        val initialDate = if (showDatePickerFor == 1) state.lastDate else state.nextDueDate
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialDate)
        DatePickerDialog(onDismissRequest = { showDatePickerFor = 0 }, confirmButton = { TextButton(onClick = { datePickerState.selectedDateMillis?.let { if (showDatePickerFor == 1) viewModel.onLastDateChange(it) else viewModel.onNextDueDateChange(it) }; showDatePickerFor = 0 }) { Text("OK") } }, dismissButton = { TextButton(onClick = { showDatePickerFor = 0 }) { Text("Cancel") } }) { DatePicker(state = datePickerState) }
    }
}
