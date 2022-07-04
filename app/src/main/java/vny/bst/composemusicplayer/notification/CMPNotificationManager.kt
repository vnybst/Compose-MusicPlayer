package vny.bst.composemusicplayer.notification

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import vny.bst.composemusicplayer.BuildConfig
import vny.bst.composemusicplayer.R

const val NOW_PLAYING_NOTIFICATION_ID = 0x123
const val NOW_PLAYING_CHANNEL_ID = "${BuildConfig.APPLICATION_ID}.notification"

class CMPNotificationManager(
    private val context: Context,
    sessionToken: MediaSessionCompat.Token,
    notificationListener: PlayerNotificationManager.NotificationListener
) {

    private val notificationManager: PlayerNotificationManager

    init {
        val mediaController = MediaControllerCompat(context, sessionToken)

        val builder = PlayerNotificationManager.Builder(
            context,
            NOW_PLAYING_NOTIFICATION_ID,
            NOW_PLAYING_CHANNEL_ID
        )
        with(builder) {
            setMediaDescriptionAdapter(DescriptionAdapter(mediaController))
            setNotificationListener(notificationListener)
            setChannelNameResourceId(R.string.notification_channel)
            setChannelDescriptionResourceId(R.string.notification_channel_description)
        }
        notificationManager = builder.build()
        notificationManager.setMediaSessionToken(sessionToken)
        notificationManager.setSmallIcon(R.mipmap.ic_launcher_round)
        notificationManager.setUseRewindAction(false)
        notificationManager.setUseFastForwardAction(false)
    }

    fun showNotification(player: ExoPlayer) {
        notificationManager.setPlayer(player)
    }

    fun hideNotification() {
        notificationManager.setPlayer(null)
    }

}

class DescriptionAdapter(private val mediaController: MediaControllerCompat) :
    PlayerNotificationManager.MediaDescriptionAdapter {
    override fun getCurrentContentTitle(player: Player): CharSequence =
        mediaController.metadata.description.title.toString()

    override fun createCurrentContentIntent(player: Player): PendingIntent? =
        mediaController.sessionActivity

    override fun getCurrentContentText(player: Player): CharSequence =
        mediaController.metadata.description.title.toString()

    override fun getCurrentLargeIcon(
        player: Player,
        callback: PlayerNotificationManager.BitmapCallback
    ): Bitmap? = mediaController.metadata.description.iconBitmap

}
