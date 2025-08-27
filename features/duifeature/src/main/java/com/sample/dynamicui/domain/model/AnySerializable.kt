package com.sample.dynamicui.domain.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.double
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.int
import kotlinx.serialization.json.intOrNull


/**
 * A wrapper class that allows primitive values to be serialized and deserialized dynamically.
 *
 * Used in the dynamic UI framework to represent property values of unknown type at compile time.
 * This wrapper supports basic primitive types: `String`, `Int`, `Double`, `Boolean`, and `null`.
 *
 * Serialization and deserialization are handled via a custom serializer to correctly map JSON primitives
 * into Kotlin `Any` and back.
 *
 * Example use cases include values in component `properties` and interaction `properties` maps.
 *
 * @property value The raw primitive value as `Any?`. Supports only string, number, boolean, or null.
 */
@Serializable(with = AnySerializable.Companion.Serializer::class)
data class AnySerializable(val value: Any?) {

    /** Returns the value as a string, or null if the type does not match. */
    fun asString() = value as? String

    /** Returns the value as an integer, or null if the type does not match. */
    fun asInt() = value as? Int

    /** Returns the value as a double, or null if the type does not match. */
    fun asDouble() = value as? Double

    /** Returns the value as a boolean, or null if the type does not match. */
    fun asBoolean() = value as? Boolean

    companion object {
        /**
         * Custom serializer to convert JSON primitives to [AnySerializable] and vice versa.
         */
        object Serializer : KSerializer<AnySerializable> {

            override val descriptor: SerialDescriptor =
                buildClassSerialDescriptor("AnySerializable")

            override fun deserialize(decoder: Decoder): AnySerializable {
                val input = decoder as? JsonDecoder
                    ?: error("AnySerializable supports only JSON decoding")

                val value: Any? = when (val element = input.decodeJsonElement()) {
                    is JsonPrimitive -> when {
                        element.isString -> element.content
                        element.booleanOrNull != null -> element.boolean
                        element.intOrNull != null -> element.int
                        element.doubleOrNull != null -> element.double
                        else -> element.content // fallback
                    }

                    else -> null
                }

                return AnySerializable(value)
            }

            override fun serialize(encoder: Encoder, value: AnySerializable) {
                val output = encoder as? JsonEncoder
                    ?: error("AnySerializable supports only JSON encoding")

                val jsonElement = when (val v = value.value) {
                    null -> JsonNull
                    is String -> JsonPrimitive(v)
                    is Int -> JsonPrimitive(v)
                    is Double -> JsonPrimitive(v)
                    is Boolean -> JsonPrimitive(v)
                    else -> error("Unsupported type for serialization: ${v::class}")
                }

                output.encodeJsonElement(jsonElement)
            }
        }
    }
}