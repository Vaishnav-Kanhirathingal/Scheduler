package com.example.scheduler.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.scheduler.R
import com.example.scheduler.values.FontSizeCustomValues
import com.example.scheduler.values.PaddingCustomValues

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun AppInfoScreen() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "App Info") },
                navigationIcon = {
                    IconButton(
                        onClick = { /*TODO*/ },
                        content = {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                        }
                    )
                }
            )
            // TODO:
        },
        content = {
            Column(
                modifier = Modifier.padding(it),
                content = {
                    Card(
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
                                        modifier = Modifier.padding(bottom = PaddingCustomValues.mediumSpacing),
                                        text = "SCHEDULER",
                                        fontSize = FontSizeCustomValues.large
                                    )
                                    Text(
                                        text = "Description"// TODO:
                                    )
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
                        }
                    )
                    // TODO: add self card
                }
            )
        }
    )
}