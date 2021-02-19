package com.manna.ext

import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

fun ImageView.setImage(imageUrl: String) {
    Glide.with(this)
        .applyDefaultRequestOptions(
            RequestOptions.bitmapTransform(RoundedCorners(getRadius(this.width)))
        )
        .load(imageUrl)
        .into(this)
}

fun ImageView.setImage(imageUri: Uri) {
    Glide.with(this)
        .applyDefaultRequestOptions(
            RequestOptions.bitmapTransform(RoundedCorners(getRadius(this.width)))
        )
        .load(imageUri)
        .into(this)
}

fun getRadius(width: Int): Int {
    return (width * 0.4286).toInt()
}