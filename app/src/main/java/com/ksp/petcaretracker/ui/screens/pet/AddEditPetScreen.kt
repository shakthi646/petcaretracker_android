package com.ksp.petcaretracker.ui.screens.pet

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.ksp.petcaretracker.domain.model.Gender
import com.ksp.petcaretracker.ui.components.BottomActionBar
import com.ksp.petcaretracker.ui.components.PetCareTopBar
import com.ksp.petcaretracker.ui.components.PetTypePickerSheet
import com.ksp.petcaretracker.utils.DateUtils
import com.ksp.petcaretracker.utils.ImageFileUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditPetScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddEditPetViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    var showDatePicker by remember { mutableStateOf(false) }
    var showPhotoSourceDialog by remember { mutableStateOf(false) }
    var showPetTypeSheet by remember { mutableStateOf(false) }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }

    val galleryPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        viewModel.onImageUriChange(uri?.toString())
    }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
        if (success) viewModel.onImageUriChange(pendingCameraUri?.toString())
    }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            val uri = ImageFileUtils.createImageUri(context)
            pendingCameraUri = uri
            cameraLauncher.launch(uri)
        }
    }

    fun launchCamera() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            val uri = ImageFileUtils.createImageUri(context)
            pendingCameraUri = uri
            cameraLauncher.launch(uri)
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    LaunchedEffect(state.isSaved) { if (state.isSaved) onNavigateBack() }

    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = { PetCareTopBar(title = if (state.isEditing) "Edit Pet" else "Add Pet", onBackClick = onNavigateBack) },
        bottomBar = {
            BottomActionBar(
                label = if (state.isEditing) "Update Pet" else "Add Pet",
                onClick = viewModel::savePet
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), CircleShape)
                    .clickable { showPhotoSourceDialog = true },
                contentAlignment = Alignment.Center
            ) {
                if (state.imageUri != null) {
                    AsyncImage(model = state.imageUri, contentDescription = "Pet photo", modifier = Modifier.size(120.dp).clip(CircleShape), contentScale = ContentScale.Crop)
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.AddAPhoto, contentDescription = "Add photo", modifier = Modifier.size(36.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Add Photo", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(value = state.name, onValueChange = viewModel::onNameChange, label = { Text("Pet Name") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), singleLine = true, isError = state.error != null)
            AnimatedVisibility(visible = state.error != null) {
                Text(text = state.error ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 4.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))

            PetTypeDropdownField(
                selectedEmoji = state.type.emoji,
                selectedName = state.type.displayName,
                onClick = { showPetTypeSheet = true }
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(value = state.breed, onValueChange = viewModel::onBreedChange, label = { Text("Breed") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), singleLine = true)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(value = DateUtils.formatDate(state.dateOfBirth), onValueChange = {}, label = { Text("Date of Birth") }, modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true }, shape = RoundedCornerShape(16.dp), readOnly = true, enabled = false, colors = OutlinedTextFieldDefaults.colors(disabledTextColor = MaterialTheme.colorScheme.onSurface, disabledBorderColor = MaterialTheme.colorScheme.outline, disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant))
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(value = state.weight, onValueChange = viewModel::onWeightChange, label = { Text("Weight (kg)") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Gender", style = MaterialTheme.typography.labelLarge, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Gender.entries.forEach { gender ->
                    FilterChip(selected = state.gender == gender, onClick = { viewModel.onGenderChange(gender) }, label = { Text(gender.displayName) }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primaryContainer))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showPetTypeSheet) {
        PetTypePickerSheet(
            selectedType = state.type,
            onTypeSelected = viewModel::onTypeChange,
            onDismiss = { showPetTypeSheet = false }
        )
    }

    if (showPhotoSourceDialog) {
        PhotoSourceDialog(
            onDismiss = { showPhotoSourceDialog = false },
            onTakePhoto = {
                showPhotoSourceDialog = false
                launchCamera()
            },
            onPickFromGallery = {
                showPhotoSourceDialog = false
                galleryPicker.launch("image/*")
            }
        )
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = state.dateOfBirth)
        DatePickerDialog(onDismissRequest = { showDatePicker = false }, confirmButton = { TextButton(onClick = { datePickerState.selectedDateMillis?.let { viewModel.onDateChange(it) }; showDatePicker = false }) { Text("OK") } }, dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } }) { DatePicker(state = datePickerState) }
    }
}

@Composable
private fun PetTypeDropdownField(selectedEmoji: String, selectedName: String, onClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Pet Type",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = selectedEmoji, fontSize = 24.sp)
                Spacer(modifier = Modifier.size(12.dp))
                Text(
                    text = selectedName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Open picker",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PhotoSourceDialog(onDismiss: () -> Unit, onTakePhoto: () -> Unit, onPickFromGallery: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Pet Photo", fontWeight = FontWeight.SemiBold) },
        text = {
            Column {
                PhotoSourceOption(icon = Icons.Default.CameraAlt, title = "Take Photo", subtitle = "Use camera to capture", onClick = onTakePhoto)
                Spacer(modifier = Modifier.height(8.dp))
                PhotoSourceOption(icon = Icons.Default.PhotoLibrary, title = "Choose from Gallery", subtitle = "Pick from your photos", onClick = onPickFromGallery)
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun PhotoSourceOption(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.size(12.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
        }
    }
}
