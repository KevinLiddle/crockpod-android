package com.krevin.crockpod.alarm;

import android.app.Activity;
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
import android.widget.ImageButton;
import android.widget.TextView;

import com.krevin.crockpod.R;
import com.krevin.crockpod.alarm.repositories.AlarmRepository;

import org.joda.time.format.DateTimeFormat;

import java.util.List;

public class AlarmListActivity extends Activity {

    private static final String CLOCK_FORMAT = "h:mma";

    private RecyclerView mAlarmList;
    private AlarmRepository mAlarmRepository;

    public static Intent getIntent(Context context) {
        return new Intent(context, AlarmListActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);

        mAlarmRepository = new AlarmRepository(this);

        FloatingActionButton mAddAlarmButton = (FloatingActionButton) findViewById(R.id.add_alarm_button);
        mAddAlarmButton.setOnClickListener(view -> startActivity(SetAlarmActivity.getIntent(AlarmListActivity.this)));

        mAlarmList = (RecyclerView) findViewById(R.id.alarm_list);
        mAlarmList.setHasFixedSize(true);
        mAlarmList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshAlarmList();
    }

    private void refreshAlarmList() {
        mAlarmList.swapAdapter(new AlarmListAdapter(mAlarmRepository.list()), true);
    }

    private class AlarmListAdapter extends RecyclerView.Adapter<AlarmHolder> {
        private final List<Alarm> mAlarms;

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
            holder.bindAlarm(mAlarms.get(position));
        }

        @Override
        public int getItemCount() {
            return mAlarms.size();
        }
    }

    private class AlarmHolder extends RecyclerView.ViewHolder {

        private final TextView mAlarmTextView;
        private final ImageButton mDeleteAlarmButton;

        AlarmHolder(View itemView) {
            super(itemView);
            mAlarmTextView = (TextView) itemView.findViewById(R.id.alarm_text);
            mDeleteAlarmButton = (ImageButton) itemView.findViewById(R.id.delete_alarm_button);
        }

        void bindAlarm(final Alarm alarm) {
            String text = alarm.getNextTriggerTime().toString(DateTimeFormat.forPattern(CLOCK_FORMAT)) +
                    " - " + alarm.getPodcast().getName();
            mAlarmTextView.setText(text);

            mDeleteAlarmButton.setOnClickListener(view -> {
                mAlarmRepository.cancel(alarm);
                refreshAlarmList();
            });
        }
    }
}
