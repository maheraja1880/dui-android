package com.sample.dynamicui.domain.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class ComponentState (
    val state: MutableMap<String, AnySerializable> = mutableMapOf()
) {
    fun toComponentState(json: String): ComponentState {
        return Json.decodeFromString(json)

    }
}