package com.notsatria.githubusers.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.notsatria.githubusers.data.Result
import com.notsatria.githubusers.data.local.entity.UserEntity
import com.notsatria.githubusers.data.local.room.UserDao
import com.notsatria.githubusers.data.remote.response.SearchUserResponse
import com.notsatria.githubusers.data.remote.retrofit.ApiService
import com.notsatria.githubusers.utils.AppExecutors
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GithubUserRepository private constructor(
    private val apiService: ApiService,
    private val userDao: UserDao,
    private val appExecutors: AppExecutors
) {

    private val result = MediatorLiveData<Result<List<UserEntity>>>()

    fun searchUser(query: String): LiveData<Result<List<UserEntity>>> {
        result.value = Result.Loading
        val client = apiService.searchUser(query)
        client.enqueue(object : Callback<SearchUserResponse> {
            override fun onResponse(call: Call<SearchUserResponse>, response: Response<SearchUserResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    val users = responseBody?.items
                    val userList = ArrayList<UserEntity>()
                    appExecutors.diskIO.execute {
                        users?.forEach { user ->
                            val isFavorite = userDao.isUserFavorite(user?.login!!)
                            val userEntity = UserEntity(
                                user.id,
                                user.login,
                                user.avatarUrl,
                                user.htmlUrl,
                                isFavorite
                            )
                            userList.add(userEntity)
                        }
                        userDao.deleteAll()
                        userDao.insertAll(userList)

                        if (responseBody?.totalCount == 0) {
//                            userDao.deleteAll()
//                            userDao.insertAll(userList)
                            result.postValue(Result.Empty("No user found"))
                        }
                    }
                }
            }

            override fun onFailure(call: Call<SearchUserResponse>, t: Throwable) {
                result.postValue(Result.Error(t.message.toString()))
                appExecutors.diskIO.execute {
                    userDao.deleteAll()
                }
            }
        })

        val localData = userDao.getAll()
        result.addSource(localData) { newData: List<UserEntity> ->
            result.value = Result.Success(newData)
        }

        return result
    }

    fun getFavoriteUsers(): LiveData<List<UserEntity>> {
        return userDao.getFavoriteUsers()
    }

    fun setFavoriteUser(user: UserEntity, favoriteState: Boolean) {
        appExecutors.diskIO.execute {
            user.isFavorite = favoriteState
            userDao.update(user)
        }
    }

    companion object {
        @Volatile
        private var instance: GithubUserRepository? = null
        fun getInstance(
            apiService: ApiService,
            newsDao: UserDao,
            appExecutors: AppExecutors
        ): GithubUserRepository =
            instance ?: synchronized(this) {
                instance ?: GithubUserRepository(apiService, newsDao, appExecutors)
            }.also { instance = it }
    }
}
