package com.notsatria.githubusers.ui.detail

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.notsatria.githubusers.data.repository.DetailUserRepository
import com.notsatria.githubusers.data.remote.response.User
import com.notsatria.githubusers.data.remote.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailViewModel(private val detailUserRepository: DetailUserRepository) : ViewModel() {
    companion object {
        const val TAG = "DetailActivity"
        const val EXTRA_USERNAME = "extra_username"
    }


    private val _detailFollowerUser = MutableLiveData<List<User>>()
    val detailFollowerUser: MutableLiveData<List<User>> = _detailFollowerUser

    private val _detailFollowingUser = MutableLiveData<List<User>>()
    val detailFollowingUser: MutableLiveData<List<User>> = _detailFollowingUser

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: MutableLiveData<Boolean> = _isError

    private val _isEmptyFollowers = MutableLiveData<Boolean>()
    val isEmptyFollowers: MutableLiveData<Boolean> = _isEmptyFollowers

    private val _isEmptyFollowing = MutableLiveData<Boolean>()
    val isEmptyFollowing: MutableLiveData<Boolean> = _isEmptyFollowing

//     fun getDetailUser(username: String, context: Context) {
//        _isLoading.value = true
//        val client = ApiConfig.getApiService(context = context).getDetailUser(username)
//        client.enqueue(object : Callback<DetailUserResponse> {
//            override fun onResponse(call: Call<DetailUserResponse>, response: Response<DetailUserResponse>) {
//                _isLoading.value = false
//                if (response.isSuccessful) {
//                    _detailUser.value = response.body() as DetailUserResponse
//                    Log.d(TAG, "onResponse: ${response.body()}")
//                } else {
//                    _isError.value = true
//                    Log.e(TAG, "onFailure: ${response.message()}")
//                }
//            }
//
//            override fun onFailure(call: Call<DetailUserResponse>, t: Throwable) {
//                _isLoading.value = false
//                _isError.value = true
//                Log.e(TAG, "onFailure: ${t.message}")
//            }
//        })
//    }

    fun getDetailUser(username: String) = detailUserRepository.getDetailUser(username)

    fun getFollowers(username: String, context: Context) {
        _isLoading.value = true
        val client = ApiConfig.getApiService(context = context).getFollowers(username)
        client.enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    if (response.body() == null || response.body()!!.isEmpty()) {
                        _isEmptyFollowers.value = true
                    }
                    _detailFollowerUser.value = response.body() as List<User>
                    Log.d(TAG, "onResponse getFolllowers: ${response.body()}")
                } else {
                    _isError.value = true
                    Log.e(TAG, "onFailure getFollowers: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                _isLoading.value = false
                _isError.value = true
                Log.e(TAG, "onFailure getFollowers: ${t.message}")
            }
        })
    }

    fun getFollowing(username: String, context: Context) {
        _isLoading.value = true
        val client = ApiConfig.getApiService(context = context).getFollowing(username)
        client.enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    if (response.body() == null || response.body()!!.isEmpty()) {
                        _isEmptyFollowing.value = true
                    }
                    _detailFollowingUser.value = response.body() as List<User>
                    Log.d(TAG, "onResponse getFollowing: ${response.body()}")
                } else {
                    _isError.value = true
                    Log.e(TAG, "onFailure getFollowing: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                _isLoading.value = false
                _isError.value = true
                Log.e(TAG, "onFailure getFollowing: ${t.message}")
            }
        })
    }
}