package at.aleb.githubstargazers.data.repositories

import at.aleb.githubstargazers.data.GitHubUser
import at.aleb.githubstargazers.data.network.GitHubService
import at.aleb.githubstargazers.di.QualifyDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GitHubRepository @Inject constructor(
    private val gitHubService: GitHubService,
    @QualifyDispatcher.IO
    private val dispatcher: CoroutineDispatcher
) {
    suspend fun getUser(
        user: String
    ): GitHubUser = withContext(dispatcher) {
        gitHubService.getUser(user)
    }

    suspend fun getStargazers(
        owner: String,
        repository: String,
        page: Int,
        per_page: Int = 30
    ): List<GitHubUser> = withContext(dispatcher) {
        gitHubService.getStargazers(owner, repository, page, per_page)
    }
}