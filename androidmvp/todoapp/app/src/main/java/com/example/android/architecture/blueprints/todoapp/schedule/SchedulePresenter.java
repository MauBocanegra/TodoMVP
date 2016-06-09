package com.example.android.architecture.blueprints.todoapp.schedule;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.architecture.blueprints.todoapp.data.source.ScheduleRepository;
import com.example.android.architecture.blueprints.todoapp.data.source.SchedulesDataSource;
import com.google.common.base.Preconditions;

import java.util.List;

/**
 * Created by Mauro on 05/06/2016.
 */
public class SchedulePresenter implements  ScheduleContract.Presenter {

    private final ScheduleRepository mSchedsRepository;

    private final ScheduleContract.View mSchedsView;

    private boolean mFirstLoad = true;

    public SchedulePresenter(@NonNull ScheduleRepository schedsRepo, @NonNull ScheduleContract.View schedsView){
        mSchedsRepository = Preconditions.checkNotNull(schedsRepo, "schedsRespo cannot be null");
        mSchedsView = Preconditions.checkNotNull(schedsView,"schedsView cannot be null");

        mSchedsView.setPresenter(this);
    }

    /**
     @param forceUpdate
     @param showLoadingUI
     * */
    private void loadScheds(boolean forceUpdate, final boolean showLoadingUI){
        if(showLoadingUI){
            mSchedsView.setLoadingIndicator(true);
        }
        if(forceUpdate){
            //mSchedsRepository.refreshTasks();
        }

        mSchedsRepository.getSchedules(new SchedulesDataSource.LoadSchedulesCallback() {
            @Override
            public void onSchedulesLoaded(List<String> schedules) {
                Log.d("SchedulePresenter","loadedSchedules");

                mSchedsView.showSchedules(schedules);
            }

            @Override
            public void onDataNotAvailable() {
                Log.d("SchedulePresenter","NODataAvailable/Presenter");
            }
        });
    }

    @Override
    public void start() {

    }

    @Override
    public void result(int requestCode, int resultCode) {
        //AddEditTaskActivity
    }

    @Override
    public void loadSchedule(boolean forceUpdate) {
        Log.d("PresenterDebug","loadSchedule");
        loadScheds(forceUpdate||mFirstLoad, true);
    }

    @Override
    public void addNewSchedule(@NonNull String sched) {
        Log.d("PresenterDebug","addNewSchedule");
        Preconditions.checkNotNull(sched);
        mSchedsRepository.saveSchedule(sched);
    }

    @Override
    public void showSchedule(@NonNull String scheduledTime) {
        Log.d("PresenterDebug","showSchedule");
    }
}
