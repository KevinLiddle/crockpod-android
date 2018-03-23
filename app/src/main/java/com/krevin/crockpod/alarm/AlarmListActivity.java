package com.krevin.crockpod.alarm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.krevin.crockpod.CrockpodActivity;
import com.krevin.crockpod.R;
import com.krevin.crockpod.alarm.repositories.AlarmRepository;

import org.joda.time.format.DateTimeFormat;

import java.util.List;

public class AlarmListActivity extends CrockpodActivity {

    private static final String CLOCK_FORMAT = "h:mma";

    private RecyclerView mAlarmList;
    private AlarmRepository mAlarmRepository;

    public static Intent getIntent(Context context) {
        return new Intent(context, AlarmListActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);

        mAlarmRepository = new AlarmRepository(this);

        FloatingActionButton mAddAlarmButton = findViewById(R.id.add_alarm_button);
        mAddAlarmButton.setOnClickListener(view -> startActivity(SetAlarmActivity.getIntent(AlarmListActivity.this)));

        mAlarmList = findViewById(R.id.alarm_list);
        mAlarmList.setHasFixedSize(true);
        mAlarmList.setLayoutManager(new LinearLayoutManager(this));
        mAlarmList.setOnFlingListener(new RecyclerView.OnFlingListener() {
            @Override
            public boolean onFling(int velocityX, int velocityY) {
                refreshAlarmList();
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshAlarmList();
    }

    private void refreshAlarmList() {
        mAlarmList.setAdapter(new AlarmListAdapter(mAlarmRepository.list()));
    }

    private class AlarmListAdapter extends RecyclerView.Adapter<AlarmHolder> {
        private final List<Alarm> mAlarms;
        private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

        AlarmListAdapter(List<Alarm> mAlarms) {
            this.mAlarms = mAlarms;
        }

        @Override
        public AlarmHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(AlarmListActivity.this);
            View view = inflater.inflate(R.layout.alarm_list_item, parent, false);
            return new AlarmHolder(view);
        }

        @Override
        public void onBindViewHolder(AlarmHolder holder, int position) {
            Alarm alarm = mAlarms.get(position);
            holder.bindAlarm(alarm);
            viewBinderHelper.bind(holder.mSwipeRevealLayout, alarm.getId().toString());
        }

        @Override
        public int getItemCount() {
            return mAlarms.size();
        }
    }

    private class AlarmHolder extends RecyclerView.ViewHolder {

        private final SwipeRevealLayout mSwipeRevealLayout;
        private final LinearLayout mAlarmLayout;
        private final TextView mAlarmTimeView;
        private final TextView mAlarmTextView;
        private final Button mDeleteAlarmButton;
        private final ToggleButton mToggleAlarmButton;

        AlarmHolder(View itemView) {
            super(itemView);
            mSwipeRevealLayout = itemView.findViewById(R.id.swipe_layout);
            mAlarmLayout = itemView.findViewById(R.id.alarm_layout);
            mAlarmTimeView = itemView.findViewById(R.id.alarm_time);
            mAlarmTextView = itemView.findViewById(R.id.alarm_text);
            mDeleteAlarmButton = itemView.findViewById(R.id.delete_alarm_button);
            mToggleAlarmButton = itemView.findViewById(R.id.toggle_alarm_button);
        }

        void bindAlarm(final Alarm alarm) {
            mAlarmTimeView.setText(alarm.getNextTriggerTime().toString(DateTimeFormat.forPattern(CLOCK_FORMAT)));
            mAlarmTextView.setText(alarm.getPodcast().getName());

            mAlarmLayout.setOnClickListener(view -> startActivity(SetAlarmActivity.getIntent(AlarmListActivity.this, alarm)));

            mDeleteAlarmButton.setOnClickListener(view -> {
                mAlarmRepository.cancel(alarm);
                refreshAlarmList();
            });

            mToggleAlarmButton.setAlpha(alarm.isEnabled() ? 1.0f : 0.2f);
            mToggleAlarmButton.setChecked(alarm.isEnabled());
            mToggleAlarmButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                alarm.toggle(isChecked);
                mAlarmRepository.set(alarm);
                bindAlarm(alarm);
            });
        }
    }
}
