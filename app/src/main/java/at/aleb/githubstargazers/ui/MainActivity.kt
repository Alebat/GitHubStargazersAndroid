package at.aleb.githubstargazers.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import at.aleb.githubstargazers.ui.compose.MainScreen
import at.aleb.githubstargazers.ui.theme.GitHubStargazersTheme
import at.aleb.githubstargazers.ui.vm.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: MainViewModel by viewModels()

        setContent {
            GitHubStargazersTheme {
                MainScreen(
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