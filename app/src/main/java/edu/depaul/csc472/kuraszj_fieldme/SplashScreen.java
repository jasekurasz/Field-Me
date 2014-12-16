package edu.depaul.csc472.kuraszj_fieldme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

/**
 * Created by jasekurasz on 11/20/14.
 */
public class SplashScreen extends Activity {

    private static int SPLASH_TIMER = 6000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);

                finish();
            }

        }, SPLASH_TIMER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BounceBall v1 = (BounceBall) findViewById(R.id.ballBounce);
        v1.startAnimation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        BounceBall v1 = (BounceBall) findViewById(R.id.ballBounce);
        v1.stopAnimation();
    }
}
