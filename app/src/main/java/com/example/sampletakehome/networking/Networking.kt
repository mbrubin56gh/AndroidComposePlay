package com.example.sampletakehome.networking

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

object Networking {
    private val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

    private val usersRetrofit = Retrofit.Builder()
        .client(OkHttpClient.Builder().build())
        .baseUrl("https://dummyjson.com/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    interface UsersService {
        @GET("users?limit=30")
        suspend fun users(): Users

        @GET("usersbad")
        suspend fun errorUsers(): Users
    }

    val usersService: UsersService = usersRetrofit.create(UsersService::class.java)
}