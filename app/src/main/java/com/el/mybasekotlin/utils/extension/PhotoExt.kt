package com.el.mybasekotlin.utils.extension

import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

fun ImageView.loadImageFromUrl(url: String?, drawableRes: Int? = null) {
//    Glide.with(context).load(url).error(R.drawable.test_avatar).into(this)
    Glide.with(context).load(url).error(drawableRes).into(this)
}

fun ImageView.loadImageFromDrawable(drawableRes: Int) {
    Glide.with(context).load(drawableRes).error(drawableRes).into(this)
}

fun ImageView.loadImageFromDrawable(drawable: Int?) {
    Glide.with(context).load(drawable).into(this)
}

fun ImageView.loadCircleImageFromUrl(url: String?, drawableRes: Int) {
    Glide.with(context).load(url).placeholder(drawableRes).error(drawableRes).fitCenter()
        .circleCrop()
        .into(this)
}

fun ImageView.loadCircleImageFromUri(uri: Uri?, placeholder: Int) {
    Glide.with(context).load(uri).placeholder(placeholder).error(placeholder).fitCenter()
        .circleCrop().into(this)
}

fun ImageView.loadCircleImageFromDrawable(drawable: Int?) {
    Glide.with(context).load(drawable).fitCenter()
        .circleCrop().into(this)
}

fun ImageView.loadImageAvatarNotice(drawable: Int?) {
    Glide.with(context).load(drawable).circleCrop().into(this)
}

fun ImageView.changeOpacityTo(alpha: Float) {
    this.alpha = alpha
}


fun ImageView.loadImageWithRadius(url: String?, radius: Int) {
//    Glide.with(context)
//        .load(url).optionalTransform(RoundedCorners(radius))
//        .error(R.drawable.test_placeholder)
//        .into(this)
    var requestOptions = RequestOptions()
    requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(radius))
    Glide.with(context)
//        .load(url).error(R.drawable.test_avatar)
        .load(url)
        .apply(requestOptions)
        .into(this)
}