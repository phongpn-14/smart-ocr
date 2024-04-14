package com.example.smartocr.data.remote

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.smartocr.data.Resource
import com.example.smartocr.data.dto.response.ResponseHelloWorld
import com.example.smartocr.data.remote.service.SmartOCRService
import com.example.smartocr.util.NetworkConnectivity
import com.example.smartocr.util.logd
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


@RequiresApi(Build.VERSION_CODES.O)
class RemoteData @Inject
constructor(
    private val serviceGenerator: ServiceGenerator,
    private val networkConnectivity: NetworkConnectivity,
    private val io: CoroutineContext
) : RemoteDataSource {
    private val smartOcr by lazy { serviceGenerator.createService(SmartOCRService::class.java) }
    override fun helloWorld(): Flow<Resource<ResponseHelloWorld>> {
        return flow {
            emit(processCall { smartOcr.helloWorld() })
        }.flowOn(io)
    }

    private suspend fun <T> processCall(responseCall: suspend () -> Response<T>): Resource<T> {
        if (!networkConnectivity.isConnected()) {
            return Resource.Error(message = "No internet")
        }
        return try {
            val response = responseCall.invoke()
            val responseCode = response.code()
            if (response.isSuccessful) {
                Resource.Success(data = response.body()!!)
            } else {
                response.body()?.logd()
                Resource.Error(errorCode = response.code(), message = response.message())
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Resource.Error(message = "Unknown error happened.")
        }
    }
}
