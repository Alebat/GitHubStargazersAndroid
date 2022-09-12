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
                    Samples.jsonUser
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
                    Samples.jsonStargazers
                )
        )

        runBlocking {
            val actual = svc.getStargazers("", "", 1)

            val expected = Samples.userList

            assertEquals(expected, actual)
        }
    }
}