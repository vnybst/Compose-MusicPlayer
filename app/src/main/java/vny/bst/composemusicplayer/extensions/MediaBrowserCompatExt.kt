package vny.bst.composemusicplayer.extensions

import android.media.MediaMetadata.METADATA_KEY_ALBUM_ART_URI
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import androidx.core.net.toUri
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.util.MimeTypes
import vny.bst.composemusicplayer.model.Songs
import vny.bst.composemusicplayer.service.CMP_CONTENT_FLAG_PLAYABLE

fun List<Songs>.toMediaMetaData(): List<MediaMetadataCompat> {
    val mediaMetaDataList = ArrayList<MediaMetadataCompat>()
    this.forEach { song ->
        val mediaMetaData = song.songDuration?.let { duration ->
            MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, song.id)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.songTitle)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.songArtist)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, song.songAlbum)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration)
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ART, song.mediaAlbum())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, song.albumArt.toString())
                .putString(
                    MediaMetadataCompat.METADATA_KEY_MEDIA_URI,
                    song.id?.toMediaUri().toString()
                )
                .putLong(
                    CMP_CONTENT_FLAG_PLAYABLE,
                    MediaBrowserCompat.MediaItem.FLAG_PLAYABLE.toLong()
                )
                .build()
        }
        mediaMetaData?.let { metaData -> mediaMetaDataList.add(metaData) }
    }
    return mediaMetaDataList
}

fun MediaMetadataCompat.toMediaItem(): com.google.android.exoplayer2.MediaItem {
    return with(com.google.android.exoplayer2.MediaItem.Builder()) {
        setMediaId(getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI))
        setUri(getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI))
        setMimeType(MimeTypes.AUDIO_MPEG)
        setMediaMetadata(toMediaItemMetadata())
    }.build()
}

fun MediaMetadataCompat.toMediaItemMetadata(): com.google.android.exoplayer2.MediaMetadata {
    return with(MediaMetadata.Builder()) {
        setTitle(getString(MediaMetadataCompat.METADATA_KEY_TITLE))
        setDisplayTitle(getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE))
        setAlbumArtist(getString(MediaMetadataCompat.METADATA_KEY_ARTIST))
        setAlbumTitle(getString(MediaMetadataCompat.METADATA_KEY_ALBUM))
//        setTotalTrackCount(trackCount.toInt())
//        setDiscNumber(discNumber.toInt())
//        setWriter(writer)
        setArtworkUri(getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI).toUri())
        val extras = Bundle()
        extras.putLong(
            MediaMetadataCompat.METADATA_KEY_DURATION,
            getLong(MediaMetadataCompat.METADATA_KEY_DURATION)
        )
        setExtras(extras)
    }.build()
}