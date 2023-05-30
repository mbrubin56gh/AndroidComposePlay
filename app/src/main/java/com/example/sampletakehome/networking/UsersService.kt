package com.example.sampletakehome.networking

import retrofit2.http.GET

interface UsersService {
    @GET("users?limit=30")
    suspend fun users(): Users

    @GET("usersbad")
    suspend fun errorUsers(): Users
}
