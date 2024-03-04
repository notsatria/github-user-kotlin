package com.notsatria.githubusers.viewmodel

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.notsatria.githubusers.response.DetailUserResponse
import com.notsatria.githubusers.response.User
import com.notsatria.githubusers.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailViewModel() : ViewModel() {
    companion object {
        const val TAG = "DetailActivity"
        const val EXTRA_USERNAME = "extra_username"
    }

    private val _detailUser = MutableLiveData<DetailUserResponse>()
    val detailUser: MutableLiveData<DetailUserResponse> = _detailUser

    private val _detailFollowerUser = MutableLiveData<List<User>>()
    val detailFollowerUser: MutableLiveData<List<User>> = _detailFollowerUser

    private val _detailFollowingUser = MutableLiveData<List<User>>()
    val detailFollowingUser: MutableLiveData<List<User>> = _detailFollowingUser

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: MutableLiveData<Boolean> = _isError

     fun getDetailUser(username: String, context: Context) {
        _isLoading.value = true
        val client = ApiConfig.getApiService(context = context).getDetailUser(username)
        client.enqueue(object : Callback<DetailUserResponse> {
            override fun onResponse(call: Call<DetailUserResponse>, response: Response<DetailUserResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _detailUser.value = response.body() as DetailUserResponse
                    Log.d(TAG, "onResponse: ${response.body()}")
                } else {
                    _isError.value = true
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<DetailUserResponse>, t: Throwable) {
                _isLoading.value = false
                _isError.value = true
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun getFollowers(username: String, context: Context) {
        _isLoading.value = true
        val client = ApiConfig.getApiService(context = context).getFollowers(username)
        client.enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                _isLoading.value = false
                if (response.isSuccessful) {
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