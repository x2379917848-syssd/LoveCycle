package com.lovecare.cycle.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lovecare.cycle.data.entity.PeriodRecord
import com.lovecare.cycle.ui.navigation.Screen
import com.lovecare.cycle.util.DateUtils
import com.lovecare.cycle.viewmodel.PeriodViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodScreen(
    navController: NavController,
    viewModel: PeriodViewModel = viewModel()
) {
    val periods by viewModel.allPeriods.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("经期记录 🌸") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.PeriodAdd.route) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "添加记录")
            }
        }
    ) { padding ->
        if (periods.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "还没有经期记录",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "点击 + 添加第一条记录",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(periods, key = { it.id }) { period ->
                    PeriodCard(
                        period = period,
                        onEdit = { navController.navigate(Screen.PeriodEdit.createRoute(period.id)) },
                        onDelete = { viewModel.deletePeriod(period.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun PeriodCard(
    period: PeriodRecord,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = DateUtils.formatMonthDay(period.startDate),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "编辑",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "删除",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                period.endDate?.let { endDate ->
                    Text(
                        text = "至 ${DateUtils.formatMonthDay(endDate)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                val periodLength = if (period.endDate != null) {
                    ((period.endDate - period.startDate) / (24 * 60 * 60 * 1000L)).toInt() + 1
                } else {
                    1
                }
                Text(
                    text = "持续 $periodLength 天",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val flowEmoji = when (period.flowLevel) {
                    "light" -> "少量 🌸"
                    "medium" -> "中量 🌺"
                    "heavy" -> "多量 🌹"
                    else -> "未知"
                }
                Text(
                    text = flowEmoji,
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "疼痛 ${period.painLevel}/10",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (period.mood.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "心情: ${period.mood}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (period.symptoms.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "症状: ${period.symptoms}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (period.note.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "备注: ${period.note}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("删除确认") },
            text = { Text("确定要删除这条经期记录吗？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("删除", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}
