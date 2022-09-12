package at.aleb.githubstargazers

import at.aleb.githubstargazers.data.GitHubUser
import at.aleb.githubstargazers.data.network.GitHubService
import at.aleb.githubstargazers.data.repositories.GitHubRepository
import at.aleb.githubstargazers.util.MainCoroutineRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GitHubRepositoryUnitTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private val service: GitHubService = mockk()
    private lateinit var repository: GitHubRepository

    private val sampleUserList = listOf(
        GitHubUser(
            name = "schacon",
            avatarUrl = "https://avatars.githubusercontent.com/u/70?v=4"
        ),
        GitHubUser(
            name = "adelcambre",
            avatarUrl = "https://avatars.githubusercontent.com/u/242?v=4"
        ),
        GitHubUser(
            name = "usergenic",
            avatarUrl = "https://avatars.githubusercontent.com/u/578?v=4"
        )
    )

    @Before
    fun setUp() {
        repository = GitHubRepository(service, Dispatchers.Main)
    }

    @Test
    fun test_user() = runTest {
        coEvery { service.getUser(any()) } returns(sampleUserList[0])

        val user = repository.getUser("test_user")

        assertEquals(sampleUserList[0], user)
    }

    @Test
    fun test_stargazers() = runTest {
        coEvery { service.getStargazers(any(), any(), any(), any()) } returns(sampleUserList)

        val stargazers = repository.getStargazers("test_user", "test_repo", 5, 40)

        assertEquals(sampleUserList, stargazers)
    }
}