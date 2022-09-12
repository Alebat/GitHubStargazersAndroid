package at.aleb.githubstargazers

import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.paging.PagingData
import at.aleb.githubstargazers.ui.MainActivity
import at.aleb.githubstargazers.ui.MainContent
import at.aleb.githubstargazers.ui.theme.GitHubStargazersTheme
import at.aleb.githubstargazers.ui.vm.GitHubViewModel
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test

class MainActivityTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun test_searchBoxes() {
        composeTestRule.activity.setContent {
            GitHubStargazersTheme {
                MainContent(
                    composeTestRule.activity.baseContext,
                    flowOf(GitHubViewModel.UserState.SUCCESS(Samples.user)),
                    flowOf(PagingData.from(Samples.userListNoAvatar)),
                    flowOf(GitHubViewModel.StargazersState.SUCCESS),
                    {}, {}
                )
            }
        }

        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.owner_name)).assertIsDisplayed()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.repo_name)).assertIsDisplayed()
        composeTestRule.onNodeWithText(Samples.user.name).assertIsDisplayed()
    }
}