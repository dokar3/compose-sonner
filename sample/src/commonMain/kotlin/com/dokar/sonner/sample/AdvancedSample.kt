package com.dokar.sonner.sample

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.dokar.sonner.TextToastAction
import com.dokar.sonner.ToastType
import com.dokar.sonner.Toaster
import com.dokar.sonner.ToasterDefaults
import com.dokar.sonner.rememberToasterState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration

// Just a random number
private const val LOADING_TOAST_ID = 0

// Just a mark
private const val LOADING_TOAST_ICON = 1

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdvancedSample(modifier: Modifier = Modifier) {
    var index by remember { mutableLongStateOf(0) }

    val toaster = rememberToasterState()

    val coolToaster = rememberToasterState()

    val coroutineScope = rememberCoroutineScope()

    var toastGenerationJob: Job? by remember { mutableStateOf(null) }

    var isLoading by remember { mutableStateOf(false) }

    fun startLoading() {
        isLoading = true
        toastGenerationJob?.cancel()
        toastGenerationJob = null
        toaster.dismissAll()
        coolToaster.dismissAll()
        toaster.show(
            message = "Now loading...",
            duration = Duration.INFINITE,
            id = LOADING_TOAST_ID,
            icon = LOADING_TOAST_ICON,
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(state = rememberScrollState()),
    ) {
        Text("Loading (Replace)")

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { startLoading() },
            enabled = !isLoading,
        ) {
            Text("Start loading")
        }

        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    isLoading = false
                    toaster.show(
                        message = "Success",
                        id = LOADING_TOAST_ID,
                        type = ToastType.Success,
                    )
                },
                enabled = isLoading,
            ) {
                Text("To success (Replace)")
            }

            Button(
                onClick = {
                    isLoading = false
                    toaster.show(
                        message = "Failed",
                        id = LOADING_TOAST_ID,
                        type = ToastType.Error,
                        action = TextToastAction(
                            text = "Reload",
                            onClick = { startLoading() },
                        ),
                    )
                },
                enabled = isLoading,
            ) {
                Text("To error (Replace)")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    isLoading = false
                    toaster.dismiss(LOADING_TOAST_ID)
                    toaster.show(
                        message = "Success",
                        type = ToastType.Success,
                    )
                },
                enabled = isLoading,
            ) {
                Text("To success (Append)")
            }

            Button(
                onClick = {
                    isLoading = false
                    toaster.dismiss(LOADING_TOAST_ID)
                    toaster.show(
                        message = "Failed",
                        type = ToastType.Error,
                        action = TextToastAction(
                            text = "Reload",
                            onClick = { startLoading() },
                        ),
                    )
                },
                enabled = isLoading,
            ) {
                Text("To error (Append)")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Toast generator")

        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    isLoading = false
                    toaster.dismissAll()
                    coolToaster.dismissAll()
                    toastGenerationJob?.cancel()
                    toastGenerationJob = coroutineScope.launch {
                        while (true) {
                            toaster.show(
                                message = "Hello #${index++}",
                                type = ToastType.entries.random(),
                            )
                            delay(255)
                        }
                    }
                },
                enabled = toastGenerationJob == null,
            ) {
                Text("Start")
            }

            Button(
                onClick = {
                    toastGenerationJob?.cancel()
                    toastGenerationJob = null
                },
                enabled = toastGenerationJob != null,
            ) {
                Text("Stop")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Styling")

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                toastGenerationJob?.cancel()
                toastGenerationJob = null
                toaster.dismissAll()
                coolToaster.show("You got me")
            },
        ) {
            Text("Cool toast")
        }
    }

    Toaster(
        state = toaster,
        richColors = true,
        swipeable = !isLoading,
        iconSlot = { toast ->
            if (toast.icon == LOADING_TOAST_ICON) {
                LoadingIcon()
            } else {
                // Fallback to the default icon slot
                ToasterDefaults.iconSlot(toast)
            }
        },
    )

    Toaster(
        state = coolToaster,
        shape = { RectangleShape },
        background = { Brush.linearGradient(0f to Color(0xff6274e7), 1f to Color(0xff8752a3)) },
        border = { BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)) },
        contentColor = { Color.White },
        contentPadding = { PaddingValues(start = 16.dp) },
        actionSlot = {
            Box(
                modifier = Modifier
                    .widthIn(min = 64.dp)
                    .requiredHeight(IntrinsicSize.Max)
                    .clickable { coolToaster.dismiss(it) }
                    .padding(16.dp),
            ) {
                Text("Dismiss", color = Color.White)
            }
        }
    )
}

@Composable
private fun LoadingIcon(modifier: Modifier = Modifier) {
    Box(modifier = modifier.padding(end = 16.dp)) {
        CircularProgressIndicator(
            modifier = Modifier.size(18.dp),
            color = Color.Black,
            strokeWidth = 1.5.dp,
        )
    }
}