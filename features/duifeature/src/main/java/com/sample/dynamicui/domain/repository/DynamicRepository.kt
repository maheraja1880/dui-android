package com.sample.dynamicui.domain.repository

import com.sample.dynamicui.domain.model.Component

interface DynamicRepository {
    suspend fun fetchLayout(layoutId: String): Component
}