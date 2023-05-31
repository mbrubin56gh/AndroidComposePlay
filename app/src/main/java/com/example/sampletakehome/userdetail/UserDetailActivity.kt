package com.example.sampletakehome.userdetail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.load
import com.example.sampletakehome.SampleUsersApplication
import com.example.sampletakehome.User
import com.example.sampletakehome.databinding.ActivityUserDetailBinding
import kotlinx.coroutines.launch

class UserDetailActivity : AppCompatActivity() {
    private val viewModel: UserDetailViewModel by viewModels {
        UserDetailViewModel.Factory(SampleUsersApplication.applicationComponent.usersRepository())
    }
    private lateinit var binding: ActivityUserDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val selectedUserId = intent.getLongExtra(USER_ID_DATA_EXTRA_TAG, INVALID_USER_ID)
        require(selectedUserId != INVALID_USER_ID) {
            "No user id passed to UserDetailActivity."
        }

        binding = ActivityUserDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                val user = viewModel.getUser(selectedUserId)
                binding.userImage.load(user.imageUrl)
                binding.userName.text = user.firstName
                // Now bind user to the UI. It's safe here because we're on the main thread
                // and we're in a STARTED state.
                // We probably should split this into a request to the ViewModel to start fetching
                // the user _and_ a distinct way to observe via Flow the results of that fetch:
                // the fetching would be scoped by viewModelScope, and so will continue even if
                // we're paused or stopped; the observation of the Flow would be scoped by the
                // started state lifecycle bit. Then the fetching can outlive the starting/stopping
                // but the hydration of our Views will be scoped by the started/stopped state.
            }
        }
    }

    companion object {
        private const val INVALID_USER_ID = -1L
        private const val USER_ID_DATA_EXTRA_TAG = "user_data_extra"

        fun startActivity(user: User, from: Activity) {
            from.startActivity(Intent(from, UserDetailActivity::class.java).apply {
                putExtra(USER_ID_DATA_EXTRA_TAG, user.id)
            })
        }
    }
}