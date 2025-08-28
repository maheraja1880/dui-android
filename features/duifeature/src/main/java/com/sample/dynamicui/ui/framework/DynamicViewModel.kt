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
import javax.inject.Inject

@HiltViewModel
class DynamicViewModel @Inject constructor(
    private val repository: DynamicRepository
) : ViewModel() {

    private val _state = MutableStateFlow<DynamicUiState>(DynamicUiState.Loading)
    val state: StateFlow<DynamicUiState> = _state

    private val _effect = Channel<DynamicUiEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun handleIntent(intent: DynamicUiIntent) {
        when (intent) {
            is DynamicUiIntent.LoadLayout -> loadLayout(intent.layoutId)
            is DynamicUiIntent.Interaction -> handleInteraction(intent.componentId, intent.event)
        }
    }

    private fun loadLayout(layoutId: String) {
        _state.value = DynamicUiState.Loading
        viewModelScope.launch {
            try {
                val component = repository.fetchLayout(layoutId)
                _state.value = DynamicUiState.Success(component)
            } catch (e: Exception) {
                _state.value = DynamicUiState.Error(e.message ?: "Unknown error")
            }
        }
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