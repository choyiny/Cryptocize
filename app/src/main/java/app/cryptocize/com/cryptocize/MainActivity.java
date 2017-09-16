package app.cryptocize.com.cryptocize;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
          FragmentManager fm = getFragmentManager();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                  fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                  fm.beginTransaction()
                      .replace(R.id.content_main2
                          , Fragment)
                      .addToBackStack("string")
                      .commit();
                    //mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                  fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                  fm.beginTransaction()
                      .replace(R.id.content_main2
                          , Frag2)
                      .addToBackStack("string")
                      .commit();
                    //mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                  fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                  fm.beginTransaction()
                      .replace(R.id.content_main2
                          , Frag3)
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }


}
