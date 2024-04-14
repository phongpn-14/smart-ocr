package com.example.smartocr.data

// A generic class that contains data and status about loading this data.
sealed class Resource<out T>(val data: T? = null, val errorCode: Int? = null) {
    class Success<out T>(data: T) : Resource<T>(data)
    object Loading : Resource<Nothing>()
    object Idle : Resource<Nothing>()
    class Error(errorCode: Int? = null, var message: String? = null) :
        Resource<Nothing>(null, errorCode)

    class Processing(val process: Float) : Resource<Nothing>()


    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$message]"
            is Loading -> "Loading"
            is Processing -> "Processing[process = $$process]"
            is Idle -> "Idle"
        }
    }

    suspend fun whenIdle(callback: suspend (Idle) -> Unit) = apply {
        if (this is Idle) {
            callback(this)
        }
    }

    suspend fun whenLoading(callback: suspend (Loading) -> Unit) = apply {
        if (this is Loading) {
            callback(this)
        }
    }

    suspend fun whenProcessing(callback: suspend (Processing) -> Unit) = apply {
        if (this is Processing) {
            callback(this)
        }
    }

    suspend fun whenError(callback: suspend (Error) -> Unit) = apply {
        if (this is Error) {
            callback(this)
        }
    }

    suspend fun whenSuccess(callback: suspend (Success<T>) -> Unit) = apply {
        if (this is Success) {
            callback(this)
        }
    }

    /**
     * Transform to another resource
     */
    suspend fun <R> map(transform: suspend (T?) -> R): Resource<R> {
        return when (this) {
            is Loading -> Loading
            is Error -> this
            is Processing -> Processing(process)
            is Success -> Success(data = transform(data))
            is Idle -> Idle
        }
    }

    suspend fun <R> transform(transform: suspend (Resource<T>) -> R): R {
        return transform(this)
    }

    val isSuccess get() = this is Success
    val isError get() = this is Error
    val isLoading get() = this is Loading
}
