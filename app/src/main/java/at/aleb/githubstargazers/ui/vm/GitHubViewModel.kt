package at.aleb.githubstargazers.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import at.aleb.githubstargazers.data.GitHubUser
import at.aleb.githubstargazers.data.repositories.GitHubRepository
import at.aleb.githubstargazers.ui.util.DataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class GitHubViewModel @Inject constructor(
    private val gitHubRepository: GitHubRepository
) : ViewModel() {

    data class Filter(
        var owner: String,
        var repo: String
    )

    sealed class UserState {
        object START : UserState()
        object LOADING : UserState()
        data class SUCCESS(val data: GitHubUser) : UserState()
        data class NOTFOUND(val message: String = "") : UserState()
        object NOCONNECTION : UserState()
        data class ERROR(val code: Int) : UserState()
    }

    sealed class StargazersState {
        object START : StargazersState()
        object SUCCESS : StargazersState()
        data class NOTFOUND(val message: String = "") : StargazersState()
        object NOCONNECTION : StargazersState()
        data class ERROR(val code: Int) : StargazersState()
    }

    var pageSize = 30

    private val parameters = MutableStateFlow(Filter("", ""))

    fun setOwner(owner: String) {
        viewModelScope.launch {
            parameters.emit(Filter(owner, ""))
        }
    }

    fun setRepoName(repoName: String) {
        viewModelScope.launch {
            parameters.emit(Filter(parameters.value.owner, repoName))
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val stargazers = parameters.map {
        Filter(
            it.owner.trim(),
            it.repo.trim()
        )
    }.flatMapLatest {
        if (it.owner == "" || it.repo == "") {
            _stargazersLoadingState.emit(StargazersState.START)
            Pager(PagingConfig(pageSize)) {
                DataSource { _, _ ->
                    listOf<GitHubUser>()
                }
            }.flow.cachedIn(viewModelScope)
        } else {
            Pager(PagingConfig(pageSize)) {
                DataSource { page, per_page ->
                    try {
                        gitHubRepository.getStargazers(it.owner, it.repo, page, per_page).also {
                            _stargazersLoadingState.emit(StargazersState.SUCCESS)
                        }
                    } catch (e: HttpException) {
                        _stargazersLoadingState.emit(
                            when (e.code()) {
                                404 -> StargazersState.NOTFOUND(it.owner)
                                else -> StargazersState.ERROR(e.code())
                            }
                        )
                        throw e
                    } catch (e: Exception) {
                        _stargazersLoadingState.emit(StargazersState.NOCONNECTION)
                        throw e
                    }
                }
            }.flow.cachedIn(viewModelScope)
        }
    }

    private val _stargazersLoadingState = MutableStateFlow<StargazersState>(StargazersState.START)

    val stargazersLoadingState = _stargazersLoadingState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val owner = parameters.flatMapLatest {
        flow {
            emit(
                if (it.owner == "")
                    UserState.START
                else
                    try {
                        emit(UserState.LOADING)
                        UserState.SUCCESS(gitHubRepository.getUser(it.owner))
                    } catch (e: HttpException) {
                        when (e.code()) {
                            404 -> UserState.NOTFOUND(it.owner)
                            else -> UserState.ERROR(e.code())
                        }
                    } catch (e: Exception) {
                        UserState.NOCONNECTION
                    }
            )
        }
    }
}