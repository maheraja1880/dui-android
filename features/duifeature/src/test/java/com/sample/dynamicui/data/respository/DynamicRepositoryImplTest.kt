package com.sample.dynamicui.data.respository

import android.util.Log
import com.sample.dynamicui.data.repository.LayoutRepositoryImpl
import com.sample.dynamicui.domain.model.Action
import com.sample.dynamicui.domain.model.AnySerializable
import com.sample.dynamicui.domain.model.Interaction
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DynamicRepositoryImplTest {

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } answers { println("Log.d: tag=${it.invocation.args[0]}, msg=${it.invocation.args[1]}"); 0 }
    }

    private val sampleJson = """
        {
          "id": "root",
          "type": "container",
          "properties": { "background": "white" },
          "children": [
            { "id": "title", "type": "text", "properties": { "text": "Hello World" } },
            { "id": "button1", "type": "button", "properties": { "text": "Click Me" } }
          ]
        }
    """.trimIndent()

    @Test
    fun `test fetchLayout deserializes correctly`() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(
                content = sampleJson,
                headers = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
            )
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        val repo = LayoutRepositoryImpl("http://test", client)

        val component = repo.fetchLayout("home")

        assertEquals("root", component.id)
        assertEquals("container", component.type)
        assertTrue(component.properties.containsKey("background"))
        assertEquals(2, component.children.size)
        assertEquals("title", component.children[0].id)
        assertEquals("text", component.children[0].type)
        assertEquals(AnySerializable("Hello World"), component.children[0].properties["text"])
    }

    @Test
    fun `test fetchLayout parses moabutton with onInteraction correctly`() = runTest {
        val sampleJson = """
            {
          "id": "root",
          "type": "container",
          "properties": { "background": "white" },
          "children": [
            { "id": "title", "type": "text", "properties": { "text": "Hello World" } },
            { "id": "button1", "type": "button", "properties": { "text": "Click Me" } }
          ],
          "onInteraction": [
            {
              "event": "onClick",
              "action": [
                {
                  "type": "navigate",
                  "properties": { "target": "nextScreen" }
                }
              ]
            }
          ]
        }
        """.trimIndent()

        val mockEngine = MockEngine { _ ->
            respond(
                content = sampleJson,
                headers = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
            )
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        val repo = LayoutRepositoryImpl("http://test", client)

        val component = repo.fetchLayout("profile")

        // Root validation
        assertEquals("root", component.id)
        assertEquals("container", component.type)
        assertTrue(component.properties.containsKey("background"))

        // Children validation
        assertEquals(2, component.children.size)
        assertEquals("title", component.children[0].id)
        assertEquals("text", component.children[0].type)
        assertEquals(AnySerializable("Hello World"), component.children[0].properties["text"])

        // Interaction validation
        assertEquals(1, component.onInteraction.size)
        val interaction: Interaction = component.onInteraction[0]
        assertEquals("onClick", interaction.event)
        assertEquals(1, interaction.action.size)

        // Action validation
        val action: Action = interaction.action[0]
        assertEquals("navigate", action.type)
        assertEquals(AnySerializable("nextScreen"), action.properties["target"])
    }


}