package at.aleb.githubstargazers.ui.compose

import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import at.aleb.githubstargazers.R
import at.aleb.githubstargazers.domain.Resource
import at.aleb.githubstargazers.domain.model.GitHubUser

@Composable
fun StargazersList(
    context: Context,
    lazyItems: LazyPagingItems<GitHubUser>,
    stargazersState: Resource<*>
) {
    if (lazyItems.loadState.refresh is LoadState.Loading)
        CircularProgressIndicator()
    else when (stargazersState) {
        is Resource.START -> {}
        is Resource.LOADING ->
            CircularProgressIndicator()
        is Resource.ERROR ->
            Text(context.getString(R.string.error) + ": " + stargazersState.code)
        is Resource.NOCONNECTION ->
            Text(context.getString(R.string.no_connection))
        is Resource.NOTFOUND ->
            Text(context.getString(R.string.repo_not_found))
        is Resource.SUCCESS -> {
            if (lazyItems.itemCount == 0)
                Text(context.getString(R.string.no_stargazers))
            else {
                Text(
                    context.getString(R.string.stargazers_title),
                    style = MaterialTheme.typography.h5
                )
                LazyColumn(
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    items(lazyItems) {
                        it?.run {
                            UserCard(it)
                        }
                    }
                }
            }
        }
    }
}