package com.cralos.mviarchitecture.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.cralos.mviarchitecture.ui.DataState
import com.cralos.mviarchitecture.ui.Response
import com.cralos.mviarchitecture.ui.ResponseType
import com.cralos.mviarchitecture.util.Constants.Companion.NETWORK_TIMEOUT
import com.cralos.mviarchitecture.util.Constants.Companion.TESTING_NETWORK_DELAY
import com.cralos.mviarchitecture.util.ErrorHandling
import com.cralos.mviarchitecture.util.ErrorHandling.Companion.ERROR_CHECK_NETWORK_CONNECTION
import com.cralos.mviarchitecture.util.ErrorHandling.Companion.ERROR_UNKNOWN
import com.cralos.mviarchitecture.util.ErrorHandling.Companion.UNABLE_TODO_OPERATION_WO_INTERNET
import com.cralos.mviarchitecture.util.ErrorHandling.Companion.UNABLE_TO_RESOLVE_HOST
import com.cralos.mviarchitecture.util.GenericApiResponse
import kotlinx.coroutines.*

/**ResponseObject -> respuesta de retrofit*/
@InternalCoroutinesApi
abstract class NetworkBoundResource<ResponseObject, ViewStateType>(
    val isNetworkAvailable: Boolean
) {

    private val TAG = "AppDebug"
    protected val result = MediatorLiveData<DataState<ViewStateType>>()
    protected lateinit var job: CompletableJob
    protected lateinit var coroutineScope: CoroutineScope

    init {
        setJob(initNewJob())
        setValue(DataState.loading(isLoading = true, cachedData = null))
        if (isNetworkAvailable) {

            /**Esta corrutina es esencialmente la api request*/
            coroutineScope.launch {
                //simulate a network delay for testing
                delay(TESTING_NETWORK_DELAY)
                withContext(Dispatchers.Main) {
                    //make network call
                    val apiResponse = createCall()
                    result.addSource(apiResponse) { response ->
                        result.removeSource(apiResponse)
                        coroutineScope.launch {
                            handleNetworkCall(response)
                        }
                    }
                }
            }

            GlobalScope.launch(Dispatchers.IO) {
                delay(NETWORK_TIMEOUT)
                if (!job.isCompleted) {
                    Log.e(TAG, "NetworkBoundResource: JOB NETWORK TIMEOUT ")
                    job.cancel(CancellationException(UNABLE_TO_RESOLVE_HOST))
                }
            }

        } else {
            onErrorReturn(UNABLE_TODO_OPERATION_WO_INTERNET, true, false)
        }

    }

    suspend fun handleNetworkCall(response: GenericApiResponse<ResponseObject>?) {
        when (response) {
            is GenericApiResponse.ApiSuccessResponse -> {
                handleNetworkCall(response)
            }
            is GenericApiResponse.ApiErrorResponse -> {
                Log.e(TAG, "NetworkBoundResource: ${response.errorMessage}")
                onErrorReturn(response.errorMessage, true, false)
            }
            is GenericApiResponse.ApiEmptyResponse -> {
                Log.e(TAG, "NetworkBoundResource: Request return NOTHING (HTTP 204)")
                onErrorReturn("HTTP 204. Returned Nothing", true, false)
            }
        }
    }

    fun onCompleteJob(dataState: DataState<ViewStateType>) {
        GlobalScope.launch(Dispatchers.Main) {
            job.complete()
            setValue(dataState)
        }
    }

    private fun setValue(dataState: DataState<ViewStateType>) {
        result.value = dataState
    }

    fun onErrorReturn(
        errorMessage: String? = null,
        shouldUseDialog: Boolean,
        shouldUseToast: Boolean
    ) {
        var msg = errorMessage
        var useDialog = shouldUseDialog
        var responseType: ResponseType = ResponseType.None()
        if (msg == null) {
            msg = ERROR_UNKNOWN
        } else if (ErrorHandling.isNetworkError(msg)) {
            msg = ERROR_CHECK_NETWORK_CONNECTION
            useDialog = false
        }
        if (shouldUseToast) {
            responseType = ResponseType.Toast()
        }
        if (useDialog) {
            responseType = ResponseType.Dialog()
        }
        onCompleteJob(
            DataState.error(
                response = Response(
                    message = msg,
                    responseType = responseType
                )
            )
        )
    }

    @InternalCoroutinesApi
    private fun initNewJob(): Job {
        Log.d(TAG, "initNewJob: called... ")
        job = Job()
        job.invokeOnCompletion(
            onCancelling = true,
            invokeImmediately = true,
            handler = object : CompletionHandler {
                override fun invoke(cause: Throwable?) {
                    if (job.isCancelled) {
                        Log.e(TAG, "NetworkBoundResource: Job has been canceled.")
                        cause?.let {
                            onErrorReturn(it.message, false, true)
                        } ?: onErrorReturn(ERROR_UNKNOWN, false, true)
                    } else if (job.isCompleted) {
                        Log.e(TAG, "NetworkBoundResource: Job has been completed")
                        //TODO("Do nothing. Should be handling already")
                    }
                }
            })
        coroutineScope = CoroutineScope(Dispatchers.IO + job)
        return job
    }

    fun asLiveData() = result as LiveData<DataState<ViewStateType>>
    abstract suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<ResponseObject>)
    abstract fun createCall(): LiveData<GenericApiResponse<ResponseObject>>
    abstract fun setJob(job: Job)

}