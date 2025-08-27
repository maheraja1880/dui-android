package com.sample.dynamicui.ui.framework

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sample.dynamicui.domain.model.AnySerializable
import com.sample.dynamicui.domain.model.Component

@Composable
fun DynamicUIScreen(
    modifier: Modifier = Modifier,
    layoutId: String,
    vm: DynamicViewModel = hiltViewModel()
) {
    val state = vm.state.collectAsState().value

    // Observe effects (navigate, messages)
    LaunchedEffect(Unit) {
        vm.effect.collect { effect ->
            when (effect) {
                is DynamicUiEffect.Navigate -> {
                    // Trigger loading of the next layout
                    vm.handleIntent(DynamicUiIntent.LoadLayout(effect.target))
                }
                is DynamicUiEffect.ShowMessage -> println(effect.message)
            }
        }
    }

    when (state) {
        is DynamicUiState.Loading -> Box(Modifier.fillMaxSize()) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
        is DynamicUiState.Error -> Box(Modifier.fillMaxSize()) {
            Text("Error: ${state.message}", Modifier.align(Alignment.Center))
        }
        is DynamicUiState.Success -> DynamicComponent(component = state.component, vm = vm)
    }

    // Trigger initial load if needed
    LaunchedEffect(layoutId) {
        vm.handleIntent(DynamicUiIntent.LoadLayout(layoutId))
    }
}

@Composable
fun DynamicComponent(component: Component, vm: DynamicViewModel) {
    when (component.type) {
        "text" -> Text(
            text = component.properties["text"] ?.asString()?: "EMPTY TEXT",
            modifier = Modifier.padding(8.dp)
        )
        "button" -> Button(
            onClick = {
                vm.handleIntent(DynamicUiIntent.Interaction(component.id, "onClick"))
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text(component.properties["text"]?.asString() ?: "NO BUTTON TEXT")
        }
        "container" -> Column(
            Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            component.children.forEach { child ->
                DynamicComponent(child, vm)
            }
        }
        else -> Text("Unsupported component: ${component.type}")
    }
}