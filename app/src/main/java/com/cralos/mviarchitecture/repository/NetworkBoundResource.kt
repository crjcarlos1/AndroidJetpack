package com.cralos.mviarchitecture.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.cralos.mviarchitecture.ui.DataState
import com.cralos.mviarchitecture.ui.Response
import com.cralos.mviarchitecture.ui.ResponseType
import com.cralos.mviarchitecture.util.Constants.Companion.NETWORK_TIMEOUT
import com.cralos.mviarchitecture.util.Constants.Companion.TESTING_CACHE_DELAY
import com.cralos.mviarchitecture.util.Constants.Companion.TESTING_NETWORK_DELAY
import com.cralos.mviarchitecture.util.ErrorHandling
import com.cralos.mviarchitecture.util.ErrorHandling.Companion.ERROR_CHECK_NETWORK_CONNECTION
import com.cralos.mviarchitecture.util.ErrorHandling.Companion.ERROR_UNKNOWN
import com.cralos.mviarchitecture.util.GenericApiResponse
import kotlinx.coroutines.*

/**ResponseObject -> respuesta de retrofit*/
@InternalCoroutinesApi
abstract class NetworkBoundResource<ResponseObject, ViewStateType>
    (
    isNetworkAvailable: Boolean, // is their a network connection?
    isNetworkRequest: Boolean    // is this a network request
) {

    private val TAG: String = "AppDebug"

    protected val result = MediatorLiveData<DataState<ViewStateType>>()
    protected lateinit var job: CompletableJob
    protected lateinit var coroutineScope: CoroutineScope

    init {
        setJob(initNewJob())
        setValue(DataState.loading(isLoading = true, cachedData = null))

        if (isNetworkRequest){
            if (isNetworkAvailable) {
                coroutineScope.launch {

                    // simulate a network delay for testing
                    delay(TESTING_NETWORK_DELAY)

                    withContext(Dispatchers.Main) {

                        // make network call
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
                        Log.e(TAG, "NetworkBoundResource: JOB NETWORK TIMEOUT.")
                        job.cancel(CancellationException(ErrorHandling.UNABLE_TO_RESOLVE_HOST))
                    }
                }
            } else {
                onErrorReturn(ErrorHandling.UNABLE_TODO_OPERATION_WO_INTERNET, shouldUseDialog = true, shouldUseToast = false)
            }
        }
        else{
            coroutineScope.launch {
                //Fake delay for testing cache
                delay(TESTING_CACHE_DELAY)

                //View data from cache ONLY and return
                createCacheRequestAndReturn()
            }
        }
    }

    suspend fun handleNetworkCall(response: GenericApiResponse<ResponseObject>) {

        when (response) {
            is GenericApiResponse.ApiSuccessResponse -> {
                handleApiSuccessResponse(response)
            }
            is GenericApiResponse.ApiErrorResponse -> {
                Log.e(TAG, "NetworkBoundResource: ${response.errorMessage}")
                onErrorReturn(response.errorMessage, true, false)
            }
            is GenericApiResponse.ApiEmptyResponse -> {
                Log.e(TAG, "NetworkBoundResource: Request returned NOTHING (HTTP 204).")
                onErrorReturn("HTTP 204. Returned NOTHING.", true, false)
            }
        }
    }

    fun onCompleteJob(dataState: DataState<ViewStateType>) {
        GlobalScope.launch(Dispatchers.Main) {
            job.complete()
            setValue(dataState)
        }
    }

    fun onErrorReturn(errorMessage: String?, shouldUseDialog: Boolean, shouldUseToast: Boolean) {
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

        onCompleteJob(DataState.error(Response(msg, responseType)))
    }

    fun setValue(dataState: DataState<ViewStateType>) {
        result.value = dataState
    }

    private fun initNewJob(): Job {
        Log.d(TAG, "initNewJob: called.")
        job = Job() // create new job
        job.invokeOnCompletion(
            onCancelling = true,
            invokeImmediately = true,
            handler = object : CompletionHandler {
                override fun invoke(cause: Throwable?) {
                    if (job.isCancelled) {
                        Log.e(TAG, "NetworkBoundResource: Job has been cancelled.")
                        cause?.let {
                            onErrorReturn(it.message, false, true)
                        } ?: onErrorReturn("Unknown error.", false, true)
                    } else if (job.isCompleted) {
                        Log.e(TAG, "NetworkBoundResource: Job has been completed.")
                        // Do nothing? Should be handled already
                    }
                }
            })
        coroutineScope = CoroutineScope(Dispatchers.IO + job)
        return job
    }

    fun asLiveData() = result as LiveData<DataState<ViewStateType>>

    abstract suspend fun createCacheRequestAndReturn()

    abstract suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<ResponseObject>)

    abstract fun createCall(): LiveData<GenericApiResponse<ResponseObject>>

    abstract fun setJob(job: Job)

}






