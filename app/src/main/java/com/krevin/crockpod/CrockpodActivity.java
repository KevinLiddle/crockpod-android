package com.krevin.crockpod;

import android.app.Activity;


public class CrockpodActivity extends Activity {

    public CrockpodApp getCrockpodApp() {
        return (CrockpodApp) getApplication();
    }
}
