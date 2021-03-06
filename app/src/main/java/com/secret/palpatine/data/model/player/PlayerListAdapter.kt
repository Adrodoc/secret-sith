package com.secret.palpatine.data.model.player

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Florian Fuchs on 19.06.2020.
 */
class PlayerListAdapter(
    var list: List<Player>,
    private var context: Context,
    private var currentUserId: String,
    private var showMembership: Boolean = false,
    private var showSate: Boolean = true

) :
    RecyclerView.Adapter<PlayerListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return PlayerListViewHolder(
            inflater,
            parent,
            context,
            currentUserId,
            showMembership,
            showSate
        )
    }

    override fun onBindViewHolder(holder: PlayerListViewHolder, position: Int) {
        val player: Player = list[position]
        holder.bind(player)
    }


    override fun getItemCount(): Int = list.size
}