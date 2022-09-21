package at.aleb.githubstargazers.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import at.aleb.githubstargazers.R
import at.aleb.githubstargazers.domain.model.GitHubUser
import coil.compose.rememberAsyncImagePainter

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