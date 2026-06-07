package com.lovecare.cycle.ui.screens

import android.app.DatePickerDialog
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lovecare.cycle.util.DateUtils
import com.lovecare.cycle.viewmodel.PeriodViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodAddEditScreen(
    navController: NavController,
    periodId: Long? = null,
    viewModel: PeriodViewModel = viewModel()
) {
    val context = LocalContext.current
    val isEditing = periodId != null

    val editingPeriod by viewModel.editingPeriod.collectAsState()

    var startDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var endDate by remember { mutableStateOf<Long?>(null) }
    var flowLevel by remember { mutableStateOf("medium") }
    var painLevel by remember { mutableStateOf(0) }
    var mood by remember { mutableStateOf("") }
    var symptoms by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    val moodOptions = listOf("开心", "平静", "焦虑", "低落", "疲惫", "烦躁")
    val symptomOptions = listOf("腹痛", "腰酸", "头痛", "胸胀", "疲惫", "失眠", "食欲变化", "情绪波动", "其他")
    val selectedSymptoms = remember { mutableStateListOf<String>() }

    LaunchedEffect(periodId) {
        if (periodId != null) {
            viewModel.loadPeriod(periodId)
        }
    }

    LaunchedEffect(editingPeriod) {
        editingPeriod?.let { period ->
            startDate = period.startDate
            endDate = period.endDate
            flowLevel = period.flowLevel
            painLevel = period.painLevel
            mood = period.mood
            symptoms = period.symptoms
            note = period.note
            selectedSymptoms.clear()
            selectedSymptoms.addAll(period.symptoms.split(",").filter { it.isNotBlank() })
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearEditing()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "编辑记录 🌸" else "添加记录 🌸") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("日期", style = MaterialTheme.typography.titleMedium)

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = {
                            val cal = Calendar.getInstance().apply { timeInMillis = startDate }
                            DatePickerDialog(
                                context,
                                { _, year, month, day ->
                                    val newCal = Calendar.getInstance()
                                    newCal.set(year, month, day)
                                    startDate = newCal.timeInMillis
                                },
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.DateRange, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("开始日期: ${DateUtils.formatMonthDay(startDate)}")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = {
                                val cal = Calendar.getInstance().apply {
                                    timeInMillis = endDate ?: startDate
                                }
                                DatePickerDialog(
                                    context,
                                    { _, year, month, day ->
                                        val newCal = Calendar.getInstance()
                                        newCal.set(year, month, day)
                                        val newEndDate = newCal.timeInMillis
                                        if (newEndDate >= startDate) {
                                            endDate = newEndDate
                                        }
                                    },
                                    cal.get(Calendar.YEAR),
                                    cal.get(Calendar.MONTH),
                                    cal.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (endDate != null) "结束: ${DateUtils.formatMonthDay(endDate!!)}" else "选择结束日期")
                        }

                        if (endDate != null) {
                            TextButton(onClick = { endDate = null }) {
                                Text("清除")
                            }
                        }
                    }
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("流量", style = MaterialTheme.typography.titleMedium)

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            "light" to "少 🌸",
                            "medium" to "中 🌺",
                            "heavy" to "多 🌹"
                        ).forEach { (value, label) ->
                            FilterChip(
                                selected = flowLevel == value,
                                onClick = { flowLevel = value },
                                label = { Text(label) }
                            )
                        }
                    }
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("疼痛程度: $painLevel/10", style = MaterialTheme.typography.titleMedium)

                    Spacer(modifier = Modifier.height(8.dp))

                    Slider(
                        value = painLevel.toFloat(),
                        onValueChange = { painLevel = it.toInt() },
                        valueRange = 0f..10f,
                        steps = 9
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("不疼", style = MaterialTheme.typography.bodySmall)
                        Text("很疼", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("心情", style = MaterialTheme.typography.titleMedium)

                    Spacer(modifier = Modifier.height(8.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        moodOptions.forEach { option ->
                            FilterChip(
                                selected = mood == option,
                                onClick = { mood = option },
                                label = { Text(option) }
                            )
                        }
                    }
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("症状", style = MaterialTheme.typography.titleMedium)

                    Spacer(modifier = Modifier.height(8.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        symptomOptions.forEach { option ->
                            FilterChip(
                                selected = option in selectedSymptoms,
                                onClick = {
                                    if (option in selectedSymptoms) {
                                        selectedSymptoms.remove(option)
                                    } else {
                                        selectedSymptoms.add(option)
                                    }
                                    symptoms = selectedSymptoms.joinToString(",")
                                },
                                label = { Text(option) }
                            )
                        }
                    }
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("备注", style = MaterialTheme.typography.titleMedium)

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("有什么想记录的...") },
                        minLines = 2
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    viewModel.savePeriod(
                        startDate = startDate,
                        endDate = endDate,
                        flowLevel = flowLevel,
                        painLevel = painLevel,
                        mood = mood,
                        symptoms = symptoms,
                        note = note,
                        existingId = periodId
                    )
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("保存")
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
