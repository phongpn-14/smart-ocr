package com.example.smartocr.data

import com.example.smartocr.data.local.LocalData
import com.example.smartocr.data.remote.RemoteData
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class DataRepository @Inject constructor(
    private val remoteRepository: RemoteData,
    private val localRepository: LocalData,
    private val ioDispatcher: CoroutineContext
) :
    DataRepositorySource {

}
