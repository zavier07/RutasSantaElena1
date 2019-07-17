package sesion;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.rutas.santaelena.app.rutas.R;

import denuncias.AbstractAsyncActivity;
import denuncias.Denuncias;
import entities.SegUsuario;
import models.HttpGetUsers;

public class IniciaSesion extends AppCompatActivity {

    String username;
    String password;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Boolean savelogin;
    CheckBox savelogincheckbox;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_inicio_sesion);

        EditText editTextMail = (EditText) findViewById(R.id.editTextMailRegistrado);
        TextInputEditText editTextPass = (TextInputEditText) findViewById(R.id.editTextPassRegitra);
        Button btnIniciaSesion = (Button) findViewById(R.id.btnIniciaSesion);

        sharedPreferences = getSharedPreferences("loginref", MODE_PRIVATE);
        savelogincheckbox = (CheckBox) findViewById(R.id.chkRecordar);
        editor = sharedPreferences.edit();

        btnIniciaSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = editTextMail.getText().toString();
                password = editTextPass.getText().toString();

                if (validateLogin(username, password)) { //COMPROBAMOS QUE LOS TEXT NO ESTEN VACIOS
                    sendUser(username, password);
                    almacenarDatos();
                }
            }
        });
        savelogin = sharedPreferences.getBoolean("savelogin", true);
        if (savelogin == true) {
            editTextMail.setText(sharedPreferences.getString("username", null));
            editTextPass.setText(sharedPreferences.getString("password", null));
        }
        Button btnRegistro = (Button) findViewById(R.id.btnCrearCuenta);
        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(IniciaSesion.this, RegistraUsuario.class);
                startActivity(intent);
            }
        });

    }

    private boolean validateLogin(String username, String password) {
        if (username == null || username.trim().length() == 0) {
            Toast.makeText(this, "Email es requerido", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password == null || password.trim().length() == 0) {
            Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void clean() {
        EditText editTextMail = (EditText) findViewById(R.id.editTextMailRegistrado);
        editTextMail.setText("");
        TextInputEditText editTextPass = (TextInputEditText) findViewById(R.id.editTextPassRegitra);
        editTextPass.setText("");
    }

    private void displayResponse(SegUsuario segUsuarios) {

        if (segUsuarios != null) {
            Toast.makeText(this,"Bienvenido " + segUsuarios.getUsuario() ,Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(IniciaSesion.this, Denuncias.class);
            intent.putExtra("idUser",segUsuarios.getId());
            intent.putExtra("user", segUsuarios.getUsuario());
            intent.putExtra("email",segUsuarios.getEmail());
            intent.putExtra("movil",segUsuarios.getMovil());
            intent.putExtra("clave", segUsuarios.getClave());
            startActivity(intent);


        } else {
            Toast.makeText(this, "Datos Erroneos, vuelva a ingresr", Toast.LENGTH_LONG).show();
            clean();
        }

    }

    public void sendUser(String user, String pass) {
        AsyncTask<Object, Void, SegUsuario> httpGetUsers = new HttpGetUsers(new HttpGetUsers.AsyncResponse() {
            @Override
            public void processFinish(SegUsuario users) {
                displayResponse(users);
            }
        }).execute(user, pass);
    }

    private void almacenarDatos() {
        if (savelogincheckbox.isChecked()) {
            editor.putBoolean("savelogin", true);
            editor.putString("username", username);
            editor.putString("password", password);
            editor.commit();
        }


    }


}
