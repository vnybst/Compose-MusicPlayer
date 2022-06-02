package vny.bst.composemusicplayer.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import vny.bst.composemusicplayer.data.loadAllSongs
import vny.bst.composemusicplayer.model.Songs

@SuppressLint("StaticFieldLeak")
class HomeViewModel(private val context: Context) : ViewModel() {

    private val _songs = MutableStateFlow(listOf(Songs()))

    fun loadSongs() {
        _songs.value = loadAllSongs(context)
    }

    fun songs(): StateFlow<List<Songs>> {
        return _songs
    }

}