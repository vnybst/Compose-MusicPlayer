package vny.bst.composemusicplayer.utils

import androidx.compose.ui.graphics.Color
import java.util.*

fun randomThumbColor(): Color {
    val rnd = Random()
    return Color(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
}