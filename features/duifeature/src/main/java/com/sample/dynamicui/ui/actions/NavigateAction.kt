package com.sample.dynamicui.ui.actions

import com.sample.dynamicui.domain.model.Action
import com.sample.dynamicui.domain.model.Component
import com.sample.dynamicui.ui.framework.DynamicUiEffect
import kotlinx.coroutines.channels.Channel


suspend inline fun executeNavigateAction(uiEffect: Channel<DynamicUiEffect>, action: Action) {
    val target = action.properties["target"]?.asString() ?: throw IllegalArgumentException("No target specified")
    uiEffect.send(DynamicUiEffect.Navigate(target.toString()))
}