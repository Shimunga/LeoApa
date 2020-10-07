package com.example.leobase

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import android.widget.ImageView
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ConvertUtils {
        companion object{
            public fun drawableToByteArray(drawable: Drawable?): ByteArray? {
                if (drawable != null) {
                    val stream = ByteArrayOutputStream()
                    (drawable as BitmapDrawable).bitmap.compress(
                        Bitmap.CompressFormat.JPEG,90,stream)
                    return stream.toByteArray()
                } else {
                    return null
                }
            }

            fun getLocalBitmapUri(imageView: ImageView): Uri? {
                var bmp: Bitmap? = null
                bmp = if (imageView.drawable is BitmapDrawable) {
                    (imageView.drawable as BitmapDrawable).bitmap
                } else {
                    return null
                }
                // Store image to default external storage directory
                var bmpUri: Uri? = null
                try {
                    // Use methods on Context to access package-specific directories on external storage.
                    // This way, you don't need to request external read/write permission.
                    // See https://youtu.be/5xVh-7ywKpE?t=25m25s
                    val file = File(
                        imageView.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        "share_image_" + System.currentTimeMillis() + ".jpg"
                    )
                    val out = FileOutputStream(file)
                    bmp.compress(Bitmap.CompressFormat.JPEG, 90, out)
                    out.close()
                    // **Warning:** This will fail for API >= 24, use a FileProvider as shown below instead.
                    bmpUri = Uri.fromFile(file)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                return bmpUri
            }
        }
    }