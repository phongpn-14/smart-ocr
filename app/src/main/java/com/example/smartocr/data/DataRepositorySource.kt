package com.example.smartocr.data

import com.example.smartocr.data.model.OcrCCCD
import kotlinx.coroutines.flow.Flow
import java.io.File

interface DataRepositorySource {
    suspend fun helloWorld(): Flow<Resource<String>>

    suspend fun processCCCD(image: File): Flow<Resource<OcrCCCD>>
}
