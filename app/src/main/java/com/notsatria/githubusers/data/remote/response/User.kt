package com.notsatria.githubusers.data.remote.response

import com.google.gson.annotations.SerializedName

data class User(

    @field:SerializedName("html_url")
    val htmlUrl: String? = null,

    @field:SerializedName("login")
    val login: String? = null,

    @field:SerializedName("avatar_url")
    val avatarUrl: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

)
