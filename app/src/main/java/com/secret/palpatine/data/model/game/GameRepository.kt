package com.secret.palpatine.data.model.game

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.secret.palpatine.data.model.PlayerRole
import com.secret.palpatine.data.model.player.Player
import com.secret.palpatine.data.model.player.PlayerState
import com.secret.palpatine.data.model.user.User

/**
 * Created by Florian Fuchs on 19.06.2020.
 */

class GameRepository {

    val db = Firebase.firestore


    fun startGame(gameId: String, players: List<Player>): Task<Void> {


        val gameRef = db.collection("games").document(gameId)
        Log.d("Players", players.size.toString())
        var roleListPlayers: List<Player> = generatePlayerRolesAndOrder(players)

        val batch = db.batch()

        val randomNumbers = (0..4).shuffled();
        var counter = 0;

        // First Player in Ref
        var playerRef = buildPlayerRef(gameId, roleListPlayers[0].id)

        for (player in roleListPlayers) {
            // Same for first player, then increment through for each
            playerRef = buildPlayerRef(gameId, player.id)
            val playerData = hashMapOf(
                "role" to player.role,
                "order" to randomNumbers[counter]
            )

            batch.set(playerRef, playerData, SetOptions.merge())
            counter++;

        }

        val gameData = hashMapOf(
            "state" to GameState.started,
            "presidentialCandidate" to buildPlayerRef(
                gameId,
                players[randomNumbers[0]].id
            )
        )
        batch.set(gameRef, gameData, SetOptions.merge())
        return batch.commit()

    }

    fun getPlayers(gameId: String): CollectionReference {

        return db.collection("games").document(gameId).collection("players")


    }

    fun createGame(playerList: List<User>, userId: String, userName: String): Task<String> {


        val game: HashMap<String, Any> = hashMapOf(
            "loyalistPolitics" to 0,
            "imperialPolitics" to 0,
            "failedGovernments" to 0,
            "state" to "pending",
            "phase" to "nominate_chancellor",
            "host" to userId,
            "numberPlayers" to playerList.size + 1
        )

        val election = hashMapOf(
            "votes_left" to playerList.size + 1,
            "counter" to 0
        )
        game["election"] = election

        val host = hashMapOf(
            "user" to userId,
            "createdAt" to FieldValue.serverTimestamp(),
            "state" to PlayerState.accepted,
            "userName" to userName,
            "isHost" to true
        )


        return db.collection("games").add(game).continueWithTask {
            if (it.exception != null) {
                throw it.exception!!
            } else {
                val id = it.result!!.id

                var plist: ArrayList<Task<DocumentReference>> = arrayListOf()

                for (player in playerList) {
                    val playerToAdd = hashMapOf(
                        "user" to player.id,
                        "inviteBy" to userName,
                        "inviteById" to userId,
                        "createdAt" to FieldValue.serverTimestamp(),
                        "state" to PlayerState.pending,
                        "userName" to player.username,
                        "order" to 0
                    )
                    plist.add(
                        db.collection("games").document(id).collection("players").add(playerToAdd)
                    )
                }
                plist.add(
                    db.collection("games").document(id).collection("players").add(host)
                )
                Tasks.whenAll(plist).continueWith { id }
            }
        }
    }


    fun getGame(gameId: String): DocumentReference {

        return db.collection("games").document(gameId)
    }


    fun joinGame(gameId: String, userId: String, userName: String): Task<Void> {

        val data = hashMapOf(
            "user" to userId,
            "createdAt" to FieldValue.serverTimestamp(),
            "state" to PlayerState.accepted,
            "userName" to userName
        )
        return db.collection("games").document(gameId).collection("players")
            .add(data).continueWithTask {

                val userUpdate = hashMapOf(
                    "currentGame" to gameId
                )
                if (it.exception != null) {
                    throw it.exception!!
                } else {
                    db.collection("users").document(userId).set(userUpdate, SetOptions.merge())
                }
            }
    }

    private fun generatePlayerRolesAndOrder(players: List<Player>): List<Player> {
        val playerRoleList: List<PlayerRole>

        if (players.size == 5) {
            playerRoleList = listOf(
                PlayerRole.loyalist,
                PlayerRole.loyalist,
                PlayerRole.loyalist,
                PlayerRole.imperialist,
                PlayerRole.sith
            ).shuffled()
        } else {
            throw Exception("Number of players is not supported.")
        }

        for (i in players.indices) {
            players[i].role = playerRoleList[i]
        }

        return players
    }


    private fun buildPlayerRef(gameId: String, playerId: String): DocumentReference {
        return db.collection("games").document(gameId).collection("players").document(playerId)

    }
}