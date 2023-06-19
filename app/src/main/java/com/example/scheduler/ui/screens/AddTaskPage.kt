package com.example.scheduler.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.util.Log
import android.widget.DatePicker
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.scheduler.R
import com.example.scheduler.data.Date
import com.example.scheduler.data.RepetitionEnum
import com.example.scheduler.data.Repetitions
import com.example.scheduler.data.Reps
import com.example.scheduler.data.StringFunctions
import com.example.scheduler.data.StringFunctions.getDateAsText
import com.example.scheduler.data.StringFunctions.getTimeAsText
import com.example.scheduler.data.StringFunctions.numFormatter
import com.example.scheduler.data.Task
import com.example.scheduler.data.Time
import com.example.scheduler.firebase.DatabaseFunctions
import com.example.scheduler.ui.prompt.ShowLoadingPrompt
import com.example.scheduler.values.FontSizeCustomValues
import com.example.scheduler.values.PaddingCustomValues
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

private const val TAG = "AddTaskPage"

@Composable
@Preview(showBackground = true)
fun AddTaskScreenPreview() {
    AddTaskScaffold {}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScaffold(navigateUp: () -> Unit) {
    val title = rememberSaveable { mutableStateOf("") }
    val description = rememberSaveable { mutableStateOf("") }
    val dateWise = rememberSaveable { mutableStateOf(false) }

    val postponeDuration = rememberSaveable { mutableStateOf(1) }
    val daysDelayed = rememberSaveable { mutableStateOf(0) }

    val calenderInstance = Calendar.getInstance()
    val local = LocalDate.now()

    val hour = rememberSaveable { mutableStateOf(calenderInstance[Calendar.HOUR_OF_DAY]) }
    val minute = rememberSaveable { mutableStateOf(calenderInstance[Calendar.MINUTE]) }

    val day = rememberSaveable { mutableStateOf(local.dayOfMonth) }
    val month = rememberSaveable { mutableStateOf(local.monthValue) }
    val year = rememberSaveable { mutableStateOf(local.year) }

    // TODO: show error
    val snackBarHostState = SnackbarHostState()
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Add Task",
                        textAlign = TextAlign.Center,
                        fontSize = FontSizeCustomValues.large
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = navigateUp,
                        content = {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                        }
                    )
                },
                actions = {
                    IconButton(
                        onClick = { TODO("show help or guide") },
                        content = {// TODO: replace the vector image for help circled
                            Icon(imageVector = Icons.Filled.Info, contentDescription = null)
                        }
                    )
                }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(it)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(remember { ScrollState(0) }),
                verticalArrangement = Arrangement.spacedBy(
                    PaddingCustomValues.smallSpacing,
                    Alignment.CenterVertically
                ),
            ) {
                Text(
                    text = "New Task",
                    fontSize = FontSizeCustomValues.extraLarge,
                    modifier = Modifier
                        .padding(
                            start = PaddingCustomValues.mediumSpacing,
                            end = PaddingCustomValues.mediumSpacing,
                            top = PaddingCustomValues.screenGap,
                            bottom = PaddingCustomValues.smallSpacing
                        ),
                    fontWeight = FontWeight.Bold
                )
                TitleAndDescription(title, description)
                StartTimePicker(hour = hour, minute = minute)
                StartDatePicker(day = day, month = month, year = year)
                RepeatSchedule(dateWise = dateWise, daysDelayed = daysDelayed, date = day)
                DelayTaskDay(postponeDuration = postponeDuration)
                val savingInProgress = remember { mutableStateOf(false) }
                if (savingInProgress.value) {
                    ShowLoadingPrompt("Saving...")
                }
                AnimatedVisibility(visible = day.value > 28) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = PaddingCustomValues.screenGap),
                        text = "*The task can't be repeated on the ${numFormatter(day.value)} of every month as it isn't applicable for every month",
