package com.sample.dynamicui.ui.framework

import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.sample.dynamicui.domain.model.AnySerializable
import com.sample.dynamicui.domain.model.Component
import kotlin.text.get

class StateManager {

    private val state: SnapshotStateMap<String, Any> = mutableStateMapOf()

//    fun recreateState(components: List<Component>) {
//        state.clear()
//        state.putAll(extractState(components))
//    }



    fun extractState(layoutId: String, component: Component): MutableMap<String, AnySerializable> {

        val result = mutableMapOf<String, AnySerializable>()
        val stateObj = component.properties["state"]
        if (stateObj is AnySerializable && stateObj.asMap()?.isNotEmpty()?: false) {
            flattenState(layoutId, stateObj, result)
        }
        println("Extracted State: $result")
        return result
    }

    fun flattenState(
        prefix: String,
        stateValue: AnySerializable,
        result: MutableMap<String, AnySerializable>
    ) {
        for ((key, value) in stateValue.asMap() ?: emptyMap<String, Any?>()) {
            val newKey = if (prefix.isEmpty()) "$key" else "$prefix.$key"
            if (value is AnySerializable && value.asMap()?.isNotEmpty() == true) {
                flattenState(newKey, value, result)
            } else {
                result[newKey] = (value ?: "") as AnySerializable
            }
        }
    }

//    fun updateState(component: Component, statePath: String, value: Any?) {
//        val stateObj = component.properties["state"]
//        if (stateObj is AnySerializable) {
//            fun update(stateValue: AnySerializable, path: List<String>) {
//                if (path.isEmpty()) {
//                    stateValue.set(value)
//                } else {
//                    val key = path[0]
//                    val nestedState = stateValue.asMap()?.get(key)
//                    if (nestedState is AnySerializable) {
//                        update(nestedState, path.subList(1, path.size))
//                    }
//                }
//                }
//                update(stateObj, statePath.split("."))
//            } else {
//                Log.e("StateManager", "Invalid state object: $stateObj")
//        }
//    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getComponentState(componentId: String, clazz: Class<T>): T? {
        return state[componentId] as? T
    }

    fun setComponentState(componentId: String, value: Any) {
        state[componentId] = value
    }

    fun getAllStates(): Map<String, Any> = state.toMap()
}