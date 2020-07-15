package com.cralos.mviarchitecture.api.main

import androidx.lifecycle.LiveData
import com.cralos.mviarchitecture.models.AccountProperties
import com.cralos.mviarchitecture.util.GenericApiResponse
import retrofit2.http.GET
import retrofit2.http.Header

interface OpenApiMainService {

    @GET("account/properties")
    fun getAccountProperties(@Header("Authorization") authorization: String): LiveData<GenericApiResponse<AccountProperties>>

}