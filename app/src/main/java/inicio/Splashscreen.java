package inicio;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
//import android.util.Log;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Window;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rutas.santaelena.app.rutas.R;

import com.rutas.santaelena.app.rutas.MapsActivity;

public class Splashscreen extends FragmentActivity {
    ProgressBar bar;
    TextView txt;
    private final int DURACION_SPLASH = 4000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        bar = (ProgressBar) findViewById(R.id.ProgressBar01);
        txt = (TextView) findViewById(R.id.txtrere);

        new Handler().postDelayed(new Runnable() {
            public void run() {

                    Intent intent = new Intent(Splashscreen.this,MapsActivity.class);
                    startActivity(intent);
                    Splashscreen.this.finish();
            }

        }, DURACION_SPLASH);
    }

}