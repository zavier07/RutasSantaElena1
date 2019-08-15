package denuncias;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

import com.rutas.santaelena.app.rutas.R;

/**
 * Created by Javier on 29/05/2018.
 */


public class AbstractAsyncActivity extends FragmentActivity {

    protected static final String TAG = AbstractAsyncActivity.class.getSimpleName();
    private ProgressDialog progressDialog;

    private boolean destroyed = false;

   @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyed = true;
    }


    public void showLoadingProgressDialog() {
        this.showProgressDialog(getString(R.string.cargar_rutas));
    }

    public void showProgressDialog(CharSequence message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setIndeterminate(true);
        }

        progressDialog.setMessage(message);
        progressDialog.show();
    }

    public void dismissProgressDialog() {
        if (progressDialog != null && !destroyed) {
            progressDialog.dismiss();
        }
    }

}
