package com.example.sampletakehome

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sampletakehome.SampleUsersApplication.Companion.applicationComponent
import com.example.sampletakehome.UsersViewModel.UsersUIState
import com.example.sampletakehome.databinding.ActivityUsersBinding
import kotlinx.coroutines.launch

class UsersActivity : AppCompatActivity() {
    private val viewModel: UsersViewModel by viewModels {
        UsersViewModel.Factory(applicationComponent.usersRepository())
    }
    private lateinit var binding: ActivityUsersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configureSwipeToRefresh()
        configureRecyclerView()
        collectUiState()
    }

    private fun configureSwipeToRefresh() {
        binding.swipeToRefresh.setOnRefreshListener {
            lifecycleScope.launch {
                // Rely on the UIState Flow collection to update only when we're in the appropriate
                // lifecycle states.
                viewModel.refreshUsers()
                binding.swipeToRefresh.isRefreshing = false
            }
        }
    }

    private fun configureRecyclerView() {
        with(binding.contactsList) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@UsersActivity)
            adapter = UsersRecyclerAdapter()
        }
    }

    private fun collectUiState() {
        lifecycleScope.launch {
            viewModel.usersUiState
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { updateUiState(it) }

            // Alternate for above's collect. Above is nicer to read, IMO, but dangerous, because
            // another similar flow collection below it would never get executed because
            // this flow collection won't complete. See, e.g., the similar issue in
            // https://itnext.io/differences-in-methods-of-collecting-kotlin-flows-3d1d4efd1c2
            // and discussion at
            // https://developer.android.com/topic/libraries/architecture/coroutines
            // repeatOnLifecycle(Lifecycle.State.STARTED) {
            //      viewModel.usersUiState.collect { updateUiState(it) }
            // }
        }
    }

    private fun updateUiState(uiState: UsersUIState) {
        when (uiState) {
            UsersUIState.Fetching -> showFetching()
            UsersUIState.Fetched.Error -> showError()
            is UsersUIState.Fetched.Success -> showContacts(uiState.users)
        }
    }

    private fun showFetching() {
        binding.fetching.visibility = VISIBLE
        binding.contactsList.visibility = GONE
        binding.error.visibility = GONE
    }

    private fun showError() {
        binding.fetching.visibility = GONE
        binding.contactsList.visibility = GONE
        binding.error.visibility = VISIBLE
    }

    private fun showContacts(users: List<User>) {
        binding.fetching.visibility = GONE
        with(binding.contactsList) {
            visibility = VISIBLE
            (adapter as UsersRecyclerAdapter).submitList(users)
        }
        binding.error.visibility = GONE
    }
}

