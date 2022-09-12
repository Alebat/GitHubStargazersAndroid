package at.aleb.githubstargazers.ui.util

import androidx.paging.PagingSource
import androidx.paging.PagingState

class DataSource<T : Any>(
    private val initialPage: Int = 1,
    private val serviceGet: suspend (page: Int, per_page: Int) -> List<T>
) : PagingSource<Int, T>() {

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        return try {
            val page = params.key ?: initialPage
            val response = serviceGet(page, params.loadSize)
            LoadResult.Page(
                data = response,
                prevKey = if (page > initialPage) page - 1 else null,
                nextKey = if (response.size == params.loadSize) page + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}