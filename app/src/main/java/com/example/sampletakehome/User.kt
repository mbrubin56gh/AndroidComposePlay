package com.example.sampletakehome

import com.example.sampletakehome.networking.UserNetworkModel

data class User(
    val id: Long,
    val firstName: String,
    val imageUrl: String
)

fun UserNetworkModel.toUser() = User(
    id = id,
    firstName = firstName,
    imageUrl = imageUrl
)

fun List<UserNetworkModel>.toUsers() = map { it.toUser() }