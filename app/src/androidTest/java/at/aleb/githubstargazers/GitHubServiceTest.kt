package at.aleb.githubstargazers

import androidx.test.ext.junit.runners.AndroidJUnit4
import at.aleb.githubstargazers.data.GitHubUser
import at.aleb.githubstargazers.data.network.GitHubService
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


@RunWith(AndroidJUnit4::class)
class GitHubServiceTest {

    private val mockWebServer = MockWebServer()

    private val client = OkHttpClient.Builder()
        .connectTimeout(1, TimeUnit.SECONDS)
        .readTimeout(1, TimeUnit.SECONDS)
        .writeTimeout(1, TimeUnit.SECONDS)
        .build()

    private val svc = Retrofit.Builder()
        .baseUrl(mockWebServer.url("/"))
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(GitHubService::class.java)

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun should_fetch_user() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(
                    "{\n" +
                            "  \"login\": \"octocat\",\n" +
                            "  \"id\": 1,\n" +
                            "  \"node_id\": \"MDQ6VXNlcjE=\",\n" +
                            "  \"avatar_url\": \"https://github.com/images/error/octocat_happy.gif\",\n" +
                            "  \"gravatar_id\": \"\",\n" +
                            "  \"url\": \"https://api.github.com/users/octocat\",\n" +
                            "  \"html_url\": \"https://github.com/octocat\",\n" +
                            "  \"followers_url\": \"https://api.github.com/users/octocat/followers\",\n" +
                            "  \"following_url\": \"https://api.github.com/users/octocat/following{/other_user}\",\n" +
                            "  \"gists_url\": \"https://api.github.com/users/octocat/gists{/gist_id}\",\n" +
                            "  \"starred_url\": \"https://api.github.com/users/octocat/starred{/owner}{/repo}\",\n" +
                            "  \"subscriptions_url\": \"https://api.github.com/users/octocat/subscriptions\",\n" +
                            "  \"organizations_url\": \"https://api.github.com/users/octocat/orgs\",\n" +
                            "  \"repos_url\": \"https://api.github.com/users/octocat/repos\",\n" +
                            "  \"events_url\": \"https://api.github.com/users/octocat/events{/privacy}\",\n" +
                            "  \"received_events_url\": \"https://api.github.com/users/octocat/received_events\",\n" +
                            "  \"type\": \"User\",\n" +
                            "  \"site_admin\": false,\n" +
                            "  \"name\": \"monalisa octocat\",\n" +
                            "  \"company\": \"GitHub\",\n" +
                            "  \"blog\": \"https://github.com/blog\",\n" +
                            "  \"location\": \"San Francisco\",\n" +
                            "  \"email\": \"octocat@github.com\",\n" +
                            "  \"hireable\": false,\n" +
                            "  \"bio\": \"There once was...\",\n" +
                            "  \"twitter_username\": \"monatheoctocat\",\n" +
                            "  \"public_repos\": 2,\n" +
                            "  \"public_gists\": 1,\n" +
                            "  \"followers\": 20,\n" +
                            "  \"following\": 0,\n" +
                            "  \"created_at\": \"2008-01-14T04:33:35Z\",\n" +
                            "  \"updated_at\": \"2008-01-14T04:33:35Z\"\n" +
                            "}"
                )
        )

        runBlocking {
            val actual = svc.getUser("")

            val expected =
                GitHubUser(
                    name = "octocat",
                    avatarUrl = "https://github.com/images/error/octocat_happy.gif"
                )

            assertEquals(expected, actual)
        }
    }

    @Test
    fun should_fetch_stargazers() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(
                    "[\n" +
                            "  {\n" +
                            "    \"login\": \"schacon\",\n" +
                            "    \"id\": 70,\n" +
                            "    \"node_id\": \"MDQ6VXNlcjcw\",\n" +
                            "    \"avatar_url\": \"https://avatars.githubusercontent.com/u/70?v=4\",\n" +
                            "    \"gravatar_id\": \"\",\n" +
                            "    \"url\": \"https://api.github.com/users/schacon\",\n" +
                            "    \"html_url\": \"https://github.com/schacon\",\n" +
                            "    \"followers_url\": \"https://api.github.com/users/schacon/followers\",\n" +
                            "    \"following_url\": \"https://api.github.com/users/schacon/following{/other_user}\",\n" +
                            "    \"gists_url\": \"https://api.github.com/users/schacon/gists{/gist_id}\",\n" +
                            "    \"starred_url\": \"https://api.github.com/users/schacon/starred{/owner}{/repo}\",\n" +
                            "    \"subscriptions_url\": \"https://api.github.com/users/schacon/subscriptions\",\n" +
                            "    \"organizations_url\": \"https://api.github.com/users/schacon/orgs\",\n" +
                            "    \"repos_url\": \"https://api.github.com/users/schacon/repos\",\n" +
                            "    \"events_url\": \"https://api.github.com/users/schacon/events{/privacy}\",\n" +
                            "    \"received_events_url\": \"https://api.github.com/users/schacon/received_events\",\n" +
                            "    \"type\": \"User\",\n" +
                            "    \"site_admin\": false\n" +
                            "  },\n" +
                            "  {\n" +
                            "    \"login\": \"adelcambre\",\n" +
                            "    \"id\": 242,\n" +
                            "    \"node_id\": \"MDQ6VXNlcjI0Mg==\",\n" +
                            "    \"avatar_url\": \"https://avatars.githubusercontent.com/u/242?v=4\",\n" +
                            "    \"gravatar_id\": \"\",\n" +
                            "    \"url\": \"https://api.github.com/users/adelcambre\",\n" +
                            "    \"html_url\": \"https://github.com/adelcambre\",\n" +
                            "    \"followers_url\": \"https://api.github.com/users/adelcambre/followers\",\n" +
                            "    \"following_url\": \"https://api.github.com/users/adelcambre/following{/other_user}\",\n" +
                            "    \"gists_url\": \"https://api.github.com/users/adelcambre/gists{/gist_id}\",\n" +
                            "    \"starred_url\": \"https://api.github.com/users/adelcambre/starred{/owner}{/repo}\",\n" +
                            "    \"subscriptions_url\": \"https://api.github.com/users/adelcambre/subscriptions\",\n" +
                            "    \"organizations_url\": \"https://api.github.com/users/adelcambre/orgs\",\n" +
                            "    \"repos_url\": \"https://api.github.com/users/adelcambre/repos\",\n" +
                            "    \"events_url\": \"https://api.github.com/users/adelcambre/events{/privacy}\",\n" +
                            "    \"received_events_url\": \"https://api.github.com/users/adelcambre/received_events\",\n" +
                            "    \"type\": \"User\",\n" +
                            "    \"site_admin\": false\n" +
                            "  },\n" +
                            "  {\n" +
                            "    \"login\": \"usergenic\",\n" +
                            "    \"id\": 578,\n" +
                            "    \"node_id\": \"MDQ6VXNlcjU3OA==\",\n" +
                            "    \"avatar_url\": \"https://avatars.githubusercontent.com/u/578?v=4\",\n" +
                            "    \"gravatar_id\": \"\",\n" +
                            "    \"url\": \"https://api.github.com/users/usergenic\",\n" +
                            "    \"html_url\": \"https://github.com/usergenic\",\n" +
                            "    \"followers_url\": \"https://api.github.com/users/usergenic/followers\",\n" +
                            "    \"following_url\": \"https://api.github.com/users/usergenic/following{/other_user}\",\n" +
                            "    \"gists_url\": \"https://api.github.com/users/usergenic/gists{/gist_id}\",\n" +
                            "    \"starred_url\": \"https://api.github.com/users/usergenic/starred{/owner}{/repo}\",\n" +
                            "    \"subscriptions_url\": \"https://api.github.com/users/usergenic/subscriptions\",\n" +
                            "    \"organizations_url\": \"https://api.github.com/users/usergenic/orgs\",\n" +
                            "    \"repos_url\": \"https://api.github.com/users/usergenic/repos\",\n" +
                            "    \"events_url\": \"https://api.github.com/users/usergenic/events{/privacy}\",\n" +
                            "    \"received_events_url\": \"https://api.github.com/users/usergenic/received_events\",\n" +
                            "    \"type\": \"User\",\n" +
                            "    \"site_admin\": false\n" +
                            "  }" +
                            "]"
                )
        )

        runBlocking {
            val actual = svc.getStargazers("", "", 1)

            val expected = listOf(
                GitHubUser(
                    name = "schacon",
                    avatarUrl = "https://avatars.githubusercontent.com/u/70?v=4"
                ),
                GitHubUser(
                    name = "adelcambre",
                    avatarUrl = "https://avatars.githubusercontent.com/u/242?v=4"
                ),
                GitHubUser(
                    name = "usergenic",
                    avatarUrl = "https://avatars.githubusercontent.com/u/578?v=4"
                )
            )

            assertEquals(expected, actual)
        }
    }
}