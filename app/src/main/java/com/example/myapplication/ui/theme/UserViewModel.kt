package com.example.myapplication.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.UserEntity
import com.example.myapplication.data.UserRepository
import com.example.myapplication.data.local.AppDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserViewModel(app: Application) : AndroidViewModel(app) {

    private val repo: UserRepository = UserRepository(
        AppDatabase.get(app).userDao()
    )

    val users: StateFlow<List<UserEntity>> =
        repo.users
            .map { it }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun refresh() {
        viewModelScope.launch {
            repo.refresh()
        }
    }
}
