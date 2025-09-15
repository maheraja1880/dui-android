package com.sample.dynamicui.domain.usecase

import com.sample.dynamicui.domain.model.Component
import com.sample.dynamicui.domain.repository.LayoutRepository
import javax.inject.Inject

class GetLayout @Inject constructor(
    private val repository: LayoutRepository
){
    suspend operator fun invoke(layoutId: String): Component {
      return repository.fetchLayout(layoutId)
    }
}