package vny.bst.composemusicplayer.ui.screens.home

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import vny.bst.composemusicplayer.R
import vny.bst.composemusicplayer.data.dummySongs
import vny.bst.composemusicplayer.data.tabsList
import vny.bst.composemusicplayer.ui.components.MusicToolbar
import vny.bst.composemusicplayer.ui.components.NowPlayingView
import vny.bst.composemusicplayer.ui.components.SongsItemView
import vny.bst.composemusicplayer.utils.randomThumbColor

@Preview
@Composable
fun HomeScreen() {
    var tabIndex by remember { mutableStateOf(0) }
    Scaffold(modifier = Modifier.fillMaxSize()) {

        ConstraintLayout(modifier = Modifier.fillMaxSize()) {

            val (toolbar, tabs, shuffleControl, songsListView, nowPlaying) = createRefs()

            MusicToolbar(
                title = stringResource(id = R.string.app_name),
                modifier = Modifier.constrainAs(toolbar) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }) {

            }

            TabRow(selectedTabIndex = tabIndex, modifier = Modifier.constrainAs(tabs) {
                top.linkTo(toolbar.bottom)
                start.linkTo(toolbar.start)
                end.linkTo(toolbar.end)
            }) {
                tabsList().forEachIndexed { index: Int, title: String ->
                    Tab(selected = tabIndex == index, onClick = {
                        tabIndex = index
                    }, text = { Text(title) })
                }
            }

            ShuffleControl(
                Modifier
                    .wrapContentWidth()
                    .padding(start = 8.dp, top = 8.dp)
                    .constrainAs(shuffleControl) {
                        top.linkTo(tabs.bottom)
                        start.linkTo(tabs.start)
                    }
            )
            SongsListView(
                Modifier
                    .padding(top = 8.dp)
                    .constrainAs(songsListView) {
                        top.linkTo(shuffleControl.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    })

            NowPlayingView(
                songName = "One love",
                songDetail = "Blue",
                modifier = Modifier.constrainAs(nowPlaying) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )

        }
    }
}

@Composable
fun ShuffleControl(modifier: Modifier = Modifier) {
    TextButton(
        onClick = { /*TODO*/ },
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.Start
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_outline_shuffle_24),
                contentDescription = stringResource(
                    id = R.string.shuffle_all_songs
                ),
                colorFilter = ColorFilter.tint(Color.Black)
            )

            Text(
                text = stringResource(id = R.string.shuffle_all_songs),
                modifier = Modifier
                    .align(
                        Alignment.CenterVertically
                    )
                    .padding(start = 10.dp),
                style = TextStyle(
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            )
        }
    }
}

@Composable
fun SongsListView(modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        itemsIndexed(dummySongs()) { _, songs ->
            SongsItemView(
                imageId = R.drawable.music_note,
                songName = songs.songName,
                songDescription = songs.songDetail,
                color = randomThumbColor()
            ) {

            }
        }
    }
}