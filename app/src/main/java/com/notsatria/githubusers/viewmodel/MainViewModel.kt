package com.notsatria.githubusers.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.notsatria.githubusers.response.SearchUserResponse
import com.notsatria.githubusers.response.User
import com.notsatria.githubusers.retrofit.ApiConfig
import com.notsatria.githubusers.utils.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel() : ViewModel() {

    companion object {
        private const val TAG = "MainViewModel"
    }

    private val _userList = MutableLiveData<Event<List<User>>>()
    val userList: MutableLiveData<Event<List<User>>> = _userList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: MutableLiveData<Boolean> = _isError

    private val _isNotFound = MutableLiveData<Boolean>()
    val isNotFound: MutableLiveData<Boolean> = _isNotFound

    fun searchUser(query: String, applicationContext: Context) {
        _isLoading.value = true
        val client = ApiConfig.getApiService(context = applicationContext).searchUser(query)
        client.enqueue(object : Callback<SearchUserResponse> {
            override fun onResponse(call: Call<SearchUserResponse>, response: Response<SearchUserResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    _userList.value = Event(responseBody?.items) as Event<List<User>>
                    _isNotFound.value = responseBody?.totalCount == 0

                    Log.d(TAG, "onResponse searchUser: ${response.body()}")
                } else {
                    _isError.value = true
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<SearchUserResponse>, t: Throwable) {
                _isLoading.value = false
                _isError.value = true
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

}