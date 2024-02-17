package com.dokar.sonner.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dokar.sonner.TextToastAction
import com.dokar.sonner.ToastDismissPause
import com.dokar.sonner.ToastType
import com.dokar.sonner.Toaster
import com.dokar.sonner.rememberToasterState
import kotlin.random.Random

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun BasicSample(modifier: Modifier = Modifier) {
    val toaster = rememberToasterState()

    var index by remember { mutableIntStateOf(0) }

    var darkTheme by remember { mutableStateOf(false) }

    var richColors by remember { mutableStateOf(false) }

    var closeButton by remember { mutableStateOf(false) }

    var expanded by remember { mutableStateOf(false) }

    var dismissPause by remember { mutableStateOf(ToastDismissPause.Never) }

    var alignment by remember { mutableStateOf(Alignment.BottomCenter) }

    var maxVisibleToasts by remember { mutableIntStateOf(3) }

    var showContainerBounds by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(state = rememberScrollState()),
        ) {
            Text("Types")

            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { toaster.show("Hello #${index++}") }) {
                    Text("Normal")
                }

                Button(onClick = {
                    toaster.show("Hello #${index++}", type = ToastType.Success)
                }) {
                    Text("Success")
                }

                Button(onClick = {
                    toaster.show("Hello #${index++}", type = ToastType.Info)
                }) {
                    Text("Info")
                }

                Button(onClick = {
                    toaster.show("Hello #${index++}", type = ToastType.Warning)
                }) {
                    Text("Warning")
                }

                Button(onClick = {
                    toaster.show("Hello #${index++}", type = ToastType.Error)
                }) {
                    Text("Error")
                }

                Button(onClick = {
                    toaster.show(
                        id = Random.nextLong(),
                        message = "Hello #${index++}",
                        action = TextToastAction(
                            text = "Dismiss",
                            onClick = { toaster.dismiss(it) }
                        ),
                    )
                }) {
                    Text("Action")
                }

                Button(onClick = { toaster.show("Title\nHello #${index++}") }) {
                    Text("Multiline")
                }
            }

            Spacer(Modifier.height(16.dp))

            Text("Options")

            ButtonDefaults

            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { darkTheme = !darkTheme },
                    )
                ) {
                    Switch(
                        checked = darkTheme,
                        onCheckedChange = { darkTheme = it },
                    )
                    Text("Dark theme")
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { richColors = !richColors },
                    )
                ) {
                    Switch(
                        checked = richColors,
                        onCheckedChange = { richColors = it },
                    )
                    Text("Rich colors")
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { closeButton = !closeButton },
                    )
                ) {
                    Switch(
                        checked = closeButton,
                        onCheckedChange = { closeButton = it },
                    )
                    Text("Close button")
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { expanded = !expanded },
                    )
                ) {
                    Switch(
                        checked = expanded,
                        onCheckedChange = { expanded = it },
                    )
                    Text("Expanded")
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { showContainerBounds = !showContainerBounds },
                    )
                ) {
                    Switch(
                        checked = showContainerBounds,
                        onCheckedChange = { showContainerBounds = it },
                    )
                    Text("Show container bounds")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Dismiss (timer) pause")

            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { dismissPause = ToastDismissPause.Never }
                    ),
                ) {
                    RadioButton(
                        selected = dismissPause == ToastDismissPause.Never,
                        onClick = { dismissPause = ToastDismissPause.Never }
                    )
                    Text("Never")
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { dismissPause = ToastDismissPause.OnNotFront }
                    ),
                ) {
                    RadioButton(
                        selected = dismissPause == ToastDismissPause.OnNotFront,
                        onClick = { dismissPause = ToastDismissPause.OnNotFront }
                    )
                    Text("OnNotFront")
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { dismissPause = ToastDismissPause.OnInvisible }
                    ),
                ) {
                    RadioButton(
                        selected = dismissPause == ToastDismissPause.OnInvisible,
                        onClick = { dismissPause = ToastDismissPause.OnInvisible }
                    )
                    Text("OnInvisible")
                }
            }

            Spacer(Modifier.height(16.dp))

            Text("Position")

            Spacer(Modifier.height(8.dp))

            val alignItemSize = 48.dp
            val containerWidth = alignItemSize * 3 + 16.dp
            val containerHeight = alignItemSize * 2 + 8.dp

            Box(modifier = Modifier.size(containerWidth, containerHeight)) {
                val alignments = remember {
                    listOf(
                        Alignment.TopStart,
                        Alignment.TopCenter,
                        Alignment.TopEnd,
                        Alignment.BottomStart,
                        Alignment.BottomCenter,
                        Alignment.BottomEnd,
                    )
                }
                for (align in alignments) {
                    Box(
                        modifier = Modifier
                            .size(alignItemSize)
                            .clip(MaterialTheme.shapes.medium)
                            .background(
                                if (alignment == align) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
                                }
                            )
                            .align(align)
                            .clickable { alignment = align }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Text("Max visible toasts")

            Spacer(Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = maxVisibleToasts.toString(), fontSize = 18.sp)

                TextButton(
                    onClick = { maxVisibleToasts-- },
                    enabled = maxVisibleToasts > 1,
                ) {
                    Text(text = "-", fontSize = 24.sp)
                }

                TextButton(
                    onClick = { maxVisibleToasts++ },
                    enabled = maxVisibleToasts < 5,
                ) {
                    Text(text = "+", fontSize = 20.sp)
                }
            }
        }
    }

    Toaster(
        state = toaster,
        modifier = if (showContainerBounds) Modifier.border(1.dp, Color.Red) else Modifier,
        maxVisibleToasts = maxVisibleToasts,
        expanded = expanded,
        richColors = richColors,
        darkTheme = darkTheme,
        showCloseButton = closeButton,
        alignment = alignment,
        dismissPause = dismissPause,
    )
}
