package com.dev.innerview.feature.record.camerax

import androidx.camera.core.CameraSelector
import androidx.camera.core.TorchState
import androidx.camera.view.CameraController

internal fun CameraController.changeTorchState() {
    cameraInfo?.hasFlashUnit()?.let { hasFlash ->
        if (hasFlash) {
            cameraControl?.enableTorch(cameraInfo?.torchState?.value != TorchState.ON)
        }
    }
}

internal fun CameraController.changeSelector() {
    cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
        CameraSelector.DEFAULT_FRONT_CAMERA
    } else {
        CameraSelector.DEFAULT_BACK_CAMERA
    }
}