package at.aleb.githubstargazers.data.repository

import at.aleb.githubstargazers.data.mapper.toEntity
import at.aleb.githubstargazers.data.network.GitHubService
import at.aleb.githubstargazers.di.QualifyDispatcher
import at.aleb.githubstargazers.domain.Resource
import at.aleb.githubstargazers.domain.model.GitHubUser
import at.aleb.githubstargazers.domain.repository.IGitHubRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GitHubRepository @Inject constructor(
    private val gitHubService: GitHubService,
    @QualifyDispatcher.IO
    private val dispatcher: CoroutineDispatcher
) : IGitHubRepository {
    override suspend fun getUser(
        user: String
    ): Resource<GitHubUser> = withContext(dispatcher) {
        try {
            Resource.SUCCESS(gitHubService.getUser(user).toEntity())
        } catch (e: IOException) {
            Resource.NOCONNECTION()
        } catch (e: HttpException) {
            when (e.code()) {
                404 -> Resource.NOTFOUND(user)
                else -> Resource.ERROR(e.code())
            }
        }
    }

    override suspend fun getStargazers(
        owner: String,
        repository: String,
        page: Int,
        per_page: Int
    ): Resource<List<GitHubUser>> = withContext(dispatcher) {
        try {
            Resource.SUCCESS(
                gitHubService.getStargazers(owner, repository, page, per_page).map {
                    it.toEntity()
                }
            )
        } catch (e: IOException) {
            Resource.NOCONNECTION()
        } catch (e: HttpException) {
            when (e.code()) {
                404 -> Resource.NOTFOUND("$owner/$repository")
                else -> Resource.ERROR(e.code())
            }
        }
    }
}