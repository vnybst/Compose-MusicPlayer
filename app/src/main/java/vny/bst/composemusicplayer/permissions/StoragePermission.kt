package vny.bst.composemusicplayer.permissions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

fun hasStoragePermissionGranted(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.READ_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED
}

@RequiresApi(Build.VERSION_CODES.M)
fun showPermissionRationale(context: Activity): Boolean {
    return context.shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
}

fun askStoragePermission(permissionResultLauncher: ActivityResultLauncher<String>) {
    permissionResultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
}