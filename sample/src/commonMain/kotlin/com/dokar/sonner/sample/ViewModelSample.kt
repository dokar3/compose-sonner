@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package com.dokar.sonner.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.dokar.sonner.TextToastAction
import com.dokar.sonner.Toast
import com.dokar.sonner.ToastType
import com.dokar.sonner.Toaster
import com.dokar.sonner.currentNanoTime
import com.dokar.sonner.listenMany
import com.dokar.sonner.rememberToasterState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ViewModelSample(
    modifier: Modifier = Modifier,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    viewModel: ViewModel = remember(coroutineScope) { ViewModel(coroutineScope) },
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(state = rememberScrollState()),
    ) {
        Text("UiState")

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = uiState.toString(),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 100.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))
                .padding(8.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Actions")

        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { viewModel.loadToSuccess() }, enabled = !uiState.isLoading) {
                Text("Load to success")
            }

            Button(onClick = { viewModel.loadToFailure() }, enabled = !uiState.isLoading) {
                Text("Load to failure")
            }
        }
    }

    UiMessageToaster(
        messages = uiState.uiMessages,
        onRemoveMessage = viewModel::removeUiMessageById,
    )
}

@Composable
private fun UiMessageToaster(
    messages: List<UiMessage>,
    onRemoveMessage: (id: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val toaster = rememberToasterState(
        onToastDismissed = { onRemoveMessage(it.id as Long) },
    )

    val currentMessages by rememberUpdatedState(messages)

    LaunchedEffect(toaster) {
        // Listen to State<List<UiMessage>> changes and map to toasts
        toaster.listenMany {
            currentMessages.map { message ->
                message.toToast(onDismiss = { onRemoveMessage(message.id) })
            }
        }
    }

    Toaster(
        state = toaster,
        modifier = modifier,
        richColors = true,
        showCloseButton = true,
    )
}

private fun UiMessage.toToast(onDismiss: (toast: Toast) -> Unit): Toast = when (this) {
    is UiMessage.Error -> Toast(
        id = id,
        message = message,
        type = ToastType.Error,
        duration = Duration.INFINITE,
        action = TextToastAction(text = "Dismiss", onClick = onDismiss),
    )

    is UiMessage.Success -> Toast(
        id = id,
        message = message,
        type = ToastType.Success,
        action = TextToastAction(text = "Dismiss", onClick = onDismiss),
    )
}

internal sealed interface UiMessage {
    val id: Long

    data class Success(
        val message: String,
        override val id: Long = currentNanoTime(),
    ) : UiMessage

    data class Error(
        val message: String,
        override val id: Long = currentNanoTime(),
        val error: Throwable? = null,
    ) : UiMessage
}

internal data class UiState(
    val isLoading: Boolean = false,
    val uiMessages: List<UiMessage> = emptyList(),
)

internal class ViewModel(
    private val viewModelScope: CoroutineScope,
) {
    private var id = 0
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    fun loadToSuccess() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        delay(1000)
        _uiState.update {
            val messages = it.uiMessages.toMutableList()
            messages.add(UiMessage.Success("Loaded ${id++}"))

            it.copy(isLoading = false, uiMessages = messages)
        }
    }

    fun loadToFailure() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        delay(1000)
        _uiState.update {
            val messages = it.uiMessages.toMutableList()
            messages.add(UiMessage.Error("Failed"))
            it.copy(isLoading = false, uiMessages = messages)
        }
    }

    fun removeUiMessageById(id: Long) {
        val index = uiState.value.uiMessages.indexOfFirst { it.id == id }
        if (index != -1) {
            _uiState.update { state ->
                val list = state.uiMessages.toMutableList()
                list.removeAt(index)
                state.copy(uiMessages = list)
            }
        }
    }
}