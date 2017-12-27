package edu.uoc.pac1;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by User on 15/08/2016.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private final static String ACTION_DELETE = "ACTION_DELETE";
    private final static String ACTION_DETAIL = "ACTION_DETAIL";
    private final static String BOOK_POSITION = "BOOK_POSITION";

    /**
     * Método llamado cuando se recibe un mensaje remoto
     *
     * @param remoteMessage Mensaje recibido de Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Mostrar una notificación al recibir un mensaje de Firebase
        sendNotification(remoteMessage.getNotification().getBody(), remoteMessage.getData().get("book_position"));
    }

    /**
     * Crea y muestra una notificación al recibir un mensaje de Firebase
     *
     * @param messageBody Texto a mostrar en la notificación
     */
    private void sendNotification(String messageBody, String bookPosition) {
        Intent intent = new Intent(this, BookListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Intent intentAction = new Intent(this, BookListActivity.class);
        intentAction.setAction(ACTION_DELETE);
        intentAction.putExtra(BOOK_POSITION, bookPosition);
        PendingIntent borrarIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intentAction, 0);
        Intent intentAction2 = new Intent(this, BookListActivity.class);
        intentAction2.setAction(ACTION_DETAIL);
        intentAction2.putExtra(BOOK_POSITION, bookPosition);
        PendingIntent resendIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intentAction2, 0);

        Uri evenSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Uri oddSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

        Integer bookPositionInt = Integer.parseInt(bookPosition);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.book)
                .setContentTitle(messageBody)
                .setContentText("Libro recibido: " + bookPosition)
                .setAutoCancel(true)
                .setSound((bookPositionInt % 2 == 0)?(evenSoundUri):(oddSoundUri))
                .setVibrate(new long[]{500, 1000})
                .setLights((bookPositionInt % 2 == 0)?(Color.BLUE):(Color.RED), 500, 1000)
                .setContentIntent(pendingIntent)
                .addAction(new NotificationCompat.Action(0, "Delete book", borrarIntent))
                .addAction(new NotificationCompat.Action(0, "View details book", resendIntent));

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}