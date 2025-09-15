package com.sample.dynamicui.data.repository


import com.sample.dynamicui.domain.model.AnySerializable
import com.sample.dynamicui.domain.repository.DataRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DataRepositoryImpl : DataRepository {
    override suspend fun fetchDataForLayout(layoutId: String): Flow<String> = flow {
        // Replace with actual data fetching logic
        delay(2000)
        emit(usage)
        delay(5000)
        emit(selection)
    }

    val usage = """
   {
        "usage": {
            "data" : "20GB",
            "used" : "10GB",
            "free" : "10GB",
            "total" : "30GB"
        }
    }
""".trimIndent()

    val selection = """
   {
       "selection" : {
            "name": "your name",
            "prefix": {
                "selected": "Mr.",
                "options": ["Mr.", "Ms.", "Mrs."]
            },
            "topics": {
                "selected": ["topic1", "topic2"],
                "options": ["topic1", "topic2", "topic3"]
            }
       }
    } 
""".trimIndent()
}

