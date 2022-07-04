package vny.bst.composemusicplayer.storage

import android.content.Context
import android.content.SharedPreferences
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import vny.bst.composemusicplayer.utils.Constants
import vny.bst.composemusicplayer.utils.Constants.CMP_PREFERENCES_NAME

class CMPStorage private constructor(context: Context) {

    private var sharedPreference: SharedPreferences? = null

    init {
        context.getSharedPreferences(
            CMP_PREFERENCES_NAME,
            Context.MODE_PRIVATE
        )
    }

    companion object {
        private var instance: CMPStorage? = null

        fun getInstance(context: Context): CMPStorage {
            return instance ?: synchronized(this) {
                CMPStorage(context).also {
                    instance = it
                }
            }
        }
    }

    private fun preferenceEditor() = sharedPreference?.edit()

    fun storeRecentSong(mediaDescriptionCompat: MediaDescriptionCompat, position: Long) {
        with(preferenceEditor()) {
            this?.putString(Constants.CMP_PREFS_MEDIA_ID, mediaDescriptionCompat.mediaId)
            this?.putString(
                Constants.CMP_PREFS_MEDIA_TITLE,
                mediaDescriptionCompat.title.toString()
            )
            this?.putString(
                Constants.CMP_PREFS_MEDIA_ARTIST,
                mediaDescriptionCompat.extras?.getString(MediaMetadataCompat.METADATA_KEY_ARTIST)
            )
            this?.putString(
                Constants.CMP_PREFS_MEDIA_ALBUM,
                mediaDescriptionCompat.extras?.getString(MediaMetadataCompat.METADATA_KEY_ALBUM)
            )
            mediaDescriptionCompat.extras?.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)?.let {
                this?.putLong(
                    Constants.CMP_PREFS_MEDIA_DURATION,
                    it
                )
            }
            this?.putLong(
                Constants.CMP_PREFS_MEDIA_POSITION,
                position
            )
            this?.putString(
                Constants.CMP_PREFS_MEDIA_ALBUM_ART,
                mediaDescriptionCompat.extras?.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI)
            )?.apply()
        }
    }

    fun loadRecentSong(): MediaBrowserCompat.MediaItem? {
        return if (sharedPreference?.getString(Constants.CMP_PREFS_MEDIA_ID, null) == null) {
            null
        } else
            MediaBrowserCompat.MediaItem(
                MediaDescriptionCompat.Builder().setMediaId(
                    sharedPreference?.getString(Constants.CMP_PREFS_MEDIA_ID, null)
                ).setTitle(
                    sharedPreference?.getString(Constants.CMP_PREFS_MEDIA_TITLE, null)
                ).setIconUri(
                    sharedPreference?.getString(Constants.CMP_PREFS_MEDIA_ALBUM_ART, null)?.toUri()
                ).setExtras(
                    bundleOf(
                        Pair(
                            Constants.CMP_PREFS_MEDIA_PLAYBACK_POSITION,
                            sharedPreference?.getLong(Constants.CMP_PREFS_MEDIA_POSITION, 0L)
                        )
                    )
                ).build(),
                MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
            )
    }

}