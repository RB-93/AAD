package com.rohit.examples.android.aad;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

/**
 * Class definition to handle Notification
 */
public class NotificationActivity extends AppCompatActivity {

    // Member variable for the Notify, Update and Cancel buttons
    private Button btn_notify;
    private Button btn_update;
    private Button btn_cancel;

    // Saving Notification Channel ID string to post Notifications
    public static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";

    /**
     * Saving a notification ID constant to associate notification to update/cancel the notification in future
     */
    public static final int NOTIFICATION_ID = 0;

    /**
     * Assigning unique constant for update notification action broadcast
     * Uniqueness is ensured by prefixing app's package name in variable value
     */
    private static final String ACTION_UPDATE_NOTIFICATION =
            "com.rohit.examples.android.aad.ACTION_UPDATE_NOTIFICATION";

    // Member variable to store NotificationManager object, used to deliver notification to the user
    private NotificationManager mNotificationManager;

    // Member variable for the receiver, initialized with default constructor
    private NotificationReceiver mReceiver = new NotificationReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        // Getting button btn_notify view ID from resource
        btn_notify = findViewById(R.id.notify);

        /*
         * Handling Notify button clicks using onClickListener()
         * Sending notification to system status bar using sendNotification() method
         */
        btn_notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Send the notification
                sendNotification();
            }
        });

        // Getting button btn_update view ID from resource
        btn_update = findViewById(R.id.update);

        /*
         * Handling Update button clicks using onClickListener()
         */
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Update the notification
                updateNotification();
            }
        });

        // Getting button btn_cancel view ID from resource
        btn_cancel = findViewById(R.id.cancel);

        /*
         * Handling Cancel button clicks using onClickListener()
         */
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Cancel the notification
                cancelNotification();
            }
        });

        // Registering our broadcast receiver to receive ACTION_UPDATE_NOTIFICATION intent
        registerReceiver(mReceiver, new IntentFilter(ACTION_UPDATE_NOTIFICATION));

        // Missing call to this issues app crashes
        createNotificationChannel();

        /*
         * On app first run, Notify Me! button should be the only button enabled,
           because there is no notification yet to update or cancel.
         */
        setNotificationButtonState(true, false, false);
    }

    /**
     * Method to send Notification to status bar
     */
    public void sendNotification() {

        // New intent using custom update action ACTION_UPDATE_NOTIFICATION
        Intent updateIntent = new Intent(ACTION_UPDATE_NOTIFICATION);

        /*
         * Call to getBroadcast() to get a PendingIntent using current context, notification ID.
         * Setting FLAG_ONE_SHOT to make sure the pending intent is sent and used only once.
         */
        PendingIntent updatePendingIntent = PendingIntent
                .getBroadcast(this, NOTIFICATION_ID, updateIntent, PendingIntent.FLAG_ONE_SHOT);

        // Getting NotificationBuilder object using getNotificationBuilder()
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();

        /*
         * Call to addAction() to add an action to and with NotificationCompat.Builder object by
           passing in the icon, label text and the PendingIntent object.
         */
        notifyBuilder
                .addAction(R.drawable.ic_update, getString(R.string.notify_update_action_text), updatePendingIntent);

        // Call to notify() on NotificationManager object
        mNotificationManager.notify(NOTIFICATION_ID, notifyBuilder.build());

        /*
         * After a notification is sent, the cancel and update buttons should be enabled,
           and the notification button should be disabled, because the notification has been delivered.
         */
        setNotificationButtonState(false, true, true);
    }

    /**
     * Method to update Notification to status bar
     * Converting drawable resource to Bitmap
     * Call to getNotificationBuilder() to get NotificationCompat.Builder object.
     * Changing notification style to accommodate the new image (Big Picture) and setting the image and the title.
     * Call to notify() by passing in NOTIFICATION_ID with NotificationManager to build the notification.
     */
    public void updateNotification() {

        Bitmap androidImage = BitmapFactory
                .decodeResource(getResources(), R.drawable.mascot_1);

        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        notifyBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                .bigPicture(androidImage)
                .setBigContentTitle(getString(R.string.notify_update)));
        //              .setDeleteIntent(dismissPendingIntent);

        mNotificationManager.notify(NOTIFICATION_ID, notifyBuilder.build());

        /*
         * After the notification is updated, the update and notify buttons should be disabled,
           leaving only the cancel button enabled.
         */
        setNotificationButtonState(false, false, true);
    }

    /**
     * Method to cancel Notification from status bar
     */
    public void cancelNotification() {
        /*
         * Call to cancel() by passing in the NOTIFICATION_ID with NotificationManager object to
           cancel the notification.
         */
        mNotificationManager.cancel(NOTIFICATION_ID);

        /*
         * If the notification is canceled, the buttons should return to their initial states,
           with only the notify button enabled.
         */
        setNotificationButtonState(true, false, false);
    }

    /**
     * Method to instantiate the NotificationManager object
     */
    public void createNotificationChannel() {

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        /*
         * Checking device API version as Notification Channels aren't available in API 26 and above
         */
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Creating NotificationChannel after condition returns true
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID,
                    getString(R.string.mascot_notify), NotificationManager.IMPORTANCE_HIGH);

            // Configuring notificationChannel object's initial settings viz., notification light color, vibration, etc.
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription(getString(R.string.mascot_notify_desc));

            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }

    /**
     * Helper method to build notification

     * Creating explicit content intent method to launch NotificationActivity from the notification,
       this way tapping the notification app sends the content intent that launches the NotificationActivity (our app).
     * Using getActivity() to get a Pending Intent, passing notification ID constant for the requestCode
       and using FLAG_UPDATE_CURRENT flag indicating that if the described PendingIntent already exists,
       then keep it but replace its extra data with what is in this new Intent.
     * Instantiating notification builder with current context and NotificationChannel ID
     * Adding title, text and icon to the builder
     * Setting the priority of the notification to HIGH using PRIORITY_HIGH constant
       and sound, vibration, etc. to default values using DEFAULT_ALL constant.
     * Call to setContentIntent() by passing PendingIntent object to set the content intent when
       the notification is clicked.
     * Setting true to setAutoCancel(), closing the notification when the user taps on it
     * return notifyBuilder
     */
    private NotificationCompat.Builder getNotificationBuilder() {

        Intent notificationIntent = new Intent(this, NotificationActivity.class);

        PendingIntent notificationPendingIntent = PendingIntent
                .getActivity(this, NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notifyBuilder;

        notifyBuilder = new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                .setContentTitle(getString(R.string.notify_content_title))
                .setContentText(getString(R.string.notify_content_text))
                .setSmallIcon(R.drawable.ic_android)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(notificationPendingIntent)
                .setAutoCancel(true);

        return notifyBuilder;
    }

    /**
     * Implementing a Broadcast Receiver for updating notification on clicking Update notification action
       in notification.
     */
    public class NotificationReceiver extends BroadcastReceiver {

        // An empty constructor to instantiate the class
        public NotificationReceiver() {
        }

        /**
         * Method to handle Intent received by the BroadcastReceiver
         * @param context The Context in which the receiver is running.
         * @param intent The Intent being received.
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            // Update the notification
            updateNotification();
        }
    }

    /**
     * A utility method to handle notification button states on different notification stages: notify(),
       update() and cancel().
     * @param isNotifyEnabled to enable/disable Notify Me! button state
     * @param isUpdateEnabled to enable/disable Update Me! button state
     * @param isCancelEnabled to enable/disable Cancel Me! button state
     */
    void setNotificationButtonState(Boolean isNotifyEnabled, Boolean isUpdateEnabled, Boolean isCancelEnabled) {

        btn_notify.setEnabled(isNotifyEnabled);
        btn_update.setEnabled(isUpdateEnabled);
        btn_cancel.setEnabled(isCancelEnabled);
    }

    /**
     * Unregistering our broadcast receiver before the activity is destroyed by the Android Framework.
     * Call to unregisterReceiver() by passing in BroadcastReceiver object.
     */

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }
}