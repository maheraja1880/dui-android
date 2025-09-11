package com.sample.dynamicui.ui.framework

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

@HiltViewModel
class DynamicViewModel @Inject constructor(
    private val getLayout: GetLayout
) : ViewModel() {

    private val _dynamicUILayout = MutableStateFlow<DynamicUiState>(DynamicUiState.Loading)
    val dynamicUILayout: StateFlow<DynamicUiState> = _dynamicUILayout
    private val _effect = Channel<DynamicUiEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()
    private val backStack: Stack<String> = Stack()

    //private var componentGlobalState = mutableMapOf<String, AnySerializable>()

    private val _componentGlobalState = MutableStateFlow<MutableMap<String, AnySerializable>>(mutableMapOf())
    val componentGlobalState: StateFlow<MutableMap<String, AnySerializable>> = _componentGlobalState

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
        _dynamicUILayout.value = DynamicUiState.Loading
        viewModelScope.launch {
            try {
                val component = getLayout(layoutId)
                restoreComponentState(layoutId, component)
                //componentGlobalState = stateManager.extractState(layoutId, component)
                if (push) backStack.push(layoutId)
                _dynamicUILayout.value = DynamicUiState.Success(component, canGoBack = backStack.size > 1)
            } catch (e: Exception) {
                _dynamicUILayout.value = DynamicUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private fun restoreComponentState(layoutId: String, component: Component) {
        if (componentGlobalState.value.isEmpty() && !componentGlobalState.value.keys.any { it.startsWith("$layoutId.") }) {
            componentGlobalState.value.putAll(stateManager.extractState(layoutId, component))
        }
    }

    fun getComponentState(layoutId: String, valuePath: String): AnySerializable {
        if (valuePath.startsWith("@@")) {
            val path = valuePath.substring(2)
            return componentGlobalState.value["$layoutId.$path"] ?: AnySerializable("NO STATE FOR PATH $layoutId.$valuePath")
        } else {
            return AnySerializable(valuePath)
        }

    }
    private fun updateComponentState(layoutId: String, path: String, value: Any?) {
        componentGlobalState.value["$layoutId.$path"] = AnySerializable(value)
    }

    fun getComponentPropertyPath(path: String): String{
         return if (path.startsWith("@@")) path.substring(2) else path
    }

    fun getRootComponent(): Component {
        if (_dynamicUILayout.value is DynamicUiState.Success) {
            return (_dynamicUILayout.value as DynamicUiState.Success).component
        } else {
            throw IllegalStateException("Root component not available")
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


    // TODO the below triggers complete screen recomposition. Modify to trigger only recomposing the relevant components
    private fun triggerRecomposition(value: Any?) {
        viewModelScope.launch {
            try {
                val currentState = _dynamicUILayout.value
                if (currentState is DynamicUiState.Success) {
                    val newComponent = currentState.component.deepCopy()
                    // Below is the dummy property added to trigger a recomposition
                    newComponent.properties.put("a", AnySerializable(value))
                    _dynamicUILayout.value = DynamicUiState.Success(newComponent, currentState.canGoBack)
                }
            } catch (e: Exception) {
                _dynamicUILayout.value = DynamicUiState.Error(e.message ?: "Unknown error")
            }
        }
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
        actions.forEach { executeAction(layoutId, componentId,it) }
    }
    private fun executeAction(layoutId: String, componentId: String, action: Action) {
        viewModelScope.launch {
            val component = getRootComponent().getComponentById(componentId)
            when (action.type) {
                // Add more actions as needed.
                // Each action should be written as function in separate file under package com/sample/dynamicui/ui/actions
                "navigate" -> executeNavigateAction(_effect, action= action)
                "refresh" -> loadLayout(layoutId , push = false)
                "setState" -> {
                    val path = getComponentPropertyPath(action.properties["toPath"]?.asString()?: "NO PATH SPECIFIED")
                    val value = getComponentState(layoutId, action.properties["fromPath"]?.asString()?: "NO PATH SPECIFIED")
                    // TODO handles only string conversion, need to handle other data types.
                    updateComponentState(layoutId, path, value.asString())
                    triggerRecomposition(value)

                }
                else -> _effect.send(DynamicUiEffect.ShowMessage("Unhandled action: ${action.type}"))
            }
        }
    }
}