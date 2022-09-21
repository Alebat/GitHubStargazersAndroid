package at.aleb.githubstargazers.ui.util

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState

class DataSource<T : Any>(
    private val initialPage: Int = 1,
    private val serviceGet: suspend (page: Int, per_page: Int) -> Result<List<T>>
) : PagingSource<Int, T>() {

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val page = params.key ?: initialPage
        val response = serviceGet(page, params.loadSize)
        return when (response) {
            is Result.ERROR ->
                LoadResult.Error(response.error)
            is Result.OK ->
                LoadResult.Page(
                    data = response.data,
                    prevKey = if (page > initialPage) page - 1 else null,
                    nextKey = if (response.data.size == params.loadSize) page + 1 else null
                )
        }
    }

    sealed class Result<T> {
        data class OK<T>(val data: T) : Result<T>()
        data class ERROR<T>(val error: Exception = Exception("Unspecified loading error")) :
            Result<T>()
    }
}

fun <T : Any> pagerFlow(
    pageSize: Int,
    block: suspend (page: Int, per_page: Int) -> DataSource.Result<List<T>>
) =
    Pager(PagingConfig(pageSize)) {
        DataSource(serviceGet = block)
    }.flow