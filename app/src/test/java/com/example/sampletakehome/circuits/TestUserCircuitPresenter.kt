package com.example.sampletakehome.circuits

import com.example.sampletakehome.database.UserEntity
import com.example.sampletakehome.fakes.FakeUsersDao
import com.example.sampletakehome.fakes.FakeUsersService
import com.example.sampletakehome.repository.UsersRepository
import com.example.sampletakehome.repository.toUser
import com.google.common.truth.Truth.assertThat
import com.slack.circuit.test.test
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TestUserCircuitPresenter {
    @Test
    fun `loading then user`() = runTest {
        val userEntity = UserEntity(id = 0, firstName = "FirstName", imageUrl = "path/to/image")
        val userEntities = listOf(userEntity)
        val usersRepository =
            UsersRepository(FakeUsersService(emptyList()), FakeUsersDao(userEntities))

        val userDetailPresenter = UserDetailPresenter(
            screen = UserDetailScreen(0),
            usersRepository = usersRepository
        )

        userDetailPresenter.test {
            assertThat(awaitItem()).isEqualTo(UserDetailScreen.State.Loading)
            assertThat(awaitItem()).isEqualTo(UserDetailScreen.State.HasUser(userEntity.toUser()))
        }
    }
}