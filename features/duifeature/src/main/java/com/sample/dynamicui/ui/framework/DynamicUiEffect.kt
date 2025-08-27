package com.sample.dynamicui.ui.framework

sealed class DynamicUiEffect {
    data class Navigate(val target: String) : DynamicUiEffect()
    data class ShowMessage(val message: String) : DynamicUiEffect()
}