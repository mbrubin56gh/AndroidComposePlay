package com.example.sampletakehome.repository

import app.cash.turbine.test
import com.example.sampletakehome.fakes.FakeUsersDao
import com.example.sampletakehome.fakes.FakeUsersService
import com.example.sampletakehome.networking.UserNetworkModel
import com.example.sampletakehome.repository.UsersRepository.UsersResult
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Test

class TestUsersRepository {
    @Test
    fun `refreshUsers with empty users`() = runTest {
        val usersRepository = UsersRepository(FakeUsersService(emptyList()), FakeUsersDao())
        usersRepository.refreshUsers()

        usersRepository.users().test {
            val usersResult = awaitItem()
            assertThat(usersResult).isInstanceOf(UsersResult.Success::class.java)
            assertThat((usersResult as UsersResult.Success).users).isEmpty()
            awaitComplete()
        }
    }

    @Test
    fun `refreshUsers with users`() = runTest {
        val userNetworkModel =
            UserNetworkModel(id = 0, firstName = "FirstName", imageUrl = "path/to/image")
        val userNetworkModels = listOf(userNetworkModel)
        val usersRepository = UsersRepository(FakeUsersService(userNetworkModels), FakeUsersDao())
        usersRepository.refreshUsers()

        usersRepository.users().test {
            val usersResult = awaitItem()
            assertThat(usersResult).isInstanceOf(UsersResult.Success::class.java)
            assertThat((usersResult as UsersResult.Success).users).isEqualTo(
                userNetworkModels.toUserEntities().toUsers()
            )
            awaitComplete()
        }
    }

    @Test
    fun `refreshUsers with users dedupes`() = runTest {
        val userNetworkModel =
            UserNetworkModel(id = 0, firstName = "FirstName", imageUrl = "path/to/image")
        val userNetworkModels = listOf(userNetworkModel)
        val usersRepository = UsersRepository(
            FakeUsersService(
                users = userNetworkModels, emitTwice = true
            ), FakeUsersDao()
        )
        usersRepository.refreshUsers()

        usersRepository.users().test {
            val usersResult = awaitItem()
            assertThat(usersResult).isInstanceOf(UsersResult.Success::class.java)
            assertThat((usersResult as UsersResult.Success).users).isEqualTo(
                userNetworkModels.toUserEntities().toUsers()
            )
            awaitComplete()
        }
    }

    @Test
    fun `refreshUsers with error`() = runTest {
        val usersRepository = UsersRepository(
            FakeUsersService(
                users = emptyList(), throwOnRequest = true
            ), FakeUsersDao()
        )
        usersRepository.refreshUsers()

        usersRepository.users().test {
            val usersResult = awaitItem()
            assertThat(usersResult).isInstanceOf(UsersResult.WithNetworkError::class.java)
            awaitComplete()
        }
    }

    @Test
    fun getUser() = runTest {
        val userNetworkModel1 =
            UserNetworkModel(id = 1, firstName = "FirstName1", imageUrl = "path/to/image1")
        val userNetworkModel2 =
            UserNetworkModel(id = 2, firstName = "FirstName1", imageUrl = "path/to/image2")
        val userNetworkModels = listOf(userNetworkModel1, userNetworkModel2)
        val usersRepository = UsersRepository(
            FakeUsersService(
                users = userNetworkModels, emitTwice = true
            ), FakeUsersDao()
        )
        usersRepository.refreshUsers()

        val userEntity = usersRepository.getUser(2)
        assertThat(userEntity).isEqualTo(userNetworkModel2.toUserEntity().toUser())
    }
}