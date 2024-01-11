package com.dokar.sonner.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private enum class SampleType {
    Basic,
    Advanced,
    ViewModel,
}

@Composable
fun SampleScreen(modifier: Modifier = Modifier) {
    val linkOpener = rememberLinkOpener()

    var sampleType by remember { mutableStateOf(SampleType.Basic) }

    MaterialTheme {
        Column(modifier = modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = {}, enabled = false) {}

                Text(text = "Compose Sonner", fontSize = 20.sp, fontWeight = FontWeight.Bold)

                IconButton(onClick = {
                    linkOpener.open("https://github.com/dokar3/compose-sonner")
                }) {
                    Icon(imageVector = GithubIcon, contentDescription = null)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                SampleType.entries.forEach {
                    val isCurrent = it == sampleType
                    Text(
                        text = it.name,
                        modifier = Modifier
                            .widthIn(min = 72.dp)
                            .clip(CircleShape)
                            .background(
                                color = if (isCurrent) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
                                },
                            )
                            .clickable { sampleType = it }
                            .padding(8.dp, 4.dp),
                        textAlign = TextAlign.Center,
                        color = if (isCurrent) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onBackground
                        },
                        fontSize = 15.sp,
                    )
                }
            }

            Box {
                when (sampleType) {
                    SampleType.Basic -> BasicSample()
                    SampleType.Advanced -> AdvancedSample()
                    SampleType.ViewModel -> ViewModelSample()
                }
            }
        }
    }
}
