package com.example.leobase

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import android.widget.ImageView
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * The class contains static data types convert functions
 */
class ConvertUtils {
    companion object{
        private const val compressionRatio = 90 //used for jpg convertion
        private val imageFormat = Bitmap.CompressFormat.JPEG //used for jpg convertion

        /**
         * The function converts image view content with type Drawable to db blob field /w type ByteArray
         * @param drawable image view content
         * @return db blob format
         */
        fun drawableToByteArray(drawable: Drawable?): ByteArray? {
            if (drawable != null) {
                val stream = ByteArrayOutputStream()
                (drawable as BitmapDrawable).bitmap.compress(
                    imageFormat, compressionRatio,stream)
                return stream.toByteArray()
            } else {
                return null
            }
        }

        /**
         * The function converts db blob field with type ByteArray to image view content
         * @param bytes db blob format
         * @return image view content
         */
        fun byteArrayToDrawable (bytes: ByteArray?): Drawable?{
            var drawable: Drawable? = null
            if (bytes != null) {
                val options = BitmapFactory.Options()
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size!!, options)
                drawable = BitmapDrawable(bitmap)
            }
            return drawable
        }

        /**
         * The function temporary (I guess) saves image view content to file
         * @param imageView content to be saved
         * @return Uri of file image's content was saved
         */
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
                bmp.compress(imageFormat, compressionRatio, out)
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