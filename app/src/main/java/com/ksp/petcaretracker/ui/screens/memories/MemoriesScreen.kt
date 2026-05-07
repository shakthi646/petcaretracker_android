package com.ksp.petcaretracker.ui.screens.memories

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoLibrary
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.ksp.petcaretracker.domain.model.Memory
import com.ksp.petcaretracker.ui.components.ConfirmationDialog
import com.ksp.petcaretracker.ui.components.EmptyState
import com.ksp.petcaretracker.ui.components.LoadingState
import com.ksp.petcaretracker.ui.components.PetCareFab
import com.ksp.petcaretracker.ui.components.PetCareTopBar
import com.ksp.petcaretracker.utils.DateUtils

@Composable
fun MemoriesScreen(onNavigateBack: () -> Unit, viewModel: MemoriesViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? -> uri?.let { viewModel.addMemory(it.toString()) } }
    var pendingDeleteId by remember { mutableStateOf<Long?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = { PetCareTopBar(title = "${state.pet?.name ?: ""} Memories", onBackClick = onNavigateBack) },
            floatingActionButton = { PetCareFab(onClick = { imagePicker.launch("image/*") }) }
        ) { padding ->
            if (state.isLoading) { LoadingState(modifier = Modifier.padding(padding)) }
            else if (state.memories.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.PhotoLibrary,
                    title = "No memories yet",
                    subtitle = "Capture special moments with ${state.pet?.name ?: "your pet"}",
                    modifier = Modifier.padding(padding),
                    actionLabel = "Add Photo",
                    onAction = { imagePicker.launch("image/*") }
                )
            }
            else {
                LazyVerticalGrid(columns = GridCells.Fixed(3), modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(8.dp), horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(state.memories, key = { it.id }) { memory -> MemoryGridItem(memory = memory, onClick = { viewModel.selectMemory(memory) }) }
                }
            }
        }
        AnimatedVisibility(visible = state.selectedMemory != null, enter = fadeIn(tween(300)) + scaleIn(tween(300)), exit = fadeOut(tween(300)) + scaleOut(tween(300))) {
            state.selectedMemory?.let { memory -> FullscreenPreview(memory = memory, onDismiss = { viewModel.selectMemory(null) }, onDelete = { pendingDeleteId = memory.id }) }
        }
    }

    pendingDeleteId?.let { id ->
        ConfirmationDialog(
            title = "Delete memory?",
            message = "This photo memory will be permanently removed. This action cannot be undone.",
            onConfirm = { viewModel.deleteMemory(id); viewModel.selectMemory(null) },
            onDismiss = { pendingDeleteId = null }
        )
    }
}

@Composable
fun MemoryGridItem(memory: Memory, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(12.dp)).clickable(onClick = onClick)) {
        AsyncImage(model = memory.imageUri, contentDescription = memory.caption, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        if (memory.caption.isNotBlank()) {
            Box(modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).background(Color.Black.copy(alpha = 0.5f)).padding(4.dp)) { Text(text = memory.caption, style = MaterialTheme.typography.labelSmall, color = Color.White, maxLines = 1) }
        }
    }
}

@Composable
fun FullscreenPreview(memory: Memory, onDismiss: () -> Unit, onDelete: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.92f)).clickable(onClick = onDismiss)) {
        AsyncImage(model = memory.imageUri, contentDescription = memory.caption, modifier = Modifier.fillMaxSize().padding(24.dp), contentScale = ContentScale.Fit)
        IconButton(onClick = onDismiss, modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)) { Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(32.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape).padding(4.dp)) }
        IconButton(onClick = onDelete, modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)) { Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White, modifier = Modifier.size(32.dp).background(Color.Red.copy(alpha = 0.6f), CircleShape).padding(4.dp)) }
        Text(text = if (memory.caption.isNotBlank()) "${memory.caption} - ${DateUtils.formatDate(memory.date)}" else DateUtils.formatDate(memory.date), style = MaterialTheme.typography.bodyMedium, color = Color.White, modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 24.dp))
    }
}
