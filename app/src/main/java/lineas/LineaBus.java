package lineas;

import android.graphics.Color;
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
import com.google.android.gms.maps.model.PolylineOptions;
import com.rutas.santaelena.app.rutas.R;
import java.util.ArrayList;
import java.util.List;

import detectaRuta.Marcador;
import entities.Parada;
import entities.Point;
import entities.Ruta;
import models.HttpGetLinea;
import models.HttpGetParadasLinea;

public class LineaBus extends AppCompatActivity implements OnMapReadyCallback {
    //CLASE QUE MUESTRA EN EL MAPA LA RUTA Y SUS PARADAS DEL BUS SELECCIONADO  EN EL MENU
    String lineaBus = null;
    private GoogleMap mMap;
    List<Point> listPuntos;
    private ArrayList<LatLng> listaPuntoosPardaderos = new ArrayList<>();
    private ArrayList<LatLng> listaPuntosRuta = new ArrayList<>();
    private Marcador marcador = new Marcador();
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lineas_buses);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        Bundle extras = getIntent().getExtras();
        if (extras != null)
            lineaBus = extras.getString("linea");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        /*lIMITA LA VISUALIZACION DEL MAPA SOLO EN LA PROVINCIA DE SANTA ELENA*/
       /* LatLngBounds limiteSantaElena = new LatLngBounds(
                new LatLng(-2.291430, -81.008619), new LatLng(-2.162431, -80.851164));
        mMap.setLatLngBoundsForCameraTarget(limiteSantaElena);*/
        consultaLinea();
        LatLng SantaElena = new LatLng(-2.228228, -80.898366);
        CameraUpdate orig = CameraUpdateFactory.newLatLngZoom(SantaElena, 14);
        mMap.animateCamera(orig);

    }


    public void consultaLinea(){

        AsyncTask<Object, Void, Ruta> httpLineas = new HttpGetLinea(new HttpGetLinea.AsyncResponse2() {
            @Override
            public void processFinish(Ruta rutaModel) {
                if (rutaModel != null) {
                    listPuntos = rutaModel.getListasPuntos();
                    polilineaRestful(listPuntos);
                    sitiosConcurridos();
                   // paraderosWpt();
                }
                else
                     Toast.makeText(getApplicationContext(),getString(R.string.ruta_noDisponible),Toast.LENGTH_SHORT).show();
            }
        }).execute(lineaBus,this);

    }

    public void polilineaRestful(List<Point> listasPuntos) {

        for (int i = 0; i < listasPuntos.size(); i++) {

            double latitude =  listasPuntos.get(i).getY();
            double longitude = listasPuntos.get(i).getX();

            LatLng punto = new LatLng(latitude, longitude);
            listaPuntosRuta.add(punto);
        }
        mMap.addPolyline(new PolylineOptions().addAll(listaPuntosRuta).width(5).color(Color.BLUE));
        Toast.makeText(getApplicationContext(),getString(R.string.mostrarRuta)+" " + lineaBus,Toast.LENGTH_SHORT).show();

    }

    private void paraderosWpt(){

       AsyncTask<Object, Void, List<Parada>> httpGetParadasLinea = new HttpGetParadasLinea(new HttpGetParadasLinea.AsyncResponse2() {
           @Override
           public void processFinish(List<Parada> paradas) {
               if (paradas!=null)
                   getCoordenadas(paradas);
           }
       }).execute(lineaBus,this);

    }

    private void getCoordenadas(List<Parada> paradas){

        for (int i = 0; i < paradas.size(); i++) {

            double latitude =  paradas.get(i).getCoordenada().getY();
            double longitude = paradas.get(i).getCoordenada().getX();

            LatLng punto = new LatLng(latitude, longitude);
            listaPuntoosPardaderos.add(punto);

        }
        dibujaParaderos();
    }

    private void dibujaParaderos(){
        for (int i=0; i<listaPuntoosPardaderos.size();i++)
            marcador.colocarParaderosRutaBus(listaPuntoosPardaderos.get(i),getString(R.string.paraderoLinea) + lineaBus,mMap,this);

    }

    private void sitiosConcurridos(){
         ArrayList<LatLng> listaPuntosSitios = new ArrayList<>();
        ArrayList<String> listanameSitios = new ArrayList<>();

        listaPuntosSitios.add(new LatLng(-2.226245, -80.921130));//paseo shoping
        listaPuntosSitios.add(new LatLng(-2.224094, -80.909905));//buenaventura
        listaPuntosSitios.add(new LatLng(-2.233049, -80.877961));//Upse
        listaPuntosSitios.add(new LatLng(-2.216555, -80.855276));//Tablazo
        listaPuntosSitios.add(new LatLng(-2.231403, -80.852838));//Liborio Panchana
        listaPuntosSitios.add(new LatLng(-2.216246, -80.866268));//Temrinal Sumpa
        listaPuntosSitios.add(new LatLng(-2.205118, -80.972596));//mALECON sLINAS
        listaPuntosSitios.add(new LatLng(-2.199854, -80.972521));//yATCH CLUB SALINAS
        listaPuntosSitios.add(new LatLng(-2.215014, -80.961556));//country club
        listaPuntosSitios.add(new LatLng(-2.217878, -80.922761));//yatch club libertad


        listanameSitios.add("El Paseo Shopping La Peninsula");
        listanameSitios.add("Centro comercial buenaventura moreno");
        listanameSitios.add("Estatal Península de Santa Elena (UPSE)");
        listanameSitios.add("Mirador Cerro El Tablazo");
        listanameSitios.add("Dr. Liborio Panchana");
        listanameSitios.add("Terminal Terrestre Regional Sumpa - Santa Elena");
        listanameSitios.add("Malecón Salinas");
        listanameSitios.add("Salinas Yacht Club");
        listanameSitios.add("Salinas Contry Club");
        listanameSitios.add("Puerto Lucía Yacht Club");

        for (int i=0; i<listaPuntosSitios.size();i++)
            marcador.colocarMarcadorSitiod(listaPuntosSitios.get(i),listanameSitios.get(i) ,mMap,this);

    //    paraderosWpt();
    }
}

