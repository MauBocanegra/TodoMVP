package com.example.android.architecture.blueprints.todoapp.schedule;

import android.support.annotation.NonNull;

import com.example.android.architecture.blueprints.todoapp.BaseView;
import com.example.android.architecture.blueprints.todoapp.BasePresenter;

import java.util.List;

/**
 * Created by Mauro on 01/06/2016.
 */
public interface ScheduleContract {

    interface View extends BaseView<Presenter>{
        void setLoadingIndicator(boolean active);

        void showSchedules(List<String> string);

        void showAddSchedule();

        void showSnackBar(String schedule);

        void showLoadingScheduleError();

        void showNoSchedules();

        void showSuccesfullySavedMessage();

        boolean isActive();
    }


    interface Presenter extends BasePresenter{
        void result(int requestCode, int resultCode);

        void loadSchedule(boolean forceUpdate);

        void addNewSchedule(@NonNull String schedule);

        void showSchedule(@NonNull String scheduledTime);
    }
}
