package app.cryptocize.com.cryptocize;

import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import app.cryptocize.com.cryptocize.models.CoinbaseInformation;
import com.coinbase.api.Coinbase;
import com.coinbase.api.CoinbaseBuilder;
import com.coinbase.api.entity.Account;
import com.coinbase.api.entity.Account.Type;
import com.coinbase.api.entity.AccountsResponse;
import com.coinbase.api.exception.CoinbaseException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

  public static final String TAG_STEPS_COUNT = "stepsCount";

  TextView steps_tv;
  SensorManager sensorManager;
  boolean walk = false;
  int step_counter = 0;
  Date currTime = Calendar.getInstance().getTime();
  SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
  String curr_time = sdf.format(currTime);
  //Log.d("TIMEE: ", sdf.format(currTime));
  int saved_counter = 0;
  SharedPreferences preferences;
  Coinbase coinbase;
  private View view;
  private TextView mTextMessage;
  private CoinbaseInformation coinbaseInfo;
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
                  , AccountFragment.newInstance(coinbaseInfo))
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

    // Authorize user on coinbase using API key
    coinbase = new CoinbaseBuilder()
        .withApiKey("N3c9qH9TrT7Qy8zb", "ahhGYPcr2KfyBHdibdV5Dyaa4o5MSsbU")
        .build();

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // get preferences
    preferences = PreferenceManager.getDefaultSharedPreferences(this);

//    if (savedInstanceState != null) {
////      saved_counter = Integer.parseInt(savedInstanceState.getString());
//      if (savedInstanceState.containsKey(TAG_STEPS_COUNT)) {
//        step_counter = savedInstanceState.get(TAG_STEPS_COUNT);
//      }
//
//    }

    step_counter = (savedInstanceState != null && savedInstanceState.containsKey(TAG_STEPS_COUNT)) ?
        savedInstanceState.getInt(TAG_STEPS_COUNT) : 0;

    //Log.d("SAVED COUNTER: ", Integer.toString(saved_counter));
    Date currTime = Calendar.getInstance().getTime();
    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
    Log.d("TIMEE: ", sdf.format(currTime));

    GoalsFragment goalsFragment = GoalsFragment.newInstance();

    FragmentManager fm = getFragmentManager();
    fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    fm.beginTransaction()
        .replace(R.id.container
            , goalsFragment, GoalsFragment.TAG)
        .addToBackStack("string")
        .commit();
    Log.d("goalsFragment", "" + goalsFragment);
    Log.d("view", "" + goalsFragment.getView());
    //steps_tv = (TextView) goalsFragment.getView().findViewById(R.id.curr_step_tv);

    welcomeUser();

    sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

    BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
    navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


  }

  /**
   * This renders the user name to the screen
   */
  protected void welcomeUser() {
    GetCoinBaseInformation job = new GetCoinBaseInformation();
    job.execute();

  }

  //start walking
  @Override
  protected void onResume() {
    super.onResume();
    walk = true;
    Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
    if (countSensor != null) {
      sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
    } else {
      Toast.makeText(this, "Sensor not found.", Toast.LENGTH_SHORT).show();
    }

  }

  @Override
  protected void onPause() {
    super.onPause();
    walk = false;

    resetDay();
  }

  protected void resetDay() {
    // reset every day
    if (curr_time.equals("00:00")) {
      // step counter is smaller than goal
      if (this.step_counter >= Integer.parseInt(preferences.getString("Step Goals", "0"))) {
        // TODO: give bitcoins back too their hot wallet
        Log.d("giveback", "wallet funds to vault");
      }
      // TODO: Store it to a separate file for number of steps each day
      Log.d("store", "num steps");

      // reset the steps for the next day
      this.step_counter = 0;
    }
  }

  @Override
  public void onSensorChanged(SensorEvent sensorEvent) {
    //float step_count;
    if (walk) {
      step_counter++;
    }
      //Log.d("STEPS: ", String.valueOf(sensorEvent.values[0]));
      Log.d("STEPS: ", String.valueOf(step_counter));
      GoalsFragment goals = (GoalsFragment) getFragmentManager().findFragmentByTag(GoalsFragment.TAG);
      if (goals != null) {
        goals.setSteps(step_counter);
      }
    //}
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int i) {

  }

  public void doCloseGoals() {
    getFragmentManager().popBackStackImmediate();
  }

  //save step counter value
  @Override
  protected void onSaveInstanceState(Bundle b) {
    b.putInt(TAG_STEPS_COUNT, step_counter);
    super.onSaveInstanceState(b);
  }

  /**
   * Getting all coinbase information on load
   */
  private class GetCoinBaseInformation extends AsyncTask<String, Void, String> {

    String username = "";

    @Override
    protected String doInBackground(String[] params) {
      String userName = "";

      // get username of user
      try {
        userName = coinbase.getUser().getName();
      } catch (IOException e) {
        e.printStackTrace();
      } catch (CoinbaseException e) {
        e.printStackTrace();
      }
      this.username = userName;
      coinbaseInfo = new CoinbaseInformation(userName);

      // get all wallets of the user
      AccountsResponse coinbaseUserAccounts = null;
      try {
        coinbaseUserAccounts = coinbase.getAccounts();
      } catch (IOException e) {
        e.printStackTrace();
      } catch (CoinbaseException e2) {
        e2.printStackTrace();
      }

      // find if there is cryptocize wallet
      boolean found = false;
      if (coinbaseUserAccounts != null) {
        for (Account account: coinbaseUserAccounts.getAccounts()) {
          if (account.getName().equals("cryptocize-wallet") {
            found = true;
            break;
          }
        }
      }

      // if not created, we want to create the wallet/vault for them
      if (!found) {
        // first wallet
        Account accountParam = new Account();
        accountParam.setName("cryptocize-wallet");
        accountParam.setType(Type.WALLET);
        try {
          coinbase.createAccount(accountParam);
        } catch (CoinbaseException e) {
          e.printStackTrace();
        } catch (IOException e2) {
          e2.printStackTrace();
        }
        // second wallet
        accountParam.setName("cryptocize-wallet");
        try {
          coinbase.createAccount(accountParam);
        } catch (CoinbaseException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }



      // other things we might want to be able to do with the Coinbase object




      return "complete";
    }

    @Override
    protected void onPostExecute(String message) {
      mTextMessage = (TextView) findViewById(R.id.username);
      mTextMessage.setText("Welcome " + this.username);
    }
  }
}
