package at.aleb.githubstargazers

import at.aleb.githubstargazers.data.GitHubUser

object Samples {
    val userList = listOf(
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

    val user = GitHubUser(
        name = "schacon",
        avatarUrl = "https://avatars.githubusercontent.com/u/70?v=4"
    )
}