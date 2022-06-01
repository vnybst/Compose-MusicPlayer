package vny.bst.composemusicplayer.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Common ViewModel class that can be use for any common tasks
 * e.g open a dialog, navigation etc.
 * */
class CommonViewModel : ViewModel() {

    private val openDialogType = MutableStateFlow<String?>(null)

    fun setPermissionDialogType(dialogType: String?) {
        openDialogType.value = dialogType
    }

    val showPermissionDialogLiveData: StateFlow<String?> = openDialogType

}