package com.yeyint.composebase.ui.common

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Camera capture destinations.
 *
 * `TakePicture` / `CaptureVideo` don't hand back a URI — they write into one we supply, so the
 * caller has to mint the file up front and remember it across the launch. Same approach HomeChat
 * uses, with the authority moved to `${applicationId}.provider`.
 */
object MediaCapture {

    /** Cache, not files: a capture is transient, and the OS may reclaim it under storage pressure. */
    private fun captureDir(context: Context): File =
        File(context.cacheDir, "captures").apply { mkdirs() }

    private fun stamp(): String =
        SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())

    fun newImageUri(context: Context): Uri =
        uriFor(context, File(captureDir(context), "IMG_${stamp()}.jpg"))

    fun newVideoUri(context: Context): Uri =
        uriFor(context, File(captureDir(context), "VID_${stamp()}.mp4"))

    private fun uriFor(context: Context, file: File): Uri =
        FileProvider.getUriForFile(
            context,
            // Must match the authority declared in AndroidManifest.xml.
            "${context.packageName}.provider",
            file
        )
}
