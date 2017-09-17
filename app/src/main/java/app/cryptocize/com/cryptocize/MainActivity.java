package app.cryptocize.com.cryptocize;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.icu.math.BigDecimal;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;
import app.cryptocize.com.cryptocize.fragments.AccountFragment;
import app.cryptocize.com.cryptocize.fragments.GoalsFragment;
import app.cryptocize.com.cryptocize.fragments.SettingsFragment;
import com.coinbase.CallbackWithRetrofit;
import com.coinbase.Coinbase;
import com.coinbase.v2.models.account.Account;
import com.coinbase.v2.models.account.Accounts;
import com.coinbase.v2.models.transactions.Transaction;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

  public static final String TAG_STEPS_COUNT = "stepsCount";

  SensorManager sensorManager;
  boolean walk = false;
  int step_counter = 0;
  Date currTime = Calendar.getInstance().getTime();
  SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
  String curr_time = sdf.format(currTime);
  SharedPreferences preferences;
  private boolean deducted = false;

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

    // set preferences - wallet is created
    if (!preferences.getBoolean("wallet-created", false)) {
      createWallet();
    }
    preferences.edit().putBoolean("wallet-created", true).apply();
  }


  protected void createWallet() {
    Coinbase coinbase = ((MainApplication) getApplicationContext()).getClient();

    // create vault
    HashMap<String, Object> vaultOptions = new HashMap<>();
    Log.d("hashmap", vaultOptions.size() + "");
    vaultOptions.put("name", "Cryptocize Vault");
    Log.d("hashmap", vaultOptions.size() + "");
    coinbase.createAccount(vaultOptions, new CallbackWithRetrofit<Account>() {
      @Override
      public void onResponse(Call<Account> call, Response<Account> response, Retrofit retrofit) {
        preferences.edit().putString("cryptocize-vault", response.body().getData().getId()).apply();
      }

      @Override
      public void onFailure(Call<Account> call, Throwable t) {
        Log.d("fail", "vault creation");
      }
    });

    coinbase.getAccounts(null, new CallbackWithRetrofit<Accounts>() {
      @Override
      public void onResponse(Call<Accounts> call, Response<Accounts> response,
          Retrofit retrofit) {

        preferences.edit().putString("cryptocize-wallet", response.body().getData().get(0).getId()).apply();
      }

      @Override
      public void onFailure(Call<Accounts> call, Throwable t) {

      }
    });


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
    weeklyNotif();
  }


  @Override
  protected void onPause() {
    super.onPause();
    walk = false;
    resetDay();
  }

  public void weeklyNotif() {
    Calendar calendar = Calendar.getInstance();
    int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
    final Coinbase coinbase = ((MainApplication) getApplicationContext()).getClient();

    if (AccountFragment.coin_amount == null) {
      return;
    }
    if (!deducted && weekDay == 1) {
      AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
      builder1.setMessage("Would you like to continue this week?");
      builder1.setCancelable(true);

      builder1.setPositiveButton(
          "Yes",
          new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
              Toast.makeText(getApplicationContext(), "Your automatic weekly amount is deducted.", Toast.LENGTH_SHORT).show();
              HashMap<String, Object> params2 = new HashMap<>();
              // Transfer from my wallet to vault
              params2.put("type", "transfer");
              params2.put("to", preferences.getString("cryptocize-vault", ""));
              params2.put("amount", (new BigDecimal(preferences.getString("bitAmt", "0"))).multiply(new BigDecimal(7)).toString());
              coinbase.transferMoney(AccountFragment.vaultId, params2, new CallbackWithRetrofit<Transaction>() {

                @Override
                public void onResponse(Call<Transaction> call, Response<Transaction> response,
                    Retrofit retrofit) {

                }

                @Override
                public void onFailure(Call<Transaction> call, Throwable t) {
                  Toast.makeText(getApplicationContext(), "Transfer not completed.", Toast.LENGTH_SHORT).show();
                }
              });
            }
          });

      builder1.setNegativeButton(
          "No",
          new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
              dialog.cancel();
            }
          });

      AlertDialog alert = builder1.create();
      alert.show();
    }
  }

  protected void resetDay() {
    Coinbase coinbase = ((MainApplication) getApplicationContext()).getClient();
    // reset every day
    if (curr_time.equals("00:00")) {
      // step counter is greater/e q than goal
      if (this.step_counter >= Integer.parseInt(preferences.getString("Step Goals", "0"))) {
        //transferring money from vault -> wallet
        HashMap<String, Object> params1 = new HashMap<>();
        params1.put("type", "transfer");
        params1.put("to", preferences.getString("cryptocize-wallet", ""));
        params1.put("amount", preferences.getString("bitAmt", "0"));
        coinbase.transferMoney(AccountFragment.vaultId, params1, new CallbackWithRetrofit<Transaction>() {

          @Override
          public void onResponse(Call<Transaction> call, Response<Transaction> response,
              Retrofit retrofit) {
            Toast.makeText(getApplicationContext(), "Transfer completed", Toast.LENGTH_SHORT).show();
          }

          @Override
          public void onFailure(Call<Transaction> call, Throwable t) {
            Toast.makeText(getApplicationContext(), "Transfer not completed.", Toast.LENGTH_SHORT).show();
          }
        });
        Log.d("giveback", "vault return to wallet");
      }
      // TODO: Store it to a separate file for number of steps each day

      Log.d("store", "num steps");

      // reset the steps for the next day
      this.step_counter = 0;
    }
  }

  @Override
  public void onSensorChanged(SensorEvent sensorEvent) {
    if (deducted && walk) { // (walk) {
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

}
