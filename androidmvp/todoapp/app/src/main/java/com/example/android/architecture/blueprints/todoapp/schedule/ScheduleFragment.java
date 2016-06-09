package com.example.android.architecture.blueprints.todoapp.schedule;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.android.architecture.blueprints.todoapp.R;
import com.example.android.architecture.blueprints.todoapp.tasks.ScrollChildSwipeRefreshLayout;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Mauro on 02/06/2016.
 */
public class ScheduleFragment extends Fragment implements ScheduleContract.View {

    private ScheduleContract.Presenter mPresenter;
    private ScheduleAdapter mListAdapter;
    private TextView mTitleLabelView;
    private LinearLayout mSchedsView;

    private View mNoSchedsView;
    private ImageView mNoSchedsIcon;
    private TextView mNoSchedMainView;

    private ListView mListView;

    public static ScheduleFragment newInstance(){ return new ScheduleFragment(); }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListAdapter = new ScheduleAdapter(new ArrayList<String>(0),mItemListener);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.schedules_frag, container, false);
        mListView = (ListView) root.findViewById(R.id.scheds_list);
        mListView.setAdapter(mListAdapter);
        mTitleLabelView = (TextView) root.findViewById(R.id.titleLabel);
        mSchedsView = (LinearLayout)root.findViewById(R.id.schedsLL);

        //Set up no scheds view
        mNoSchedsView = root.findViewById(R.id.noScheds);
        mNoSchedsIcon = (ImageView)root.findViewById(R.id.noSchedsIcon);
        mNoSchedMainView = (TextView)root.findViewById(R.id.noSchedsMain);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab_add_schedule);
        fab.setImageResource(R.drawable.ic_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddSchedule();
            }
        });

        final SwipeRefreshLayout swipeRefreshLayout =
                (SwipeRefreshLayout) root.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark)
        );

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.loadSchedule(false);
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
        mPresenter.loadSchedule(false);
    }

    @Override
    public void setPresenter(ScheduleContract.Presenter presenter) {
        mPresenter = Preconditions.checkNotNull(presenter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.result(requestCode, resultCode);
    }

    @Override
    public void setLoadingIndicator(boolean active) {
        if(getView()==null){
            return;
        }
    }

    private void showNoSchedsView(String mainText, int iconRes, boolean showAddView){
        mSchedsView.setVisibility(View.GONE);
        mNoSchedsView.setVisibility(View.VISIBLE);

        mNoSchedMainView.setText(mainText);
        mNoSchedsIcon.setImageDrawable(getResources().getDrawable(iconRes));
    }

    @Override
    public void showSchedules(List<String> scheds) {
        Log.d("ShouldShow","IsItCalling?");
        for(String st : scheds){
            Log.d("inShow",st);
        }
        mListAdapter = new ScheduleAdapter(scheds,mItemListener);
        mListView.setAdapter(mListAdapter);
        mSchedsView.setVisibility(View.VISIBLE);
        mNoSchedsView.setVisibility(View.GONE);

        mListAdapter.notifyDataSetChanged();
    }

    @Override
    public void showAddSchedule() {
        //Intent?
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(getContext(), R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                Log.d("ScheduleFragmentDebug","Reminder at "+selectedHour+":"+selectedMinute);
                mPresenter.addNewSchedule(""+selectedHour+":"+selectedMinute);
                mPresenter.loadSchedule(false);
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    @Override
    public void showSnackBar(String schedule) {
        Snackbar.make(getView(), schedule, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showLoadingScheduleError() {

    }

    @Override
    public void showNoSchedules() {
        showNoSchedsView(
                "You have no schedules!",
                R.drawable.ic_check_circle_24dp,
                false
        );
    }

    @Override
    public void showSuccesfullySavedMessage() {
        Snackbar.make(getView(), "Succesfully saved!", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    ScheduleItemListener mItemListener = new ScheduleItemListener() {
        @Override
        public void onScheduleClick(String clickedSchedule) {
            mPresenter.showSchedule(clickedSchedule);
        }
    };

    private static class ScheduleAdapter extends BaseAdapter{

        private List<String> mSchedules;
        private ScheduleItemListener mItemListener;

        public ScheduleAdapter(List<String> schedules, ScheduleItemListener itemListener){
            setList(schedules);
            mItemListener = itemListener;
        }

        public void replaceData(List<String> scheds){
            setList(scheds);
            notifyDataSetChanged();
        }

        private void setList(List<String> scheds){ mSchedules = Preconditions.checkNotNull(scheds); }

        @Override
        public int getCount() {
            return mSchedules.size();
        }

        @Override
        public Object getItem(int i) {
            return mSchedules.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View rowView = view;
            if(rowView==null){
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                rowView = inflater.inflate(R.layout.schedule_item, viewGroup, false);

                TextView titleTV = (TextView) rowView.findViewById(R.id.title_sch);
                titleTV.setText((String)getItem(i));
            }

            final String schedule = (String)getItem(i);

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemListener.onScheduleClick(schedule);
                }
            });
            return rowView;
        }
    }

    public interface ScheduleItemListener{
        void onScheduleClick(String clickedSchedule);
    }
}
