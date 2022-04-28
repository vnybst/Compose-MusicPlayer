package vny.bst.composemusicplayer.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vny.bst.composemusicplayer.R
import vny.bst.composemusicplayer.utils.randomThumbColor

@Composable
fun NowPlayingView(
    songName: String,
    songDetail: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(0.dp),
            elevation = 10.dp
        ) {
            Column {
                LinearProgressIndicator(
                    modifier = Modifier
                        .height(3.dp)
                        .fillMaxWidth(),
                    progress = .5f,
                    color = Color.Red,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(10.dp)
                ) {
                    SquareImageView(randomThumbColor())
                    Column(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .weight(0.9f)
                    ) {
                        Text(
                            text = songName,
                            modifier = Modifier
                                .fillMaxWidth(),
                            style = TextStyle(
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp
                            )
                        )
                        Text(
                            modifier = Modifier
                                .fillMaxWidth(),
                            text = songDetail,
                            style = TextStyle(
                                fontWeight = FontWeight.Normal,
                                color = Color.Gray
                            )
                        )
                    }
                    Image(
                        modifier = Modifier
                            .weight(0.1f)
                            .size(50.dp),
                        imageVector = Icons.Rounded.PlayArrow,
                        contentDescription = stringResource(id = R.string.play_songs)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewNowPlaying() {
    NowPlayingView(
        songName = "One republic",
        songDetail = "Passenger"
    )
}