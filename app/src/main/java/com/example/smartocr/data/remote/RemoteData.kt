package com.example.smartocr.data.remote

import com.example.smartocr.data.Resource
import com.example.smartocr.data.dto.response.ResponseHelloWorld
import com.example.smartocr.data.dto.response.ResponseLogin
import com.example.smartocr.data.dto.response.ResponseTable
import com.example.smartocr.data.dto.response.ResponseTemplate
import com.example.smartocr.data.dto.response.ResponseTemplateMetadata
import com.example.smartocr.data.dto.response.Template
import com.example.smartocr.data.model.FileDocument
import com.example.smartocr.data.model.OcrCCCD
import com.example.smartocr.data.model.TemplateKey
import com.example.smartocr.data.remote.service.SmartOCRService
import com.example.smartocr.util.NetworkConnectivity
import com.example.smartocr.util.logd
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
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

    override fun processOcrCCCD(image: File): Flow<Resource<OcrCCCD>> {
        val part = MultipartBody.Part.createFormData(
            "file",
            image.name,
            UploadRequestBody(image, onUpload = {

            })
        )
        return flow {
            emit(
                processCall { smartOcr.processOcrCCCD(part) }
            )
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

    override fun processTableMetadata(file: File): Flow<Resource<String>> {
        val part = MultipartBody.Part.createFormData(
            "file",
            file.name,
            UploadRequestBody(file, onUpload = {

            })
        )
        return flow {
            emit(processCall(true) {
                smartOcr.processTableMetadata(part)
            })
        }.flowOn(io)
    }

    override fun processTable(file: File, fileName: String): Flow<Resource<ResponseTable>> {
        val part = MultipartBody.Part.createFormData(
            "file",
            file.name,
            UploadRequestBody(file, onUpload = {

            })
        )
        val name = MultipartBody.Part.createFormData("filename", fileName)

        return flow {
            emit(processCall {
                smartOcr.processTable(part, name)
            })
        }.flowOn(io)
    }

    override fun login(userName: String, password: String): Flow<Resource<ResponseLogin>> {
        val username = MultipartBody.Part.createFormData("username", userName)
        val password = MultipartBody.Part.createFormData("password", password)
        return flow {
            emit(processCall {
                smartOcr.login(username, password)
            })
        }.flowOn(io)
    }

    override fun signIn(
        userName: String,
        password: String,
        rePassword: String,
        phone: String
    ): Flow<Resource<String>> {
        val username = MultipartBody.Part.createFormData("username", userName)
        val password = MultipartBody.Part.createFormData("password", password)
        val rePassword = MultipartBody.Part.createFormData("re_password", rePassword)
        val phone = MultipartBody.Part.createFormData("phone", phone)
        return flow {
            emit(processCall(true) {
                smartOcr.signIn(username, password, rePassword, phone)
            })
        }.flowOn(io)

    }

    override fun listCCCD(): Flow<Resource<List<OcrCCCD>>> {
        return flow {
            emit(processCall {
                smartOcr.listCCCD()
            })
        }.flowOn(io)
            .map {
                it.map { it?.map { it.document.copy(objectID = it.id) } ?: listOf() }
            }
    }

    override fun editCCCD(documentId: String, file: File): Flow<Resource<String>> {
        val cccdId = MultipartBody.Part.createFormData("document_id", documentId)
        val part = MultipartBody.Part.createFormData(
            "file",
            file.name,
            UploadRequestBody(
                file,
                onUpload = {
                })
        )
        return flow {
            emit(processCall(true) {
                smartOcr.editCCCD(documentId = cccdId, file = part)
            })
        }
            .flowOn(io)

    }

    override fun deleteCCCD(documentId: String): Flow<Resource<String>> {
        val cccdId = MultipartBody.Part.createFormData("document_id", documentId)

        return flow {
            emit(processCall(true) {
                smartOcr.deleteCCCD(documentId = cccdId)
            })
        }
    }

    override fun listTemplate(): Flow<Resource<List<Template>>> {
        return flow {
            emit(processCall {
                smartOcr.listTemplate()
            })
        }.flowOn(io)
    }

    override fun createKeyTemplate(data: String, keyName: String): Flow<Resource<String>> {
        return flow {
            emit(processCall(scalar = true) {
                smartOcr.createKeyTemplate(
                    MultipartBody.Part.createFormData("text", data),
                    MultipartBody.Part.createFormData("name", keyName),
                )
            })
        }.flowOn(io)
    }

    override fun listKeyTemplate(): Flow<Resource<List<TemplateKey>>> {
        return flow {
            emit(processCall {
                smartOcr.listTemplateKey()
            })
        }.flowOn(io)
    }

    override fun deleteKeyTemplate(id: String): Flow<Resource<String>> {
        return flow {
            emit(processCall {
                smartOcr.deleteKeyTemplate(
                    documentId = MultipartBody.Part.createFormData(
                        "document_id",
                        id
                    )
                )
            })
        }.flowOn(io)
    }

    override fun autoFill(
        templateId: String,
        documentId: String
    ): Flow<Resource<ResponseTemplate>> = flow {
        emit(Resource.Loading)
        emit(processCall {
            smartOcr.autoFill(
                MultipartBody.Part.createFormData("template_id", templateId),
                MultipartBody.Part.createFormData("document_id", documentId),
            )
        })
    }.flowOn(io)

    override fun getAllFileResult(): Flow<Resource<List<FileDocument>>> {
        return flow {
            emit(processCall {
                smartOcr.getListFileResult()
            })
        }.flowOn(io)
    }

    override fun deleteFileResult(id: String): Flow<Resource<Unit>> = flow {
        emit(processCall(scalar = true) {
            smartOcr.deleteFileResult(
                documentId = MultipartBody.Part.createFormData("document_id", id)
            )

        })
    }.map { it.map { } }.flowOn(io)

    private suspend
    fun <T> processCall(
        scalar: Boolean = false,
        responseCall: suspend () -> Response<T>
    ): Resource<T> {
        smartOcr = serviceGenerator.createService(SmartOCRService::class.java, scalar = scalar)
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
                Resource.Error(
                    errorCode = response.code(),
                    message = response.message()
                )
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Resource.Error(message = "Unknown error happened.")
        }
    }
}
