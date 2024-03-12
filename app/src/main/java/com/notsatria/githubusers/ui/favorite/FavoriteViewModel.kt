package com.notsatria.githubusers.ui.favorite

import androidx.lifecycle.ViewModel
import com.notsatria.githubusers.data.local.entity.UserEntity
import com.notsatria.githubusers.data.repository.GithubUserRepository

class FavoriteViewModel(private val githubUserRepository: GithubUserRepository) : ViewModel() {

    fun getFavoriteUsers() = githubUserRepository.getFavoriteUsers()

    fun setFavoriteUser(user: UserEntity) {
        githubUserRepository.setFavoriteUser(user, true)
    }

    fun deleteFavoriteUser(user: UserEntity) {
        githubUserRepository.setFavoriteUser(user, false)
    }
}