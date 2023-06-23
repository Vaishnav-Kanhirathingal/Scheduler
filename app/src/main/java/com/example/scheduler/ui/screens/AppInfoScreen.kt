package com.example.scheduler.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
        },
        content = {
            AppInfoContent(paddingValues = it)
        }
    )
}

@Composable
fun AppInfoContent(paddingValues: PaddingValues) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(horizontal = PaddingCustomValues.infoScreenCardSpacing)
            .verticalScroll(ScrollState(0)),
        content = {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            )
            AppInfoCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(PaddingCustomValues.infoScreenCardSpacing),
                title = "SCHEDULER",
                description = "Scheduler is an app which a user can use to create tasks. Tasks " +
                        "scheduled for the day would show up as a permanent or ongoing notification. " +
                        "Once the user has completed the task, He/She can dismiss the task. If the task " +
                        "is pending, they can postpone the task to new date. This app ensures that the " +
                        "user does not have to open the app every time to track his tasks. Scheduler " +
                        "simplifies the task of keeping track of daily activities to be done.",
                bottomContent = {
                    val context = LocalContext.current
                    TextButton(
                        modifier = Modifier.align(Alignment.End),
                        onClick = {
                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://github.com/Vaishnav-Kanhirathingal/Scheduler")
                                )
                            )

                        },
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
            AppInfoCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(PaddingCustomValues.infoScreenCardSpacing)
                    .padding(bottom = PaddingCustomValues.screenGap),
                title = "DEVELOPER",
                description = "Hello, Vaishnav here, I am a Kotlin based android developer from Vasai, " +
                        "Maharashtra, India. I started my android journey near start november 2021. " +
                        "Since then I have been continuously making projects based on the Kotlin language. " +
                        "Scheduler is a new project I undertook to understand and practice Compose to " +
                        "improve my skills in using the compose architecture. You can find my accounts " +
                        "and github linked below. Click on the buttons to open respective apps.",
                bottomContent = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(ScrollState(0)),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(PaddingCustomValues.smallSpacing),
                        content = {
                            LinkIconButton(
                                painter = painterResource(id = R.drawable.github_icon_24),
                                link = "https://github.com/Vaishnav-Kanhirathingal"
                            )
                            LinkIconButton(
                                painter = painterResource(id = R.drawable.google_icon),
                                link = "mailto:vaishnav.kanhira@gmail.com"
                            )
                            LinkIconButton(
                                painter = painterResource(id = R.drawable.microsoft_outlook_icon),
                                link = "mailto:vaishnav.kanhira@outlook.com"
                            )
                            LinkIconButton(
                                painter = painterResource(id = R.drawable.whatsapp_icon_24),
                                link = "https://wa.me/917219648837"
                            )
                            LinkIconButton(
                                painter = painterResource(id = R.drawable.instagram_24),
                                link = "https://www.instagram.com/vaishnav_k.p/"
                            )
                            LinkIconButton(
                                painter = painterResource(id = R.drawable.stack_overflow_24),
                                link = "https://stackexchange.com/users/23358250/vaishnav-kanhirathingal?tab=accounts"
                            )
                            LinkIconButton(
                                painter = painterResource(id = R.drawable.linkedin_24),
                                link = "https://www.linkedin.com/in/vaishnav-kanhirathingal-2b8b6b224/"
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
    bottomContent: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = PaddingCustomValues.infoScreenCardSpacing),
        modifier = modifier,
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = PaddingCustomValues.mediumSpacing),
                        text = description,
                        fontFamily = FontFamily.Monospace
                    )
                    bottomContent()
                }
            )
        }
    )
}

@Composable
fun LinkIconButton(
    painter: Painter,
    link: String
) {
    val context = LocalContext.current
    IconButton(
        onClick = {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(link)
                )
            )
        },
        content = {
            Image(painter = painter, contentDescription = null)
        }
    )
}