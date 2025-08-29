package com.sample.dynamicui.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class Component(
    val id: String,
    val type: String,
    val properties: MutableMap<String, AnySerializable> = mutableMapOf(),
    val children: List<Component> = emptyList(),
    val onInteraction: List<Interaction> = emptyList()
) {
    operator fun get(key: String): AnySerializable? = properties[key]
    operator fun set(key: String, value: AnySerializable) {
        properties[key] = value
    }
}

@Serializable
data class Interaction(
    val event: String,
    val action: List<Action>
)

@Serializable
data class Action(
    val type: String,
    val properties: Map<String, AnySerializable> = emptyMap()
)