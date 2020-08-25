package com.pushkar.packagecustomer.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.pushkar.packagecustomer.R;
import com.pushkar.packagecustomer.databinding.ActivityDirectionsBinding;
import com.pushkar.packagecustomer.utils.Constants;

public class DirectionsActivity extends AppCompatActivity {
    private ActivityDirectionsBinding databinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databinding = DataBindingUtil.setContentView(this,R.layout.activity_directions);
        String directions = getIntent().getStringExtra(Constants.DIRECTIONS_INTENT_KEY);
        databinding.directionsTV.setText(directions);
        databinding.foundStationBtn.setOnClickListener(v -> {
            finish();
        });
    }
}
