package app.cryptocize.com.cryptocize;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;
import com.coinbase.Coinbase;
import com.coinbase.OAuth;
import com.coinbase.v1.entity.OAuthTokensResponse;

/**
 * Created by choyiny on 17/9/2017.
 */

public class LoginActivity extends AppCompatActivity {


  public static final String API_KEY = "e9c477c59e4d863d46cc35787587aa7f4d8d7af8da8b6288763c41c36cc334ee";
  public static final String API_SECRET = "d7383cb899cfa53f4dfa90c7b9511c91ca79b0ca2f89bb0e044b8e463beae338";

  SharedPreferences preferences;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // load view
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    preferences = PreferenceManager.getDefaultSharedPreferences(this);

    // In the Activity we set up to listen to our redirect URI
    Intent intent = getIntent();
    if (intent != null && intent.getAction() != null && intent.getAction().equals("android.intent.action.VIEW")) {
      new CompleteAuthorizationTask(intent).execute();
    }

}

  protected void auth(View v) {
    // oauth
    final OAuth oauth = ((MainApplication) getApplicationContext()).getOAuth();

    try {
      oauth.beginAuthorization(LoginActivity.this,
          API_KEY,
          "wallet:user:read,wallet:accounts:read,wallet:accounts:create,wallet:transactions:transfer",
          "cryptocize://coinbase-oauth",
          null);
    } catch (Exception e) {
      e.printStackTrace();
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
        return oauth.completeAuthorization(LoginActivity.this,
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
      coinbase.init(LoginActivity.this, tokens.getAccessToken());
      Intent intent = new Intent(getBaseContext(), MainActivity.class);
      startActivity(intent);
    }
  }

}
