package com.arxgsf.torpedo

import android.telephony.SmsManager
import android.util.Log
import androidx.core.content.getSystemService
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessagingService: FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if (remoteMessage.data.isNotEmpty()) {
            val data = remoteMessage.data
            val id = remoteMessage.messageId
            if(id != null){
                Log.d("FCM", "${data["text"]} -> ${data["send_to"]}")
                val sendTo = data["send_to"]
                val text = data["text"]

                val smsManager = this.getSystemService<SmsManager>()

                try {
                    smsManager?.sendTextMessage(sendTo, null, text, null, null)
//                    Toast.makeText(context, "Mensagem enviada com sucesso!", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
//                    Toast.makeText(context, "Falha ao enviar mensagem.", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}