package com.sample.dynamicui.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockEngine.Companion.invoke
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object ApiClient {
    val http = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    val mockEngine = MockEngine { req ->
        val sampleJson = getSampleJson(req.url.toString().substringAfterLast("/"))
        respond(
            content = sampleJson,
            headers = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
        )
    }
    val mockClient = HttpClient(mockEngine) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    private fun getSampleJson (layoutId: String): String {
        val layoutJsonMap = mapOf(
            "home" to home,
            "profile" to profile,
            "settings" to settings,
            "server-contents" to serverContents
        )
        val jsonString = layoutJsonMap[layoutId]
            ?: throw IllegalArgumentException("No mock layout found for id: $layoutId")

        return jsonString
    }
}

val serverContents = """
{
    "id": "server-container",
    "type": "container",
    "properties": 
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
                "prefix": "Mr.",
                "topics": ["topic1", "topic2"]
            }
        }        
    },
    "children": [
        {
            "id": "title",
            "type": "text",
            "properties": {
                "text": "@@usage.data"
            }
        },
        {
            "id": "title2",
            "type": "text",
            "properties": {
                "text": "@@usage.used"
            }
        },
        {
            "id": "title3",
            "type": "text",
            "properties": {
                "text": "@@usage.free"
            }
        },
        {
            "id": "title2",
            "type": "textInput",
            "properties": {
                "value": "@@selection.name",
                "label": "Label here"
            }
        },
        {
          "id": "dropdownTitle",
          "type": "singleSelect",
          "properties": {
            "options": ["Mr.", "Ms.", "Mrs."],
            "label": "Label here",
            "selected": "@@selection.prefix"
          }
        },
        {
          "id": "mulitselectTitle",
          "type": "multiSelect",
          "properties": {
            "options": ["topic1", "topic2", "topic3"],
            "label": "Label here",
            "selected": "@@selection.topics"
          }
        },
        
        
        {
            "id": "button1",
            "type": "button",
            "properties": {
                "text": "Click Me"
            },
            "onInteraction": [
            {
              "event": "onClick",
              "action": [
                {
                  "type": "navigate",
                  "properties": {
                    "target": "profile"
                  }
                }
              ]
            }
            ]
        }
    ]
}
""".trimIndent()

val settings = """
{
  "id": "settings-component",
  "type": "container",
  "properties": {
    "background": "white",
    "state" : [
        "settings.appSettings",
        "settings.profileSettings"
    ]
  },
  "children": [
    {
      "id": "title",
      "type": "text",
      "properties": {
        "text": "@@settings.appSettings"
      }
    },
    {
      "id": "title2",
      "type": "text",
      "properties": {
        "text": "@@settings.profileSettings"
      }
    }
  ]
}  
""".trimIndent()
val home = """ 
{
  "id": "home-component",
  "type": "container",
  "properties": {
    "background": "white"
  },
  "children": [
    {
      "id": "card-component",
      "type": "column",
      "children": [
        {
          "id": "card-content",
          "type": "text",
          "properties": {
            "text": "This is a card component"
          }
        },
        {
          "id": "dynamic-content",
          "type": "dynamicText",
          "properties": {
            "value": "This is a dynamic value"
          }
        }
      ]
    },
    {
      "id": "title",
      "type": "text",
      "properties": {
        "text": "Hello World 1"
      }
    },
    {
      "id": "title2",
      "type": "textInput",
      "properties": {
        "value": "Some value",
        "label": "Label here"
      }
    },
    {
      "id": "mulitselectTitle",
      "type": "multiSelect",
      "properties": {
        "options": ["Some value", "value2", "value 3"],
        "label": "Label here"
      }
    },
    {
      "id": "dropdownTitle",
      "type": "singleSelect",
      "properties": {
        "options": ["Some value", "value2", "value 3"],
        "label": "Label here"
      }
    },
    {
      "id": "button1",
      "type": "button",
      "properties": {
        "text": "Click Me"
      },
      "onInteraction": [
        {
          "event": "onClick",
          "action": [
            {
              "type": "navigate",
              "properties": {
                "target": "profile"
              }
            }
          ]
        }
      ]
    },
    {
      "id": "button2",
      "type": "button",
      "properties": {
        "text": "Refresh Me"
      },
      "onInteraction": [
        {
          "event": "onClick",
          "action": [
            {
              "type": "refresh"
            }
          ]
        }
      ]
    }
  ]
} 
""".trimIndent()


val profile = """
{
  "id": "profile-component",
  "type": "container",
  "properties": {
    "background": "white"
  },
  "children": [
    {
      "id": "title",
      "type": "text",
      "properties": {
        "text": "Hello World 2"
      }
    },
    {
      "id": "title2",
      "type": "textInput",
      "properties": {
        "value": "Some value",
        "label": "Label here"
      }
    },
    {
      "id": "button1",
      "type": "button",
      "properties": {
        "text": "Click Me"
      },
      "onInteraction": [
        {
          "event": "onClick",
          "action": [
            {
              "type": "navigate",
              "properties": {
                "target": "settings"
              }
            }
          ]
        }
      ]
    }
  ]
} 
""".trimIndent()