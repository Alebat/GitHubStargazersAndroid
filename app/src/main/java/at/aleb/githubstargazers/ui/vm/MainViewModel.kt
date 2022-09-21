package at.aleb.githubstargazers.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import at.aleb.githubstargazers.data.repository.GitHubRepository
import at.aleb.githubstargazers.domain.Resource
import at.aleb.githubstargazers.domain.model.GitHubUser
import at.aleb.githubstargazers.ui.util.DataSource
import at.aleb.githubstargazers.ui.util.pagerFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val gitHubRepository: GitHubRepository
) : ViewModel() {

    data class Filter(
        var owner: String,
        var repo: String
    )

    private var pageSize = 30

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
            _stargazersLoadingState.emit(Resource.START())
            pagerFlow(pageSize) { _, _ ->
                DataSource.Result.OK(listOf<GitHubUser>())
            }.cachedIn(viewModelScope)
        } else {
            pagerFlow(pageSize) { page, per_page ->
                // LOADING state is managed by the pager
                val resource = gitHubRepository.getStargazers(it.owner, it.repo, page, per_page)
                _stargazersLoadingState.emit(resource)
                if (resource is Resource.SUCCESS)
                    DataSource.Result.OK(resource.data)
                else
                    DataSource.Result.ERROR()
            }.cachedIn(viewModelScope)
        }
    }

    private val _stargazersLoadingState =
        MutableStateFlow<Resource<List<GitHubUser>>>(Resource.START())

    val stargazersLoadingState = _stargazersLoadingState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val owner = parameters.flatMapLatest {
        flow {
            emit(Resource.LOADING())
            emit(gitHubRepository.getUser(it.owner))
        }
    }
}