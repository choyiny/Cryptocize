package app.cryptocize.com.cryptocize.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import app.cryptocize.com.cryptocize.R;


public class AccountFragment extends Fragment {

  View myView;

  public static AccountFragment newInstance() {
    AccountFragment fragment = new AccountFragment();
    Bundle bundle = new Bundle();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      Bundle savedInstanceState) {
    myView = inflater.inflate(R.layout.fragment_account, container, false);

    return myView;
  }

  private void closeFragment() {
    getActivity().getFragmentManager().popBackStack();
  }
}