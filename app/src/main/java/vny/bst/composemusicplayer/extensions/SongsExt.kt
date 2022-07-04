package vny.bst.composemusicplayer.extensions

import android.content.ContentUris
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import vny.bst.composemusicplayer.model.Songs

fun Songs.mediaAlbum(): Bitmap? {
    val mediaMetadataRetriever = MediaMetadataRetriever()
    mediaMetadataRetriever.setDataSource(this.songData)
    val data = mediaMetadataRetriever.embeddedPicture
    return if (data != null) {
        BitmapFactory.decodeByteArray(data, 0, data.size)
    } else {
        null
    }
}

fun String.toMediaUri(): Uri {
    return ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, this.toLong())
}