package com.example.scheduler.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.util.Log
import android.widget.DatePicker
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scheduler.data.Date
import com.example.scheduler.data.Repetition
import com.example.scheduler.data.Repetitions
import com.example.scheduler.data.Reps
import com.example.scheduler.data.StringFunctions.getDateAsText
import com.example.scheduler.data.StringFunctions.getTimeAsText
import com.example.scheduler.data.StringFunctions.numFormatter
import com.example.scheduler.data.Task
import com.example.scheduler.data.Time
import com.example.scheduler.firebase.DatabaseFunctions
import com.example.scheduler.values.FontSizeCustomValues
import com.example.scheduler.values.PaddingCustomValues.externalSpacing
import com.example.scheduler.values.PaddingCustomValues.internalSpacing
import com.google.gson.GsonBuilder

private const val TAG = "AddTaskPage"

@Composable
@Preview(showBackground = true)
fun AddTaskScreenPreview() {
    AddTaskScreen {}
}

@Composable
fun AddTaskScreen(onCompletion: () -> Unit) {
    val title = rememberSaveable { mutableStateOf("") }
    val description = rememberSaveable { mutableStateOf("") }
    val dateWise = rememberSaveable { mutableStateOf(false) }
    val snoozeDuration = rememberSaveable { mutableStateOf(0) }
    val postponeDuration = rememberSaveable { mutableStateOf(1) }
    val daysDelayed = rememberSaveable { mutableStateOf(0) }

    val calenderInstance = Calendar.getInstance()

    val hour = rememberSaveable { mutableStateOf(calenderInstance[Calendar.HOUR_OF_DAY]) }
    val minute = rememberSaveable { mutableStateOf(calenderInstance[Calendar.MINUTE]) }

    val day = rememberSaveable { mutableStateOf(calenderInstance[Calendar.DAY_OF_MONTH]) }
    val month = rememberSaveable { mutableStateOf(calenderInstance[Calendar.MONTH]) }
    val year = rememberSaveable { mutableStateOf(calenderInstance[Calendar.YEAR]) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(horizontal = 20.dp)
            .verticalScroll(remember { ScrollState(0) }),
        verticalArrangement = Arrangement.spacedBy(internalSpacing, Alignment.CenterVertically),
    ) {
        Text(
            text = "Add Task",
            fontSize = 36.sp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(externalSpacing)
        )
        TitleAndDescription(title, description)
        StartTimePicker(hour = hour, minute = minute)
        StartDatePicker(day = day, month = month, year = year)
        RepeatSchedule(dateWise = dateWise, daysDelayed = daysDelayed, date = day)
        DelayTaskTime(snoozeDuration = snoozeDuration)
        DelayTaskDay(postponeDuration = postponeDuration)
        Button(
            onClick = {
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
                    snoozeDuration = snoozeDuration.value,
                    postponeDuration = postponeDuration.value
                )
                Log.d(TAG, "gson str = ${GsonBuilder().setPrettyPrinting().create().toJson(task)}")
                DatabaseFunctions.uploadTaskToFirebase(
                    task = task,
                    onSuccessListener = onCompletion,
                    onFailureListener = {
                        // TODO: set something
                    }
                )
                // TODO: save the task variable to datastore
                // TODO: add a undismissable prompt to show saving in progress
                // TODO: save to firebase
            },
            modifier = Modifier
                .align(Alignment.End)
                .padding(bottom = 20.dp),
            content = { Text(text = "Add Task") }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitleAndDescription(
    title: MutableState<String>,
    description: MutableState<String>, modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            verticalArrangement = Arrangement.spacedBy(externalSpacing),
            modifier = Modifier.padding(externalSpacing)
        ) {
            OutlinedTextField(
                value = title.value,
                onValueChange = { title.value = it },
                label = { Text(text = "Title") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(
                        onClick = { title.value = "" },
                        content = {
                            Icon(imageVector = Icons.Filled.Refresh, contentDescription = null)
                        }
                    )
                }
            )
            OutlinedTextField(
                value = description.value,
                onValueChange = { description.value = it },
                label = { Text(text = "Description") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(
                        onClick = { description.value = "" },
                        content = {
                            Icon(imageVector = Icons.Filled.Refresh, contentDescription = null)
                        }
                    )
                }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepeatSchedule(
    modifier: Modifier = Modifier,
    dateWise: MutableState<Boolean>,
    daysDelayed: MutableState<Int>,
    date: MutableState<Int>
) {
    Card(modifier = modifier) {
        Column {
            val selected = remember { mutableStateOf(Repetitions.DAY) }
            Row(
                modifier = modifier
                    .padding(horizontal = externalSpacing)
                    .horizontalScroll(ScrollState(0)),
                horizontalArrangement = Arrangement.spacedBy(externalSpacing)
            ) {
                val selector: (Reps) -> Unit = { reps: Reps ->
                    selected.value = reps
                    daysDelayed.value = (daysDelayed.value / reps.step) * reps.step
                }
                FilterChip(
                    onClick = { selector(Repetitions.DAY) },
                    label = { Text(text = "Day") },
                    selected = Repetition.DAY == selected.value.enumValue
                )
                FilterChip(
                    onClick = { selector(Repetitions.WEEK) },
                    label = { Text(text = "Week") },
                    selected = Repetition.WEEK == selected.value.enumValue
                )
                FilterChip(
                    onClick = { selector(Repetitions.MONTH) },
                    label = { Text(text = "Month") },
                    selected = Repetition.MONTH == selected.value.enumValue
                )
                FilterChip(
                    onClick = { selector(Repetitions.SAME_DATE) },
                    label = { Text(text = "Date-Wise") },
                    selected = Repetition.SAME_DATE == selected.value.enumValue
                )
                dateWise.value = (selected.value.enumValue == Repetition.SAME_DATE)
            }

            AnimatedVisibility(visible = selected.value.enumValue == Repetition.SAME_DATE) {
                // TODO: display the date of month
                Row {
                    Text(
                        text = "The given task would be repeated on ${numFormatter(date.value)} of every month",
                        fontSize = FontSizeCustomValues.medium,
                        modifier = Modifier
                            .padding(externalSpacing)
                    )
                }

            }
            AnimatedVisibility(visible = selected.value.enumValue != Repetition.SAME_DATE) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Repeat in",
                        fontSize = FontSizeCustomValues.medium,
                        modifier = Modifier
                            .weight(1f)
                            .padding(externalSpacing)
                    )
                    IconButton(
                        onClick = {
                            if (daysDelayed.value >= selected.value.step) {
                                daysDelayed.value -= selected.value.step
                            }
                        },
                        content = {
                            Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                        }
                    )
                    val display = daysDelayed.value / selected.value.step
                    Text(
                        text = "$display ${selected.value.timeUnit}${if (display > 1) "s" else ""} ${
                            if (selected.value.enumValue != Repetition.DAY) "[${daysDelayed.value}d]" else ""
                        }",
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
                    .padding(externalSpacing)
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
            month.value = m
            day.value = d
        },
        year.value,
        month.value,
        day.value
    )

    Card(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Select date for Task",
                modifier = Modifier
                    .padding(externalSpacing)
                    .weight(1f),
                fontSize = FontSizeCustomValues.medium
            )
            Text(
                text = getDateAsText(year.value, month.value + 1, day.value),
                fontSize = FontSizeCustomValues.medium
            )
            IconButton(onClick = { datePickerDialog.show() }) {
                Icon(imageVector = Icons.Filled.Edit, contentDescription = null)
            }
        }
    }
}

@Composable
fun DelayTaskTime(modifier: Modifier = Modifier, snoozeDuration: MutableState<Int>) {
    Card(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Snooze Duration",
                modifier = Modifier
                    .weight(1f)
                    .padding(externalSpacing),
                fontSize = FontSizeCustomValues.medium
            )
            SelectNumberRange(
                unit = "min",
                value = snoozeDuration
            )
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
                    .padding(externalSpacing),
                fontSize = FontSizeCustomValues.medium
            )
            SelectNumberRange(
                unit = "day",
                value = postponeDuration,
                rangeMin = 1,
                rangeMax = 15
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
            Icon(imageVector = Icons.Filled.Add, contentDescription = null)
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
