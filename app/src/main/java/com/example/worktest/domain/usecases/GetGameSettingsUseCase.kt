package com.example.worktest.domain.usecases

import com.example.worktest.domain.entity.GameSettings
import com.example.worktest.domain.entity.Level
import com.example.worktest.domain.repository.GameRepository

class GetGameSettingsUseCase(
    private val repository: GameRepository
) {

    operator fun invoke(level: Level): GameSettings {
        return repository.getGameSettings(level)
    }
}