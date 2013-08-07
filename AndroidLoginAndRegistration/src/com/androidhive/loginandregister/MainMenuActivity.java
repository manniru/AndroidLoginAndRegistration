package com.androidhive.loginandregister;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainMenuActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
    }
    
    public void activities (View v) {
    	Intent i = new Intent(getApplicationContext(), ActivitiesActivity.class);
		startActivity(i);
    }
    
}