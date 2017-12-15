package com.jobschedule.androido.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by nguyenvanlinh on 12/15/17.
 */

public class MyStartServiceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startServiceIntent = new Intent(context, MyJobService.class);
        context.startService(startServiceIntent);
    }
}