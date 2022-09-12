package at.aleb.githubstargazers

import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.LOGGER
import androidx.paging.LoadState
import app.cash.turbine.test
import at.aleb.githubstargazers.data.GitHubUser
import at.aleb.githubstargazers.data.repositories.GitHubRepository
import at.aleb.githubstargazers.ui.vm.GitHubViewModel
import at.aleb.githubstargazers.util.MainCoroutineRule
import at.aleb.githubstargazers.util.MyDiffCallback
import at.aleb.githubstargazers.util.NoopListCallback
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response


@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var differ: AsyncPagingDataDiffer<GitHubUser>

    private val gitHubRepository: GitHubRepository = mockk()

    private lateinit var viewModel: GitHubViewModel

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
        // Workaround: Android's Log used here, overriding
        LOGGER = mockk()
        every { LOGGER!!.isLoggable(any()) } returns (false)

        differ = AsyncPagingDataDiffer(
            MyDiffCallback(),
            NoopListCallback(),
            TestCoroutineScheduler()
        )
        viewModel = GitHubViewModel(gitHubRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun happyPath() = runTest {

        coEvery {
            gitHubRepository.getStargazers(
                any(), any(), any(), any()
            )
        } returns sampleUserList

        viewModel.setOwner("o")
        viewModel.setRepoName("r")

        val job = launch {
            differ.refresh()
            differ.submitData(viewModel.stargazers.first())
            differ.loadStateFlow.test {
                assertEquals(LoadState.Loading::class.java, awaitItem().refresh::class.java)
                assertEquals(LoadState.NotLoading::class.java, awaitItem().refresh::class.java)
                awaitComplete()
            }
        }

        advanceUntilIdle()

        assertEquals(sampleUserList, differ.snapshot().items)
        job.cancel()
    }

    @Test
    fun errorPath() = runTest {

        coEvery {
            gitHubRepository.getStargazers(
                any(), any(), any(), any()
            )
        } throws HttpException(
            Response.error<GitHubUser>(404, ResponseBody.create(MediaType.parse(""), ""))
        )

        viewModel.setOwner("o")
        viewModel.setRepoName("r")

        val job = launch {
            differ.refresh()
            differ.submitData(viewModel.stargazers.first())
            differ.loadStateFlow.test {
                assertEquals(LoadState.Loading::class.java, awaitItem().refresh::class.java)
                assertEquals(LoadState.Error::class.java, awaitItem().refresh::class.java)
                awaitComplete()
            }
        }

        advanceUntilIdle()

        assertEquals(listOf<GitHubUser>(), differ.snapshot().items)
        job.cancel()
    }
}
