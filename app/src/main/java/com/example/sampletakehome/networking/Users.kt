package com.example.sampletakehome.networking

data class Users(
    val users: List<User>,
    val total: Long,
    val skip: Long,
    val limit: Long
)

data class User(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val birthDate: String,
    val image: String
)
