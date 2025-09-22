package com.castlecoach.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.castlecoach.app.data.PlanItemWithDone
import com.castlecoach.app.data.PlanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlanViewModel(app: Application): AndroidViewModel(app) {
    private val repo = PlanRepository.get(app)

    private val _today = MutableStateFlow<List<PlanItemWithDone>>(emptyList())
    val today: StateFlow<List<PlanItemWithDone>> = _today

    fun load() = viewModelScope.launch {
        repo.ensureTodayPlanGenerated()
        _today.value = repo.getToday()
    }

    fun toggle(id: Long, done: Boolean) = viewModelScope.launch {
        if (done) repo.markDone(id) else repo.markUndone(id)
        _today.value = repo.getToday()
    }
}
