package vny.bst.composemusicplayer.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import vny.bst.composemusicplayer.R

@Composable
fun SquareImageView(
    color: Color = Color.Cyan
) {
    Card(
        shape = RoundedCornerShape(5.dp),
        backgroundColor = color,
        elevation = 0.dp,
        modifier = Modifier
            .size(48.dp)
            .padding(5.dp)
    ) {
        Image(
            modifier = Modifier
                .padding(10.dp)
                .wrapContentSize(),
            painter = painterResource(id = R.drawable.music_note),
            contentDescription = stringResource(
                R.string.song_description
            )
        )
    }
}