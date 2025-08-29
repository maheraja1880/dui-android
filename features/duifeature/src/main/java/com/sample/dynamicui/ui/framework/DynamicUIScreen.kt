package com.sample.dynamicui.ui.framework

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sample.dynamicui.domain.model.Component

@OptIn(ExperimentalMaterial3Api::class)
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

    Scaffold(
        topBar = {
            if (state is DynamicUiState.Success && state.canGoBack) {
                TopAppBar(
                    title = { Text("Dynamic Screen") },
                    navigationIcon = {
                        IconButton(onClick = { vm.handleIntent(DynamicUiIntent.Back) }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (state) {
                is DynamicUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is DynamicUiState.Error -> Text("Error: ${state.message}", Modifier.align(Alignment.Center))
                is DynamicUiState.Success -> DynamicComponent(layoutId, component = state.component, vm = vm)
            }
        }
    }

    // Initial load (deep link aware)
    LaunchedEffect(layoutId) {
        vm.handleIntent(DynamicUiIntent.DeepLink(layoutId))
    }
}

@Composable
fun DynamicComponent(layoutId: String, component: Component, vm: DynamicViewModel) {
    Log.d("DynamicComponent", "Rendering component: ${component.id}")
    when (component.type) {
        "text" -> Text(
            text = component.properties["text"] ?.asString()?: "EMPTY TEXT",
            modifier = Modifier.padding(8.dp)
        )
        "button" -> Button(
            onClick = {
                vm.handleIntent(DynamicUiIntent.Interaction(layoutId, component.id, "onClick", component.onInteraction))
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
                DynamicComponent(layoutId,child, vm)
            }
        }
        else -> Text("Unsupported component: ${component.type}")
    }
}