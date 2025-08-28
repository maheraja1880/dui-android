package com.sample.dynamicui.ui.framework

sealed class DynamicUiIntent {
    data class LoadLayout(val layoutId: String) : DynamicUiIntent()
    //object Refresh : DynamicUiIntent()
    data class Interaction(val componentId: String, val event: String) : DynamicUiIntent()
    object Back : DynamicUiIntent()
}