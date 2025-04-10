package com.adityacodes.rtsp


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VideoViewModel(application: Application) : AndroidViewModel(application) {

    private val context = getApplication<Application>().applicationContext
    val player = VLCPlayerManager(context)

    private val _rtspUrl = MutableStateFlow("")
    val rtspUrl = _rtspUrl.asStateFlow()

    fun updateUrl(url: String) {
        _rtspUrl.value = url
    }

    fun play() {
        player.playStream(_rtspUrl.value)
    }

    fun record() {
        val output = context.getExternalFilesDir(null)?.absolutePath + "/stream_${System.currentTimeMillis()}.mp4"
        player.recordStream(_rtspUrl.value, output)
    }

    fun release() {
        player.release()
    }
}
