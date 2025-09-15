package com.sample.dynamicui.data.respository

import com.sample.dynamicui.domain.model.AnySerializable
import com.sample.dynamicui.domain.model.ComponentState
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class ComponentStateTest {
    @Test
    fun `should deserialize state from json and validate values`() {
        val jsonString = """
            {
                "state": {
                    "usage": {
                        "data" : "20GB",
                        "used" : "10GB",
                        "free" : "10GB",
                        "total" : "30GB"
                    },
                    "selection" : {
                        "name": "your name",
                        "prefix": {
                            "selected": "Mr.",
                            "options": ["Mr.", "Ms.", "Mrs."]
                        },
                        "topics": {
                            "selected": ["topic1", "topic2"],
                            "options": ["topic1", "topic2", "topic3"]
                        }
                    }
                } 
            }
        """
        val componentState = Json.decodeFromString(ComponentState.serializer(), jsonString)
        assertEquals("20GB", (componentState.state["usage"] as AnySerializable).asMap()?.get("data")?.asString() )
    }
}