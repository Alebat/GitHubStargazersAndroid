package at.aleb.githubstargazers.domain.repository

import at.aleb.githubstargazers.domain.Resource
import at.aleb.githubstargazers.domain.model.GitHubUser

interface IGitHubRepository {
    suspend fun getUser(user: String): Resource<GitHubUser>
    suspend fun getStargazers(
        owner: String, repository: String, page: Int, per_page: Int = 30
    ): Resource<List<GitHubUser>>
}