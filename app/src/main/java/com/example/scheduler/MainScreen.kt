package com.example.scheduler

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.scheduler.values.PaddingCustomValues

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(heightDp = 600)
fun MainScreen() {
    // TODO: use scroll state to set visibility for text
    val scrollState: ScrollState = rememberScrollState()
    val lazyListState = rememberLazyListState()
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /*TODO*/ },
                shape = CircleShape,
                content = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(PaddingCustomValues.externalSpacing)
                    ) {
                        AnimatedVisibility(
                            visible = lazyListState.firstVisibleItemIndex == 0,
                            modifier = Modifier.padding(start = PaddingCustomValues.externalSpacing),
                        ) {
                            Text(text = "Add Task")
                        }
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = null,
                            tint = Color.Blue
                        )
                    }
                }
            )
        },
        content = {
            it
            // TODO: use lazy list to display tasks after sorting and remove this sample column
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth(),
                state = lazyListState
            ) {
                items(
                    count = 15,
                    itemContent = {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(PaddingCustomValues.internalSpacing)
                        ) {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        horizontal = PaddingCustomValues.externalSpacing,
                                        vertical = 15.dp
                                    ),
                                text = "sample text $it",
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                )
            }
        }
    )
}