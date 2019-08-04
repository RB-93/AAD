package com.rohit.examples.android.aad;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Class definition to handle Job Scheduling
 */
public class JobSchedulerActivity extends AppCompatActivity {

    // Member constant set to 0 to uniquely identify Job IDs
    public static final int JOB_ID = 0;

    // Member variable for RadioGroup view
    RadioGroup networkOptions;

    // Member variables Switches Device Idle and Device Charging for setting job options
    private Switch mDeviceIdleSwitch;
    private Switch mDeviceChargingSwitch;

    // Member variable for Override Deadline SeekBar
    private SeekBar mSeekBar;

    // Member variable for job scheduler
    JobScheduler mScheduler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_scheduler);

        // Initializing the switch variables with their ID resource
        mDeviceIdleSwitch = findViewById(R.id.idleSwitch);
        mDeviceChargingSwitch = findViewById(R.id.chargingSwitch);

        // Initializing the SeekBar variable with their ID resource
        mSeekBar = findViewById(R.id.seekBar);

        // Create and initialize SeekBar Progress text variable with Id resource
        final TextView seekBarProgress = findViewById(R.id.seekBarProgress);

        // Implement SeekBar object to track Progress changes and SeekBar thumb touch
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                // Check SeekBar value greater than 0 to set SeekBar progress label accordingly
                if (i > 0) {
                    seekBarProgress.setText(String.format("" + getString(R.string.seekBar_time_unit), i));
                } else {
                    seekBarProgress.setText(getString(R.string.deadline_not_set));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    /**
     * Method to schedule jobs
     * @param view View to be used for creating jobs
     */
    public void scheduleJobs(View view) {

        // Getting view ID of RadioGroup from resource
        networkOptions = findViewById(R.id.networkOptions);

        // Saving the selected network ID
        int selectedNetworkId = networkOptions.getCheckedRadioButtonId();

        // Saving the default network option i.e., NETWORK_TYPE_NONE
        int selectedNetworkOption = JobInfo.NETWORK_TYPE_NONE;

        // Saving the seekBar's progress value
        int seekBarInteger = mSeekBar.getProgress();

        // Check variable set to true if seekBar progress value greater than 0
        boolean seekBarSet = seekBarInteger > 0;

        /*
         * Assigning the appropriate JobInfo network constant to selected network option using switch
         */
        switch (selectedNetworkId) {
            case R.id.noNetwork:
                selectedNetworkOption = JobInfo.NETWORK_TYPE_NONE;
                break;

            case R.id.anyNetwork:
                selectedNetworkOption = JobInfo.NETWORK_TYPE_ANY;
                break;

            case R.id.wifiNetwork:
                selectedNetworkOption = JobInfo.NETWORK_TYPE_UNMETERED;

        }

        //Initializing the JobScheduler object using getSystemService()
        mScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);

        /*
         * Instantiating ComponentName to associate Job ID with JobInfo object by passing in
           getPackageName() to obtain application package name and Job Scheduling class to get its all entities
         */
        ComponentName serviceName = new ComponentName(getPackageName(), NotificationJobService.class.getName());

        /*
         * Instantiating JobInfo.Builder object by passing in Job ID and Component Name for Job Service created
         * Call to setRequiredNetworkType() with JobInfo.Builder object by passing in selectedNetworkOption
           to set some description of the kind of network the job need to have.
         * Call to setRequiresDeviceIdle() with JobInfo.Builder object by passing in user selection in Switch views
           to specify that to run, the job needs the device to be in idle mode.
         * Call to setRequiresCharging() with JobInfo.Builder object by passing in user selection in Switch views
           to specify that to run this job, the device needs to be plugged in.
         */
        JobInfo.Builder jobBuilder;
        jobBuilder = new JobInfo.Builder(JOB_ID, serviceName)
                .setRequiredNetworkType(selectedNetworkOption)
                .setRequiresDeviceIdle(mDeviceIdleSwitch.isChecked())
                .setRequiresCharging(mDeviceChargingSwitch.isChecked());

        /*
         * If seekBarSet is true, call to setOverrideDeadline() with JobInfo.Builder object
         * Passing in seekBar progress value multiplied by 1000 (to convert in milliseconds)
           to Set deadline which is the maximum scheduling latency.
         * The job will be run by this deadline even if other requirements are not met.
         */
        if (seekBarSet) {
            jobBuilder.setOverrideDeadline(seekBarInteger * 1000);
        }

        /*
         * A check variable to track network requirements
         * Default network option is NETWORK_TYPE_NONE nad not valid constraint
         * Stores true if the network option is not set to default, otherwise false
         * New constraints i.e., DeviceIdle and DeviceCharging chained in constrainSet
         * Including the value of seekBarSet as a possible constraint.
         */
        boolean constraintSet = selectedNetworkOption != JobInfo.NETWORK_TYPE_NONE
                || mDeviceIdleSwitch.isChecked() || mDeviceChargingSwitch.isChecked()
                || seekBarSet;

        if (constraintSet) {
            // Schedule the job and notify the user

            // Assigning JobInfo object with JobInfo.Builder returned object to hand to the JOb Scheduler
            JobInfo myJobInfo = jobBuilder.build();

            // Call to schedule() to schedule a job to be executed with JobScheduler object by passing in
            // JobInfo job object scheduled.
            mScheduler.schedule(myJobInfo);

            // A toast message to let user know the job was scheduled
            Toast.makeText(this, getString(R.string.job_scheduled_success_text), Toast.LENGTH_SHORT).show();
        } else {
            //A toast message to let user know the valid job constraint not set
            Toast.makeText(this, getString(R.string.job_constraint_not_set), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Method to cancel jobs
     * @param view View to be used to cancel jobs from
     */
    public void cancelJobs(View view) {

        /*
         * Check if JobScheduler object is handling any jobs with null
         * If not, then call to cancelAll() with JobScheduler object to remove all pending jobs.
         * Resetting JobScheduler object to null for new Job scheduling
         * A toast message to let user know the job was cancelled
         */
        if (mScheduler != null) {
            mScheduler.cancelAll();
            mScheduler = null;

            Toast.makeText(this, getString(R.string.job_cancel_text), Toast.LENGTH_SHORT).show();
        }
    }
}
