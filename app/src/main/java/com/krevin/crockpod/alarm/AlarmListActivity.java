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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

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
        private final LinearLayout mDeleteAlarmLayout;
        private final Button mDeleteAlarmButton;
        private final ToggleButton mToggleAlarmButton;
        private final ImageButton mExpandButton;
        private final ImageButton mContractButton;

        AlarmHolder(View itemView) {
            super(itemView);
            mAlarmTextView = itemView.findViewById(R.id.alarm_text);
            mDeleteAlarmLayout = itemView.findViewById(R.id.delete_alarm_layout);
            mDeleteAlarmButton = itemView.findViewById(R.id.delete_alarm_button);
            mToggleAlarmButton = itemView.findViewById(R.id.toggle_alarm_button);
            mExpandButton = itemView.findViewById(R.id.alarm_item_expand);
            mContractButton = itemView.findViewById(R.id.alarm_item_contract);
        }

        void bindAlarm(final Alarm alarm) {
            String text = alarm.getNextTriggerTime()
                    .toString(DateTimeFormat.forPattern(CLOCK_FORMAT))
                    .concat(" - ")
                    .concat(alarm.getPodcast().getName());
            mAlarmTextView.setText(text);

            mDeleteAlarmButton.setOnClickListener(view -> {
                mAlarmRepository.cancel(alarm);
                refreshAlarmList();
            });

            mToggleAlarmButton.setChecked(alarm.isEnabled());
            setToggleAlarmButtonOpacity();
            mToggleAlarmButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                alarm.toggle(isChecked);
                setToggleAlarmButtonOpacity();
                mAlarmRepository.set(alarm);
            });

            mExpandButton.setOnClickListener(buttonView -> {
                mDeleteAlarmLayout.setVisibility(View.VISIBLE);
                mExpandButton.setVisibility(View.GONE);
                mContractButton.setVisibility(View.VISIBLE);
            });
            mContractButton.setOnClickListener(buttonView -> {
                mDeleteAlarmLayout.setVisibility(View.GONE);
                mExpandButton.setVisibility(View.VISIBLE);
                mContractButton.setVisibility(View.GONE);
            });
        }

        void setToggleAlarmButtonOpacity() {
            if (mToggleAlarmButton.isChecked()) {
                mToggleAlarmButton.setAlpha(1.0f);
            } else {
                mToggleAlarmButton.setAlpha(0.2f);
            }
        }
    }
}
