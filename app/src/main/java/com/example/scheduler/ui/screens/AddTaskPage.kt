package com.example.scheduler.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.widget.DatePicker
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scheduler.values.FontSizeCustomValues
import com.example.scheduler.values.PaddingCustomValues.externalSpacing
import com.example.scheduler.values.PaddingCustomValues.internalSpacing

@Composable
@Preview(showBackground = true)
fun AddTaskScreen() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
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
        TitleAndDescription()
        StartTimePicker()
        StartDatePicker()
        RepeatSchedule()
        DelayTaskTime()
        DelayTaskDay()
        Button(
            onClick = { /*TODO*/ }, modifier = Modifier.align(Alignment.End)
        ) { Text(text = "Add Task") }
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .height(20.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun TitleAndDescription(modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            verticalArrangement = Arrangement.spacedBy(externalSpacing),
            modifier = Modifier.padding(externalSpacing)
        ) {
            val title = rememberSaveable { mutableStateOf("") }
            OutlinedTextField(
                value = title.value,
                onValueChange = { title.value = it },
                label = { Text(text = "Title") },
                modifier = Modifier.fillMaxWidth()
            )
            val description = rememberSaveable { mutableStateOf("") }
            OutlinedTextField(
                value = description.value,
                onValueChange = { description.value = it },
                label = { Text(text = "Description") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
@Preview
fun RepeatSchedule(modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Repeat in",
                fontSize = FontSizeCustomValues.medium,
                modifier = Modifier
                    .weight(1f)
                    .padding(externalSpacing)
            )
            SelectNumberRange("day")
        }
    }
}

@Composable
@Preview
fun StartTimePicker(modifier: Modifier = Modifier) {
    val calendar = Calendar.getInstance()
    val hour = remember { mutableStateOf(calendar[Calendar.HOUR_OF_DAY]) }
    val minute = remember { mutableStateOf(calendar[Calendar.MINUTE]) }
    val timerDialog = TimePickerDialog(
        LocalContext.current, { _, h: Int, m: Int ->
            hour.value = h
            minute.value = m
        }, hour.value, minute.value, false
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
@Preview
fun StartDatePicker(modifier: Modifier = Modifier) {
    val instance = Calendar.getInstance()

    val day = remember { mutableStateOf(instance[Calendar.DAY_OF_MONTH]) }
    val month = remember { mutableStateOf(instance[Calendar.MONTH]) }
    val year = remember { mutableStateOf(instance[Calendar.YEAR]) }

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
@Preview
fun DelayTaskTime(modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Time allowed to delay",
                modifier = Modifier
                    .weight(1f)
                    .padding(externalSpacing),
                fontSize = FontSizeCustomValues.medium
            )
            SelectNumberRange("min")
        }
    }
}

@Composable
@Preview
fun DelayTaskDay(modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Time allowed to Postpone",
                modifier = Modifier
                    .weight(1f)
                    .padding(externalSpacing),
                fontSize = FontSizeCustomValues.medium
            )
            SelectNumberRange("day")
        }
    }
}

@Composable
fun SelectNumberRange(unit: String) {
    val value = remember { mutableStateOf(1) }
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { if (value.value > 0) value.value-- }) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = null)
        }
        Text(
            text = "${value.value} $unit${if (value.value > 1) "s" else ""}",
            fontSize = FontSizeCustomValues.medium
        )
        IconButton(onClick = { value.value++ }) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = null)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RangePreview() {
    SelectNumberRange(unit = "min")
}

private fun getTimeAsText(hour: Int, minute: Int): String {
    val t = { i: Int -> if (i < 10) "0$i" else i.toString() }
    return "${if (hour > 12) (hour - 12).toString() else hour.toString()}:${t(minute)} ${if (hour > 12) "PM" else "AM"}"
}

private fun getDateAsText(y: Int, m: Int, d: Int): String {
    val t = { i: Int -> if (i < 10) "0$i" else i.toString() }
    return "${t(d)}/${t(m)}/${t(y)}"
}