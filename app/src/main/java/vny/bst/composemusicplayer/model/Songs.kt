package vny.bst.composemusicplayer.model

import android.net.Uri

data class Songs(
    val id: String? = null,
    val songName: String? = null,
    val songTitle: String? = null,
    val songArtist: String? = null,
    val songDuration: Long? = null,
    val songAlbum: String? = null,
    val songSize: Long? = null,
    val songData: String? = null,
    val albumArt: Uri? = null
)