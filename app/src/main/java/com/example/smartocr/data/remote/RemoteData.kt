package com.example.smartocr.data.remote

import com.example.smartocr.data.Resource
import com.example.smartocr.data.dto.response.ResponseHelloWorld
import com.example.smartocr.data.dto.response.ResponseOcrCCCD
import com.example.smartocr.data.dto.response.ResponseTemplate
import com.example.smartocr.data.dto.response.ResponseTemplateMetadata
import com.example.smartocr.data.remote.service.SmartOCRService
import com.example.smartocr.util.NetworkConnectivity
import com.example.smartocr.util.logd
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import retrofit2.Response
import java.io.File
import java.io.IOException
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


class RemoteData @Inject
constructor(
    private val serviceGenerator: ServiceGenerator,
    private val networkConnectivity: NetworkConnectivity,
    private val io: CoroutineContext
) : RemoteDataSource {
    var smartOcr = serviceGenerator.createService(SmartOCRService::class.java)
    override fun helloWorld(): Flow<Resource<ResponseHelloWorld>> {
        return flow {
            emit(processCall { smartOcr.helloWorld() })
        }.flowOn(io)
    }

    override fun processOcrCCCD(image: File): Flow<Resource<ResponseOcrCCCD>> {
        val part = MultipartBody.Part.createFormData(
            "file",
            image.name,
            UploadRequestBody(image, onUpload = {

            })
        )
        return flow {
            emit(processCall { smartOcr.processOcrCCCD(part) })
        }.flowOn(io)
    }

    override fun processWithoutTemplate(file: File): Flow<Resource<ResponseTemplateMetadata>> {
        val part = MultipartBody.Part.createFormData(
            "file",
            file.name,
            UploadRequestBody(file, onUpload = {

            })
        )
        return flow {
            emit(processCall { smartOcr.processWithoutTemplate(part) })
        }.flowOn(io)
    }

    override fun processTemplate(file: File, templateId: String): Flow<Resource<ResponseTemplate>> {
        val part = MultipartBody.Part.createFormData(
            "file",
            file.name,
            UploadRequestBody(file, onUpload = {

            })
        )
        val templateId = MultipartBody.Part.createFormData("template_id", templateId)
        return flow {
            emit(processCall { smartOcr.processTemplate(part, templateId) })
        }
    }

    private suspend fun <T> processCall(responseCall: suspend () -> Response<T>): Resource<T> {
        smartOcr = serviceGenerator.createService(SmartOCRService::class.java)
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
