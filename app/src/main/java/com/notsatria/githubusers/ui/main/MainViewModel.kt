package com.notsatria.githubusers.ui.main

import androidx.lifecycle.ViewModel
import com.notsatria.githubusers.data.local.entity.UserEntity
import com.notsatria.githubusers.data.repository.GithubUserRepository

class MainViewModel(private val githubUserRepository: GithubUserRepository) : ViewModel() {

    companion object {
        private const val TAG = "MainViewModel"
    }

    fun searchUser(query: String) = githubUserRepository.searchUser(query)

    fun getFavoriteUsers() = githubUserRepository.getFavoriteUsers()

    fun setFavoriteUser(user: UserEntity) {
        githubUserRepository.setFavoriteUser(user, true)
    }

    fun deleteFavoriteUser(user: UserEntity) {
        githubUserRepository.setFavoriteUser(user, false)
    }

}