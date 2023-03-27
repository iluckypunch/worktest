package com.example.worktest.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.worktest.R
import com.example.worktest.databinding.FragmentGameFinishedBinding

class GameFinishedFragment: Fragment() {

    private val args by navArgs<GameFinishedFragmentArgs>()

    private var _binding: FragmentGameFinishedBinding? = null
    private val binding: FragmentGameFinishedBinding
        get() = _binding ?: throw Exception("FragmentGameFinishedBinding == null")


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        bindViews()
    }

    private fun setClickListeners() {
        binding.buttonRetry.setOnClickListener {
            retryGame()
        }
    }

    private fun bindViews() {
        with(binding) {
            emojiResult.setImageResource(getSmileResId())
            tvRequiredAnswers.text = String.format(getString(R.string.required_score), args.gameResult.gameSettings.minCountOfRightAnswers)
            tvScoreAnswers.text = String.format(getString(R.string.score_answers), args.gameResult.countOfRightAnswers)
            tvRequiredPercentage.text = String.format(getString(R.string.required_percentage), args.gameResult.gameSettings.minPercentOfRightAnswers)
            tvScorePercentage.text = String.format(getString(R.string.score_percentage), getScorePercentage())
        }
    }

    private fun getScorePercentage(): Int {
        return if (args.gameResult.countOfQuestions == 0) {
            0
        } else {
            (args.gameResult.countOfRightAnswers / args.gameResult.countOfQuestions.toDouble() * 100).toInt()
        }
    }

    private fun getSmileResId(): Int {
        return if (args.gameResult.winner) {
            R.drawable.ic_smile
        } else {
            R.drawable.ic_sad
        }
    }

    private fun retryGame() {
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}