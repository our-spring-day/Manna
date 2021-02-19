package com.manna.ext

import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

fun ImageView.setImage(imageUrl: String) {
    post {
        val result = runCatching {
            Glide.with(this)
                .applyDefaultRequestOptions(
                    RequestOptions.bitmapTransform(RoundedCorners(getRadius(this.width)))
                )
                .load(imageUrl)
                .into(this)
        }

        if (result.isFailure) {
            result.exceptionOrNull()?.printStackTrace()
        }
    }
}

fun ImageView.setImage(imageUri: Uri) {
    post {
        val result = runCatching {
            Glide.with(this)
                .applyDefaultRequestOptions(
                    RequestOptions.bitmapTransform(RoundedCorners(getRadius(this.width)))
                )
                .load(imageUri)
                .into(this)
        }

        if (result.isFailure) {
            result.exceptionOrNull()?.printStackTrace()
        }
    }
}

fun getRadius(width: Int): Int {
    return (width * 0.4286).toInt()
}