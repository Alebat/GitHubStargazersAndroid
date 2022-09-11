package at.aleb.githubstargazers.data

import com.google.gson.annotations.SerializedName

data class GitHubUser (
    @SerializedName("login") val name : String,
    @SerializedName("avatar_url") val avatarUrl : String
)