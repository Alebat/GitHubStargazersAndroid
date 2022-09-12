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
import io.mockk.coVerify
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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response


@OptIn(ExperimentalCoroutinesApi::class)
class GitHubViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var differ: AsyncPagingDataDiffer<GitHubUser>
    private val gitHubRepository: GitHubRepository = mockk()
    private lateinit var viewModel: GitHubViewModel

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
    fun userHappyPath() = runTest {
        coEvery {
            gitHubRepository.getUser(
                any()
            )
        } returns Samples.user

        launch {
            viewModel.owner.test {
                assertEquals(GitHubViewModel.UserState.LOADING::class.java, awaitItem()::class.java)
                val item = awaitItem()
                assertEquals(GitHubViewModel.UserState.SUCCESS::class.java, item::class.java)
                assertEquals((item as GitHubViewModel.UserState.SUCCESS).data, Samples.user)
                cancel()
            }
        }

        viewModel.setOwner("o")

        advanceUntilIdle()

        coVerify { gitHubRepository.getUser(any()) }
    }

    @Test
    fun stargazersHappyPath() = runTest {
        coEvery {
            gitHubRepository.getStargazers(
                any(), any(), any(), any()
            )
        } returns Samples.userList


        launch {
            differ.refresh()
            differ.submitData(viewModel.stargazers.first())
        }

        launch {
            differ.loadStateFlow.test {
                assertEquals(LoadState.Loading::class.java, awaitItem().refresh::class.java)
                assertEquals(LoadState.NotLoading::class.java, awaitItem().refresh::class.java)
            }
        }

        viewModel.setOwner("o")
        viewModel.setRepoName("r")

        advanceUntilIdle()

        assertEquals(Samples.userList, differ.snapshot().items)
        coVerify { gitHubRepository.getStargazers(any(), any(), any(), any()) }

        // Exit blocking 'submitData'
        differ.refresh()
    }

    @Test
    fun userNoConnection() = runTest {
        coEvery {
            gitHubRepository.getUser(
                any()
            )
        } throws Exception()

        launch {
            viewModel.owner.test {
                assertEquals(GitHubViewModel.UserState.LOADING::class.java, awaitItem()::class.java)
                val item = awaitItem()
                assertEquals(GitHubViewModel.UserState.NOCONNECTION::class.java, item::class.java)
                cancel()
            }
        }

        viewModel.setOwner("o")

        advanceUntilIdle()

        coVerify { gitHubRepository.getUser(any()) }
    }

    @Test
    fun stargazersNoConnection() = runTest {
        coEvery {
            gitHubRepository.getStargazers(
                any(), any(), any(), any()
            )
        } throws Exception()

        launch {
            differ.submitData(viewModel.stargazers.first())
        }

        launch {
            differ.loadStateFlow.test {
                assertEquals(LoadState.Loading::class.java, awaitItem().refresh::class.java)
                assertEquals(LoadState.Error::class.java, awaitItem().refresh::class.java)
            }
            viewModel.stargazersLoadingState.test {
                assertEquals(GitHubViewModel.StargazersState.NOCONNECTION::class.java, awaitItem()::class.java)
            }
        }

        viewModel.setOwner("o")
        viewModel.setRepoName("r")

        advanceUntilIdle()

        assertEquals(mutableListOf<GitHubUser>(), differ.snapshot().items)

        coVerify { gitHubRepository.getStargazers(any(), any(), any(), any()) }

        // Exit blocking 'submitData'
        differ.refresh()
    }

    @Test
    fun userNotFound() = runTest {
        coEvery {
            gitHubRepository.getUser(
                any()
            )
        } throws HttpException(Response.error<GitHubUser>(404, "".toResponseBody("".toMediaTypeOrNull())))

        launch {
            viewModel.owner.test {
                assertEquals(GitHubViewModel.UserState.LOADING::class.java, awaitItem()::class.java)
                val item = awaitItem()
                assertEquals(GitHubViewModel.UserState.NOTFOUND::class.java, item::class.java)
            }
        }

        viewModel.setOwner("o")

        advanceUntilIdle()

        coVerify { gitHubRepository.getUser(any()) }
    }

    @Test
    fun stargazersNotFound() = runTest {
        coEvery {
            gitHubRepository.getStargazers(
                any(), any(), any(), any()
            )
        } throws HttpException(Response.error<GitHubUser>(404, "".toResponseBody("".toMediaTypeOrNull())))

        launch {
            differ.submitData(viewModel.stargazers.first())
        }

        launch {
            differ.loadStateFlow.test {
                assertEquals(LoadState.Loading::class.java, awaitItem().refresh::class.java)
                assertEquals(LoadState.Error::class.java, awaitItem().refresh::class.java)
            }
            viewModel.stargazersLoadingState.test {
                assertEquals(GitHubViewModel.StargazersState.NOTFOUND::class.java, awaitItem()::class.java)
            }
        }

        viewModel.setOwner("o")
        viewModel.setRepoName("r")

        advanceUntilIdle()

        assertEquals(mutableListOf<GitHubUser>(), differ.snapshot().items)

        coVerify { gitHubRepository.getStargazers(any(), any(), any(), any()) }

        // Exit blocking 'submitData'
        differ.refresh()
    }
}