//                        textAlign = TextAlign.Left,
                        color = Color.Red
                    )
                }
                Button(
                    onClick = {
                        savingInProgress.value = true
                        val task = Task(
                            title = title.value,
                            description = description.value,
                            timeForReminder = Time(
                                hour = hour.value,
                                minute = minute.value
                            ),
                            dateForReminder = Date(
                                dayOfMonth = day.value,
                                month = month.value,
                                year = year.value
                            ),
                            dateWise = dateWise.value,
                            repeatGapDuration = daysDelayed.value,
                            postponeDuration = postponeDuration.value
                        )
                        Log.d(
                            TAG, "gson str = " +
                                    GsonBuilder().setPrettyPrinting().create().toJson(task)
                        )

                        DatabaseFunctions.uploadTaskToFirebase(
                            task = task,
                            onSuccessListener = navigateUp,
                            onFailureListener = { issue ->
                                savingInProgress.value = false
                                CoroutineScope(Dispatchers.IO).launch {
                                    snackBarHostState.showSnackbar(
                                        message = "Database Error: $issue",
                                        withDismissAction = true
                                    )
                                }
                            }
                        )
                        // TODO: add a un dismissible prompt to show saving in progress
                    },
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(
                            bottom = PaddingCustomValues.screenGap,
                            end = PaddingCustomValues.smallSpacing
                        ),
                    content = { Text(text = "Add Task") },
                    enabled = !savingInProgress.value
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitleAndDescription(
    title: MutableState<String>,
    description: MutableState<String>, modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            verticalArrangement = Arrangement.spacedBy(PaddingCustomValues.mediumSpacing),
            modifier = Modifier.padding(PaddingCustomValues.mediumSpacing)
        ) {
            val titleLimit = 35
            val descriptionLimit = 500
            OutlinedTextField(
                value = title.value,
                onValueChange = {
                    try {
                        if (it.length <= titleLimit && (it.last() != '\n')) {
                            title.value = it
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        title.value = ""
                    }
                },
                label = { Text(text = "Title") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(
                        onClick = { title.value = "" },
                        content = {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = null
                            )
                        }
                    )
                },
                singleLine = true,
                supportingText = {
                    Text(
                        text = "${title.value.length}/$titleLimit",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End
                    )
                }
            )
            OutlinedTextField(
                value = description.value,
                onValueChange = {
                    try {
                        if (it.length <= descriptionLimit && it.last() != '\n') {
                            description.value = it
                        }
                    } catch (e: Exception) {
                        description.value = ""
                        e.printStackTrace()
                    }
                },
                label = { Text(text = "Description") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(
                        onClick = { description.value = "" },
                        content = {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = null
                            )
                        }
                    )
                },
                supportingText = {
                    Text(
                        text = "${description.value.length}/$descriptionLimit",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End
                    )
                },
                maxLines = 5
            )
        }
    }
}

@Composable
@Preview
fun RepeatSchedulePreview() {
    RepeatSchedule(
        dateWise = remember { mutableStateOf(true) },
        daysDelayed = remember { mutableStateOf(0) },
        date = remember { mutableStateOf(0) }
    )
}

@Composable
fun RepeatSchedule(
    modifier: Modifier = Modifier,
    dateWise: MutableState<Boolean>,
    daysDelayed: MutableState<Int>,
    date: MutableState<Int>
) {
    Card(modifier = modifier) {
        val selected = remember { mutableStateOf(Repetitions.DAY) }
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = PaddingCustomValues.mediumSpacing),
            horizontalArrangement = Arrangement.spacedBy(PaddingCustomValues.mediumSpacing)
        ) {
            TimeFilterChip(
                chipType = Repetitions.DAY,
                modifier = Modifier.weight(1f),
                selectedReps = selected,
                daysDelayed = daysDelayed
            )
            TimeFilterChip(
                chipType = Repetitions.WEEK,
                modifier = Modifier.weight(1f),
                selectedReps = selected,
                daysDelayed = daysDelayed
            )
            TimeFilterChip(
                chipType = Repetitions.MONTH,
                modifier = Modifier.weight(1f),
                selectedReps = selected,
                daysDelayed = daysDelayed
            )
            TimeFilterChip(
                chipType = Repetitions.SAME_DATE,
                modifier = Modifier.weight(1f),
                selectedReps = selected,
                daysDelayed = daysDelayed,
                enabled = date.value < 28
            )
            dateWise.value = (selected.value.enumValue == RepetitionEnum.SAME_DATE)
        }

        AnimatedVisibility(visible = selected.value.enumValue == RepetitionEnum.SAME_DATE) {
            Row {
                Text(
                    text = "The given task would be repeated on ${numFormatter(date.value)} of every month",
                    fontSize = FontSizeCustomValues.medium,
                    modifier = Modifier
                        .padding(PaddingCustomValues.mediumSpacing)
                )
            }

        }
        AnimatedVisibility(visible = selected.value.enumValue != RepetitionEnum.SAME_DATE) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Repeat in",
                    fontSize = FontSizeCustomValues.medium,
                    modifier = Modifier
                        .weight(1f)
                        .padding(PaddingCustomValues.mediumSpacing)
                )
                IconButton(
                    onClick = {
                        if (daysDelayed.value >= selected.value.step) {
                            daysDelayed.value -= selected.value.step
                        }
                    },
                    content = {
                        Icon(
                            painter = painterResource(id = (R.drawable.remove_24)),
                            contentDescription = null
                        )
                    }
                )
                val display = daysDelayed.value / selected.value.step
                Text(
                    text = "${StringFunctions.getTextWithS(selected.value.timeUnit, display)} " +
                            if (selected.value.enumValue != RepetitionEnum.DAY) "[${daysDelayed.value}d]" else "",
                    fontSize = FontSizeCustomValues.medium
                )
                IconButton(
                    onClick = {
                        if (daysDelayed.value < 1000) {
                            daysDelayed.value += selected.value.step
                        }
                    },
                    content = {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                    }
                )
            }

        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeFilterChip(
    chipType: Reps,
    modifier: Modifier,
    selectedReps: MutableState<Reps>,
    daysDelayed: MutableState<Int>,
    enabled: Boolean = true
) {
    val selector: (Reps) -> Unit = { reps: Reps ->
        selectedReps.value = reps
        daysDelayed.value = (daysDelayed.value / reps.step) * reps.step
    }
    FilterChip(
        enabled = enabled,
        modifier = modifier,
        onClick = { selector(chipType) },
        label = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = chipType.timeUnit,
                textAlign = TextAlign.Center
            )
        },
        selected = chipType.enumValue == selectedReps.value.enumValue
    )
}

