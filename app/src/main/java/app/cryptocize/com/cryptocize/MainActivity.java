package app.cryptocize.com.cryptocize;

import android.app.FragmentManager;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import app.cryptocize.com.cryptocize.fragments.AccountFragment;
import app.cryptocize.com.cryptocize.fragments.GoalsFragment;
import app.cryptocize.com.cryptocize.fragments.SettingsFragment;
import com.coinbase.api.Coinbase;
import com.coinbase.api.CoinbaseBuilder;
import com.coinbase.api.exception.CoinbaseException;
import java.io.IOException;
import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private TextView mTextMessage;

    TextView steps_tv;
    SensorManager sensorManager;
    boolean walk = false;

  Coinbase coinbase;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
                          , GoalsFragment.newInstance())
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


    // Authorize user on coinbase using API key
    coinbase = new CoinbaseBuilder()
        .withApiKey("", "")
        .build();

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    steps_tv = (TextView) findViewById(R.id.curr_step_tv);

    welcomeUser();

    sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);


    BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
    navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


  }

  private class GetUserNameJob extends AsyncTask<String, Void, String> {

    String username = "";
    @Override
    protected String doInBackground(String[] params) {
      String userName = "";
      try {
        userName = coinbase.getUser().getName();
      } catch (IOException e) {
        e.printStackTrace();
      } catch (CoinbaseException e) {
        e.printStackTrace();
      }
      this.username = userName;
      return userName;
    }

    @Override
    protected void onPostExecute(String message) {
      mTextMessage = (TextView) findViewById(R.id.message);
      mTextMessage.setText("Welcome " + this.username);
    }
  }


  protected void welcomeUser() {
    GetUserNameJob job = new GetUserNameJob();
    job.execute();

  }

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
  }

  @Override
  protected void onPause() {
    super.onPause();
    walk = false;
  }

  @Override
  public void onSensorChanged(SensorEvent sensorEvent) {
    if (walk) {
      //steps_tv.setText(String.valueOf(sensorEvent.values[0]));
      Log.d("STEPS: ", String.valueOf(sensorEvent.values[0]));
    }
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int i) {

  }
}
