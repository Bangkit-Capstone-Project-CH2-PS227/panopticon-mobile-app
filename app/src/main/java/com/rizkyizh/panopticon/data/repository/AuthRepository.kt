package com.rizkyizh.panopticon.data.repository

import com.rizkyizh.panopticon.data.pref.UserModel
import com.rizkyizh.panopticon.data.pref.UserPreference
import com.rizkyizh.panopticon.data.remote.request.LoginRequest
import com.rizkyizh.panopticon.data.remote.request.RegisterRequest
import com.rizkyizh.panopticon.data.remote.response.LoginResponse
import com.rizkyizh.panopticon.data.remote.response.RegisterResponse
import com.rizkyizh.panopticon.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking


class AuthRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference

) {

    fun saveSession(user: UserModel){
        runBlocking {
            userPreference.saveSession(user)
        }
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()

    }

    suspend fun register(registerRequest: RegisterRequest): RegisterResponse {
        return apiService.userRegister(
            name = registerRequest.name,
            email = registerRequest.email,
            password = registerRequest.password
        )
    }

    suspend fun login(loginRequest: LoginRequest): LoginResponse {
        return apiService.userLogin(
            email = loginRequest.email,
            password = loginRequest.password
        )
    }


    companion object {
        @Volatile
        private var instance: AuthRepository? = null

        fun getInstance(
            apiService: ApiService, userPreference: UserPreference
        ): AuthRepository = instance ?: synchronized(this) {
            instance ?: AuthRepository(apiService, userPreference)
        }.also { instance = it }
    }
}