package com.secret.palpatine.ui.mainmenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.secret.palpatine.R
import com.secret.palpatine.data.model.friends.friend.FriendRepository
import com.secret.palpatine.data.model.friends.friend.request.FriendRequest
import com.secret.palpatine.data.model.friends.friend.request.FriendRequestsListAdapter
import com.secret.palpatine.databinding.FragmentFriendsmenuBinding
import com.secret.palpatine.databinding.FragmentFriendsrequestmenuBinding
import kotlinx.android.synthetic.main.activity_main_menu.*
import kotlinx.android.synthetic.main.fragment_friendsmenu.*

/**
 * Created by Florian Fuchs on 09.06.2020.
 */
class FriendRequestsFragment : Fragment(), FriendRequestsListAdapter.FriendRequestAcceptListener {

    private lateinit var viewModel: MainMenuViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(
            this,
            MainMenuViewModelFactory()
        )
            .get(MainMenuViewModel::class.java)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friendsrequestmenu, container, false)

    }

    override fun onAccept(data: FriendRequest) {
        viewModel.acceptFriendRequest(data.id)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loading.visibility = View.VISIBLE

        viewModel.friendsRequestResult.observe(viewLifecycleOwner, Observer {
            val friendListResult = it ?: return@Observer
            loading.visibility = View.GONE

            if (friendListResult.error != null) {
            }
            if (friendListResult.success != null) {
                populateList(friendListResult.success)
            }
        })

        viewModel.acceptFriendRequestResult.observe(viewLifecycleOwner, Observer {
            val loginResult = it ?: return@Observer

            if (loginResult.error != null) {
                Toast.makeText(context, R.string.error_accept_friend, Toast.LENGTH_SHORT)
            }
            if (loginResult.success) {
                Toast.makeText(context, R.string.success_accept_friend, Toast.LENGTH_SHORT)
                viewModel.getUserFriendRequests()
            }
        })


        viewModel.getUserFriendRequests()
    }


    private fun populateList(requests: List<FriendRequest>) {
        val context = (activity as AppCompatActivity).applicationContext

        friends_recyclerview.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = FriendRequestsListAdapter(requests, context,this@FriendRequestsFragment)
        }
    }


    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity).toolbar.findViewById<TextView>(R.id.mainmenu_toolbar_title)
            .setText(R.string.submenu_friends_toolbar_title)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).toolbar.findViewById<TextView>(R.id.mainmenu_toolbar_title)
            .setText(R.string.submenu_friends_toolbar_title)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }


    companion object {
        fun newInstance(): FriendRequestsFragment = FriendRequestsFragment()
    }
}