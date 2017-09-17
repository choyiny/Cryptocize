package app.cryptocize.com.cryptocize.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import app.cryptocize.com.cryptocize.MainApplication;
import app.cryptocize.com.cryptocize.R;
import app.cryptocize.com.cryptocize.models.AccountInformation;
import com.coinbase.CallbackWithRetrofit;
import com.coinbase.Coinbase;
import com.coinbase.v1.exception.CoinbaseException;
import com.coinbase.v2.models.account.Account;
import com.coinbase.v2.models.account.Accounts;
import com.coinbase.v2.models.account.Data;
import java.io.IOException;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;


public class AccountFragment extends Fragment {

  View myView;
  Coinbase coinbase;
  TextView walletFunds;
  AccountInformation accountInformation = new AccountInformation();
  String walletId;
  String vaultId;


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

    coinbase = ((MainApplication) getActivity().getApplicationContext()).getClient();

    myView = inflater.inflate(R.layout.fragment_account, container, false);

    // load values
    walletFunds = myView.findViewById(R.id.wallet_funds);

    getCryptocizeWalletInformation();

    return myView;

  }

  private void getCryptocizeWalletInformation() {
    // get preferences
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
    walletId = preferences.getString("cryptocize-wallet", "");
    vaultId = preferences.getString("cryptocize-vault", "");

    coinbase.getAccount(walletId, new CallbackWithRetrofit<Account>() {
      @Override
      public void onResponse(Call<Account> call, Response<Account> response, Retrofit retrofit) {

      }

      @Override
      public void onFailure(Call<Account> call, Throwable t) {

      }
    });
  }

}