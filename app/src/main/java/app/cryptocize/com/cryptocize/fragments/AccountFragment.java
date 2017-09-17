package app.cryptocize.com.cryptocize.fragments;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import app.cryptocize.com.cryptocize.R;
import app.cryptocize.com.cryptocize.models.CoinbaseInformation;
import java.io.Serializable;


public class AccountFragment extends Fragment {

  View myView;
  private static final String DESCRIBABLE_KEY = "describable_key";
  private CoinbaseInformation coinInfo;

  public static AccountFragment newInstance(Serializable obj) {
    AccountFragment fragment = new AccountFragment();
    Bundle bundle = new Bundle();
    bundle.putSerializable(DESCRIBABLE_KEY, obj);
    fragment.setArguments(bundle);
    return fragment;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      Bundle savedInstanceState) {
    myView = inflater.inflate(R.layout.fragment_account, container, false);

    coinInfo = (CoinbaseInformation) getArguments().getSerializable(DESCRIBABLE_KEY);
    // load values

    TextView textbox = myView.findViewById(R.id.wallet_funds);
    textbox.setText(coinInfo.coinbaseUserName);

    return myView;
  }

  private void closeFragment() {
    getActivity().getFragmentManager().popBackStack();
  }
}