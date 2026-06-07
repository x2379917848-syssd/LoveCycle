package com.lovecare.cycle.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lovecare.cycle.ui.navigation.Screen
import com.lovecare.cycle.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var showWaterGoalDialog by remember { mutableStateOf(false) }
    var showCupSizeDialog by remember { mutableStateOf(false) }
    var showIntervalDialog by remember { mutableStateOf(false) }
    var showCycleLengthDialog by remember { mutableStateOf(false) }
    var showPeriodDurationDialog by remember { mutableStateOf(false) }
    var showPrePeriodDaysDialog by remember { mutableStateOf(false) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.setNotificationPermissionGranted(isGranted)
    }

    LaunchedEffect(Unit) {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.POST_NOTIFICATIONS
        } else {
            null
        }

        if (permission != null) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
            viewModel.setNotificationPermissionGranted(granted)
        } else {
            viewModel.setNotificationPermissionGranted(true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置 ⚙️") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            SettingsSection(title = "喝水设置") {
                SettingsItem(
                    icon = Icons.Default.WaterDrop,
                    title = "每日喝水目标",
                    subtitle = "${uiState.dailyWaterGoalMl} ml",
                    onClick = { showWaterGoalDialog = true }
                )
                SettingsItem(
                    icon = Icons.Default.Coffee,
                    title = "杯子容量",
                    subtitle = "${uiState.cupSizeMl} ml",
                    onClick = { showCupSizeDialog = true }
                )
            }

            SettingsSection(title = "提醒设置") {
                SettingsItem(
                    icon = Icons.Default.Notifications,
                    title = "喝水提醒",
                    subtitle = if (uiState.waterReminderEnabled) "已开启" else "已关闭",
                    onClick = { viewModel.setWaterReminderEnabled(!uiState.waterReminderEnabled) },
                    trailing = {
                        Switch(
                            checked = uiState.waterReminderEnabled,
                            onCheckedChange = { viewModel.setWaterReminderEnabled(it) }
                        )
                    }
                )

                if (!uiState.notificationPermissionGranted) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "开启通知后，我才能准时提醒你喝水",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                TextButton(
                                    onClick = {
                                        notificationPermissionLauncher.launch(
                                            Manifest.permission.POST_NOTIFICATIONS
                                        )
                                    }
                                ) {
                                    Text("开启")
                                }
                            }
                        }
                    }
                }

                SettingsItem(
                    icon = Icons.Default.Timer,
                    title = "提醒间隔",
                    subtitle = "${uiState.reminderIntervalMinutes} 分钟（最低15分钟）",
                    onClick = { showIntervalDialog = true }
                )

                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "经期前提醒",
                    subtitle = if (uiState.prePeriodReminderEnabled) "已开启，提前 ${uiState.prePeriodReminderDays} 天" else "已关闭",
                    onClick = { viewModel.setPrePeriodReminderEnabled(!uiState.prePeriodReminderEnabled) },
                    trailing = {
                        Switch(
                            checked = uiState.prePeriodReminderEnabled,
                            onCheckedChange = { viewModel.setPrePeriodReminderEnabled(it) }
                        )
                    }
                )

                if (uiState.prePeriodReminderEnabled) {
                    SettingsItem(
                        icon = Icons.Default.CalendarMonth,
                        title = "经期前提醒天数",
                        subtitle = "提前 ${uiState.prePeriodReminderDays} 天",
                        onClick = { showPrePeriodDaysDialog = true }
                    )
                }
            }

            SettingsSection(title = "经期设置") {
                SettingsItem(
                    icon = Icons.Default.DateRange,
                    title = "默认周期长度",
                    subtitle = "${uiState.periodDefaultCycleLength} 天",
                    onClick = { showCycleLengthDialog = true }
                )
                SettingsItem(
                    icon = Icons.Default.Schedule,
                    title = "默认经期时长",
                    subtitle = "${uiState.periodDefaultDuration} 天",
                    onClick = { showPeriodDurationDialog = true }
                )
            }

            SettingsSection(title = "外观") {
                SettingsItem(
                    icon = Icons.Default.Palette,
                    title = "主题",
                    subtitle = when (uiState.themeMode) {
                        "light" -> "浅色"
                        "dark" -> "深色"
                        else -> "跟随系统"
                    },
                    onClick = {
                        val newMode = when (uiState.themeMode) {
                            "system" -> "light"
                            "light" -> "dark"
                            else -> "system"
                        }
                        viewModel.setThemeMode(newMode)
                    }
                )
            }

            SettingsSection(title = "关于") {
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "关于她的小日历",
                    subtitle = "版本 1.0.0",
                    onClick = { navController.navigate(Screen.About.route) }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showWaterGoalDialog) {
        NumberInputDialog(
            title = "每日喝水目标",
            value = uiState.dailyWaterGoalMl,
            unit = "ml",
            onDismiss = { showWaterGoalDialog = false },
            onConfirm = {
                viewModel.setDailyWaterGoalMl(it)
                showWaterGoalDialog = false
            }
        )
    }

    if (showCupSizeDialog) {
        NumberInputDialog(
            title = "杯子容量",
            value = uiState.cupSizeMl,
            unit = "ml",
            onDismiss = { showCupSizeDialog = false },
            onConfirm = {
                viewModel.setCupSizeMl(it)
                showCupSizeDialog = false
            }
        )
    }

    if (showIntervalDialog) {
        NumberInputDialog(
            title = "提醒间隔",
            value = uiState.reminderIntervalMinutes,
            unit = "分钟",
            minValue = 15,
            onDismiss = { showIntervalDialog = false },
            onConfirm = {
                viewModel.setReminderIntervalMinutes(it)
                showIntervalDialog = false
            }
        )
    }

    if (showCycleLengthDialog) {
        NumberInputDialog(
            title = "默认周期长度",
            value = uiState.periodDefaultCycleLength,
            unit = "天",
            minValue = 15,
            maxValue = 60,
            onDismiss = { showCycleLengthDialog = false },
            onConfirm = {
                viewModel.setPeriodDefaultCycleLength(it)
                showCycleLengthDialog = false
            }
        )
    }

    if (showPeriodDurationDialog) {
        NumberInputDialog(
            title = "默认经期时长",
            value = uiState.periodDefaultDuration,
            unit = "天",
            minValue = 1,
            maxValue = 15,
            onDismiss = { showPeriodDurationDialog = false },
            onConfirm = {
                viewModel.setPeriodDefaultDuration(it)
                showPeriodDurationDialog = false
            }
        )
    }

    if (showPrePeriodDaysDialog) {
        NumberInputDialog(
            title = "经期前提醒天数",
            value = uiState.prePeriodReminderDays,
            unit = "天",
            minValue = 1,
            maxValue = 7,
            onDismiss = { showPrePeriodDaysDialog = false },
            onConfirm = {
                viewModel.setPrePeriodReminderDays(it)
                showPrePeriodDaysDialog = false
            }
        )
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        content()
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    trailing: @Composable (() -> Unit)? = null
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyLarge)
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            trailing?.invoke()
        }
    }
}

@Composable
fun NumberInputDialog(
    title: String,
    value: Int,
    unit: String,
    minValue: Int = 0,
    maxValue: Int = Int.MAX_VALUE,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var input by remember { mutableStateOf(value.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it.filter { c -> c.isDigit() } },
                label = { Text("数值 ($unit)") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    input.toIntOrNull()?.let { intValue ->
                        val clamped = intValue.coerceIn(minValue, maxValue)
                        onConfirm(clamped)
                    }
                }
            ) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
