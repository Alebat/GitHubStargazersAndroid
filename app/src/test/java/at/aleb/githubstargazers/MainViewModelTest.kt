package at.aleb.githubstargazers

import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.LOGGER
import androidx.paging.LoadState
import app.cash.turbine.test
import at.aleb.githubstargazers.data.dto.GitHubUserDto
import at.aleb.githubstargazers.data.mapper.toEntity
import at.aleb.githubstargazers.data.repository.GitHubRepository
import at.aleb.githubstargazers.domain.Resource
import at.aleb.githubstargazers.domain.model.GitHubUser
import at.aleb.githubstargazers.ui.vm.MainViewModel
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
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var differ: AsyncPagingDataDiffer<GitHubUser>
    private val gitHubRepository: GitHubRepository = mockk()
    private lateinit var viewModel: MainViewModel

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
        viewModel = MainViewModel(gitHubRepository)
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
        } returns Resource.SUCCESS(Samples.user.toEntity())

        launch {
            viewModel.owner.test {
                assertEquals(Resource.LOADING::class.java, awaitItem()::class.java)
                val item = awaitItem()
                assertEquals(Resource.SUCCESS::class.java, item::class.java)
                assertEquals(Samples.user.toEntity(), (item as Resource.SUCCESS).data)
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
        } returns Resource.SUCCESS(Samples.userList.map { it.toEntity() })


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

        assertEquals(differ.snapshot().items, Samples.userList.map { it.toEntity() })
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
        } returns Resource.NOCONNECTION()

        launch {
            viewModel.owner.test {
                assertEquals(Resource.LOADING::class.java, awaitItem()::class.java)
                val item = awaitItem()
                assertEquals(Resource.NOCONNECTION::class.java, item::class.java)
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
        } returns Resource.NOCONNECTION()

        launch {
            differ.submitData(viewModel.stargazers.first())
        }

        launch {
            differ.loadStateFlow.test {
                assertEquals(LoadState.Loading::class.java, awaitItem().refresh::class.java)
                assertEquals(LoadState.Error::class.java, awaitItem().refresh::class.java)
            }
            viewModel.stargazersLoadingState.test {
                assertEquals(Resource.NOCONNECTION::class.java, awaitItem()::class.java)
            }
        }

        viewModel.setOwner("o")
        viewModel.setRepoName("r")

        advanceUntilIdle()

        assertEquals(mutableListOf<GitHubUserDto>(), differ.snapshot().items)

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
        } returns Resource.NOTFOUND()

        launch {
            viewModel.owner.test {
                assertEquals(Resource.LOADING::class.java, awaitItem()::class.java)
                val item = awaitItem()
                assertEquals(Resource.NOTFOUND::class.java, item::class.java)
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
        } returns Resource.NOTFOUND()

        launch {
            differ.submitData(viewModel.stargazers.first())
        }

        launch {
            differ.loadStateFlow.test {
                assertEquals(LoadState.Loading::class.java, awaitItem().refresh::class.java)
                assertEquals(LoadState.Error::class.java, awaitItem().refresh::class.java)
            }
            viewModel.stargazersLoadingState.test {
                assertEquals(Resource.NOTFOUND::class.java, awaitItem()::class.java)
            }
        }

        viewModel.setOwner("o")
        viewModel.setRepoName("r")

        advanceUntilIdle()

        assertEquals(mutableListOf<GitHubUserDto>(), differ.snapshot().items)

        coVerify { gitHubRepository.getStargazers(any(), any(), any(), any()) }

        // Exit blocking 'submitData'
        differ.refresh()
    }
}
