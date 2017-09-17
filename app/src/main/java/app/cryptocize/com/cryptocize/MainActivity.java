package app.cryptocize.com.cryptocize;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
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
import com.coinbase.CallbackWithRetrofit;
import com.coinbase.Coinbase;
import com.coinbase.OAuth;
import com.coinbase.v1.entity.OAuthTokensResponse;
import com.coinbase.v2.models.account.Account;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

  public static final String TAG_STEPS_COUNT = "stepsCount";
  public static final String API_KEY = "e9c477c59e4d863d46cc35787587aa7f4d8d7af8da8b6288763c41c36cc334ee";
  public static final String API_SECRET = "d7383cb899cfa53f4dfa90c7b9511c91ca79b0ca2f89bb0e044b8e463beae338";


  TextView usernameTV;

  SensorManager sensorManager;
  boolean walk = false;
  int step_counter = 0;
  Date currTime = Calendar.getInstance().getTime();
  SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
  String curr_time = sdf.format(currTime);
  SharedPreferences preferences;
  private View view;

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

    // load the view
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // textview
    usernameTV = (TextView) findViewById(R.id.username);

    // In the Activity we set up to listen to our redirect URI
    Intent intent = getIntent();
    if (intent != null && intent.getAction() != null && intent.getAction().equals("android.intent.action.VIEW")) {
      new CompleteAuthorizationTask(intent).execute();
    }


    // get preferences
    preferences = PreferenceManager.getDefaultSharedPreferences(this);

    step_counter = (savedInstanceState != null && savedInstanceState.containsKey(TAG_STEPS_COUNT)) ?
        savedInstanceState.getInt(TAG_STEPS_COUNT) : 0;

    GoalsFragment goals = (GoalsFragment) getFragmentManager().findFragmentByTag(GoalsFragment.TAG);
    if (goals != null) {
      goals.setSteps(step_counter);
    }

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


    sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

    BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
    navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
  }


  protected void createWallets() {
    // wallet is not created yet
    if (!preferences.getBoolean("wallet-created", false)) {
      Coinbase coinbase = ((MainApplication) getApplicationContext()).getClient();
      // create wallet
      HashMap<String, Object> walletOptions = new HashMap<>();
      walletOptions.put("name", "Cryptocize Wallet");
      coinbase.createAccount(walletOptions, new CallbackWithRetrofit<Account>() {
        @Override
        public void onResponse(Call<Account> call, Response<Account> response, Retrofit retrofit) {
          preferences.edit().putString("cryptocize-wallet", response.body().getData().getId()).apply();
        }

        @Override
        public void onFailure(Call<Account> call, Throwable t) {
          Log.d("fail", "wallet creation");
        }
      });
      // create vault
      HashMap<String, Object> vaultOptions = new HashMap<>();
      vaultOptions.put("name", "Cryptocize Vault");
      coinbase.createAccount(vaultOptions, new CallbackWithRetrofit<Account>() {
        @Override
        public void onResponse(Call<Account> call, Response<Account> response, Retrofit retrofit) {
          //preferences.edit().putString("cryptocize-vault", response.body().getData().getId()).apply();
        }

        @Override
        public void onFailure(Call<Account> call, Throwable t) {
          Log.d("fail", "vault creation");
        }
      });

      // set preferences - wallet is created
      preferences.edit().putBoolean("wallet-created", true).apply();

    } else {
      Log.d("wallet", "already created");
    }
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

  protected void auth(View v) {
    // oauth
    final OAuth oauth = ((MainApplication) getApplicationContext()).getOAuth();

    try {
      oauth.beginAuthorization(MainActivity.this,
          API_KEY,
          "wallet:user:read,wallet:accounts:read",
          "cryptocize://coinbase-oauth",
          null);
      createWallets();
    } catch (Exception e) {
      e.printStackTrace();
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
    if (walk) {
      step_counter++;
    }
      //Log.d("STEPS: ", String.valueOf(sensorEvent.values[0]));
      Log.d("STEPS: ", String.valueOf(step_counter));
      GoalsFragment goals = (GoalsFragment) getFragmentManager().findFragmentByTag(GoalsFragment.TAG);
      if (goals != null) {
        goals.setSteps(step_counter);
      }
    preferences.edit().putString("Step Count", Integer.toString(step_counter)).apply();

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

  private class CreateWalletsForApplication extends AsyncTask<String, Void, String> {
    private Intent mIntent;

    public CreateWalletsForApplication(Intent intent) {
      mIntent = intent;
    }

    @Override
    public String doInBackground(String[] params) {
      return "Hello";
    }
  }

  public class CompleteAuthorizationTask extends AsyncTask<Void, Void, OAuthTokensResponse> {
    private Intent mIntent;

    public CompleteAuthorizationTask(Intent intent) {
      mIntent = intent;
    }

    @Override
    public OAuthTokensResponse doInBackground(Void... params) {
      try {
        final OAuth oauth = ((MainApplication)getApplicationContext()).getOAuth();
        return oauth.completeAuthorization(MainActivity.this,
            API_KEY,
            API_SECRET,
            mIntent.getData());
      } catch (Exception e) {
        Toast.makeText(getBaseContext(), "authorization failed", Toast.LENGTH_SHORT);
        return null;
      }
    }

    @Override
    public void onPostExecute(OAuthTokensResponse tokens) {
      Coinbase coinbase = ((MainApplication)getApplicationContext()).getClient();
      coinbase.init(MainActivity.this, tokens.getAccessToken());
    }
  }

}
