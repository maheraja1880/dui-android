package com.sample.dynamicui.ui.framework

import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.sample.dynamicui.domain.model.AnySerializable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DynamicUIScreen2(
    modifier: Modifier = Modifier,
    layoutId: String,
    vm: DynamicViewModel = hiltViewModel()
) {
    val state = vm.componentGlobalState.collectAsState().value
    Log.d("DynamicUIScreen2", "DynamicUIScreen2: $layoutId , State: $state")
    RenderComponent(state = state, layoutId = layoutId)
}

@Composable
fun RenderComponent(state: MutableMap<String, AnySerializable>, layoutId: String) {
    Log.d("DynamicUIScreen2", "RenderComponent: layoutId: $layoutId, State: $state")
}