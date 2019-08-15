package lineas;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.rutas.santaelena.app.rutas.R;

import java.util.ArrayList;
import java.util.List;

import detectaRuta.Marcador;
import entities.EstadoBusTemporal;
import entities.Point;
import models.HttpGetAllbus;

public class AllBusesCirculando extends AppCompatActivity implements OnMapReadyCallback {
    //CLASE QUE MUESTRA EN EL MAPA LA RUTA Y SUS PARADAS DEL BUS SELECCIONADO  EN EL MENU
    String lineaBus = null;
    private GoogleMap mMap;
    private BusesMapa busesMapa;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                  /*  if (lineaBus!=null) {
                        busesMapa = new BusesMapa(mMap, this,lineaBus);
                        busesMapa.ini();
                    } */
                detener();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buses_circulando);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
            lineaBus = extras.getString("bus");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        System.out.println("bus corriendo : "  + lineaBus);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        /*lIMITA LA VISUALIZACION DEL MAPA SOLO EN LA PROVINCIA DE SANTA ELENA*/
       /* LatLngBounds limiteSantaElena = new LatLngBounds(
                new LatLng(-2.291430, -81.008619), new LatLng(-2.162431, -80.851164));
        mMap.setLatLngBoundsForCameraTarget(limiteSantaElena);*/

        LatLng SantaElena = new LatLng(-2.228228, -80.898366);
        CameraUpdate orig = CameraUpdateFactory.newLatLngZoom(SantaElena, 14);
        mMap.animateCamera(orig);
        ini();

    }

    private void getAllbusesCiruculando(){
        AsyncTask<Object, Void, List<EstadoBusTemporal>> httpGetAllbus = new HttpGetAllbus(new HttpGetAllbus.Posicionbus() {
            @Override
            public void busPosicion(List<EstadoBusTemporal> estadoBus) {
                if (estadoBus!=null)
                    new AnimateBusPosicion().mostrarBusesLista(mMap,getApplicationContext(),estadoBus);
                else {
                    Toast.makeText(getApplicationContext(), "No disponibles por el momento", Toast.LENGTH_LONG).show();
                    detener();
                }
            }
        }).execute(this);
    }

    Thread tThread = new Thread(new Runnable() {

        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {

                    getAllbusesCiruculando();

                    System.out.println("Todos los buses ahora ");
                    Thread.sleep(5000);

                } catch (InterruptedException ex) {

                    break;
                }
            }

        }

    });

    public void ini() {
        tThread.start();
    }

    public void detener() {
        tThread.interrupt();
        System.out.println("detener todos los buses");
    }
}

