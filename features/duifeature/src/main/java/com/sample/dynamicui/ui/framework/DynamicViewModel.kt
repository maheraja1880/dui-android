package com.sample.dynamicui.ui.framework

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.dynamicui.domain.model.Action
import com.sample.dynamicui.domain.model.AnySerializable
import com.sample.dynamicui.domain.model.Component
import com.sample.dynamicui.domain.repository.DynamicRepository
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
            is DynamicUiIntent.Interaction -> handleInteraction(intent.componentId, intent.event)
            is DynamicUiIntent.DeepLink -> deepLink(intent.layoutId)
            DynamicUiIntent.Back -> navigateBack()
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

    private fun handleInteraction(componentId: String, event: String) {
        val current = _state.value
        if (current is DynamicUiState.Success) {
            val actions = findActions(current.component, componentId, event)
            actions.forEach { executeAction(it) }
        }
    }

    private fun findActions(component: Component, componentId: String, event: String): List<Action> {
        if (component.id == componentId) {
            val interaction = component.onInteraction.find { it.event == event }
            if (interaction != null) return interaction.action
        }
        return component.children.flatMap { findActions(it, componentId, event) }
    }

    private fun executeAction(action: Action) {
        viewModelScope.launch {
            when (action.type) {
                "navigate" -> {
                    val target = action.properties["target"]?.asString() ?: return@launch
                    // Instead of NavController directly, we trigger effect
                    _effect.send(DynamicUiEffect.Navigate(target.toString()))
                }
                else -> _effect.send(DynamicUiEffect.ShowMessage("Unhandled action: ${action.type}"))
            }
        }
    }
}