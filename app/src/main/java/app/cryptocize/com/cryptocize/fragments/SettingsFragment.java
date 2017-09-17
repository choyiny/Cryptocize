package app.cryptocize.com.cryptocize.fragments;

import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import app.cryptocize.com.cryptocize.MainApplication;
import app.cryptocize.com.cryptocize.R;
import com.coinbase.CallbackWithRetrofit;
import com.coinbase.Coinbase;
import com.coinbase.v2.models.transactions.Transaction;
import java.math.BigDecimal;
import java.util.HashMap;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;


public class SettingsFragment extends Fragment {

  View myView;

  public static SettingsFragment newInstance() {
    SettingsFragment fragment = new SettingsFragment();
    Bundle bundle = new Bundle();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      Bundle savedInstanceState) {
    myView = inflater.inflate(R.layout.fragment_settings, container, false);

    final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
    TextView bit_amount = myView.findViewById(R.id.curr_bit_am_tv);
    bit_amount.setText(preferences.getString("bitAmt", ""));
    Button button = (Button) myView.findViewById(R.id.submit_bt);
    button.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {

        if (((EditText) myView.findViewById(R.id.goal_et)).getText().toString() != ""||
            ((EditText) myView.findViewById(R.id.bit_amt_et)).getText().toString() != ""){
          int stepGoal = Integer.valueOf(((EditText) myView.findViewById(R.id.goal_et)).getText().toString());
          BigDecimal bitAmt = new BigDecimal(((EditText) myView.findViewById(R.id.bit_amt_et)).getText().toString());
          //stores values
          preferences.edit().putString("Step Goals", Integer.toString(stepGoal)).apply();
          if (preferences.getString("bitAmt", "").equals("")) {
            preferences.edit().putString("bitAmt", bitAmt.toString()).apply();
          } else {
            BigDecimal finalAmount = new BigDecimal(preferences.getString("bitAmt", "0")).add(new BigDecimal(bitAmt.toString()));
            preferences.edit().putString("bitAmt", finalAmount.toString()).apply();

            HashMap<String, Object> params = new HashMap<>();
            params.put("type", "transfer");
            params.put("to", preferences.getString("cryptocize-vault", ""));
            params.put("amount", bitAmt.toString());
            Coinbase coinbase = ((MainApplication) getActivity().getApplicationContext()).getClient();
            coinbase.transferMoney(preferences.getString("cryptocize-wallet", ""), params,
                new CallbackWithRetrofit<Transaction>() {
                  @Override
                  public void onResponse(Call<Transaction> call, Response<Transaction> response,
                      Retrofit retrofit) {
                  //  Toast.makeText(getContext(), "Transfer Success", Toast.LENGTH_SHORT).show();
                  }

                  @Override
                  public void onFailure(Call<Transaction> call, Throwable t) {

                  }
                });
          }

          EditText goal_field = myView.findViewById(R.id.goal_et);
          goal_field.setText("");
          EditText coin_field = myView.findViewById(R.id.bit_amt_et);
          coin_field.setText("");
          TextView bit_amount = myView.findViewById(R.id.curr_bit_am_tv);
          bit_amount.setText(preferences.getString("bitAmt", ""));
        }

      }
    });
    return myView;
  }

  private void closeFragment() {
    getActivity().getFragmentManager().popBackStack();
  }
}