package com.secret.palpatine.ui.mainmenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.secret.palpatine.R
import com.secret.palpatine.data.model.friends.friend.FriendsListAdapter
import com.secret.palpatine.data.model.friends.friendgroup.FriendGroup
import com.secret.palpatine.data.model.friends.friendgroup.FriendGroupAdapter
import com.secret.palpatine.data.model.user.User
import kotlinx.android.synthetic.main.activity_main_menu.*
import kotlinx.android.synthetic.main.fragment_start_game_menu.*

class StartGameMenuFragment : Fragment(), FriendsListAdapter.FriendListAdapterListener,
    View.OnClickListener {
    private lateinit var viewModel: MainMenuViewModel

    private var selectedUsers: List<User> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        viewModel = ViewModelProvider(this).get(MainMenuViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_start_game_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loading.visibility = View.VISIBLE
        start_game_button.isClickable = true
        start_game_button.setOnClickListener(this)
        viewModel.friendListResult.observe(viewLifecycleOwner, Observer {
            val loginResult = it ?: return@Observer

            if (loginResult.error != null) {
                loading.visibility = View.GONE
                showErrorLoading(loginResult.error)
            }
            if (loginResult.success != null) {
                loading.visibility = View.GONE
                generateLetteredList(loginResult.success)
            }
        })
        viewModel.usersToStartGame.observe(viewLifecycleOwner, Observer {
            val userList = it ?: return@Observer
            selectedUsers = userList
            // start_game_button.isClickable = selectedUsers.size > 3

        })

        viewModel.refreshUserFriends()
    }

    private fun showErrorLoading(error: Int) {

        errorText.text = "Error while loading your friends..."

        errorText.visibility = View.VISIBLE
        start_game_recyclerview.visibility = View.GONE
    }

    private fun generateLetteredList(friendList: List<User>) {
        val letterList: MutableList<Char> = ArrayList()
        val userList: MutableList<User> = ArrayList()
        for (user in friendList) {
            letterList.add(user.username.decapitalize().first())
            userList.add(user)
        }
        val letterSet: Set<Char> = letterList.toSortedSet()
        val friendGroupList: MutableList<FriendGroup> = ArrayList()
        for (letter in letterSet) {
            val friendGroup =
                FriendGroup(
                    letter,
                    ArrayList()
                )
            for (user in userList) {
                if (user.username.decapitalize().first() == letter) {
                    friendGroup.friendList.add(user)
                }
            }
            friendGroupList.add(friendGroup)
        }
        val context = (activity as AppCompatActivity).applicationContext
        start_game_recyclerview.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = FriendGroupAdapter(friendGroupList, context, this@StartGameMenuFragment)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.start_game_button -> {
                viewModel.startGame().addOnSuccessListener {
                    val bundle = bundleOf(Pair("gameId", it))
                    findNavController().navigate(
                        R.id.action_startGameMenuFragment_to_gamePendingFragment,
                        bundle
                    )

                }
            }
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


    override fun onDestroyView() {
        viewModel.resetUsersToStartGame()
        super.onDestroyView()
    }
    override fun onSelect(data: User) {
        viewModel.updateUserToStartGameList(data)
    }

    companion object {
        fun newInstance(): StartGameMenuFragment =
            StartGameMenuFragment()
    }
}
