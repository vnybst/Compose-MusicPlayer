package vny.bst.composemusicplayer.player

import android.content.Context
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import android.widget.Toast
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.util.Util
import vny.bst.composemusicplayer.extensions.toMediaItem
import vny.bst.composemusicplayer.notification.CMPNotificationManager

//Media player(based on exo player) for playing songs
class CMPlayer(
    context: Context,
    sessionToken: MediaSessionCompat.Token,
    notificationListener: PlayerNotificationManager.NotificationListener,
    stopForegroundService: (stop: Boolean) -> Unit,
    saveSongToRecent: (currentItemIndex: Int, playerPosition: Long) -> Unit
) {

    private val TAG = CMPlayer::class.java.simpleName

    private var player: ExoPlayer? = null
    private var cmpNotification: CMPNotificationManager
    private var currentPlaylistItems: List<MediaMetadataCompat> = emptyList()
    private var currentMediaItemIndex = 0

    private val cmpAudioAttributes = AudioAttributes.Builder()
        .setContentType(C.CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)
        .build()

    init {
        cmpNotification = CMPNotificationManager(
            context, sessionToken, notificationListener
        )

        player = ExoPlayer.Builder(context).build().apply {
            setAudioAttributes(cmpAudioAttributes, true)
            setHandleAudioBecomingNoisy(true)
            addListener(playerListener)
        }

    }

    private val playerListener = object : Player.Listener {

        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_BUFFERING,
                Player.STATE_READY -> {
                    player?.let { cmpNotification.showNotification(it) }
                    if (playbackState == Player.STATE_READY) {
                        player?.currentMediaItemIndex?.let { currentMediaItemIndex ->
                            player?.currentPosition?.let { currentPosition ->
                                saveSongToRecent(
                                    currentMediaItemIndex,
                                    currentPosition
                                )
                            }
                        }
                    }
                }
                else -> cmpNotification.hideNotification()
            }
        }

        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
            super.onPlayWhenReadyChanged(playWhenReady, reason)
            if (!playWhenReady) {
                stopForegroundService(false)
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            Toast.makeText(context, "${error.message}", Toast.LENGTH_SHORT).show()
        }

        override fun onEvents(player: Player, events: Player.Events) {
            if (events.contains(Player.EVENT_POSITION_DISCONTINUITY)
                || events.contains(Player.EVENT_MEDIA_ITEM_TRANSITION)
                || events.contains(Player.EVENT_PLAY_WHEN_READY_CHANGED)
            ) {
                currentMediaItemIndex = if (currentPlaylistItems.isNotEmpty()) {
                    Util.constrainValue(
                        player.currentMediaItemIndex,
                        /* min= */ 0,
                        /* max= */ currentPlaylistItems.size - 1
                    )
                } else 0
            }
        }

    }

    fun setCurrentPlayMediaList(currentPlaylistItems: List<MediaMetadataCompat>) {
        this.currentPlaylistItems = currentPlaylistItems
    }

    /**
     * Load the supplied list of songs and the song to play into the current player.
     */
    fun preparePlaylist(
        metadataList: List<MediaMetadataCompat>,
        itemToPlay: MediaMetadataCompat?,
        playWhenReady: Boolean,
        playbackStartPositionMs: Long
    ) {
        Log.e(TAG, "preparePlayList $metadataList")
        // Since the playlist was probably based on some ordering (such as tracks
        // on an album), find which window index to play first so that the song the
        // user actually wants to hear plays first.
        val initialWindowIndex = if (itemToPlay == null) 0 else metadataList.indexOf(itemToPlay)
        currentPlaylistItems = metadataList

        player?.playWhenReady = playWhenReady
        player?.stop()
        // Set playlist and prepare.
        player?.setMediaItems(
            metadataList.map { it.toMediaItem() }, initialWindowIndex, playbackStartPositionMs
        )
        player?.prepare()
    }


}