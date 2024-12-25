package com.dicoding.picodiploma.loginwithanimation.ml

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

class OCRHelper(private val context: Context) {
    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    suspend fun runMLKitInference(originalBitmap: Bitmap): String {
        return suspendCancellableCoroutine { continuation ->
            val processedBitmap = preprocessImage(originalBitmap)
            val image = InputImage.fromBitmap(processedBitmap, 0)

            textRecognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val resultText = StringBuilder()
                    for (block in visionText.textBlocks) {
                        for (line in block.lines) {
                            for (element in line.elements) {
                                if (element.confidence ?: 0f > 0.5f) {
                                    resultText.append(element.text)
                                }
                            }
                        }
                    }
                    continuation.resume(resultText.toString()) { }
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }
    }

    private fun preprocessImage(bitmap: Bitmap): Bitmap {
        val scale = calculateOptimalScale(bitmap)
        val scaledBitmap = if (scale > 1.0f) {
            Bitmap.createScaledBitmap(
                bitmap,
                (bitmap.width * scale).toInt(),
                (bitmap.height * scale).toInt(),
                true
            )
        } else {
            bitmap
        }

        val matrix = Matrix()
        return Bitmap.createBitmap(
            scaledBitmap,
            0,
            0,
            scaledBitmap.width,
            scaledBitmap.height,
            matrix,
            true
        )
    }

    private fun calculateOptimalScale(bitmap: Bitmap): Float {
        val minDimension = 640
        val smallestDimension = minOf(bitmap.width, bitmap.height)
        return if (smallestDimension < minDimension) {
            minDimension.toFloat() / smallestDimension
        } else 1.0f
    }

    suspend fun runMLKitCharacterInference(bitmap: Bitmap): List<String> {
        val processedBitmap = preprocessImage(bitmap)
        val text = runMLKitInference(processedBitmap)
        return text.map { it.toString() }
    }

    fun validateWithLabels(text: String): String {
        return text.filter { char ->
            char.toString().uppercase() in labels
        }.toString()
    }

    companion object {
        val labels = setOf(
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
            "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z",
            "+", "-", "*", "/", "=", "^",
            "%", "(", ")", ".", ",", "@",
            "#", "!", "?", "_"
        )
    }
}