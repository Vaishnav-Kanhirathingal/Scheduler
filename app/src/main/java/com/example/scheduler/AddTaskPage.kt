package com.example.scheduler

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.widget.DatePicker
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.sharp.Warning
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scheduler.values.FontSizeCustomValues
import com.example.scheduler.values.PaddingCustomValues.externalSpacing
import com.example.scheduler.values.PaddingCustomValues.internalSpacing

@Composable
@Preview(showBackground = true)
fun AddTaskScreen() {
    val scrollState = remember { ScrollState(0) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(internalSpacing, Alignment.CenterVertically),
    ) {
        Text(
            text = "Add Task",
            fontSize = 30.sp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(externalSpacing)
        )
        TitleAndDescription()
        StartTimePicker()
        StartDatePicker()
        RepeatSchedule()
        Button(
            onClick = { /*TODO*/ }, modifier = Modifier.align(Alignment.End)
        ) { Text(text = "Add Task") }
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
                label = { Text(text = "Title...") },
                modifier = Modifier.fillMaxWidth()
            )
            val description = rememberSaveable { mutableStateOf("") }
            OutlinedTextField(
                value = description.value,
                onValueChange = { description.value = it },
                label = { Text(text = "Description...") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
@Preview
fun RepeatSchedule(modifier: Modifier = Modifier) {
    val repeatNumber = remember {
        mutableStateOf(1)
    }
    Card(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Days to repeat after",
                fontSize = FontSizeCustomValues.medium,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = externalSpacing)
            )
            IconButton(onClick = {
                if (repeatNumber.value > 0) {
                    repeatNumber.value--
                }
            }) {
                Icon(imageVector = Icons.Sharp.Warning, contentDescription = null)
            }
            Text(
                text = repeatNumber.value.toString(),
                fontSize = FontSizeCustomValues.medium,
                textAlign = TextAlign.Center
            )
            IconButton(onClick = { repeatNumber.value++ }) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = null)
            }
        }
    }
}

@Composable
@Preview
fun StartTimePicker(modifier: Modifier = Modifier) {
    val calendar = Calendar.getInstance()
    val hour = calendar[Calendar.HOUR_OF_DAY]
    val minute = calendar[Calendar.MINUTE]
    val time = remember { mutableStateOf(getTimeAsText(hour, minute)) }
    val timerDialog = TimePickerDialog(
        LocalContext.current, { _, mHour: Int, mMinute: Int ->
            time.value = getTimeAsText(mHour, mMinute)
        }, hour, minute, false
    )
    Card(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Select time for reminder",
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = externalSpacing)
                    .weight(1f),
                fontSize = FontSizeCustomValues.medium
            )
            Text(
                text = time.value, fontSize = FontSizeCustomValues.medium
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
    val date = remember {
        mutableStateOf("--1/1/1--")
    }
    val datePickerDialog = DatePickerDialog(
        LocalContext.current, { _: DatePicker, y: Int, m: Int, d: Int ->
            date.value = getDateAsText(y, m, d)
        }, 2022, 1, 1
    )
    Card(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Initial Date",
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = externalSpacing)
                    .weight(1f),
                fontSize = FontSizeCustomValues.medium
            )
            Text(
                text = date.value, fontSize = FontSizeCustomValues.medium
            )
            IconButton(onClick = { datePickerDialog.show() }) {
                Icon(imageVector = Icons.Filled.Edit, contentDescription = null)
            }
        }
    }
}

fun getTimeAsText(hour: Int, minute: Int): String {
    return "${if (hour < 10) "0$hour" else hour.toString()}:${if (minute < 10) "0$minute" else minute.toString()}"
}

fun getDateAsText(y: Int, m: Int, d: Int): String {
    val t = { i: Int -> if (i < 10) "0$i" else i.toString() }
    return "${t(d)}/${t(m)}/${t(y)}"
}