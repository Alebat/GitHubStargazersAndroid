package at.aleb.githubstargazers

import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.paging.PagingData
import at.aleb.githubstargazers.data.mapper.toEntity
import at.aleb.githubstargazers.domain.Resource
import at.aleb.githubstargazers.ui.MainActivity
import at.aleb.githubstargazers.ui.compose.MainScreen
import at.aleb.githubstargazers.ui.theme.GitHubStargazersTheme
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
                MainScreen(
                    composeTestRule.activity.baseContext,
                    flowOf(Resource.SUCCESS(Samples.user.toEntity())),
                    flowOf(PagingData.from(Samples.userListNoAvatar.map { it.toEntity() })),
                    flowOf(Resource.SUCCESS(listOf())),
                    {}, {}
                )
            }
        }

        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.owner_name)).assertIsDisplayed()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.repo_name)).assertIsDisplayed()
        composeTestRule.onNodeWithText(Samples.user.name).assertIsDisplayed()
    }
}