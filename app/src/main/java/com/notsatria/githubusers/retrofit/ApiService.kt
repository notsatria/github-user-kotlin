package com.notsatria.githubusers.retrofit

import com.notsatria.githubusers.response.DetailUserResponse
import com.notsatria.githubusers.response.SearchUserResponse
import com.notsatria.githubusers.response.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("search/users")
    fun searchUser(@Query("q") query: String): Call<SearchUserResponse>

    @GET("users/{username}")
    fun getDetailUser(@Path("username") username: String) : Call<DetailUserResponse>

    @GET("users/{username}/followers")
    fun getFollowers(@Path("username") username: String) : Call<List<User>>

    @GET("users/{username}/following")
    fun getFollowing(@Path("username") username: String) : Call<List<User>>
}