package com.example.smartocr.data.remote

import com.example.smartocr.util.NetworkConnectivity
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


class RemoteData @Inject
constructor(
    private val serviceGenerator: ServiceGenerator,
    private val networkConnectivity: NetworkConnectivity,
    private val io: CoroutineContext
) : RemoteDataSource {

}
