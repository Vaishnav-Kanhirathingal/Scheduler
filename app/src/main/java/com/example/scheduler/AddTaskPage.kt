package com.example.scheduler

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AddTaskScreen() {
    val title = remember { mutableStateOf(true) }
    val description = remember { mutableStateOf("") }
//    repeatGapDuration
//    remindAt
//    start
//    delayTime
//    delayDuration
    Column(modifier = Modifier.fillMaxWidth()) {
        titleAndDescription(Modifier.fillMaxWidth())
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun titleAndDescription(modifier: Modifier) {
    Card(modifier = modifier) {
        Column(
            modifier = modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val title = rememberSaveable { mutableStateOf("") }
            TextField(
                value = title.value,
                onValueChange = { title.value = it },
                modifier = modifier,
                label = {
                    Text(text = "Title...")
                }
            )
            val description = rememberSaveable { mutableStateOf("") }
            TextField(
                value = description.value,
                onValueChange = { description.value = it },
                modifier = modifier,
                label = {
                    Text(text = "Description...")
                }
            )
        }
    }
}

@Preview()
@Composable
fun preview() {
    titleAndDescription(Modifier.fillMaxWidth())
}