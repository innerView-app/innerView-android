package com.dev.innerview.feature.home.component

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import com.dev.innerview.core.designsystem.component.InnerViewDialog
import com.dev.innerview.feature.home.R

@Composable
fun NotificationPermissionLauncher() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

    val context = LocalContext.current

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var shouldShowRationaleDialog by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        shouldShowRationaleDialog = shouldShowRequestPermissionRationale(
            context as Activity,
            Manifest.permission.POST_NOTIFICATIONS
        )
    }

    LaunchedEffect(hasPermission) {
        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    if (shouldShowRationaleDialog) {
        InnerViewDialog(
            titleText = stringResource(id = R.string.feature_home_permission_dialog_title),
            contentText = stringResource(id = R.string.feature_home_permission_dialog_content),
            confirmText = stringResource(id = R.string.feature_home_permission_dialog_approve),
            dismissText = stringResource(id = R.string.feature_home_dialog_dismiss),
            onDismissRequest = { shouldShowRationaleDialog = false },
            onConfirmRequest = {
                shouldShowRationaleDialog = false
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        )
    }
}
