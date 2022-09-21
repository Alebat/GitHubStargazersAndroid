package at.aleb.githubstargazers.data.mapper

import at.aleb.githubstargazers.data.dto.GitHubUserDto
import at.aleb.githubstargazers.domain.model.GitHubUser

fun GitHubUserDto.toEntity(): GitHubUser =
    GitHubUser(
        name = name,
        avatarUrl = avatarUrl
    )
