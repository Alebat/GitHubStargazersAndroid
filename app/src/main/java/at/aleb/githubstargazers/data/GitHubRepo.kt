package at.aleb.githubstargazers.data

import com.google.gson.annotations.SerializedName

data class GitHubRepo(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("stargazers_count") val stargazers: Number,
    @SerializedName("owner") val owner: GitHubUser
)