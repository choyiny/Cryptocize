package app.cryptocize.com.cryptocize.fragments;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
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
  private static int steps = 0;

  public static GoalsFragment newInstance() {
    GoalsFragment fragment = new GoalsFragment();
    Bundle bundle = new Bundle();
    fragment.setArguments(bundle);
    return fragment;
  }

  public void setSteps(int steps) {
//    TextView currStepTV = myView.findViewById(R.id.curr_step_tv);
//    currStepTV.setText(steps);
    if (currStepTV != null) {
      currStepTV.setText(String.valueOf(steps));
    }
  }

  TextView stepGoalTV;
  TextView currStepTV;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_goals, container, false);

    preferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
    stepGoalTV = (TextView) rootView.findViewById(R.id.step_goal_tv);
    currStepTV = (TextView) rootView.findViewById(R.id.curr_step_tv);

    stepGoalTV.setText(preferences.getString("Step Goals", ""));

    return rootView;
  }


  private void closeFragment() {

  }

  private void closeGoalsFragment() {
    ((MainActivity) getActivity()).doCloseGoals();
  }
}

/*
public class GoalsObj{

    private int goal_step;
    private double bit_amt;

    public GoalsObj(int goal, int bit) {
      goal_step = goal;
      bit_amt = bit;
    }
}*/
