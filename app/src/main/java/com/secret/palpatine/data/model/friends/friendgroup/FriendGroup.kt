package com.secret.palpatine.data.model.friends.friendgroup

import com.secret.palpatine.data.model.user.User

data class FriendGroup(
    val letter: Char,
    var friendList: MutableList<User>
)