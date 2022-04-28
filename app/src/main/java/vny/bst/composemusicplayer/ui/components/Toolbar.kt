package vny.bst.composemusicplayer.ui.components

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import vny.bst.composemusicplayer.R

@Composable
fun MusicToolbar(
    title: String,
    modifier: Modifier = Modifier,
    OnSearchClick: () -> Unit
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(text = title, color = Color.White)
        },
        actions = {
            IconButton(onClick = { OnSearchClick() }) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = stringResource(id = R.string.menu_search)
                )
            }
        }
    )
}

@Preview
@Composable
fun PreviewToolbar() {
    MusicToolbar("Music Player") {

    }
}