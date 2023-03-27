package com.example.worktest.domain.repository

import com.example.worktest.domain.entity.GameSettings
import com.example.worktest.domain.entity.Level
import com.example.worktest.domain.entity.Question

interface GameRepository {

    fun generateQuestion(
        maxSumValue: Int,
        countOfOptions: Int
    ): Question

    fun getGameSettings(level: Level): GameSettings
}