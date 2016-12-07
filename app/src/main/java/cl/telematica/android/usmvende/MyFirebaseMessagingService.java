package cl.telematica.android.usmvende;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import cl.telematica.android.usmvende.Vistas.Map_notification;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        //Hay dos tipos de mensajes: mensajes de datos y mensajes de notificación. Los mensajes de datos se manejan
        // aquí en onMessageReceived si la aplicación está en primer plano o en segundo plano. Los mensajes de datos son del tipo
        // tradicionalmente utilizado con GCM. Los mensajes de notificación solo se reciben aquí en onMessageReceived cuando la aplicación
        // está en primer plano. Cuando la aplicación está en segundo plano, se muestra una notificación generada automáticamente.
        // Cuando el usuario teclea la notificación, se devuelve a la aplicación. Mensajes que contengan ambas notificaciones
        // y las cargas de datos son tratadas como mensajes de notificación. La consola Firebase siempre envía una notificación
        // mensajes.

        Log.d(TAG, "FROM:" + remoteMessage.getFrom());
        //Check if the message contains data
        if(remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data: " + remoteMessage.getData());
        }

        //Check if the message contains notification

        if(remoteMessage.getNotification() != null) {
            Log.d(TAG, "Mesage body:" + remoteMessage.getNotification().getBody());
            sendNotification(remoteMessage.getNotification().getBody());
        }
    }

    // Also if you intend on generating your own notifications as a result of a received FCM
    // message, here is where that should be initiated. See sendNotification method below.

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param body FCM message body received.
     */
    private void sendNotification(String body) {
        Intent intent = new Intent(this, Map_notification.class); // ojo cambie mainactivity por menu sera meenu? o registro producto?
        Log.d(TAG,body);
        intent.putExtra("latlng",body);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0/*Request code*/, intent, PendingIntent.FLAG_ONE_SHOT);
        //Set sound of notification
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notifiBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(notificationSound)
                .setContentIntent(pendingIntent)
                .setContentTitle("Usm Vende");
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 /*ID of notification*/, notifiBuilder.build());
    }
}
