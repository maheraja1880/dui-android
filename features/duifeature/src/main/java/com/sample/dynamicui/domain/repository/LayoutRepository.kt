package com.sample.dynamicui.domain.repository

import com.sample.dynamicui.domain.model.Component

interface LayoutRepository {
    suspend fun fetchLayout(layoutId: String): Component
}