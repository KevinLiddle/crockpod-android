package com.krevin.crockpod.alarm.repositories;

import android.content.Context;
import android.util.Log;

import com.krevin.crockpod.alarm.Alarm;

import java.util.List;

public class AlarmRepository {

    private final AlarmDataRepository mDataRepository;
    private final AlarmServiceRepository mServiceRepository;

    public AlarmRepository(Context context) {
        mDataRepository = new AlarmDataRepository(context);
        mServiceRepository = new AlarmServiceRepository(context);
    }

    AlarmRepository(AlarmDataRepository dataRepo, AlarmServiceRepository serviceRepo) {
        mDataRepository = dataRepo;
        mServiceRepository = serviceRepo;
    }

    public void set(Alarm alarm) {
        if (alarm.isEnabled()) {
            mServiceRepository.set(alarm);
        } else {
            mServiceRepository.cancel(alarm);
        }
        mDataRepository.upsert(alarm);
    }

    public void cancel(Alarm alarm) {
        mServiceRepository.cancel(alarm);
        mDataRepository.remove(alarm);
    }

    public List<Alarm> list() {
        return mDataRepository.all();
    }

    public Alarm find(String id) {
        return mDataRepository.find(id);
    }
}
