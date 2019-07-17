package denuncias;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.rutas.santaelena.app.rutas.R;

import java.util.ArrayList;
import java.util.List;

import entities.Cooperativa;
import entities.Reporte;
import models.HttpGetAsuntos;
import models.HttpGetCooperativas;
import models.HttpPostDenuncias;

/**
 * Created by Javier on 29/08/2018.
 */

public class Denuncias extends AppCompatActivity {

   // protected static final String TAG = Denuncias.class.getSimpleName();
    private Spinner spinnerAsunto,spinnerCooperativas;
    private ArrayList<String> listaAsuntosTransporte = new ArrayList<String>();
    private ArrayList<String> listaCooperativas = new ArrayList<String>();
    private ArrayList<String> ListaIdCooperativas = new ArrayList<String>();
    private String email;
    private String username,idUser;
    private String movil;
    private String password;
    private EditText editTextPlaca;
    private EditText editTextNumeroDisco;
    private EditText editubicacion;
    private EditText editTextMensaje;
    private String id_CooperativaSend;

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

        setContentView(R.layout.activity_denuncias);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            idUser = extras.getString("idUser");
            username = extras.getString("user");
            email = extras.getString("email");
            movil = extras.getString("movil");
            password = extras.getString("clave");

        }


        editTextNumeroDisco = (EditText) findViewById(R.id.id_numeroDisco);
        editubicacion = (EditText) findViewById(R.id.id_ubicacionIncidente);
        editTextMensaje = (EditText) findViewById(R.id.id_mensage);
        spinnerAsunto = (Spinner) findViewById(R.id.id_asuntoSpinner);
        spinnerCooperativas =(Spinner) findViewById(R.id.idSpinnerCooperativas);
        editTextPlaca =(EditText) findViewById(R.id.editextPlaca);
        llenarSpinnerAsuntos();
        llenarSpinnerCooperativas();
        seleccionaCooperativa();

        final Button submitButton = (Button) findViewById(R.id.btnEnviar);
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (validateRegistro(editTextMensaje.getText().toString()))
                    enviaParametros();
            }
        });
    }

    private void displayResponse(Reporte Reporte) {
        sendEmail();
        Toast.makeText(this,  "Mensaje enviado con Exito :)  ", Toast.LENGTH_LONG).show();
        cleanEditext();

    }

    /**
     * Metodo llenarSpinnerAsuntos(): Obtiene los diferentes asuntos que seran presnetados
     */
    private void llenarSpinnerAsuntos() {

        AsyncTask<Object, Void, List<String>> httpGetAsuntos = new HttpGetAsuntos(new HttpGetAsuntos.AsynGetAsuntos() {
            @Override
            public void asuntos(List<String> asuntos) {
                if (asuntos != null) {
                    for (int j = 0; j < asuntos.size(); j++)
                        listaAsuntosTransporte.add(j, asuntos.get(j));
                    presentarDatosAsuntos(listaAsuntosTransporte);
                }
            }
        }).execute(this);
    }

    /**
     * Metodo llenarSpinnerCooperativas(): Obtiene los nombres de las diferentes cooperativas
     */
    private void llenarSpinnerCooperativas() {
        AsyncTask<Object, Void, List<Cooperativa>> httpGetCooperativas = new HttpGetCooperativas(new HttpGetCooperativas.AsynGetCooperativas() {
            @Override
            public void cooperativas(List<Cooperativa> cooperativas) {
                if (cooperativas!=null){
                    for(int j=0; j<cooperativas.size();j++) {
                        listaCooperativas.add(cooperativas.get(j).getNombre());
                        ListaIdCooperativas.add(cooperativas.get(j).getId());
                    }
                    presentarDatosCooperativa(listaCooperativas);
                }
            }
        }).execute(this);
    }

    private void seleccionaCooperativa(){
        spinnerCooperativas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                 id_CooperativaSend =  ListaIdCooperativas.get(position) ;
                System.out.println("idcooperativa : " + id_CooperativaSend);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void presentarDatosAsuntos(ArrayList listaAsuntos) {
        ArrayAdapter<String> adapterAsuntos = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, listaAsuntos);
        spinnerAsunto.setAdapter(adapterAsuntos);
    }

    private void presentarDatosCooperativa(ArrayList listaCooperativas) {
        ArrayAdapter<Cooperativa> adapterCooperativa = new ArrayAdapter<Cooperativa>(this, R.layout.support_simple_spinner_dropdown_item, listaCooperativas);
        spinnerCooperativas.setAdapter(adapterCooperativa);
    }

    private boolean validateRegistro(String mensaje) {

        if (mensaje == null || mensaje.trim().length() == 0) {
            Toast.makeText(this, "Detalle del Mensaje es requerido", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void enviaParametros() {
        AsyncTask<Object, Object, Reporte> httpPostDenuncias = new HttpPostDenuncias(new HttpPostDenuncias.AsynDenuncias() {
            @Override
            public void processDenuncia(Reporte reporte) {
                if (reporte!=null)
                    displayResponse(reporte);
                else
                    Toast.makeText(getApplicationContext(),"hubo un error", Toast.LENGTH_LONG);
            }
        }).execute(this,
                (String) spinnerAsunto.getSelectedItem(),
                id_CooperativaSend,
                editTextNumeroDisco.getText().toString(),
                editTextPlaca.getText().toString(),
                editTextMensaje.getText().toString(),
                username,
                password,
                editubicacion.getText().toString(),
                idUser);

    }

    public void cleanEditext() {
        editTextMensaje.setText("");
        editTextNumeroDisco.setText("");
        editubicacion.setText("");
        editTextPlaca.setText("");
    }

    private void sendEmail() {
        //Getting content for email
        String subject = (String) spinnerAsunto.getSelectedItem();
        String nuevalinea = System.getProperty("line.separator");
        String msmCompleto = ("Usuario : " + username + nuevalinea +
                "Telefóno: " + movil + nuevalinea +
                "# Disco : " + editTextNumeroDisco.getText().toString() + nuevalinea +
                "Cooperativa : " + (String) spinnerCooperativas.getSelectedItem()+ nuevalinea +
                "Placa: " + editTextPlaca.getText().toString() + nuevalinea +
                "Ubicacion: " + editubicacion.getText().toString() + nuevalinea +
                "Mensaje  :" + editTextMensaje.getText().toString());
        //Creating SendMail object
        SendMail sm = new SendMail(this, email, subject, msmCompleto);
        //Executing sendmail to send email
        sm.execute();
    }

}