package vny.bst.composemusicplayer.data

import android.content.Context
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import vny.bst.composemusicplayer.model.Songs

fun loadAllSongs(context: Context): List<Songs> {
    val songs = ArrayList<Songs>()

    val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.DISPLAY_NAME,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.SIZE,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.DATA,
    )
    val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
//    val sortOrder = sql-order-by-clause

    val collection =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL
            )
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }

    context.contentResolver.query(
        collection,
        projection,
        selection,
        null,
        null
    )?.use { cursor ->
        Log.i("SongsCount", "${cursor.count}")
        while (cursor.moveToNext()) {
            // Use an ID column from the projection to get
            // a URI representing the media item itself.
            Log.i(
                "SongsList",
                cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME))
            )

            val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID))
            val songName =
                cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME))
            val songTitle =
                cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE))
            val songArtist =
                cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.ARTIST))
            val songAlbum =
                cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.ALBUM))
            val songData =
                cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA))
            val songDuration =
                cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION))
            val songSize = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE))

            songs.add(
                Songs(
                    id, songName, songTitle, songArtist, songDuration, songAlbum, songSize, songData
                )
            )
        }
    }
    return songs
}