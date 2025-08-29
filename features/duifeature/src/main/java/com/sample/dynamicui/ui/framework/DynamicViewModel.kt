package com.sample.dynamicui.ui.framework

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.dynamicui.domain.model.Action
import com.sample.dynamicui.domain.model.Interaction
import com.sample.dynamicui.domain.repository.DynamicRepository
import com.sample.dynamicui.ui.actions.executeNavigateAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.Stack
import javax.inject.Inject

@HiltViewModel
class DynamicViewModel @Inject constructor(
    private val repository: DynamicRepository
) : ViewModel() {

    private val _state = MutableStateFlow<DynamicUiState>(DynamicUiState.Loading)
    val state: StateFlow<DynamicUiState> = _state

    private val _effect = Channel<DynamicUiEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private val backStack: Stack<String> = Stack()

    fun handleIntent(intent: DynamicUiIntent) {
        when (intent) {
            is DynamicUiIntent.LoadLayout -> loadLayout(intent.layoutId, push = true)
            is DynamicUiIntent.Interaction -> handleInteraction(layoutId= intent.layoutId, intent.componentId, intent.event, intent.interactions)
            is DynamicUiIntent.DeepLink -> deepLink(intent.layoutId)
            is DynamicUiIntent.Back -> navigateBack()
        }
    }

    private fun loadLayout(layoutId: String, push: Boolean) {
        _state.value = DynamicUiState.Loading
        viewModelScope.launch {
            try {
                val component = repository.fetchLayout(layoutId)
                if (push) backStack.push(layoutId)
                _state.value = DynamicUiState.Success(component, canGoBack = backStack.size > 1)
            } catch (e: Exception) {
                _state.value = DynamicUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private fun navigateBack() {
        if (backStack.size > 1) {
            backStack.pop() // remove current
            val prev = backStack.peek()
            loadLayout(prev, push = false)
        }
    }

    private fun deepLink(layoutId: String) {
        // Clear stack and start fresh from deep link
        backStack.clear()
        loadLayout(layoutId, push = true)
    }

    private fun handleInteraction(
        layoutId : String,
        componentId: String,
        event: String,
        interactions: List<Interaction>,

    ) {
        val matching = interactions.find { it.event == event }
        val actions = matching?.action ?: emptyList()
        actions.forEach { executeAction(layoutId, it) }
    }
    private fun executeAction(layoutId: String, action: Action) {
        viewModelScope.launch {
            when (action.type) {
                // Add more actions as needed.
                // Each action should be written as function in separate file under package com/sample/dynamicui/ui/actions
                "navigate" -> executeNavigateAction(_effect, action= action)
                "refresh" -> loadLayout(layoutId , push = false)
                else -> _effect.send(DynamicUiEffect.ShowMessage("Unhandled action: ${action.type}"))
            }
        }
    }
}