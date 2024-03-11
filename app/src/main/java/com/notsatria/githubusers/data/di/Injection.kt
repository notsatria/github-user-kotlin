package com.notsatria.githubusers.data.di

import android.content.Context
import com.notsatria.githubusers.data.repository.DetailUserRepository
import com.notsatria.githubusers.data.repository.GithubUserRepository
import com.notsatria.githubusers.data.local.room.GithubUserDatabase
import com.notsatria.githubusers.data.remote.retrofit.ApiConfig
import com.notsatria.githubusers.utils.AppExecutors

object Injection {
    fun provideGithubRepository(context: Context): GithubUserRepository {
        val apiService = ApiConfig.getApiService(context)
        val database = GithubUserDatabase.getInstance(context)
        val dao = database.userDao()
        val appExecutors = AppExecutors()
        return GithubUserRepository.getInstance(apiService, dao, appExecutors)
    }

    fun provideDetailRepository(context: Context): DetailUserRepository {
        val apiService = ApiConfig.getApiService(context)
        val database = GithubUserDatabase.getInstance(context)
        val dao = database.userDao()
        val appExecutors = AppExecutors()
        return DetailUserRepository.getInstance(apiService, dao, appExecutors)
    }

}