package app.cryptocize.com.cryptocize;

import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import app.cryptocize.com.cryptocize.fragments.AccountFragment;
import app.cryptocize.com.cryptocize.fragments.GoalsFragment;
import app.cryptocize.com.cryptocize.fragments.SettingsFragment;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.Inflater;
import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private View view;
    private TextView mTextMessage;
    TextView steps_tv;
    SensorManager sensorManager;
    boolean walk = false;
    int step_counter = 0;
    Date currTime = Calendar.getInstance().getTime();
    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
    String curr_time = sdf.format(currTime);
    //Log.d("TIMEE: ", sdf.format(currTime));
    int saved_counter = 0;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
          View holder;
          FragmentManager fm = getFragmentManager();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                  fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                  fm.beginTransaction()
                      .replace(R.id.container
                          , AccountFragment.newInstance())
                      .addToBackStack("string")
                      .commit();
                    //mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                  fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                  fm.beginTransaction()
                      .replace(R.id.container
                          , GoalsFragment.newInstance(), "goals_fragment")
                      .addToBackStack("string")
                      .commit();
                    //mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_settings:
                  fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                  fm.beginTransaction()
                      .replace(R.id.container
                          , SettingsFragment.newInstance())
                      .addToBackStack("string")
                      .commit();
                    //mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }

    };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    if (savedInstanceState != null) {
      saved_counter = Integer.parseInt(savedInstanceState.getString("StepCounter"));
    }
    //Log.d("SAVED COUNTER: ", Integer.toString(saved_counter));
    Date currTime = Calendar.getInstance().getTime();
    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
    Log.d("TIMEE: ", sdf.format(currTime));

    FragmentManager fm = getFragmentManager();
    fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    fm.beginTransaction()
        .replace(R.id.container
            , GoalsFragment.newInstance(), "goals_fragment")
        .addToBackStack("string")
        .commit();
    GoalsFragment gm = (GoalsFragment)getFragmentManager().findFragmentByTag("goals_fragment");
    steps_tv = (TextView) gm.get_view().findViewById(R.id.curr_step_tv);

    sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

    mTextMessage = (TextView) findViewById(R.id.message);
    BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
    navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


  }

  //start walking
  @Override
  protected void onResume() {
    super.onResume();
    walk = true;
    Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
    if(countSensor != null){
      sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
    } else {
      Toast.makeText(this, "Sensor not found.", Toast.LENGTH_SHORT).show();
    }

    //reset counter
    if(curr_time.equals("00:00")) {
      step_counter = 0;
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    walk = false;

    //reset counter
    if(curr_time.equals("00:00")) {
      step_counter = 0;
    }
  }

  @Override
  public void onSensorChanged(SensorEvent sensorEvent) {
    //float step_count;
    if (walk) {
      step_counter++;
      //Log.d("STEPS: ", String.valueOf(sensorEvent.values[0]));
      Log.d("STEPS: ", String.valueOf(step_counter));
      if (steps_tv!=null) {
        steps_tv.setText(String.valueOf(step_counter));
      }
    }
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int i) {

  }

  //save step counter value
  @Override
  protected void onSaveInstanceState(Bundle b){
    b.putString("StepCounter", Integer.toString(step_counter));
    super.onSaveInstanceState(b);
  }
}
