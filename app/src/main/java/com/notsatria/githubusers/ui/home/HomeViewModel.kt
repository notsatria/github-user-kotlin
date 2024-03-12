package com.notsatria.githubusers.ui.home

import androidx.lifecycle.ViewModel
import com.notsatria.githubusers.data.local.entity.UserEntity
import com.notsatria.githubusers.data.repository.GithubUserRepository

class HomeViewModel(private val githubUserRepository: GithubUserRepository) : ViewModel() {
    companion object {
        private const val TAG = "HomeViewModel"
    }

    fun searchUser(query: String) = githubUserRepository.searchUser(query)

    fun setFavoriteUser(user: UserEntity) {
        githubUserRepository.setFavoriteUser(user, true)
    }

    fun deleteFavoriteUser(user: UserEntity) {
        githubUserRepository.setFavoriteUser(user, false)
    }
}