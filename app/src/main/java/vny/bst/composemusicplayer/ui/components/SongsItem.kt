package vny.bst.composemusicplayer.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vny.bst.composemusicplayer.R

@Composable
fun SongsItemView(
    imageId: Int,
    songName: String,
    songDescription: String,
    color: Color,
    clickListener: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .clickable {
                clickListener()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        SquareImageView(color)
        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(
                text = songName, style = TextStyle(
                    color = Color.Black,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = songDescription, style = TextStyle(
                    color = Color.Gray,
                    fontWeight = FontWeight.Normal
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(top = 5.dp)
                    .fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
fun PreViewSongItem() {
    SongsItemView(
        imageId = R.drawable.music_note,
        songName = "Piya Tu Ab To Aaja (Monica, Oh My Darling!)",
        color = Color.Red,
        songDescription = stringResource(
            id = R.string.song_description,
            "Asha Bhosle",
            "Download"
        )
    ) {

    }
}