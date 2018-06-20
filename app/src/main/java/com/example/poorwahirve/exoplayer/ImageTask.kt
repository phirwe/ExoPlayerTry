package com.example.poorwahirve.exoplayer

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.util.Log
import android.widget.ImageView
import java.io.InputStream
import java.net.URL
import android.content.Context
import android.graphics.drawable.BitmapDrawable

class ImageTask(val imageView : ImageView, val context : Context) : AsyncTask<String, Void, Bitmap>() {

    var url : String? = null
    var bitmap: Bitmap? = null

    override fun doInBackground(vararg params: String?): Bitmap? {
        url = params[0]
        try {
            val inputStream = URL(url).openStream()
            bitmap = BitmapFactory.decodeStream(inputStream)
        } catch (e : Exception) {
            Log.e("Error", e.message)
            e.printStackTrace()
        }

        return bitmap
    }

    override fun onPostExecute(result : Bitmap?) {
        if (result == null) {
            imageView.setImageDrawable(context.getDrawable(R.drawable.exo_controls_shuffle))
        }
        else {
            imageView.setImageBitmap(result)
        }
    }


}