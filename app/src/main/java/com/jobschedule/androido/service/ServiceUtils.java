package com.jobschedule.androido.service;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

/**
 * Created by nguyenvanlinh on 12/15/17.
 */

public class ServiceUtils {

    public static void startJobService(Context mContext, int min) {
        ComponentName mServiceComponent = new ComponentName(mContext, MyJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(1000, mServiceComponent);
        builder.setPersisted(true);
        builder.setMinimumLatency(min * 60000); //Set Restart 1min
        JobScheduler tm = (JobScheduler) mContext.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (tm != null) {
            tm.schedule(builder.build());
        }
    }


    public static void cancelJobSchedule (Context mContext){
        JobScheduler tm = (JobScheduler) mContext.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (tm != null) {
            tm.cancel(1000);
        }
    }
}
