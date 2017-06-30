package com.krevin.crockpod.alarm.repositories;

import android.content.Context;

import com.krevin.crockpod.alarm.Alarm;

import org.joda.time.DateTime;

import java.util.List;

public class AlarmRepository {

    private final AlarmDataRepository mDataRepository;
    private final AlarmServiceRepository mServiceRepository;

    public AlarmRepository(Context context) {
        mDataRepository = new AlarmDataRepository(context);
        mServiceRepository = new AlarmServiceRepository(context);
    }

    public void set(Alarm alarm) {
        DateTime nextTriggerTime = alarm.getNextTriggerTime();
        alarm.setHourOfDay(nextTriggerTime.getHourOfDay());
        alarm.setMinute(nextTriggerTime.getMinuteOfHour());

        mDataRepository.put(alarm);
        mServiceRepository.set(alarm);
    }

    public void cancel(Alarm alarm) {
        mServiceRepository.cancel(alarm);
        mDataRepository.remove(alarm.getId());
    }

    public List<Alarm> list() {
        return mDataRepository.all();
    }
}
