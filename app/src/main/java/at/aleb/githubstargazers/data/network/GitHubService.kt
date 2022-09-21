package at.aleb.githubstargazers.data.network

import at.aleb.githubstargazers.data.dto.GitHubUserDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GitHubService {

    @GET("users/{username}")
    suspend fun getUser(@Path("username") username: String): GitHubUserDto

    @GET("/repos/{owner}/{repo}/stargazers")
    suspend fun getStargazers(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("page") page: Int,
        @Query("per_page") per_page: Int = 30
    ): List<GitHubUserDto>

}