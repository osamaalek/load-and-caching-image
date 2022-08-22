package com.osamaalek.etest

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.FileOutputStream
import java.net.URL

class CachingUtil {

    companion object{

        // save the image in internal storage
        fun storeBitmap (bitmap: Bitmap, context: Context) {
            val fileOutputStream: FileOutputStream = context.openFileOutput(Constants.TEMP_IMAGE_NAME, Context.MODE_PRIVATE)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
        }

        // load the cached image from internal storage
        fun loadBitmap(context: Context) : Bitmap? {
            return BitmapFactory.decodeFile(context.filesDir.absolutePath + "/" + Constants.TEMP_IMAGE_NAME)
        }

        // download an image with the url
        fun getBitmapFromURL() : Bitmap? {
            val url = URL(Constants.IMAGE_URL)
            return BitmapFactory.decodeStream(url.openConnection().getInputStream())
        }
    }
}