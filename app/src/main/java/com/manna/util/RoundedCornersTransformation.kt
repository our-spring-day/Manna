package com.manna.util

import android.graphics.*
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest


class RoundedCornersTransformation @JvmOverloads constructor(
    private val radius: Float,
    private val margin: Float,
    private val color: String = "",
    private val border: Float = 0f,
    private val cornerType: CornerType = CornerType.ALL
) : BitmapTransformation() {

    private val mDiameter: Int = (radius * 2).toInt()

    enum class CornerType {
        ALL,
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT,
        TOP, BOTTOM, LEFT, RIGHT,
        OTHER_TOP_LEFT, OTHER_TOP_RIGHT, OTHER_BOTTOM_LEFT, OTHER_BOTTOM_RIGHT,
        DIAGONAL_FROM_TOP_LEFT, DIAGONAL_FROM_TOP_RIGHT, BORDER
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {

    }
    override fun transform(
        pool: BitmapPool, toTransform: Bitmap, outWidth: Int,
        outHeight: Int
    ): Bitmap {
        val width = toTransform.width
        val height = toTransform.height

        val bitmap =
            pool.get(width, height, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.isAntiAlias = true
        paint.shader = BitmapShader(toTransform, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        drawRoundRect(canvas, paint, width.toFloat(), height.toFloat())

        return bitmap
    }

    private fun drawRoundRect(canvas: Canvas, paint: Paint, width: Float, height: Float) {
        val right = width - margin
        val bottom = height - margin

        when (cornerType) {
            CornerType.ALL -> canvas.drawRoundRect(
                RectF(
                    margin,
                    margin,
                    right,
                    bottom
                ), radius, radius, paint
            )
            CornerType.TOP_LEFT -> drawTopLeftRoundRect(
                canvas,
                paint,
                right,
                bottom
            )
            CornerType.TOP_RIGHT -> drawTopRightRoundRect(
                canvas,
                paint,
                right,
                bottom
            )
            CornerType.BOTTOM_LEFT -> drawBottomLeftRoundRect(
                canvas,
                paint,
                right,
                bottom
            )
            CornerType.BOTTOM_RIGHT -> drawBottomRightRoundRect(
                canvas,
                paint,
                right,
                bottom
            )
            CornerType.TOP -> drawTopRoundRect(
                canvas,
                paint,
                right,
                bottom
            )
            CornerType.BOTTOM -> drawBottomRoundRect(
                canvas,
                paint,
                right,
                bottom
            )
            CornerType.LEFT -> drawLeftRoundRect(
                canvas,
                paint,
                right,
                bottom
            )
            CornerType.RIGHT -> drawRightRoundRect(
                canvas,
                paint,
                right,
                bottom
            )
            CornerType.OTHER_TOP_LEFT -> drawOtherTopLeftRoundRect(
                canvas,
                paint,
                right,
                bottom
            )
            CornerType.OTHER_TOP_RIGHT -> drawOtherTopRightRoundRect(
                canvas,
                paint,
                right,
                bottom
            )
            CornerType.OTHER_BOTTOM_LEFT -> drawOtherBottomLeftRoundRect(
                canvas,
                paint,
                right,
                bottom
            )
            CornerType.OTHER_BOTTOM_RIGHT -> drawOtherBottomRightRoundRect(
                canvas,
                paint,
                right,
                bottom
            )
            CornerType.DIAGONAL_FROM_TOP_LEFT -> drawDiagonalFromTopLeftRoundRect(
                canvas,
                paint,
                right,
                bottom
            )
            CornerType.DIAGONAL_FROM_TOP_RIGHT -> drawDiagonalFromTopRightRoundRect(
                canvas,
                paint,
                right,
                bottom
            )
            CornerType.BORDER -> drawBorder(canvas, paint, right, bottom)
            else -> canvas.drawRoundRect(
                RectF(margin, margin, right, bottom),
                radius,
                radius,
                paint
            )
        }
    }

    private fun drawTopLeftRoundRect(canvas: Canvas, paint: Paint, right: Float, bottom: Float) {
        canvas.drawRoundRect(
            RectF(
                margin,
                margin,
                margin + mDiameter,
                margin + mDiameter
            ),
            radius, radius, paint
        )
        canvas.drawRect(
            RectF(
                margin,
                margin + radius,
                margin + radius,
                bottom
            ), paint
        )
        canvas.drawRect(
            RectF(margin + radius, margin, right, bottom),
            paint
        )
    }

    private fun drawTopRightRoundRect(canvas: Canvas, paint: Paint, right: Float, bottom: Float) {
        canvas.drawRoundRect(
            RectF(right - mDiameter, margin, right, margin + mDiameter),
            radius,
            radius,
            paint
        )
        canvas.drawRect(
            RectF(margin, margin, right - radius, bottom),
            paint
        )
        canvas.drawRect(
            RectF(
                right - radius,
                margin + radius,
                right,
                bottom
            ), paint
        )
    }

    private fun drawBottomLeftRoundRect(canvas: Canvas, paint: Paint, right: Float, bottom: Float) {
        canvas.drawRoundRect(
            RectF(margin, bottom - mDiameter, margin + mDiameter, bottom),
            radius, radius, paint
        )
        canvas.drawRect(
            RectF(
                margin,
                margin,
                margin + mDiameter,
                bottom - radius
            ), paint
        )
        canvas.drawRect(
            RectF(margin + radius, margin, right, bottom),
            paint
        )
    }

    private fun drawBottomRightRoundRect(
        canvas: Canvas,
        paint: Paint,
        right: Float,
        bottom: Float
    ) {
        canvas.drawRoundRect(
            RectF(right - mDiameter, bottom - mDiameter, right, bottom), radius,
            radius, paint
        )
        canvas.drawRect(
            RectF(margin, margin, right - radius, bottom),
            paint
        )
        canvas.drawRect(
            RectF(right - radius, margin, right, bottom - radius),
            paint
        )
    }

    private fun drawTopRoundRect(canvas: Canvas, paint: Paint, right: Float, bottom: Float) {
        canvas.drawRoundRect(
            RectF(margin, margin, right, bottom),
            radius,
            radius,
            paint
        )
        canvas.drawRect(
            RectF(margin, margin + radius, right, bottom),
            paint
        )
    }

    private fun drawBottomRoundRect(canvas: Canvas, paint: Paint, right: Float, bottom: Float) {
        canvas.drawRoundRect(
            RectF(margin, bottom - mDiameter, right, bottom),
            radius,
            radius,
            paint
        )
        canvas.drawRect(RectF(margin, margin, right, bottom - radius), paint)
    }

    private fun drawLeftRoundRect(canvas: Canvas, paint: Paint, right: Float, bottom: Float) {
        canvas.drawRoundRect(
            RectF(margin, margin, margin + mDiameter, bottom),
            radius,
            radius,
            paint
        )
        canvas.drawRect(
            RectF(margin + radius, margin, right, bottom),
            paint
        )
    }

    private fun drawRightRoundRect(canvas: Canvas, paint: Paint, right: Float, bottom: Float) {
        canvas.drawRoundRect(
            RectF(right - mDiameter, margin, right, bottom),
            radius,
            radius,
            paint
        )
        canvas.drawRect(
            RectF(margin, margin, right - radius, bottom),
            paint
        )
    }

    private fun drawOtherTopLeftRoundRect(
        canvas: Canvas,
        paint: Paint,
        right: Float,
        bottom: Float
    ) {
        canvas.drawRoundRect(
            RectF(margin, bottom - mDiameter, right, bottom),
            radius,
            radius,
            paint
        )
        canvas.drawRoundRect(
            RectF(right - mDiameter, margin, right, bottom),
            radius,
            radius,
            paint
        )
        canvas.drawRect(
            RectF(
                margin,
                margin,
                right - radius,
                bottom - radius
            ), paint
        )
    }

    private fun drawOtherTopRightRoundRect(
        canvas: Canvas,
        paint: Paint,
        right: Float,
        bottom: Float
    ) {
        canvas.drawRoundRect(
            RectF(margin, margin, margin + mDiameter, bottom),
            radius,
            radius,
            paint
        )
        canvas.drawRoundRect(
            RectF(margin, bottom - mDiameter, right, bottom),
            radius,
            radius,
            paint
        )
        canvas.drawRect(
            RectF(
                margin + radius,
                margin,
                right,
                bottom - radius
            ), paint
        )
    }

    private fun drawOtherBottomLeftRoundRect(
        canvas: Canvas,
        paint: Paint,
        right: Float,
        bottom: Float
    ) {
        canvas.drawRoundRect(
            RectF(margin, margin, right, margin + mDiameter),
            radius,
            radius,
            paint
        )
        canvas.drawRoundRect(
            RectF(right - mDiameter, margin, right, bottom),
            radius,
            radius,
            paint
        )
        canvas.drawRect(
            RectF(
                margin,
                margin + radius,
                right - radius,
                bottom
            ), paint
        )
    }

    private fun drawOtherBottomRightRoundRect(
        canvas: Canvas, paint: Paint, right: Float,
        bottom: Float
    ) {
        canvas.drawRoundRect(
            RectF(margin, margin, right, margin + mDiameter),
            radius,
            radius,
            paint
        )
        canvas.drawRoundRect(
            RectF(margin, margin, margin + mDiameter, bottom),
            radius,
            radius,
            paint
        )
        canvas.drawRect(
            RectF(
                margin + radius,
                margin + radius,
                right,
                bottom
            ), paint
        )
    }

    private fun drawDiagonalFromTopLeftRoundRect(
        canvas: Canvas, paint: Paint, right: Float,
        bottom: Float
    ) {
        canvas.drawRoundRect(
            RectF(
                margin,
                margin,
                margin + mDiameter,
                margin + mDiameter
            ),
            radius, radius, paint
        )
        canvas.drawRoundRect(
            RectF(right - mDiameter, bottom - mDiameter, right, bottom), radius,
            radius, paint
        )
        canvas.drawRect(
            RectF(
                margin,
                margin + radius,
                right - mDiameter,
                bottom
            ), paint
        )
        canvas.drawRect(
            RectF(
                margin + mDiameter,
                margin,
                right,
                bottom - radius
            ), paint
        )
    }

    private fun drawDiagonalFromTopRightRoundRect(
        canvas: Canvas, paint: Paint, right: Float,
        bottom: Float
    ) {
        canvas.drawRoundRect(
            RectF(right - mDiameter, margin, right, margin + mDiameter),
            radius,
            radius,
            paint
        )
        canvas.drawRoundRect(
            RectF(margin, bottom - mDiameter, margin + mDiameter, bottom),
            radius, radius, paint
        )
        canvas.drawRect(
            RectF(
                margin,
                margin,
                right - radius,
                bottom - radius
            ), paint
        )
        canvas.drawRect(
            RectF(
                margin + radius,
                margin + radius,
                right,
                bottom
            ), paint
        )
    }

    private fun drawBorder(
        canvas: Canvas, paint: Paint, right: Float,
        bottom: Float
    ) {

        // stroke
        val strokePaint = Paint()
        strokePaint.style = Paint.Style.STROKE
        if (color.isNotEmpty()) {
            strokePaint.color = Color.parseColor(color)
        } else {
            strokePaint.color = Color.BLACK
        }
        strokePaint.strokeWidth = border
        canvas.drawRoundRect(RectF(margin, margin, right, bottom), radius, radius, paint)

        // stroke
        canvas.drawRoundRect(RectF(margin, margin, right, bottom), radius, radius, strokePaint)
    }


    override fun hashCode(): Int {
        return this::class.java.name.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other is RoundedCornersTransformation
    }

    fun getId(): String {
        return ("RoundedTransformation(radius=" + radius + ", margin=" + margin + ", diameter="
                + mDiameter + ", cornerType=" + cornerType.name + ")")
    }
}