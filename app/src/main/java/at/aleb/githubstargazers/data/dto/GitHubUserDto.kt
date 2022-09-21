package at.aleb.githubstargazers.data.dto

import com.google.gson.annotations.SerializedName

data class GitHubUserDto (
    @SerializedName("login") val name : String,
    @SerializedName("avatar_url") val avatarUrl : String
)