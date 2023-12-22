package com.rizkyizh.panopticon.di

import android.content.Context
import com.rizkyizh.panopticon.data.pref.UserPreference
import com.rizkyizh.panopticon.data.pref.dataStore
import com.rizkyizh.panopticon.data.remote.retrofit.ApiConfig
import com.rizkyizh.panopticon.data.repository.AuthRepository
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first

object Injection {
    fun provideAuthRepository(context: Context): AuthRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return AuthRepository.getInstance(apiService, pref)
    }
}