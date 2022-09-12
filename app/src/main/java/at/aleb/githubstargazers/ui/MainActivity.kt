package at.aleb.githubstargazers.ui

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import at.aleb.githubstargazers.R
import at.aleb.githubstargazers.data.GitHubUser
import at.aleb.githubstargazers.ui.theme.GitHubStargazersTheme
import at.aleb.githubstargazers.ui.vm.GitHubViewModel
import at.aleb.githubstargazers.ui.vm.GitHubViewModel.StargazersState
import at.aleb.githubstargazers.ui.vm.GitHubViewModel.UserState
import coil.compose.rememberAsyncImagePainter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: GitHubViewModel by viewModels()

        setContent {
            GitHubStargazersTheme {
                MainContent(
                    this,
                    viewModel.owner,
                    viewModel.stargazers,
                    viewModel.stargazersLoadingState,
                    { viewModel.setOwner(it) },
                    { viewModel.setRepoName(it) }
                )
            }
        }
    }
}

@Composable
fun MainContent(
    context: Context,
    user: Flow<UserState>,
    stargazers: Flow<PagingData<GitHubUser>>,
    stargazersLoadingState: Flow<StargazersState>,
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

                val userState = user.collectAsState(initial = UserState.START)
                when (userState.value) {
                    is UserState.START -> {}
                    is UserState.LOADING -> {
                        CircularProgressIndicator()
                    }
                    is UserState.NOTFOUND -> {
                        Text(context.getString(R.string.user_not_exists) + ": " + (userState.value as UserState.NOTFOUND).message)
                    }
                    is UserState.NOCONNECTION -> {
                        Text(context.getString(R.string.no_connection))
                    }
                    is UserState.ERROR -> {
                        Text(context.getString(R.string.error) + ": " + (userState.value as UserState.ERROR).code)
                    }
                    is UserState.SUCCESS -> {
                        val userData =
                            (userState.value as UserState.SUCCESS).data

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
                                initial = StargazersState.START
                            ),
                            userState
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun StargazersList(
    context: Context,
    lazyItems: LazyPagingItems<GitHubUser>,
    stargazersState: State<StargazersState>,
    userState: State<UserState>
) {
    if (lazyItems.loadState.refresh is LoadState.Loading)
        CircularProgressIndicator()
    else when (stargazersState.value) {
        is StargazersState.START -> {}
        is StargazersState.ERROR ->
            Text(context.getString(R.string.error) + ": " + (userState.value as UserState.ERROR).code)
        is StargazersState.NOCONNECTION ->
            Text(context.getString(R.string.no_connection))
        is StargazersState.NOTFOUND ->
            Text(context.getString(R.string.repo_not_found))
        is StargazersState.SUCCESS -> {
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

@Composable
fun SearchBox(
    label: String,
    term: String,
    onChange: (String) -> Unit,
    onSearch: (String) -> Unit
) {
    Row {
        TextField(
            label = {
                Text(label)
            },
            value = term,
            onValueChange = onChange,
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch(term) })
        )
    }
}


@Composable
fun UserCard(
    user: GitHubUser
) {
    Surface(
        elevation = 20.dp,
        modifier = Modifier
            .background(MaterialTheme.colors.surface)
            .padding(5.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = rememberAsyncImagePainter(user.avatarUrl),
                    contentDescription = stringResource(R.string.user_avatar),
                    modifier = Modifier.size(64.dp),
                    contentScale = ContentScale.Crop,
                )
                Text(user.name, Modifier.padding(8.dp))
            }
        }
    }
}