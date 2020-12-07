package ai.chadda.myruns_4.view;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import ai.chadda.myruns_4.view.fragments.SettingsFragment;

public class SettingsActivity extends AppCompatActivity {


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();

    }


}
