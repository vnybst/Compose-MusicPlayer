package vny.bst.composemusicplayer

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModelProvider
import vny.bst.composemusicplayer.data.loadAllSongs
import vny.bst.composemusicplayer.extensions.openSettings
import vny.bst.composemusicplayer.permissions.askStoragePermission
import vny.bst.composemusicplayer.permissions.hasStoragePermissionGranted
import vny.bst.composemusicplayer.permissions.showPermissionRationale
import vny.bst.composemusicplayer.ui.screens.main.MainScreen
import vny.bst.composemusicplayer.ui.theme.ComposeMusicPlayerTheme
import vny.bst.composemusicplayer.utils.Constants
import vny.bst.composemusicplayer.utils.Dialog
import vny.bst.composemusicplayer.viewmodels.CommonViewModel

class MainActivity : ComponentActivity() {

    private lateinit var commonViewModel: CommonViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViewModel()

        setContent {
            ComposeMusicPlayerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colors.background
                ) {
                    Scaffold {
                        MainScreen()

                        val dialogType by
                        commonViewModel.showPermissionDialogLiveData.collectAsState()

                        when (dialogType) {
                            Constants.DIALOG_TYPE_PERMISSION_DENIED -> {
                                Dialog(
                                    dialogTitle = stringResource(id = R.string.dialog_title_permission_declined),
                                    dialogText = stringResource(id = R.string.dialog_text_permission_declined),
                                    dismissRequest = {
                                        commonViewModel.setPermissionDialogType(null)
                                    }) {
                                    commonViewModel.setPermissionDialogType(null)
                                    openSettings()
                                }
                            }
                            Constants.DIALOG_TYPE_PERMISSION_RATIONALE -> {
                                Dialog(
                                    dialogTitle = stringResource(id = R.string.dialog_title_permission_rationale),
                                    dialogText = stringResource(id = R.string.dialog_text_permission_rationale),
                                    dismissRequest = {
                                        commonViewModel.setPermissionDialogType(null)
                                    }) {
                                    commonViewModel.setPermissionDialogType(null)
                                    checkStoragePermission()
                                }
                            }
                        }
                    }
                }
            }
        }
        checkStoragePermission()
    }

    private fun initViewModel() {
        commonViewModel = ViewModelProvider(this)[CommonViewModel::class.java]
    }

    private fun checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                !hasStoragePermissionGranted(this) -> {
                    askStoragePermission(permissionRequestLauncher)
                }
                else -> {
                    loadSongs()
                }
            }
        } else {
            loadSongs()
        }
    }

    private val permissionRequestLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { permissionGranted ->
            if (permissionGranted) {
                loadSongs()
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (showPermissionRationale(this)) {
                        commonViewModel.setPermissionDialogType(Constants.DIALOG_TYPE_PERMISSION_RATIONALE)
                    } else {
                        commonViewModel.setPermissionDialogType(Constants.DIALOG_TYPE_PERMISSION_DENIED)
                    }
                } else {
                    commonViewModel.setPermissionDialogType(Constants.DIALOG_TYPE_PERMISSION_DENIED)
                }
            }
        }

    private fun loadSongs() = loadAllSongs(this)

}
