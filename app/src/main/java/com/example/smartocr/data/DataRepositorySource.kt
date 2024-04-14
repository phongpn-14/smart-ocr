package com.example.smartocr.data

import kotlinx.coroutines.flow.Flow

interface DataRepositorySource {
    suspend fun helloWorld(): Flow<Resource<String>>
}
