package at.aleb.githubstargazers

import at.aleb.githubstargazers.data.network.GitHubService
import at.aleb.githubstargazers.data.repositories.GitHubRepository
import at.aleb.githubstargazers.util.MainCoroutineRule
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

    @Before
    fun setUp() {
        repository = GitHubRepository(service, Dispatchers.Main)
    }

    @Test
    fun test_repository() = runTest {

        val stargazers = repository.getStargazers("test_user", "test_repo", 5, 40)

        assertEquals(2, stargazers.size)
        assertEquals("test_user", stargazers[0].name)
        assertEquals("test_repo", stargazers[1].name)
        assertEquals("url", stargazers[0].avatarUrl)
        assertEquals("url", stargazers[1].avatarUrl)
    }
}