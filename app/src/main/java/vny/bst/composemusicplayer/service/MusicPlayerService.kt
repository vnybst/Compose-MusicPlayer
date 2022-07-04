package vny.bst.composemusicplayer.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.media.MediaBrowserServiceCompat
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import vny.bst.composemusicplayer.BuildConfig
import vny.bst.composemusicplayer.data.loadAllSongs
import vny.bst.composemusicplayer.storage.CMPStorage
import vny.bst.composemusicplayer.extensions.toMediaMetaData
import vny.bst.composemusicplayer.model.Songs
import vny.bst.composemusicplayer.player.CMPlayer
import vny.bst.composemusicplayer.utils.Constants

private val TAG = MusicPlayerService::class.java.simpleName

class MusicPlayerService : MediaBrowserServiceCompat() {

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var stateBuilder: PlaybackStateCompat.Builder
    private lateinit var mediaSessionConnector: MediaSessionConnector
    private lateinit var mediaItemList: List<Songs>

    private lateinit var cmPlayer: CMPlayer

    //Coroutine
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private var isForegroundService = false

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onCreate() {
        super.onCreate()

        serviceScope.launch {
            mediaItemList = loadAllSongs(baseContext)
        }

        // Build a PendingIntent that can be used to launch the UI.
        val sessionActivityPendingIntent =
            packageManager?.getLaunchIntentForPackage(packageName)?.let { sessionIntent ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PendingIntent.getActivity(
                        this,
                        0,
                        sessionIntent,
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )
                } else {
                    PendingIntent.getActivity(
                        this,
                        0,
                        sessionIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                }
            }

        mediaSession = MediaSessionCompat(baseContext, TAG).apply {
            setSessionActivity(sessionActivityPendingIntent)
            isActive = true
            // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
            stateBuilder = PlaybackStateCompat.Builder()
                .setActions(
                    PlaybackStateCompat.ACTION_PLAY
                            or PlaybackStateCompat.ACTION_PLAY_PAUSE
                )
            setPlaybackState(stateBuilder.build())
            setSessionToken(sessionToken)
        }
        // ExoPlayer will manage the MediaSession for us.
        mediaSessionConnector = MediaSessionConnector(mediaSession)
        mediaSessionConnector.setPlaybackPreparer(CMPlaybackPreparer())
        mediaSessionConnector.setQueueNavigator(CmpQueueNavigator(mediaSession))

        cmPlayer = CMPlayer(
            baseContext, mediaSession.sessionToken,
            PlayerNotificationListener(),
            stopForegroundService = { stop ->
                stopForeground(stop)
                isForegroundService = stop
            }, saveSongToRecent = { currentItemIndex, playerPosition ->
                saveRecentSongToStorage(currentItemIndex, playerPosition)
            }
        )
    }

