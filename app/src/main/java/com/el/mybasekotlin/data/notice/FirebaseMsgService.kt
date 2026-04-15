package com.el.mybasekotlin.data.notice

import androidx.core.content.ContextCompat.getSystemService

//
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.app.PendingIntent
//import android.content.Context
//import android.content.Intent
//import android.os.Build
//import androidx.core.app.NotificationCompat
//import androidx.localbroadcastmanager.content.LocalBroadcastManager
//import com.google.firebase.messaging.FirebaseMessagingService
//import com.google.firebase.messaging.RemoteMessage
//import com.vcc.ticket.R
//import com.vcc.ticket.data.model.notices.Notice
//import com.vcc.ticket.data.source.local.preferences.AppPreferences
//import com.vcc.ticket.cui.MainActivity
//import com.el.mybasekotlin.data.notice.NotificationHelper
//import com.vcc.ticket.utils.extension.getOrBlank
//import org.json.JSONException
//import org.json.JSONObject
//import timber.log.Timber
//import vn.chayluoi.stream.app.constant.Constant.FIREBASE_NEW_NOTICE
//import vn.chayluoi.stream.app.constant.Constant.FIREBASE_NEW_TOKEN
//
///**
// * Created by ElChuanmen on 7/15/2024.
// * Telegram : elchuanmen
// * Phone :0949514503-0773209008
// * Mail :doanvanquang146@gmail.com
// */
//class FirebaseMsgService : FirebaseMessagingService() {
//    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        super.onMessageReceived(remoteMessage)
//
//        // Check if the message contains data payload.
//        if (remoteMessage.data.isNotEmpty()) {
//            Timber.tag(TAG).d("Message data payload: %s", remoteMessage.data)
//            // Handle message within 10 seconds
//            handleNow(remoteMessage.data)
//        }
//
//        // Check if the message contains a notification payload.
//        remoteMessage.notification?.let {
//            Timber.tag(TAG).d("Message Notification Body: %s", it.body)
//            sendNotification(it.body)
//        }
//    }
//
//    private fun handleNow(dataNotice: Map<String, String>) {
//        Timber.tag(TAG).d("Short lived task is done.")
//        // Handle your data payload here
//        val resultData = dataNotice["result"]
//        try {
//            if (resultData != null) {
//                val result = JSONObject(resultData)
//                val data: JSONObject? = result.optJSONObject("data")
//                val notificationID = result.optString("notificationID", "")
//                val dataNotifyOs = result.optJSONObject("notification")
//                val noticeIdTemp = dataNotifyOs?.optJSONObject("ticket")?.optString("id")
//                val typeNotify = data?.optInt("type", 0)
//                val content = dataNotifyOs?.optString("body", "")
//                val image = dataNotifyOs?.optString("image", "")
//                val refType = dataNotifyOs?.optJSONObject("ticket")?.optString("refType", "")
//                val refId = dataNotifyOs?.optJSONObject("ticket")?.optString("refId", "")
//
//                Timber.d("handleNow Notice notificationID notice:${notificationID} ")
//                Timber.d("handleNow Notice dataNotifyOs notice:${dataNotifyOs} ")
//                Timber.d("handleNow Notice typeNotify notice:${typeNotify} ")
//                Timber.d("handleNow Notice content :${content} ")
//                Timber.d("handleNow Notice image :${image} ")
//                val notice = Notice(
////                    id = notificationID,
//                    id = noticeIdTemp.getOrBlank(),// lấy ta cai id nay, vi co the server se doi
//                    image = image.getOrBlank(),
//                    content = content.getOrBlank(),
//                    title = dataNotifyOs?.optString("title", "").getOrBlank(),
//                    refId = refId.getOrBlank(),
//                    refType = refType.getOrBlank()
//                )
//                NotificationHelper(this).showNotificationDefault(notice)
//                sendBroadCastNewNotice()
//            }
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }
//    }
//
//    private fun sendNotification(messageBody: String?) {
//        val intent = Intent(this, MainActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
//        val pendingIntent = PendingIntent.getActivity(
//            this, 0, intent,
//            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
//        )
//
//        val channelId = getString(R.string.default_notification_channel_id)
//        val notificationBuilder = NotificationCompat.Builder(this, channelId)
//            .setSmallIcon(R.mipmap.ic_launcher)
//            .setContentTitle(getString(R.string.notice_title))
//            .setContentText(messageBody)
//            .setAutoCancel(true)
//            .setContentIntent(pendingIntent)
//
//        val notificationManager =
//            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                channelId,
//                "Channel human readable title",
//                NotificationManager.IMPORTANCE_DEFAULT
//            )
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        notificationManager.notify(0, notificationBuilder.build())
//    }
//
//    override fun onNewToken(token: String) {
//        Timber.tag(TAG).d("Refreshed token: %s", token)
//        AppPreferences.fcmToken = token
//        // If you want to send messages to this application instance or
//        // manage this app's subscriptions on the server side,
//        // send the token to your app server.
//        sendRegistrationToServer(token)
//    }
//
//    private fun sendRegistrationToServer(token: String?) {
//        //TOD send notice to main activity for call api
//        AppPreferences.fcmToken
//        val intent = Intent(FIREBASE_NEW_TOKEN)
//        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
//    }
//    private fun sendBroadCastNewNotice() {
//        val intent = Intent(FIREBASE_NEW_NOTICE)
//        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
//    }
//
//    companion object {
//        private const val TAG = "FirebaseMsgService"
//    }
//}