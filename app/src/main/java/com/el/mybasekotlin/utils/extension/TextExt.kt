package com.el.mybasekotlin.utils.extension

import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.Html
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ForegroundColorSpan
import android.text.style.LeadingMarginSpan
import android.text.style.TypefaceSpan
import android.view.View
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import com.el.mybasekotlin.R

/**
 * Created by ElChuanmen on 11/28/2022.
 * Telegram : elchuanmen
 * Phone :0949514503-0773209008
 * Mail :doanvanquang146@gmail.com
 * ----------------------------------------------
 * A class Extension for Textview Android
 *
 */

fun TextView.changeTextToVnBold() {
    val typefaceBold: Typeface? = ResourcesCompat.getFont(context, R.font.be_vietnam_pro_bold)
    this.typeface = typefaceBold
}

fun TextView.changeTextToVNRegular() {
    val typeFace: Typeface? = ResourcesCompat.getFont(context, R.font.be_vietnam_pro_regular)
    this.typeface = typeFace
}

fun TextView.changeColorTo(color: Int) {
    this.setTextColor(context.getColor(color))
}

fun TextView.changeOpacityTo(alpha: Float) {
    this.alpha = alpha
}
//----other

fun TextView.setSpannableColor(result: String, color: Int, start: Int, end: Int) {
    val builder = SpannableStringBuilder()
    builder.append(result)
    builder.setSpan(
        ForegroundColorSpan(color), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE
    )
    text = builder
}

fun TextView.setSpannableHTMLStyle(result: Spanned, color: Int, start: Int, end: Int) {
    val builder = SpannableStringBuilder()
    builder.append(result)
    builder.setSpan(
        ForegroundColorSpan(color), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE
    )
    text = builder
}

fun TextView.setSpannableColorAndMargin(
    result: Spanned,
    color: Int,
    start: Int,
    end: Int,
    marginFirstLine: Int
) {
    val builder = SpannableStringBuilder()
    builder.append(result)
    builder.setSpan(
        ForegroundColorSpan(color), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE
    )
    val span = SpannableString(builder)
    span.setSpan(LeadingMarginSpan.Standard(marginFirstLine, 0), 0, result.length, 0)
    setLineSpacing("22".dpToPx().toFloat(), 0f)
    text = span
}

fun Context.getSpannableColor(
    result: String, color: Int, start: Int, end: Int
): SpannableStringBuilder {
    val builder = SpannableStringBuilder()
    builder.append(result)
    builder.setSpan(
        ForegroundColorSpan(color), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE
    )
    return builder
}

fun TextView.setTextGradient(startColor: Int = 0, endColor: Int = 0, angle: Float = 0f) {
    val textShader = LinearGradient(
        0f, 0f,
        paint.measureText(text.toString()), textSize,
        startColor, endColor,
        Shader.TileMode.CLAMP
    )
    if (angle != 0f) {
        val matrix = Matrix()
        matrix.setRotate(angle)
        textShader.setLocalMatrix(matrix)
    }
    paint.shader = textShader
    invalidate()
}

fun TextView.htmlLoadImg(
    htmlText: String,
    width: Int = 42,
    height : Int = 42,
//    imageLoader: ImageLoader = Coil.imageLoader(this.context),
    sourceModifier: ((source: String?) -> String)? = null,
) {
//    val imageGetter = Html.ImageGetter { source ->
//        val finalSource = sourceModifier?.invoke(source) ?: source
//
//        val drawablePlaceholder = DrawablePlaceHolder(width,height)
//        imageLoader.enqueue(
//            ImageRequest.Builder(this@htmlLoadImg.context)
//                .data(finalSource)
//                .target { drawable ->
//                    drawablePlaceholder.updateDrawable(drawable, this@htmlLoadImg)
//                    this@htmlLoadImg.text = this@htmlLoadImg.text
//                }
//                .build()
//        )
//        drawablePlaceholder
//    }
//
//    this.text = HtmlCompat.fromHtml(htmlText, HtmlCompat.FROM_HTML_MODE_LEGACY, imageGetter, null)
}

@Suppress("DEPRECATION")
private class DrawablePlaceHolder(val width: Int,val height: Int) : BitmapDrawable() {

    private var drawable: Drawable? = null

    override fun draw(canvas: Canvas) {
        drawable?.draw(canvas)
    }

    fun updateDrawable(drawable: Drawable, view: View) {
        this.drawable = drawable
        val width = width
        val height = height
        drawable.setBounds(0, 0, width, height)
        setBounds(0, 0, width, height)
    }
}

/**
 * A class to custom font to setSpan
 *
 */
class CustomTypefaceSpan(family: String?, private val newType: Typeface) :
    TypefaceSpan(family) {

    override fun updateDrawState(ds: TextPaint) {
        applyCustomTypeFace(ds, newType)
    }

    override fun updateMeasureState(paint: TextPaint) {
        applyCustomTypeFace(paint, newType)
    }

    companion object {
        private fun applyCustomTypeFace(paint: Paint, tf: Typeface) {
            val oldStyle: Int
            val old = paint.typeface
            oldStyle = old?.style ?: 0
            val fake = oldStyle and tf.style.inv()
            if (fake and Typeface.BOLD != 0) {
                paint.isFakeBoldText = true
            }
            if (fake and Typeface.ITALIC != 0) {
                paint.textSkewX = -0.25f
            }
            paint.typeface = tf
        }
    }
}


