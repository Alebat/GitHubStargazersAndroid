package at.aleb.githubstargazers

import at.aleb.githubstargazers.data.dto.GitHubUserDto

object Samples {
    val userList = listOf(
        GitHubUserDto(
            name = "schacon",
            avatarUrl = "https://avatars.githubusercontent.com/u/70?v=4"
        ),
        GitHubUserDto(
            name = "adelcambre",
            avatarUrl = "https://avatars.githubusercontent.com/u/242?v=4"
        ),
        GitHubUserDto(
            name = "usergenic",
            avatarUrl = "https://avatars.githubusercontent.com/u/578?v=4"
        )
    )

    val user = GitHubUserDto(
        name = "schacon",
        avatarUrl = "https://avatars.githubusercontent.com/u/70?v=4"
    )
}