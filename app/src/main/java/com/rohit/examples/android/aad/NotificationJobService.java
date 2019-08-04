package com.rohit.examples.android.aad;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

/**
 * Class definition to handle Job Scheduling
 */
public class NotificationJobService extends JobService {

    // Constant and unique channel ID for the notification
    public static final String PRIMARY_CHANNEL_ID =
            "primary_notification_channel";
    /**
     * Saving a notification ID constant to associate notification to update/cancel the notification in future
     */
    public static final int NOTIFICATION_ID = 0;
    //Member variable for NotificationManager
    NotificationManager mNotifyManager;

    /**
     * Called when the system determines that your task should be run
     *
     * @param jobParameters Parameters specifying info about this job
     * @return true from this method if your job needs to continue running or
     * false from this method means your job is already finished
     */
    @Override
    public boolean onStartJob(JobParameters jobParameters) {

        // Create the notification channel
        createNotificationChannel();

        /*
         * Setting up the notification content intent to launch the app when clciked
         */
        PendingIntent contentPendingIntent = PendingIntent
                .getActivity(
                        this, NOTIFICATION_ID,
                        new Intent(this, JobSchedulerActivity.class),
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        /*
         * Instantiating notification builder with current context and NotificationChannel ID
         * Adding title, text and icon to the builder
         * Setting the priority of the notification to HIGH using PRIORITY_HIGH constant
           and sound, vibration, etc. to default values using DEFAULT_ALL constant.
         * Call to setContentIntent() by passing PendingIntent object to set the content intent when
           the notification is clicked.
         * Setting true to setAutoCancel(), closing the notification when the user taps on it
         */
        NotificationCompat.Builder notifyBuilder;

        notifyBuilder = new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                .setContentTitle(getString(R.string.notify_job_title))
                .setContentText(getString(R.string.notify_job_content_text))
                .setContentIntent(contentPendingIntent)
                .setSmallIcon(R.drawable.ic_job_running)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true);

        /*
         * Call to notify() to post a notification to be shown in the status bar with NotificationManager object
           by passing unique NOTIFICATION_ID and build() to combine all of the options that have been set
           and return a new Notification object.
         */
        mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());

        // Return false, because for this app, all of the work is completed in the onStartJob() callback.
        return false;
    }

    /**
     * Called if the system has determined that you must stop execution of your job
     *
     * @param jobParameters Parameters specifying info about this job as supplied to the job in the onStartJob()
     * @return true to indicate to the JobManager whether you'd like to reschedule this job based on the retry
     * criteria provided at job creation-time ot false to end the job entirely.
     * In AndroidManifest, inside <application> register JobService with BIND_JOB_SERVICE permission
     */
    @Override
    public boolean onStopJob(JobParameters jobParameters) {

        // Return true, because if the job fails, we want the job to be rescheduled instead of dropped.
        return true;
    }

    /*
     * Method to create Notification Channel
     */
    public void createNotificationChannel() {

        //Defining NotificationManager object with NOTIFICATION_SERVICE constant
        mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        /*
         * Adding a check on SDK version as for Notification channels are only available in OREO and higher
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Creating NotificationChannel with all params, light; light color, vibration and text label
            NotificationChannel notificationChannel = new NotificationChannel(
                    PRIMARY_CHANNEL_ID,
                    getString(R.string.job_notify),
                    NotificationManager.IMPORTANCE_HIGH
            );

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription(getString(R.string.job_notify_desc));

            // Call to createNotificationChannel() by passing in NotificationChannel object
            // with NotificationManager object
            mNotifyManager.createNotificationChannel(notificationChannel);
        }
    }
}
