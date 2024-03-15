package com.notsatria.githubusers.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.notsatria.githubusers.data.Result
import com.notsatria.githubusers.data.local.entity.UserEntity
import com.notsatria.githubusers.data.local.room.UserDao
import com.notsatria.githubusers.data.remote.response.DetailUserResponse
import com.notsatria.githubusers.data.remote.retrofit.ApiService
import com.notsatria.githubusers.utils.AppExecutors
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailUserRepository private constructor(
    private val apiService: ApiService,
    private val userDao: UserDao,
    private val appExecutors: AppExecutors
){
    private val result = MediatorLiveData<Result<UserEntity>>()

    fun getDetailUser(username: String): LiveData<Result<UserEntity>> {
        val result = MediatorLiveData<Result<UserEntity>>()
        result.value = Result.Loading
        val client = apiService.getDetailUser(username)
        client.enqueue(object : Callback<DetailUserResponse> {
            override fun onResponse(call: Call<DetailUserResponse>, response: Response<DetailUserResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    appExecutors.diskIO.execute {
                        val isFavorite = userDao.isUserFavorite(username)
                        userDao.updateDetailUser(
                            responseBody?.login!!,
                            responseBody?.followers!!,
                            responseBody?.following!!,
                            responseBody?.name!!,
                            isFavorite
                        )
                        Log.d("DetailUserRepository", "onResponse: $responseBody")
                    }
                }
            }

            override fun onFailure(call: Call<DetailUserResponse>, t: Throwable) {
                result.value = Result.Error(t.message.toString())
            }
        })

        val localData = userDao.getUser(username)
        result.addSource(localData) { newData: UserEntity ->
            result.value = Result.Success(newData)
        }
        return result
    }

    fun setFavoriteUser(user: UserEntity, favoriteState: Boolean) {
        appExecutors.diskIO.execute {
            user.isFavorite = favoriteState
            userDao.update(user)
        }
    }

    fun isFavoriteUser(username: String) {
        appExecutors.diskIO.execute {
            userDao.isUserFavorite(username)
        }
    }

    companion object {
        @Volatile
        private var instance: DetailUserRepository? = null
        fun getInstance(
            apiService: ApiService,
            newsDao: UserDao,
            appExecutors: AppExecutors
        ): DetailUserRepository =
            instance ?: synchronized(this) {
                instance ?: DetailUserRepository(apiService, newsDao, appExecutors)
            }.also { instance = it }
    }
}