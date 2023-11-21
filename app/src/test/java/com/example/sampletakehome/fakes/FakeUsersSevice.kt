package com.example.sampletakehome.fakes

import com.example.sampletakehome.networking.UserNetworkModel
import com.example.sampletakehome.networking.UsersNetworkModel
import com.example.sampletakehome.networking.UsersService

class FakeUsersService(
    private val users: List<UserNetworkModel>,
    private val throwOnRequest: Boolean = false,
    private val emitTwice: Boolean = false
) : UsersService {
    override suspend fun users(): UsersNetworkModel = if (throwOnRequest) {
        error("FakeUsersService configured to throw")
    } else {
        UsersNetworkModel(users)
    }
}
