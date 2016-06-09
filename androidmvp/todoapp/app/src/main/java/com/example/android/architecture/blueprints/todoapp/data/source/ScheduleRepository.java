package com.example.android.architecture.blueprints.todoapp.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Mauro on 05/06/2016.
 */
public class ScheduleRepository implements SchedulesDataSource {

    private static ScheduleRepository INSTANCE = null;
    private final SchedulesDataSource mSchedulesLocalDataSourse;
    List<String> scheds;

    private ScheduleRepository (@NonNull SchedulesDataSource schedsLocalDataSourse){
        mSchedulesLocalDataSourse = Preconditions.checkNotNull(schedsLocalDataSourse);
    }

    public static ScheduleRepository getInstance (SchedulesDataSource schedulesLocalDataSource){
        if(INSTANCE==null){
            INSTANCE=new ScheduleRepository(schedulesLocalDataSource);
        }

        return INSTANCE;
    }

    public static void destroyInstance(){INSTANCE=null;}

    @Override
    public void getSchedules(@NonNull final LoadSchedulesCallback callback) {
        mSchedulesLocalDataSourse.getSchedules(new LoadSchedulesCallback() {
            @Override
            public void onSchedulesLoaded(List<String> schedules) {
                scheds=schedules;
                refreshCache(schedules);
                callback.onSchedulesLoaded(schedules);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    private void refreshCache(List<String> scheds){
    }

    @Override
    public void getSchedule(@NonNull String scheduleId, @NonNull final GetScheduleCallback callback) {
        Preconditions.checkNotNull(scheduleId);
        Preconditions.checkNotNull(callback);

        mSchedulesLocalDataSourse.getSchedule(scheduleId, new GetScheduleCallback() {
            @Override
            public void onScheduleLoaded(String schedule) {
                callback.onScheduleLoaded(schedule);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void saveSchedule(@NonNull String schedule) {
        Preconditions.checkNotNull(schedule);
        mSchedulesLocalDataSourse.saveSchedule(schedule);
    }
}
