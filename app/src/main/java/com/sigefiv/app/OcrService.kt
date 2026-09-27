package com.sigefiv.app

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class OcrService(
    private val context: Context
) {

    private val recognizer =
        TextRecognition.getClient(
            TextRecognizerOptions.DEFAULT_OPTIONS
        )

    fun reconocerTexto(
        uri: Uri,
        onSuccess: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        try {
            val image = InputImage.fromFilePath(
                context,
                uri
            )

            recognizer.process(image)
                .addOnSuccessListener { resultado ->
                    onSuccess(resultado.text)
                }
                .addOnFailureListener { error ->
                    onError(error)
                }

        } catch (e: Exception) {
            onError(e)
        }
    }

    fun cerrar() {
        recognizer.close()
    }
}