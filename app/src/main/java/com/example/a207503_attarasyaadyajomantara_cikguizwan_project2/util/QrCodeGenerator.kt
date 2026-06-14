package com.example.a207503_attarasyaadyajomantara_cikguizwan_project2.util

import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

/*
 * ─────────────────────────────────────────────────────────────────────────────
 *  PILLAR 4 helper — QR CODE GENERATION (ZXing)
 *
 *  Each shareable Kouleej is encoded as a QR using a custom payload format:
 *
 *      KOULEEJ:<id>
 *
 *  The QR scanner screen reads this string back and navigates straight to the
 *  matching quiz. Generation (here) and scanning (QrScannerScreen) are kept
 *  separate and clearly labelled for the Q&A.
 * ─────────────────────────────────────────────────────────────────────────────
 */
object QrCodeGenerator {

    /** Prefix that identifies our QR payloads so the scanner ignores other codes. */
    const val PAYLOAD_PREFIX = "KOULEEJ:"

    /** Build the QR payload string for a given local Kouleej id. */
    fun payloadFor(kouleejId: Int): String = "$PAYLOAD_PREFIX$kouleejId"

    /**
     * Parse a scanned QR string and return the Kouleej id, or null if the QR
     * isn't one of ours.
     */
    fun parseId(scanned: String): Int? =
        scanned.takeIf { it.startsWith(PAYLOAD_PREFIX) }
            ?.removePrefix(PAYLOAD_PREFIX)
            ?.toIntOrNull()

    /**
     * Render [content] into a square black-and-white QR [Bitmap] of [size] px.
     */
    fun generate(content: String, size: Int = 512): Bitmap {
        val bitMatrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size)
        val bitmap = createBitmap(size, size, Bitmap.Config.RGB_565)
        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap[x, y] = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
            }
        }
        return bitmap
    }
}
