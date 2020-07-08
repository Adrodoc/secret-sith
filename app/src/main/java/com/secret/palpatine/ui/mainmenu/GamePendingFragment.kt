package com.secret.palpatine.ui.mainmenu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.secret.palpatine.R
import com.secret.palpatine.data.model.game.Game
import com.secret.palpatine.data.model.game.GameState
import com.secret.palpatine.data.model.player.Player
import com.secret.palpatine.data.model.player.PlayerListAdapter
import com.secret.palpatine.ui.game.GameActivity
import com.secret.palpatine.ui.game.GameViewModel
import kotlinx.android.synthetic.main.fragment_game_pending.*

/**
 * Fragment to show a game which is still pending with the invited players
 */
class GamePendingFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var list: RecyclerView
    private lateinit var viewModel: GameViewModel
    private lateinit var startButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = Firebase.auth
        // Inflate the layout for this fragment
        viewModel = ViewModelProvider(this).get(GameViewModel::class.java)

        return inflater.inflate(R.layout.fragment_game_pending, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        list = view.findViewById(R.id.players_list_overlay)
        startButton = view.findViewById(R.id.btnStart)

        progress_overlay.rootView.visibility= View.VISIBLE
        startButton.setOnClickListener {
            if (true) { // TODO
                viewModel.start()
                val intent = Intent(context, GameActivity::class.java).apply {
                    putExtra("gameId", arguments?.getString("gameId")!!)
                    putExtra("userId", viewModel.userId)
                }
                startActivity(intent)
                activity?.finish()

            } else {
                Toast.makeText(
                    context,
                    "At least 2 Players have to accept to start a game",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        viewModel.players.observe(viewLifecycleOwner, Observer {
            Log.d("Players", it.toString())
            populatePlayerList(it)
        })

        viewModel.game.observe(viewLifecycleOwner, Observer {
            init(it)
            progress_overlay.rootView.visibility= View.INVISIBLE
        })

        viewModel.loadGameAndPlayersForPendingState(arguments?.getString("gameId")!!)
    }

    override fun onStart() {
        super.onStart()
        reset()
    }

    override fun onResume() {
        super.onResume()
        reset()
    }

    private fun reset() {
    }

    private fun populatePlayerList(players: List<Player>) {
        list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = PlayerListAdapter(players, context, auth.currentUser!!.uid, false)
        }

    }

    private fun init(game: Game) {
        if (game.host != auth.currentUser?.uid) {
            startButton.visibility = View.INVISIBLE
        }

        if(game.state == GameState.started){
            val intent = Intent(context, GameActivity::class.java).apply {
                putExtra("gameId", arguments?.getString("gameId")!!)
                putExtra("userId", viewModel.userId)
            }
            startActivity(intent)
            activity?.finish()
        }

    }

}
