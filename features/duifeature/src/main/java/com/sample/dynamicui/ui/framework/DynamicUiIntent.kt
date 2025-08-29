package com.sample.dynamicui.ui.framework

sealed class DynamicUiIntent {
    data class LoadLayout(val layoutId: String) : DynamicUiIntent()
    //object Refresh : DynamicUiIntent()
    data class Interaction(val layoutId: String, val componentId: String, val event: String, val interactions: List<com.sample.dynamicui.domain.model.Interaction>) : DynamicUiIntent()
    object Back : DynamicUiIntent()
    data class DeepLink(val layoutId: String) : DynamicUiIntent()
}