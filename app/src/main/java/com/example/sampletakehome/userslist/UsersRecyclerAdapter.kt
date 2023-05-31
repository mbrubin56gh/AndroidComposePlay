package com.example.sampletakehome.userslist

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.sampletakehome.R
import com.example.sampletakehome.User
import com.example.sampletakehome.databinding.UserRowBinding

class UsersRecyclerAdapter(private val onUserClicked: (User) -> Unit) :
    ListAdapter<User, UsersRecyclerAdapter.UserViewHolder>(UserDiffCallBack()) {

    class UserViewHolder(
        private val binding: UserRowBinding,
        private val onUserClicked: (User) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(user: User) {
            binding.userName.text = user.firstName
            binding.userImage.load(user.imageUrl) {
                crossfade(true)
                transformations(CircleCropTransformation())
                val context = binding.userName.context
                placeholder(ColorDrawable(ContextCompat.getColor(context, R.color.grey_50)))
            }
            binding.root.setOnClickListener { onUserClicked(user) }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = UserRowBinding.inflate(inflater, parent, false)
        return UserViewHolder(binding, onUserClicked)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    private class UserDiffCallBack : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(
            oldItem: User,
            newItem: User
        ): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: User,
            newItem: User
        ): Boolean = oldItem == newItem
    }
}
