package com.example.randomuser.util

import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.LifecycleOwner

class ImagePicker(
    activityResultRegistry: ActivityResultRegistry,
    lifecycleOwner: LifecycleOwner,
    callback: (imageUri: Uri?) -> Unit
) {
    private val getImage: ActivityResultLauncher<String> =
        activityResultRegistry.register(
            "image",
            lifecycleOwner,
            ActivityResultContracts.GetContent(),
            callback
        )

    fun pickImage() {
        getImage.launch("image/*")
    }
}

