package app.cryptocize.com.cryptocize.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import app.cryptocize.com.cryptocize.R;


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

    return myView;
  }

  private void closeFragment() {
    getActivity().getFragmentManager().popBackStack();
  }
}