package com.ksp.petcaretracker.ui.screens.vet

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.ksp.petcaretracker.ui.components.BottomActionBar
import com.ksp.petcaretracker.ui.components.PetCareTopBar
import com.ksp.petcaretracker.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditVetVisitScreen(onNavigateBack: () -> Unit, viewModel: AddEditVetVisitViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? -> viewModel.onPrescriptionImageChange(uri?.toString()) }

    LaunchedEffect(state.isSaved) { if (state.isSaved) onNavigateBack() }

    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = { PetCareTopBar(title = if (state.isEditing) "Edit Vet Visit" else "Add Vet Visit", onBackClick = onNavigateBack) },
        bottomBar = { BottomActionBar(label = if (state.isEditing) "Update" else "Save", onClick = viewModel::save) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp)) {
            OutlinedTextField(value = state.clinicName, onValueChange = viewModel::onClinicNameChange, label = { Text("Clinic Name") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), singleLine = true, isError = state.error != null)
            AnimatedVisibility(visible = state.error != null) { Text(text = state.error ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 16.dp, top = 4.dp)) }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = state.doctorName, onValueChange = viewModel::onDoctorNameChange, label = { Text("Doctor Name") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), singleLine = true)
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = DateUtils.formatDate(state.date), onValueChange = {}, label = { Text("Appointment Date") }, modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true }, shape = RoundedCornerShape(16.dp), readOnly = true, enabled = false, colors = OutlinedTextFieldDefaults.colors(disabledTextColor = MaterialTheme.colorScheme.onSurface, disabledBorderColor = MaterialTheme.colorScheme.outline, disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant))
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) { Text(text = "Enable Reminder", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f)); Switch(checked = state.reminderEnabled, onCheckedChange = viewModel::onReminderToggle, colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary, checkedTrackColor = MaterialTheme.colorScheme.primaryContainer)) }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(onClick = { imagePicker.launch("image/*") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) { Text("Attach Prescription Image") }
            state.prescriptionImageUri?.let { uri -> Spacer(modifier = Modifier.height(8.dp)); AsyncImage(model = uri, contentDescription = "Prescription", modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(16.dp)), contentScale = ContentScale.Crop) }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = state.notes, onValueChange = viewModel::onNotesChange, label = { Text("Notes") }, modifier = Modifier.fillMaxWidth().height(120.dp), shape = RoundedCornerShape(16.dp), maxLines = 5)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = state.date)
        DatePickerDialog(onDismissRequest = { showDatePicker = false }, confirmButton = { TextButton(onClick = { datePickerState.selectedDateMillis?.let { viewModel.onDateChange(it) }; showDatePicker = false }) { Text("OK") } }, dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } }) { DatePicker(state = datePickerState) }
    }
}
