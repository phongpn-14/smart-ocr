package com.example.smartocr.data

import com.example.smartocr.data.local.LocalData
import com.example.smartocr.data.remote.RemoteData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class DataRepository @Inject constructor(
    private val remoteRepository: RemoteData,
    private val localRepository: LocalData,
    private val ioDispatcher: CoroutineContext
) :
    DataRepositorySource, CoroutineScope {
    override suspend fun helloWorld(): Flow<Resource<String>> = remoteRepository.helloWorld().map {
        if (it.isSuccess) {
            Resource.Success(data = it.data!!.message)
        } else {
            it as Resource.Error
        }
    }

    override val coroutineContext: CoroutineContext
        get() = ioDispatcher


}
