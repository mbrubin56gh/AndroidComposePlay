package com.example.sampletakehome.networking

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

object Networking {
    private val usersRetrofit = Retrofit.Builder()
        .client(OkHttpClient.Builder().build())
        .baseUrl("https://dummyjson.com/")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    interface UsersService {
        @GET("users?limit=30")
        suspend fun users(): List<User>

        @GET("users?limit=0")
        suspend fun noUsers(): List<User>

        @GET("usersbad")
        suspend fun errorUsers(): List<User>
    }

    val usersService: UsersService = usersRetrofit.create(UsersService::class.java)
}