    private fun saveRecentSongToStorage(currentItemIndex: Int, playerPosition: Long) {

        // Obtain the current song details *before* saving them on a separate thread, otherwise
        // the current player may have been unloaded by the time the save routine runs.
        if (mediaItemList.isEmpty()) {
            return
        }
        val description = mediaItemList.toMediaMetaData()[currentItemIndex].description

        serviceScope.launch {
            CMPStorage.getInstance(baseContext).storeRecentSong(
                description,
                playerPosition
            )
        }
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot {
        val rootExtras = Bundle().apply {
            putBoolean(CONTENT_STYLE_SUPPORTED, true)
            putInt(CONTENT_STYLE_BROWSABLE_HINT, CONTENT_STYLE_GRID)
            putInt(CONTENT_STYLE_PLAYABLE_HINT, CONTENT_STYLE_LIST)
        }
        return BrowserRoot(UAMP_BROWSABLE_ROOT, rootExtras)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<List<MediaBrowserCompat.MediaItem>>
    ) {
        if (parentId == UAMP_BROWSABLE_ROOT) {
            val children = loadAllSongs(baseContext).toMediaMetaData().map {
                MediaBrowserCompat.MediaItem(
                    it.description,
                    it.getLong(CMP_CONTENT_FLAG_PLAYABLE).toInt()
                )
            }
            result.sendResult(children)
        }
    }

    private inner class CMPlaybackPreparer : MediaSessionConnector.PlaybackPreparer {

        /**
         * UAMP supports preparing (and playing) from search, as well as media ID, so those
         * capabilities are declared here.
         *
         * TODO: Add support for ACTION_PREPARE and ACTION_PLAY, which mean "prepare/play something".
         */
        override fun getSupportedPrepareActions(): Long =
            PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID or
                    PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID or
                    PlaybackStateCompat.ACTION_PREPARE_FROM_SEARCH or
                    PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH

        override fun onPrepare(playWhenReady: Boolean) {
            val recentSong = CMPStorage.getInstance(baseContext).loadRecentSong() ?: return
            val description = recentSong.description.extras
            recentSong.mediaId?.let {
                onPrepareFromMediaId(
                    it,
                    playWhenReady,
                    description
                )
            }
        }

        override fun onPrepareFromMediaId(
            mediaId: String,
            playWhenReady: Boolean,
            extras: Bundle?
        ) {

            val itemToPlay: MediaMetadataCompat? = mediaItemList.toMediaMetaData().find { item ->
                item.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID) == mediaId
            }
            if (itemToPlay == null) {
                Log.w(TAG, "Content not found: MediaID=$mediaId")
                // TODO: Notify caller of the error.
            } else {

                val playbackStartPositionMs =
                    extras?.getLong(
                        Constants.CMP_PREFS_MEDIA_PLAYBACK_POSITION,
                        C.TIME_UNSET
                    ) ?: C.TIME_UNSET

                cmPlayer.preparePlaylist(
                    buildPlaylist(itemToPlay),
                    itemToPlay,
                    playWhenReady,
                    playbackStartPositionMs
                )
            }

        }

        /**
         * This method is used by the Google Assistant to respond to requests such as:
         * - Play Geisha from Wake Up on UAMP
         * - Play electronic music on UAMP
         * - Play music on UAMP
         *
         * For details on how search is handled, see [AbstractMusicSource.search].
         */
        override fun onPrepareFromSearch(query: String, playWhenReady: Boolean, extras: Bundle?) {
            /*mediaSource.whenReady {
                val metadataList = mediaSource.search(query, extras ?: Bundle.EMPTY)
                if (metadataList.isNotEmpty()) {
                    preparePlaylist(
                        metadataList,
                        metadataList[0],
                        playWhenReady,
                        playbackStartPositionMs = C.TIME_UNSET
                    )
                }
            }*/
        }

        override fun onPrepareFromUri(uri: Uri, playWhenReady: Boolean, extras: Bundle?) = Unit

        override fun onCommand(
            player: Player,
            command: String,
            extras: Bundle?,
            cb: ResultReceiver?
        ) = false

        /**
         * Builds a playlist based on a [MediaMetadataCompat].
         *
         * TODO: Support building a playlist by artist, genre, etc...
         *
         * @param item Item to base the playlist on.
         * @return a [List] of [MediaMetadataCompat] objects representing a playlist.
         */
        private fun buildPlaylist(item: MediaMetadataCompat): List<MediaMetadataCompat> =
            mediaItemList.toMediaMetaData().filter {
                it.getString(MediaMetadataCompat.METADATA_KEY_ALBUM) == item.getString(
                    MediaMetadataCompat.METADATA_KEY_ALBUM
                )
            }
    }

    override fun onDestroy() {
        super.onDestroy()

        serviceJob.cancel()
    }

    /**
     * Listen for notification events.
     */
    private inner class PlayerNotificationListener :
        PlayerNotificationManager.NotificationListener {
        override fun onNotificationPosted(
            notificationId: Int,
            notification: Notification,
            ongoing: Boolean
        ) {
            if (ongoing && !isForegroundService) {
                ContextCompat.startForegroundService(
                    applicationContext,
                    Intent(applicationContext, this@MusicPlayerService.javaClass)
                )

                startForeground(notificationId, notification)
                isForegroundService = true
            }
        }

        override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
            stopForeground(true)
            isForegroundService = false
            stopSelf()
        }
    }

    private inner class CmpQueueNavigator(
        mediaSession: MediaSessionCompat
    ) : TimelineQueueNavigator(mediaSession) {
        override fun getMediaDescription(player: Player, windowIndex: Int): MediaDescriptionCompat {
            if (windowIndex < mediaItemList.size) {
                return mediaItemList.toMediaMetaData()[windowIndex].description
            }
            return MediaDescriptionCompat.Builder().build()
        }
    }

}

/** Content styling constants */
private const val CONTENT_STYLE_BROWSABLE_HINT =
    "android.media.browse.CONTENT_STYLE_BROWSABLE_HINT"
private const val CONTENT_STYLE_PLAYABLE_HINT =
    "android.media.browse.CONTENT_STYLE_PLAYABLE_HINT"
private const val CONTENT_STYLE_SUPPORTED = "android.media.browse.CONTENT_STYLE_SUPPORTED"
private const val CONTENT_STYLE_LIST = 1
private const val CONTENT_STYLE_GRID = 2
const val UAMP_BROWSABLE_ROOT = "/"
const val CMP_CONTENT_FLAG_PLAYABLE = "${BuildConfig.APPLICATION_ID}.flag_playable"