package com.sample.dynamicui.ui.framework

import ShimmerCard
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sample.dynamicui.domain.model.AnySerializable
import com.sample.dynamicui.domain.model.Component

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DynamicUIScreen(
    modifier: Modifier = Modifier,
    layoutId: String,
    vm: DynamicViewModel = hiltViewModel()
) {
    val state = vm.state.collectAsState().value

    // Handle system back button
    BackHandler(enabled = state is DynamicUiState.Success && state.canGoBack) {
        vm.handleIntent(DynamicUiIntent.Back)
    }

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
    ) { padding ->
        Box(
            modifier
                .fillMaxSize()
                .padding(padding)) {
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
        "text" -> vm.getComponentState(layoutId, component.properties["text"] ?.asString()?: "EMPTY TEXT").asString()
            ?.let {
                Text(
                    //text = component.properties["text"] ?.asString()?: "EMPTY TEXT",
                    text = it,
                    modifier = Modifier.padding(8.dp)
                )
            }
        "dynamicText" -> {
            Text(
                text = component.properties["value"] ?.asString()?: "EMPTY VALUE",
                modifier = Modifier.padding(8.dp)
            )
        }
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
        "column" -> Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            component.children.forEach { child ->
                DynamicComponent(layoutId,child, vm)
            }
        }
        "card" -> Card(
            modifier = Modifier
                .padding(8.dp)
                .clip(RoundedCornerShape(8.dp))
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            border = BorderStroke(1.dp, Color.LightGray)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                component.children.forEach { child ->
                    DynamicComponent(layoutId, child, vm)
                }
            }
        }
        "shimmer" -> ShimmerCard()
        "textInput" -> {
            //Log.d("DynamicComponent", "Rendering textInput component: ${component.id} with value ${component.properties["value"]?.asString()}")
            //var value by remember { mutableStateOf(component.properties["value"]?.asString() ?: "") }
            val statePath = vm.getComponentPropertyPath(component.properties["value"] ?.asString()?: "EMPTY TEXT")
            var value by remember { mutableStateOf(vm.getComponentState(layoutId, component.properties["value"] ?.asString()?: "EMPTY TEXT").asString()) }

            TextField(
                value = value?: "EMPTY",
                onValueChange = {
                    value = it
                    vm.handleIntent(
                        //DynamicUiIntent.UpdateState(layoutId,component.id, it)
                        DynamicUiIntent.UpdateState(layoutId,statePath, it)
                    )
                },
                label = { Text(component.properties["label"]?.asString() ?: "") },
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            )
        }
        "singleSelect" -> {
            val options = component.properties["options"]?.asList()?.map { it.asString()?: "EMPTY OPTION" } ?: emptyList<String>()
            val statePath = vm.getComponentPropertyPath(component.properties["selected"] ?.asString()?: "EMPTY PATH OR VALUE")
            var selected by remember { mutableStateOf(vm.getComponentState(layoutId, component.properties["selected"] ?.asString()?: "EMPTY TEXT").asString()) }
            //var selected by remember { mutableStateOf(component.properties["value"]?.asString() ?: "") }
            var expanded by remember { mutableStateOf(false) }
           Box {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        selected?.ifEmpty { "Select an option" } ?: "EMPTY",
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Open dropdown")
                    }
                }
              DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            onClick = {
                                selected = option
                                expanded = false
                                vm.handleIntent(DynamicUiIntent.UpdateState(layoutId, statePath, option))
                            },
                            text = { Text(option) }
                        )
                    }
                }
            }
        }
        "multiSelect" -> {
            val options = component.properties["options"]?.asList()?.map { it.asString()?: "EMPTY OPTION" } ?: emptyList<String>()
            val statePath = vm.getComponentPropertyPath(component.properties["selected"] ?.asString()?: "EMPTY PATH OR VALUE")
            //var selected by remember { mutableStateOf(component.properties["value"]?.asList()?: emptyList<AnySerializable>()) }
            var selected by remember { mutableStateOf(vm.getComponentState(layoutId, component.properties["selected"] ?.asString()?: "EMPTY TEXT").asList()) }

            Column {
                options.forEach { it ->
                    val option = AnySerializable(it)
                    Row(Modifier.fillMaxWidth()) {
                        val isChecked = selected?.contains(option)
                        Checkbox(
                            checked = isChecked == true,
                            onCheckedChange = {
                                val newList = if (it) selected?.plus(option) ?: emptyList<AnySerializable>() else selected?.minus(
                                    option
                                ) ?: emptyList<AnySerializable>()
                                selected = newList
                                vm.handleIntent(DynamicUiIntent.UpdateState(layoutId, component.id, newList))
                            }
                        )
                        Text(option.asString()?:"EMPTY OPTION", Modifier.align(Alignment.CenterVertically))
                    }
                }
            }
        }
        else -> Text("Unsupported component: ${component.type}")
    }
}