package com.example.scheduler.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.scheduler.R
import com.example.scheduler.values.FontSizeCustomValues
import com.example.scheduler.values.PaddingCustomValues


@Composable
@Preview
fun AppInfoScreenPrev() {
    AppInfoScreen {
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppInfoScreen(back: () -> Unit) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "App Info") },
                navigationIcon = {
                    IconButton(
                        onClick = back,
                        content = {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                        }
                    )
                }
            )
            // TODO:
        },
        content = {
            AppInfoContent(paddingValues = it)
        }
    )
}

@Composable
fun AppInfoContent(paddingValues: PaddingValues) {
    Column(
        modifier = Modifier.padding(paddingValues),
        content = {
            AppInfoCard(
                title = "SCHEDULER",
                description = "description",
                bottomContent = {
                    ElevatedButton(
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = PaddingCustomValues.mediumSpacing),
                        onClick = { /*TODO*/ },
                        content = {
                            Icon(
                                modifier = Modifier.padding(end = PaddingCustomValues.mediumSpacing),
                                painter = painterResource(id = R.drawable.github_icon_24),
                                contentDescription = null
                            )
                            Text(text = "Scheduler")
                        }
                    )
                }
            )
            // TODO: add self card
            AppInfoCard(
                title = "DEVELOPER",
                description = "description",
                bottomContent = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        content = {
                            FilledIconButton(
                                onClick = { /*TODO*/ },
                                content = {
                                    Icon(
                                        painter = painterResource(id = R.drawable.github_icon_24),
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                    )
                }
            )
        }
    )
}

@Composable
fun AppInfoCard(
    title: String,
    description: String,
    bottomContent: @Composable () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = PaddingCustomValues.mediumSpacing),
        modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingCustomValues.cardSpacing),
        content = {
            Column(
                modifier = Modifier
                    .padding(PaddingCustomValues.mediumSpacing)
                    .fillMaxWidth(),
                content = {
                    Text(
                        modifier = Modifier
                            .padding(bottom = PaddingCustomValues.mediumSpacing)
                            .fillMaxWidth(),
                        text = title,
                        fontSize = FontSizeCustomValues.large
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = description// TODO:
                    )
                    bottomContent()
                }
            )
        }
    )
}

@Composable
fun TitleAndDescription(title: String, description: String) {
    Text(
        modifier = Modifier
            .padding(bottom = PaddingCustomValues.mediumSpacing)
            .fillMaxWidth(),
        text = title,
        fontSize = FontSizeCustomValues.large
    )
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = description// TODO:
    )
}