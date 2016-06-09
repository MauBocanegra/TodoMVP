package com.example.android.architecture.blueprints.todoapp.data.source;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by Mauro on 05/06/2016.
 */
public interface SchedulesDataSource {

    interface LoadSchedulesCallback{
        void onSchedulesLoaded(List<String> schedules);
        void onDataNotAvailable();
    }

    interface GetScheduleCallback{
        void onScheduleLoaded(String schedule);
        void onDataNotAvailable();
    }

    void getSchedules(@NonNull LoadSchedulesCallback callback);

    void getSchedule(@NonNull String scheduleId, @NonNull GetScheduleCallback callback);

    void saveSchedule(@NonNull String schedule);
}
