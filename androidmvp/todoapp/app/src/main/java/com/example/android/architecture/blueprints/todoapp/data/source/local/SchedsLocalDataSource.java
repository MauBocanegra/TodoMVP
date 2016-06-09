package com.example.android.architecture.blueprints.todoapp.data.source.local;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.android.architecture.blueprints.todoapp.R;
import com.example.android.architecture.blueprints.todoapp.data.source.SchedulesDataSource;
import com.example.android.architecture.blueprints.todoapp.schedule.ScheduleActivity;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Mauro on 06/06/2016.
 */
public class SchedsLocalDataSource implements SchedulesDataSource{

    private static SchedsLocalDataSource INSTANCE;
    private static Context context;

    private SchedsLocalDataSource(@NonNull Context c){
        Preconditions.checkNotNull(c);
        context=c;
    }

    public static SchedsLocalDataSource getInstance(@NonNull Context c){
        if(INSTANCE==null){
            context=c;
            INSTANCE = new SchedsLocalDataSource(context);

        }
        return INSTANCE;
    }

    @Override
    public void getSchedules(@NonNull LoadSchedulesCallback callback) {
        String keySet = "SharedPrefScheds";
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        Set<String> stringSet=sharedPref.getStringSet(keySet,null);

        if(stringSet==null){
            Log.d("SchedsLocal","NotFound");
            callback.onDataNotAvailable();
        }else{
            List<String> asList = new ArrayList<String>();
            for(String  st : stringSet){
                asList.add(st);
            }
            Log.d("SchedsLocal","schedsTRUE");
            callback.onSchedulesLoaded(asList);
        }
    }

    @Override
    public void getSchedule(@NonNull String scheduleId, @NonNull GetScheduleCallback callback) {
        String keySet = "SharedPrefScheds";
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        Set<String> stringSet=sharedPref.getStringSet(keySet,null);

        if(stringSet == null){
            Log.d("SchedsLocal","SingleNotFound");
            callback.onDataNotAvailable();
        }else{
            boolean found = false;
            for(String st : stringSet){
                if(st.equals(scheduleId))
                    found=true;
            }

            if(found){
                Log.d("SchedsLocal","SingleTRUE");
                callback.onScheduleLoaded(scheduleId);
            }else{
                Log.d("SchedsLocal","SingleNotFound");
                callback.onDataNotAvailable();
            }
        }
    }

    @Override
    public void saveSchedule(@NonNull String schedule) {
        String keySet = "SharedPrefScheds";
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        Set<String> stringSet=sharedPref.getStringSet(keySet,null);
        if(stringSet==null){
            Log.d("ArrayDebug","wasNull");
            stringSet=new HashSet<String>();
        }

        stringSet.add(schedule);

        Log.d("ArrayDebug","SetNow!");
        for(String st : stringSet){
            Log.d("ArrayDebug",st);
        }

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putStringSet(keySet,stringSet);
        editor.commit();

        Notification not = getNotification(schedule);

        NotificationManager notificationManager =
                (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, not);

        String[] splitSched = schedule.split(":");

        // Set the alarm to start at approximately 2:00 p.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY,Integer.parseInt(splitSched[0]),Integer.parseInt(splitSched[1]) );

        AlarmManager alarmMgr;
        PendingIntent alarmIntent;
        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ScheduleActivity.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        if (alarmMgr!= null) {
            alarmMgr.cancel(alarmIntent);
        }

// With setInexactRepeating(), you have to use one of the AlarmManager interval
// constants--in this case, AlarmManager.INTERVAL_DAY.
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);
    }

    private Notification getNotification(String content) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_assignment_turned_in_24dp)
                        .setContentTitle("Reminder set")
                        .setContentText("Your reminder alarm will ring at "+content);
        return  mBuilder.build();
    }
}
