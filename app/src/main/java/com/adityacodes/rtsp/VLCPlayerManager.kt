package com.adityacodes.rtsp

import android.content.Context
import android.net.Uri
import android.util.Log
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.util.VLCVideoLayout

class VLCPlayerManager(private val context: Context) {

    private val libVLC = LibVLC(context)
    private val mediaPlayer = MediaPlayer(libVLC)
    private var videoLayout: VLCVideoLayout? = null

    private val dir = context.getExternalFilesDir("recordings")
    val path = "${dir?.absolutePath}/stream_${System.currentTimeMillis()}.ts"

    init {
        if (dir != null && !dir.exists()) dir.mkdirs()
    }

    fun attachView(layout: VLCVideoLayout) {
        layout.post {
            videoLayout = layout
            mediaPlayer.attachViews(layout, null, false, false)
        }
    }

    fun playStream(url: String) {
        val media = Media(libVLC, Uri.parse(url))
        media.setHWDecoderEnabled(false, false)
        media.addOption(":network-caching=300")
        mediaPlayer.media = media
        mediaPlayer.play()

    }

    fun recordStream(url: String, outputPath: String = path) {
        val media = Media(libVLC, Uri.parse(url))
        media.setHWDecoderEnabled(true, false)

        media.addOption(":network-caching=1000")
        // Save and display simultaneously
        media.addOption(":sout=#duplicate{dst=display,dst=standard{access=file,mux=mp4,dst=$outputPath}}")
        media.addOption(":no-sout-all")
        media.addOption(":sout-keep")

        mediaPlayer.media = media
        mediaPlayer.play()
        Log.d("VLC", "Recording to $outputPath")
    }

    fun release() {
        try {
            mediaPlayer.stop()
            mediaPlayer.detachViews()
        } catch (e: Exception) {
            Log.e("VLC", "Release error: ${e.message}")
        } finally {
            mediaPlayer.release()
            libVLC.release()
        }
    }

}
