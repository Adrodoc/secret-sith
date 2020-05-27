package com.example.secret.palpatine.data.model.friends.friend

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class FriendsListAdapter(private val list: List<Friend>): RecyclerView.Adapter<FriendViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return FriendViewHolder(
            inflater,
            parent
        )
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val friend: Friend = list[position]
        holder.bind(friend)
    }


    override fun getItemCount(): Int = list.size
}