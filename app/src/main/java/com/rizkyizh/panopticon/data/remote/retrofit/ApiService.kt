package com.rizkyizh.panopticon.data.remote.retrofit

import com.rizkyizh.panopticon.data.remote.response.LoginResponse
import com.rizkyizh.panopticon.data.remote.response.RegisterResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {

    @FormUrlEncoded
    @POST("users")
    suspend fun userRegister(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
    ): RegisterResponse


    @FormUrlEncoded
    @POST("login")
    suspend fun userLogin(
        @Field("email") email: String,
        @Field("password") password: String,
    ): LoginResponse

}