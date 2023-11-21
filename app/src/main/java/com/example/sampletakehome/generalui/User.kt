package com.example.sampletakehome.generalui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.sampletakehome.repository.User

@Composable
fun User(
    modifier: Modifier = Modifier,
    user: User,
    onUserClicked: (Long) -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = 96.dp)
            .clickable { onUserClicked(user.id) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        UserImage(
            contentDescription= user.firstName,
            modifier = modifier.align(Alignment.CenterVertically),
            url = requireNotNull(user.imageUrl) { "Image url was null" })
        Spacer(modifier = modifier.width(12.dp))
        UserLabel(modifier = modifier.align(Alignment.CenterVertically), name = user.firstName)
    }
}

@Composable
fun UserImage(
    contentDescription: String,
    modifier: Modifier = Modifier,
    url: String
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current).data(url).crossfade(true).build(),
        placeholder = ColorPainter(Color.Gray),
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .padding(4.dp)
            .clip(CircleShape)
            .fillMaxHeight()
            .aspectRatio(ratio = 1f, matchHeightConstraintsFirst = true)
    )
}

@Composable
fun UserLabel(
    modifier: Modifier = Modifier,
    name: String
) {
    Text(
        modifier = modifier,
        text = name,
        style = MaterialTheme.typography.displaySmall
    )
}
