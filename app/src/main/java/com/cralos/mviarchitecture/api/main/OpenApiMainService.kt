package com.cralos.mviarchitecture.api.main

import androidx.lifecycle.LiveData
import com.cralos.mviarchitecture.api.GenericResponse
import com.cralos.mviarchitecture.models.AccountProperties
import com.cralos.mviarchitecture.util.GenericApiResponse
import retrofit2.http.*

interface OpenApiMainService {

    @GET("account/properties")
    fun getAccountProperties(@Header("Authorization") authorization: String): LiveData<GenericApiResponse<AccountProperties>>

    @PUT("account/properties/update")
    @FormUrlEncoded
    fun saveAccountProperties(
        @Header("Authorization") authorization: String,
        @Field("email") email: String,
        @Field("username") username: String
    ) : LiveData<GenericApiResponse<GenericResponse>>

}