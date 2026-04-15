package com.el.mybasekotlin.data.notice

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
//import com.vcc.ticket.R
//import com.vcc.ticket.data.model.notices.Notice
//import com.vcc.ticket.helpers.ACTION_NOTIFICATION_CLICK
//import com.vcc.ticket.cui.MainActivity
//
///**
// * Created by QuangDV on 7/17/2024.
// * Telegram : elchuanmen
// * Phone :0949514503-0773209008
// * Mail :doanvanquang146@gmail.com
// */
//class NotificationHelper(private val context: Context) {
//
//    fun showNotificationDefault(notice: Notice) {
//        val notificationManager =
//            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        val channelId = "DEFAULT_CHANNEL_ID"
//
//        val intent = Intent(context, MainActivity::class.java).apply {
//            action = ACTION_NOTIFICATION_CLICK
//        }
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
//
//        intent.putExtra("DATA_NOTICE", notice)
//
//        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        } else {
//            PendingIntent.FLAG_UPDATE_CURRENT
//        }
//        val pendingIntent = PendingIntent.getActivity(context, 0, intent, pendingIntentFlags)
//
////        val bitmap = getBitmapByGlide(context,image)
//        val bitmap = if (notice.image?.isNotEmpty() == true) getBitmapByGlide(
//            context,
//            notice.image
//        ) else null
//        val notification = NotificationCompat.Builder(context, channelId).setAutoCancel(true)
//            .setSmallIcon(R.mipmap.ic_launcher) // Replace with your notification icon
//            .setContentTitle(notice.title)
//            .setContentText(notice.content)
//            .setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
//            .setContentIntent(pendingIntent)
//            .setNumber(0)
//            .setAutoCancel(true).setPriority(NotificationCompat.PRIORITY_MAX)
//            .build()
//
//        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
//    }
//
//    fun showHeadsUpNotification(title: String, image: String, message: String, data: String) {
//        val notificationManager =
//            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        val channelId = "IMPORTANT_CHANNEL_ID"
//
//
//        val intent = Intent(context, MainActivity::class.java).apply {
//            action = ACTION_NOTIFICATION_CLICK
//            putExtra("data", title)
//        }
//        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        } else {
//            PendingIntent.FLAG_UPDATE_CURRENT
//        }
//        val pendingIntent = PendingIntent.getActivity(context, 0, intent, pendingIntentFlags)
//
//        val notification = NotificationCompat.Builder(context, channelId).setAutoCancel(true)
//            .setSmallIcon(R.mipmap.ic_launcher) // Thay thế icon
//            .setContentTitle(title)
//            .setContentText(message)
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .setDefaults(NotificationCompat.DEFAULT_ALL)
//            .setNumber(0)
//            .setContentIntent(pendingIntent)
//            .setAutoCancel(true)
//            .build()
//
//        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
//    }
//
//    private fun getBitmapFromUrl(context: Context, imageUrl: String): Bitmap? {
//        var bitmap: Bitmap? = null
//        Glide.with(context)
//            .asBitmap()
//            .load(imageUrl)
//            .into(object : CustomTarget<Bitmap>() {
//                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
//                    bitmap = resource
//                }
//
//                override fun onLoadCleared(placeholder: Drawable?) {
//                    bitmap = null
//                }
//            })
//        return bitmap
//    }
//
//    private fun getBitmapByGlide(context: Context, urlImage: String): Bitmap? {
//        return try {
//            Glide.with(context)
//                .asBitmap()
//                .load(urlImage)
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .skipMemoryCache(true)
//                .submit()
//                .get()
//        } catch (e: Exception) {
//            e.printStackTrace()
//            null
//        }
//    }
//}