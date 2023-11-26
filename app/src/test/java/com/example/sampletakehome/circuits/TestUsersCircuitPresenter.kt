package com.example.sampletakehome.circuits

import com.example.sampletakehome.circuits.UsersScreen.State
import com.example.sampletakehome.database.UserEntity
import com.example.sampletakehome.fakes.FakeUsersDao
import com.example.sampletakehome.fakes.FakeUsersService
import com.example.sampletakehome.repository.UsersRepository
import com.example.sampletakehome.repository.toUsers
import com.google.common.truth.Truth.assertThat
import com.slack.circuit.test.FakeNavigator
import com.slack.circuit.test.test
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TestUsersCircuitPresenter {
    @Test
    fun `fetching then success with refresh`() = runTest {
        val userEntity = UserEntity(id = 0, firstName = "FirstName", imageUrl = "path/to/image")
        val userEntities = listOf(userEntity)
        val usersRepository =
            UsersRepository(FakeUsersService(emptyList()), FakeUsersDao(userEntities))

        val usersPresenter = UsersPresenter(
            navigator = FakeNavigator(),
            usersRepository = usersRepository
        )

        usersPresenter.test {
            assertThat(awaitItem()).isEqualTo(State.Fetching)
            val fetched = awaitItem() as State.Fetched.Success
            assertThat(fetched.users).isEqualTo(userEntities.toUsers())
            assertThat(fetched.isRefreshing).isFalse()

            fetched.eventSink(UsersScreen.Event.RefreshUsers)
            val refreshed = awaitItem() as State.Fetched.Success

            assertThat(refreshed.users).isEqualTo(userEntities.toUsers())
            assertThat(refreshed.isRefreshing).isTrue()

            val afterRefreshed = awaitItem() as State.Fetched.Success
            assertThat(afterRefreshed.users).isEqualTo(userEntities.toUsers())
            assertThat(afterRefreshed.isRefreshing).isFalse()
        }
    }

    @Test
    fun `fetching then network error with refresh`() = runTest {
        val userEntity = UserEntity(id = 0, firstName = "FirstName", imageUrl = "path/to/image")
        val userEntities = listOf(userEntity)
        val usersRepository =
            UsersRepository(
                FakeUsersService(users = emptyList(), throwOnRequest = true),
                FakeUsersDao(userEntities)
            )

        val usersPresenter = UsersPresenter(
            navigator = FakeNavigator(),
            usersRepository = usersRepository
        )

        usersPresenter.test {
            assertThat(awaitItem()).isEqualTo(State.Fetching)
            val fetched = awaitItem() as State.Fetched.Error
            assertThat(fetched.users).isEqualTo(userEntities.toUsers())
            assertThat(fetched.isRefreshing).isFalse()

            fetched.eventSink(UsersScreen.Event.RefreshUsers)
            val refreshed = awaitItem() as State.Fetched.Error
            assertThat(refreshed.users).isEqualTo(userEntities.toUsers())
            assertThat(refreshed.isRefreshing).isTrue()

            val afterRefreshed = awaitItem() as State.Fetched.Error
            assertThat(afterRefreshed.users).isEqualTo(userEntities.toUsers())
            assertThat(afterRefreshed.isRefreshing).isFalse()
        }
    }
}
