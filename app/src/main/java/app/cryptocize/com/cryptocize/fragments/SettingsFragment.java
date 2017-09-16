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
import app.cryptocize.com.cryptocize.R;
import java.math.BigDecimal;


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

    Button button = (Button) myView.findViewById(R.id.submit_bt);
    button.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {

        if (((EditText) myView.findViewById(R.id.goal_et)).getText().toString() != ""||
            ((EditText) myView.findViewById(R.id.bit_amt_et)).getText().toString() != ""){
          int stepGoal = Integer.valueOf(((EditText) myView.findViewById(R.id.goal_et)).getText().toString());
          double bitAmt = Double.valueOf(((EditText) myView.findViewById(R.id.bit_amt_et)).getText().toString());
          //stores values
          preferences.edit().putString("Step Goals", Integer.toString(stepGoal)).apply();
          preferences.edit().putString("bitAmpt", Double.toString(bitAmt)).apply();
//          TextView stepGoalTV = (TextView) gf.myView.findViewById(R.id.step_goal_tv);
//          TextView bitAmtTV = gf.myView.findViewById(R.id.curr_step_tv);
//          stepGoalTV.setText(Integer.toString(stepGoal));
//          bitAmtTV.setText(Double.toString(bitAmt));
        }

      }
    });
    return myView;
  }

  private void closeFragment() {
    getActivity().getFragmentManager().popBackStack();
  }
}