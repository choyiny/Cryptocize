package app.cryptocize.com.cryptocize.fragments;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import app.cryptocize.com.cryptocize.MainActivity;
import app.cryptocize.com.cryptocize.R;


public class GoalsFragment extends Fragment {

  public static final String TAG = "goals_fragment";

  SharedPreferences preferences;

  public static GoalsFragment newInstance() {
    GoalsFragment fragment = new GoalsFragment();
    Bundle bundle = new Bundle();
    fragment.setArguments(bundle);
    return fragment;
  }

  public void setSteps(int steps) {
    if (currStepTV != null) {
      Log.d("set steps", steps + "");

      currStepTV.setText(String.valueOf(steps));
    }
//
//    if (Integer.parseInt((currStepTV.getText()).toString())==0){
//      Log.d("HEEEERREEEEEEE", "HEEYEYEYEYEY");
//    }
  }

  TextView stepGoalTV;
  TextView currStepTV;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_goals, container, false);

    preferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
    stepGoalTV = rootView.findViewById(R.id.step_goal_tv);
    currStepTV = rootView.findViewById(R.id.curr_step_tv);

    currStepTV.setText(preferences.getString("Step Count", ""));

    String goal_text = (preferences.getString("Step Goals", "") != "") ? preferences.getString("Step Goals", "") : "0";
    stepGoalTV.setText(goal_text);

    return rootView;
  }

  private void closeGoalsFragment() {
    ((MainActivity) getActivity()).doCloseGoals();
  }
}

