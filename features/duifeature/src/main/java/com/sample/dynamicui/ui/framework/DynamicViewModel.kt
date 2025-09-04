package com.sample.dynamicui.ui.framework

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.dynamicui.domain.model.Action
import com.sample.dynamicui.domain.model.AnySerializable
import com.sample.dynamicui.domain.model.Component
import com.sample.dynamicui.domain.model.Interaction
import com.sample.dynamicui.domain.usecase.GetLayout
import com.sample.dynamicui.ui.actions.executeNavigateAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.Stack
import javax.inject.Inject
import kotlin.random.Random
import kotlin.text.set

@HiltViewModel
class DynamicViewModel @Inject constructor(
    private val getLayout: GetLayout
) : ViewModel() {

    private val _state = MutableStateFlow<DynamicUiState>(DynamicUiState.Loading)
    val state: StateFlow<DynamicUiState> = _state
    private val _effect = Channel<DynamicUiEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()
    private val backStack: Stack<String> = Stack()
    private val componentState: MutableMap<String, Any?> = mutableMapOf()
    private var componentGlobalState = mutableMapOf<String, AnySerializable>()

    private val stateManager: StateManager = StateManager()

    fun handleIntent(intent: DynamicUiIntent) {
        when (intent) {
            is DynamicUiIntent.LoadLayout -> loadLayout(intent.layoutId, push = true)
            is DynamicUiIntent.Interaction -> handleInteraction(layoutId= intent.layoutId, intent.componentId, intent.event, intent.interactions)
            is DynamicUiIntent.DeepLink -> deepLink(intent.layoutId)
            is DynamicUiIntent.UpdateState -> updateComponentState(intent.layoutId, intent.componentId, intent.value)
            is DynamicUiIntent.Back -> navigateBack()
        }
    }

    private fun loadLayout(layoutId: String, push: Boolean) {
        _state.value = DynamicUiState.Loading
        viewModelScope.launch {
            try {
                val component = getLayout(layoutId)
                restoreComponentState(layoutId, component)
                //componentGlobalState = stateManager.extractState(layoutId, component)
                if (push) backStack.push(layoutId)
                _state.value = DynamicUiState.Success(component, canGoBack = backStack.size > 1)
            } catch (e: Exception) {
                _state.value = DynamicUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun getComponentState(layoutId: String, valuePath: String): AnySerializable {
        if (valuePath.startsWith("@@")) {
            val path = valuePath.substring(2)
            return componentGlobalState["$layoutId.$path"] ?: AnySerializable("")
        } else {
            return AnySerializable(valuePath)
        }

    }

    fun getComponentPropertyPath(path: String): String{
         return if (path.startsWith("@@")) path.substring(2) else path
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

    private fun updateComponentState(layoutId: String, path: String, value: Any?) {
        //componentState[layoutId +componentId] = value
        componentGlobalState["$layoutId.$path"] = AnySerializable(value)
        componentGlobalState["$layoutId.usage.data"] = AnySerializable(value)

        val currentState = _state.value
        if (currentState is DynamicUiState.Success) {
            // TODO the below triggers complete screen recomposition. Modify to trigger only recomposing the relevant components
            val newComponent = currentState.component.deepCopy()
            // Below is the dummy property added to trigger a recomposition
            newComponent.properties.put("a", AnySerializable(value))
            _state.value = DynamicUiState.Success(newComponent, currentState.canGoBack)
        }
    }

    private fun restoreComponentState(layoutId: String, component: Component) {
        if (componentGlobalState.isEmpty() && !componentGlobalState.keys.any { it.startsWith("$layoutId.") }) {
            componentGlobalState = stateManager.extractState(layoutId, component)
        }

        if (componentState.containsKey(layoutId + component.id)) {
            component.properties["value"] = AnySerializable(componentState[layoutId + component.id])
            Log.d("DynamicViewModel", "Restoring state for component: ${component.id} with value: ${component.properties["value"]}" )
        }
        component.children.forEach { restoreComponentState(layoutId, it) }
    }

    private fun createComponentState(layoutId: String, component: Component) {

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