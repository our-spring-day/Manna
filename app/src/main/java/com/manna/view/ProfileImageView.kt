package com.manna.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.manna.R
import com.manna.util.RoundedCornersTransformation
import com.manna.util.ViewUtil

class ProfileImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int = 0
): AppCompatImageView(context, attrs, defStyle) {

    private var cornerRadius = 0f

    init {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.ProfileImageView)
            cornerRadius = a.getDimension(R.styleable.ProfileImageView_corner_radius, 0f)
            a.recycle()
        }
    }

    fun setImage(imageUrl: String) {
        setImageUrl(imageUrl)
    }

    @BindingAdapter("bind:imageUrl")
    fun ProfileImageView.setImageUrl(imageUrl: String) {
        val builder = Glide.with(this)
            .applyDefaultRequestOptions(
                RequestOptions.bitmapTransform(
                    RoundedCornersTransformation(
                        ViewUtil.convertDpToPixel(context, cornerRadius),
                        0f,
                        "#5a6bff",
                        ViewUtil.convertDpToPixel(context, 3f),
                        RoundedCornersTransformation.CornerType.BORDER
                    )
                )
            )
            .load(imageUrl)
            .override(this.width, this.height)
            .into(this)
    }
}