package com.dev.innerview.feature.record

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dev.innerview.core.designsystem.component.InnerViewDialog
import com.dev.innerview.core.designsystem.component.InnerViewTopAppBar
import com.dev.innerview.core.designsystem.component.TopAppBarNavigationType
import com.dev.innerview.core.designsystem.component.appBarSize
import com.dev.innerview.core.designsystem.theme.InnerViewTheme
import com.dev.innerview.feature.record.camerax.CameraScreen
import com.dev.innerview.feature.record.model.FilmingUiEvent
import com.dev.innerview.feature.record.model.FilmingUiState
import com.dev.innerview.feature.record.model.PermissionState
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun FilmingScreen(
    innerViewId: String,
    interviewGroupId: Int,
    question: String,
    padding: PaddingValues,
    onBackClick: () -> Unit,
    navigationToEdit: (String, Int, String) -> Unit,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
    viewModel: FilmingViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val filmingUiState by viewModel.filmingUiState.collectAsStateWithLifecycle()

    val requiredPermissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)

    val multiplePermissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.all { it }) {
            viewModel.updatePermissionsState(PermissionState.Granted)
        } else {
            val shouldShowRationale = permissions.keys.any {
                shouldShowRequestPermissionRationale(context as Activity, it)
            }
            viewModel.updatePermissionsState(PermissionState.Denied)
            if (shouldShowRationale) {
                viewModel.selectPermissionDenied()
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    val hasRequiredPermissions = requiredPermissions.all {
                        ContextCompat.checkSelfPermission(
                            context,
                            it
                        ) == PackageManager.PERMISSION_GRANTED
                    }
                    if (!hasRequiredPermissions) {
                        multiplePermissionsLauncher.launch(requiredPermissions)
                    } else {
                        viewModel.updatePermissionsState(PermissionState.Granted)
                    }
                }

                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.errorFlow.collectLatest { throwable -> onShowErrorSnackBar(throwable) }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchFilmingUiState(innerViewId, interviewGroupId, question)
        viewModel.uiEventFlow.collectLatest { event ->
            when (event) {
                FilmingUiEvent.NavigateToEdit -> {
                    navigationToEdit(innerViewId, interviewGroupId, question)
                }
            }
        }
    }

    FilmingContent(
        filmingUiState = filmingUiState,
        question = question,
        padding = padding,
        onBackClick = onBackClick,
        addInnerProject = { viewModel.addInnerProject(innerViewId, interviewGroupId, question) },
        startRecording = viewModel::startRecording,
        resetRecording = viewModel::resetRecording,
        pauseRecording = viewModel::pauseRecording,
        resumeRecording = viewModel::resumeRecording,
        updateRecordingState = viewModel::updateRecordingState,
        updateFlashState = viewModel::updateFlashState,
        onGoToAppSettings = { context.openAppSettings() }
    )

    if (filmingUiState.isPermissionDialogVisible) {
        InnerViewDialog(
            titleText = stringResource(R.string.feature_record_permission_dialog_title),
            contentText = stringResource(R.string.feature_record_permission_dialog_content),
            confirmText = stringResource(R.string.feature_record_dialog_confirm),
            dismissText = stringResource(R.string.feature_record_dialog_dismiss),
            onDismissRequest = { viewModel.selectPermissionDenied() },
            onConfirmRequest = {
                multiplePermissionsLauncher.launch(requiredPermissions)
                viewModel.selectPermissionDenied()
            }
        )
    }
}

@Composable
private fun FilmingContent(
    filmingUiState: FilmingUiState,
    question: String,
    padding: PaddingValues,
    onBackClick: () -> Unit,
    onGoToAppSettings: () -> Unit,
    startRecording: () -> Unit,
    resetRecording: () -> Unit,
    pauseRecording: () -> Unit,
    resumeRecording: () -> Unit,
    updateFlashState: (Boolean) -> Unit,
    updateRecordingState: (Long, Long) -> Unit,
    addInnerProject: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
    ) {
        InnerViewTopAppBar(
            title = question,
            navigationType = TopAppBarNavigationType.Back,
            onNavigationClick = onBackClick,
        )

        Box(
            modifier = Modifier
                .padding(top = appBarSize)
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            when (filmingUiState.permissionState) {
                is PermissionState.Loading -> {
                    CircularProgressIndicator()
                }

                is PermissionState.Denied -> {
                    Text(
                        modifier = Modifier.clickable { onGoToAppSettings() },
                        textAlign = TextAlign.Center,
                        text = stringResource(R.string.feature_record_permission_denied_text),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.onTertiary,
                            lineHeight = 18.sp
                        )
                    )
                }

                is PermissionState.Granted -> {
                    CameraScreen(
                        recordingState = filmingUiState.recordingState,
                        outputFileName = filmingUiState.outputFileName,
                        modifier = Modifier.fillMaxSize(),
                        startRecording = startRecording,
                        resetRecording = resetRecording,
                        pauseRecording = pauseRecording,
                        resumeRecording = resumeRecording,
                        updateFlashState = updateFlashState,
                        updateRecordingState = updateRecordingState,
                        addInnerProject = addInnerProject
                    )
                }
            }
        }
    }
}

private fun Context.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also { startActivity(it) }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun FilmingContentPreview() {
    InnerViewTheme {
        FilmingContent(
            filmingUiState = FilmingUiState(
                permissionState = PermissionState.Granted,
            ),
            question = "question",
            padding = PaddingValues(),
            onBackClick = {},
            onGoToAppSettings = {},
            addInnerProject = {},
            startRecording = {},
            resetRecording = {},
            pauseRecording = {},
            resumeRecording = {},
            updateFlashState = {},
            updateRecordingState = { _, _ -> },
        )
    }
}