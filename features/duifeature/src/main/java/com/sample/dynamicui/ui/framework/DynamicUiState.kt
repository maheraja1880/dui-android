package com.sample.dynamicui.ui.framework

import com.sample.dynamicui.domain.model.Component

sealed class DynamicUiState {
    object Loading : DynamicUiState()
    data class Success(val component: Component) : DynamicUiState()
    data class Error(val message: String) : DynamicUiState()
}