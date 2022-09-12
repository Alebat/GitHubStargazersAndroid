package at.aleb.githubstargazers.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import at.aleb.githubstargazers.data.repositories.GitHubRepository
import at.aleb.githubstargazers.ui.util.DataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GitHubViewModel @Inject constructor(
    private val gitHubRepository: GitHubRepository
) : ViewModel() {

    data class Filter(
        var owner: String,
        var repo: String
    )

    private val parameters = MutableStateFlow(Filter("", ""))

    fun setOwner(owner: String) {
        viewModelScope.launch {
            parameters.emit(Filter(owner, parameters.value.repo))
        }
    }

    fun setRepoName(repoName: String) {
        viewModelScope.launch {
            parameters.emit(Filter(parameters.value.owner, repoName))
        }
    }

    val query = parameters.map {
        Filter(
            it.owner.trim(),
            it.repo.trim()
        )
    }.filter {
        it.owner.isNotBlank() && it.repo.isNotBlank()
    }

    var pageSize = 30

    @OptIn(ExperimentalCoroutinesApi::class)
    val stargazers = query.flatMapLatest {
        Pager(PagingConfig(pageSize)) {
            DataSource { page, per_page ->
                gitHubRepository.getStargazers(it.owner, it.repo, page, per_page)
            }
        }.flow.cachedIn(viewModelScope)
    }
}