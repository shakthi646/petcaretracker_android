package com.ksp.petcaretracker.ui.screens.growth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.TrendingUp
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ksp.petcaretracker.domain.model.GrowthLog
import com.ksp.petcaretracker.ui.components.ConfirmationDialog
import com.ksp.petcaretracker.ui.components.EmptyState
import com.ksp.petcaretracker.ui.components.LoadingState
import com.ksp.petcaretracker.ui.components.PetCareFab
import com.ksp.petcaretracker.ui.components.PetCareTopBar
import com.ksp.petcaretracker.utils.DateUtils

@Composable
fun GrowthLogScreen(onNavigateBack: () -> Unit, onNavigateToAddLog: (Long) -> Unit, viewModel: GrowthLogViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    var pendingDeleteId by remember { mutableStateOf<Long?>(null) }
    Scaffold(topBar = { PetCareTopBar(title = "${state.pet?.name ?: ""} Growth", onBackClick = onNavigateBack) }, floatingActionButton = { PetCareFab(onClick = { onNavigateToAddLog(viewModel.petId) }) }) { padding ->
        if (state.isLoading) { LoadingState(modifier = Modifier.padding(padding)) }
        else if (state.logs.isEmpty()) {
            EmptyState(
                icon = Icons.Default.MonitorWeight,
                title = "No growth logs yet",
                subtitle = "Track ${state.pet?.name ?: "your pet"}'s weight over time to monitor their health",
                modifier = Modifier.padding(padding),
                actionLabel = "Log Weight",
                onAction = { onNavigateToAddLog(viewModel.petId) }
            )
        }
        else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (state.logs.size >= 2) { item { WeightGraph(logs = state.logs, modifier = Modifier.fillMaxWidth().height(220.dp)) } }
                itemsIndexed(items = state.logs.reversed(), key = { _, log -> log.id }) { index, log ->
                    AnimatedVisibility(visible = true, enter = fadeIn() + slideInVertically(initialOffsetY = { it * (index + 1) })) { GrowthLogCard(log = log, onDelete = { pendingDeleteId = log.id }) }
                }
            }
        }
    }

    pendingDeleteId?.let { id ->
        ConfirmationDialog(
            title = "Delete weight log?",
            message = "This weight entry will be permanently removed. This action cannot be undone.",
            onConfirm = { viewModel.deleteGrowthLog(id) },
            onDismiss = { pendingDeleteId = null }
        )
    }
}

@Composable
fun WeightGraph(logs: List<GrowthLog>, modifier: Modifier = Modifier) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val onSurface = MaterialTheme.colorScheme.onSurface
    Card(modifier = modifier, shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.TrendingUp, contentDescription = null, tint = primaryColor, modifier = Modifier.size(20.dp)); Spacer(modifier = Modifier.width(8.dp)); Text("Weight Trend", style = MaterialTheme.typography.titleSmall) }
            Spacer(modifier = Modifier.height(12.dp))
            Canvas(modifier = Modifier.fillMaxWidth().height(150.dp)) {
                if (logs.size < 2) return@Canvas
                val minWeight = logs.minOf { it.weight }; val maxWeight = logs.maxOf { it.weight }
                val weightRange = if (maxWeight - minWeight < 0.1f) 1f else maxWeight - minWeight
                val pad = 24f; val graphWidth = size.width - pad * 2; val graphHeight = size.height - pad * 2
                for (i in 0..4) { val y = pad + graphHeight * (1 - i / 4f); drawLine(color = surfaceVariant, start = Offset(pad, y), end = Offset(size.width - pad, y), strokeWidth = 1f) }
                val path = Path()
                logs.forEachIndexed { index, log ->
                    val x = pad + (index.toFloat() / (logs.size - 1)) * graphWidth
                    val y = pad + graphHeight * (1 - (log.weight - minWeight) / weightRange)
                    if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                drawPath(path = path, color = primaryColor, style = Stroke(width = 3f, cap = StrokeCap.Round))
                logs.forEachIndexed { index, log ->
                    val x = pad + (index.toFloat() / (logs.size - 1)) * graphWidth
                    val y = pad + graphHeight * (1 - (log.weight - minWeight) / weightRange)
                    drawCircle(color = primaryColor, radius = 5f, center = Offset(x, y)); drawCircle(color = Color.White, radius = 3f, center = Offset(x, y))
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = DateUtils.formatShortDate(logs.first().date), style = MaterialTheme.typography.labelSmall, color = onSurface.copy(alpha = 0.5f))
                Text(text = DateUtils.formatShortDate(logs.last().date), style = MaterialTheme.typography.labelSmall, color = onSurface.copy(alpha = 0.5f))
            }
        }
    }
}

@Composable
fun GrowthLogCard(log: GrowthLog, onDelete: () -> Unit, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) { Icon(Icons.Default.MonitorWeight, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp)) }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "${log.weight} kg", style = MaterialTheme.typography.titleMedium)
                Text(text = DateUtils.formatDate(log.date), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                if (log.notes.isNotBlank()) { Text(text = log.notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)) }
            }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error) }
        }
    }
}
