package com.lovecare.cycle.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lovecare.cycle.util.DateUtils
import com.lovecare.cycle.viewmodel.DiaryViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryAddEditScreen(
    navController: NavController,
    diaryId: Long? = null,
    viewModel: DiaryViewModel = viewModel()
) {
    val context = LocalContext.current
    val isEditing = diaryId != null

    val editingEntry by viewModel.editingEntry.collectAsState()

    var date by remember { mutableStateOf(System.currentTimeMillis()) }
    var mood by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }
    val selectedTags = remember { mutableStateListOf<String>() }

    val moodOptions = listOf("开心", "平静", "焦虑", "低落", "疲惫", "烦躁")
    val tagOptions = listOf("休息", "工作", "学习", "运动", "饮食", "身体", "情绪", "约会", "其他")

    LaunchedEffect(diaryId) {
        if (diaryId != null) {
            viewModel.loadEntry(diaryId)
        }
    }

    LaunchedEffect(editingEntry) {
        editingEntry?.let { entry ->
            date = entry.date
            mood = entry.mood
            content = entry.content
            tags = entry.tags
            selectedTags.clear()
            selectedTags.addAll(entry.tags.split(",").filter { it.isNotBlank() })
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
                title = { Text(if (isEditing) "编辑日记 ✨" else "写日记 ✨") },
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
                            val cal = Calendar.getInstance().apply { timeInMillis = date }
                            DatePickerDialog(
                                context,
                                { _, year, month, day ->
                                    val newCal = Calendar.getInstance()
                                    newCal.set(year, month, day)
                                    date = newCal.timeInMillis
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
                        Text(DateUtils.formatMonthDay(date) + " " + DateUtils.formatWeekday(date))
                    }
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("今天心情", style = MaterialTheme.typography.titleMedium)

                    Spacer(modifier = Modifier.height(8.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        moodOptions.forEach { option ->
                            val emoji = when (option) {
                                "开心" -> "😊"
                                "平静" -> "😌"
                                "焦虑" -> "😰"
                                "低落" -> "😔"
                                "疲惫" -> "😴"
                                "烦躁" -> "😤"
                                else -> "😐"
                            }
                            FilterChip(
                                selected = mood == option,
                                onClick = { mood = option },
                                label = { Text("$option $emoji") }
                            )
                        }
                    }
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("标签", style = MaterialTheme.typography.titleMedium)

                    Spacer(modifier = Modifier.height(8.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        tagOptions.forEach { option ->
                            FilterChip(
                                selected = option in selectedTags,
                                onClick = {
                                    if (option in selectedTags) {
                                        selectedTags.remove(option)
                                    } else {
                                        selectedTags.add(option)
                                    }
                                    tags = selectedTags.joinToString(",")
                                },
                                label = { Text(option) }
                            )
                        }
                    }
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("日记内容", style = MaterialTheme.typography.titleMedium)

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("今天发生了什么...") },
                        minLines = 5,
                        maxLines = 10
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (content.isNotBlank()) {
                        viewModel.saveEntry(
                            date = date,
                            mood = mood,
                            content = content,
                            tags = tags,
                            existingId = diaryId
                        )
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = content.isNotBlank()
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("保存")
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
