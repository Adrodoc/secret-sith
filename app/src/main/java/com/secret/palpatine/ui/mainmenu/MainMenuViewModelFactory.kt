package com.secret.palpatine.ui.mainmenu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.secret.palpatine.data.model.friends.friend.FriendRepository
import com.secret.palpatine.data.model.game.GameRepository
import com.secret.palpatine.data.model.user.UserRepository

/**
 * Created by Florian Fuchs on 08.06.2020.
 */
class MainMenuViewModelFactory(
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainMenuViewModel(Firebase.auth, FriendRepository(), GameRepository(), UserRepository()) as T
    }
}