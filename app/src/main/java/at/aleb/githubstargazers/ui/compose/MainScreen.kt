package at.aleb.githubstargazers.ui.compose

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import at.aleb.githubstargazers.R
import at.aleb.githubstargazers.domain.Resource
import at.aleb.githubstargazers.domain.model.GitHubUser
import kotlinx.coroutines.flow.Flow

@Composable
fun MainScreen(
    context: Context,
    user: Flow<Resource<GitHubUser>>,
    stargazers: Flow<PagingData<GitHubUser>>,
    stargazersLoadingState: Flow<Resource<List<GitHubUser>>>,
    setOwner: (String) -> Unit,
    setRepoName: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(context.getString(R.string.app_name))
                }
            )
        },
        backgroundColor = MaterialTheme.colors.background,
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val userState = user.collectAsState(initial = Resource.START()).value

                var ownerName by remember { mutableStateOf("") }
                var repoName by remember { mutableStateOf("") }

                SearchBox(label = context.getString(R.string.owner_name),
                    term = ownerName,
                    onChange = {
                        ownerName = it
                    },
                    onSearch = {
                        setOwner(ownerName)
                    })

                Spacer(modifier = Modifier.height(10.dp))

                when (userState) {
                    is Resource.START -> {}
                    is Resource.LOADING -> {
                        CircularProgressIndicator()
                    }
                    is Resource.NOTFOUND -> {
                        Text(context.getString(R.string.user_not_exists) + ": " + userState.message)
                    }
                    is Resource.NOCONNECTION -> {
                        Text(context.getString(R.string.no_connection))
                    }
                    is Resource.ERROR -> {
                        Text(context.getString(R.string.error) + ": " + userState.code)
                    }
                    is Resource.SUCCESS -> {
                        val userData = userState.data

                        UserCard(user = userData)

                        Spacer(modifier = Modifier.height(10.dp))

                        SearchBox(
                            label = context.getString(R.string.repo_name),
                            term = repoName,
                            onChange = {
                                repoName = it
                            },
                            onSearch = {
                                setRepoName(repoName)
                            })

                        Spacer(modifier = Modifier.height(10.dp))

                        StargazersList(
                            context,
                            stargazers.collectAsLazyPagingItems(),
                            stargazersLoadingState.collectAsState(
                                initial = Resource.START<Any>()
                            ).value
                        )
                    }
                }
            }
        }
    )
}