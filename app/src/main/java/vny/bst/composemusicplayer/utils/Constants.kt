package vny.bst.composemusicplayer.utils

import android.net.Uri
import vny.bst.composemusicplayer.BuildConfig

object Constants {

    const val DIALOG_TYPE_PERMISSION_RATIONALE = "PERMISSION_RATIONALE_DIALOG"
    const val DIALOG_TYPE_PERMISSION_DENIED = "PERMISSION_DENIED_DIALOG"

    // Album uri
    val ALBUM_ART_BASE_URI = Uri
        .parse("content://media/external/audio/albumart")

    //shared prefs file name
    const val CMP_PREFERENCES_NAME = "${BuildConfig.APPLICATION_ID}.storage"

    // Shared Prefs keys
    const val CMP_PREFS_MEDIA_ID = "cmp.storage.media.id"
    const val CMP_PREFS_MEDIA_TITLE = "cmp.storage.media.title"
    const val CMP_PREFS_MEDIA_ARTIST = "cmp.storage.media.artist"
    const val CMP_PREFS_MEDIA_ALBUM = "cmp.storage.media.album"
    const val CMP_PREFS_MEDIA_DURATION = "cmp.storage.media.duration"
    const val CMP_PREFS_MEDIA_POSITION = "cmp.storage.media.position"
    const val CMP_PREFS_MEDIA_DATA = "cmp.storage.media.data"
    const val CMP_PREFS_MEDIA_ALBUM_ART = "cmp.storage.media.album_art"
    const val CMP_PREFS_MEDIA_PLAYBACK_POSITION = "cmp.storage.media.playback_position"

}