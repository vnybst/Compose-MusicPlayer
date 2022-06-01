package vny.bst.composemusicplayer.utils

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import vny.bst.composemusicplayer.R

@Composable
fun Dialog(
    dialogTitle: String,
    dialogText: String,
    dismissRequest: () -> Unit,
    confirmRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = dismissRequest,
        title = { Text(text = dialogTitle) },
        text = { Text(text = dialogText) },
        confirmButton = {
            TextButton(onClick = {
                confirmRequest()
            }) {
                Text(text = stringResource(id = R.string.str_dialog_ok))
            }
        },
        dismissButton = {
            TextButton(onClick = {
                dismissRequest()
            }) {
                Text(text = stringResource(id = R.string.str_dialog_dismiss))
            }
        }
    )
}