@Composable
fun StartTimePicker(
    modifier: Modifier = Modifier,
    hour: MutableState<Int>,
    minute: MutableState<Int>
) {
    val calendar = Calendar.getInstance()
    val timerDialog = TimePickerDialog(
        LocalContext.current,
        { _, h: Int, m: Int ->
            hour.value = h
            minute.value = m
        },
        calendar[Calendar.HOUR_OF_DAY],
        calendar[Calendar.MINUTE],
        false
    )
    Card(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Select time for reminder",
                modifier = Modifier
                    .padding(PaddingCustomValues.mediumSpacing)
                    .weight(1f),
                fontSize = FontSizeCustomValues.medium
            )
            Text(
                text = getTimeAsText(hour.value, minute.value),
                fontSize = FontSizeCustomValues.medium
            )
            IconButton(onClick = { timerDialog.show() }) {
                Icon(imageVector = Icons.Filled.Edit, contentDescription = null)
            }
        }
    }
}

@Composable
fun StartDatePicker(
    modifier: Modifier = Modifier,
    day: MutableState<Int>,
    month: MutableState<Int>,
    year: MutableState<Int>,
) {
    val datePickerDialog = DatePickerDialog(
        LocalContext.current, { _: DatePicker, y: Int, m: Int, d: Int ->
            year.value = y
            month.value = m + 1
            day.value = d
        },
        year.value,
        month.value - 1,
        day.value
    )

    Card(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Select date for Task",
                modifier = Modifier
                    .padding(PaddingCustomValues.mediumSpacing)
                    .weight(1f),
                fontSize = FontSizeCustomValues.medium
            )
            Text(
                text = getDateAsText(year.value, month.value, day.value),
                fontSize = FontSizeCustomValues.medium
            )
            IconButton(onClick = { datePickerDialog.show() }) {
                Icon(imageVector = Icons.Filled.Edit, contentDescription = null)
            }
        }
    }
}

@Composable
fun DelayTaskDay(modifier: Modifier = Modifier, postponeDuration: MutableState<Int>) {
    Card(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Event Postpone Duration",
                modifier = Modifier
                    .weight(1f)
                    .padding(PaddingCustomValues.mediumSpacing),
                fontSize = FontSizeCustomValues.medium
            )
            SelectNumberRange(
                unit = "day",
                value = postponeDuration,
                rangeMin = 1,
                rangeMax = 5
            )
        }
    }
}

@Composable
fun SelectNumberRange(
    unit: String,
    rangeMin: Int = 0,
    rangeMax: Int = 20,
    value: MutableState<Int>
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { if (value.value > rangeMin) value.value-- }) {
            Icon(painter = painterResource(id = R.drawable.remove_24), contentDescription = null)
        }
        Text(
            text = "${value.value} $unit${if (value.value > 1) "s" else ""}",
            fontSize = FontSizeCustomValues.medium
        )
        IconButton(onClick = { if (value.value < rangeMax) value.value++ }) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = null)
        }
    }
}

@Composable
@Preview(showBackground = true)
fun RangePreview() {
    SelectNumberRange(unit = "min", value = remember { mutableStateOf(1) })
}