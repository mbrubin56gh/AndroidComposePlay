package com.example.sampletakehome.networking

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UsersNetworkModel(
    val users: List<UserNetworkModel>,
    val total: Long,
    val skip: Long,
    val limit: Long
)

@JsonClass(generateAdapter = true)
data class UserNetworkModel(
    val id: Long,
    val firstName: String,
    @Json(name = "image") val imageUrl: String
)
