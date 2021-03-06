package com.secret.palpatine.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.TransitionAdapter
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.secret.palpatine.R
import com.secret.palpatine.data.model.game.GamePhase
import com.secret.palpatine.databinding.GameChancellorDiscardPolicyFragmentBinding

class ChancellorDiscardPolicyFragment : Fragment() {
    private lateinit var binding: GameChancellorDiscardPolicyFragmentBinding
    private lateinit var viewModel: GameViewModel
    private lateinit var detector: GestureDetectorCompat

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = GameChancellorDiscardPolicyFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(GameViewModel::class.java)
        val discardPolicyBindings = listOf(
            binding.discardPolicy0,
            binding.discardPolicy1
        )
        viewModel.currentHand.observe(viewLifecycleOwner, Observer { currentHand ->
            for (i in discardPolicyBindings.indices) {
                val policy = currentHand.getOrNull(i)
                val resourceId = policy?.type?.drawableResource ?: R.drawable.policy_back
                discardPolicyBindings[i].setImageResource(resourceId)
            }
        })
        binding.motionLayout.setTransitionListener(object : TransitionAdapter() {
            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                val discardedIndex = when (currentId) {
                    R.id.end0 -> 0
                    R.id.end1 -> 1
                    else -> null
                }
                if (discardedIndex != null) {
                    viewModel.activeGamePhase.value = null
                    viewModel.chancellorDiscardPolicy(discardedIndex)
                }
            }
        })

        viewModel.game.observe(viewLifecycleOwner, Observer { game ->
            binding.discardPolicyVetoButton.visibility =
                if (game.imperialPolitics >= 5) View.VISIBLE else View.INVISIBLE
            binding.motionLayout.rebuildScene()
        })

        binding.discardPolicyVetoButton.setOnClickListener {
            viewModel.setGamePhase(GamePhase.president_accept_veto)
        }
    }
}