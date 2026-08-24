package com.example.rentmanagement.ui.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController

/**
 * Tapping anywhere on a composable with this modifier (that isn't itself
 * consumed by a child, e.g. a button or text field) clears focus and hides
 * the on-screen keyboard. Apply to the outer scrollable container of a form.
 */
@Composable
fun Modifier.dismissKeyboardOnTap(): Modifier {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    return this.pointerInput(Unit) {
        detectTapGestures(onTap = {
            keyboardController?.hide()
            focusManager.clearFocus()
        })
    }
}
