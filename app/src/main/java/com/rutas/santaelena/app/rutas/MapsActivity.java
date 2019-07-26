package com.rutas.santaelena.app.rutas;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import denuncias.AbstractAsyncActivity;
import detectaRuta.Buses_disponibles;
import detectaRuta.EncuentraRuta;
import detectaRuta.Marcador;
import inicio.SplashInicio;
import lineas.BusesMapa;
import lineas.LineaBus;
import placesNearPoint.Radio;
import placesNearPoint.SearchPlacesParaderosNear;
import sesion.CustomInfoWindowAdapter;
import sesion.IniciaSesion;
import static com.rutas.santaelena.app.rutas.R.id.map;

public class MapsActivity extends AbstractAsyncActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {
    private Marker markerOrigen, markerFindelViajeEnbus;
    private boolean[] detectarRutaCercanaPuntoDestino, detectarRutaCercanaPuntoOrigen;
    private boolean rutaDisponible = false, origenRutaEncontrado = false;
    private ArrayList<LatLng> markerPointsOrigenDestino = new ArrayList<>();
    private EncuentraRuta encuentraRuta = new EncuentraRuta();
    private Marcador colocarMarker = new Marcador();
    private BusesMapa busesMapa;
    private HorizontalScrollView horizontalScrollView;
    private GoogleMap mMap;
    private List<ArrayList<LatLng>> listaRutasPuntosLatLng = new ArrayList<>();
    private List<String> nombreRutasTodasLineas = new ArrayList<>();
    private List<String> rutasDisponibleOrigenDestino = new ArrayList<>();
    private double distanciaOrigenDestino = 0;
    private LatLng origen;
    private AlertDialog alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        showLoadingProgressDialog();
        new SplashInicio().getTodasLineasBusesWS(this, new SplashInicio.OnOkRutaNombreCargadas() {@Override
        public void seleccionada(List<ArrayList<LatLng>> listaRutas, List<String> nombreRutasTodas) {
            listaRutasPuntosLatLng = listaRutas;
            nombreRutasTodasLineas = nombreRutasTodas;
            dismissProgressDialog();
            currentLocation();
        }});

        horizontalScrollView = findViewById(R.id.H_scroll);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else
            super.onBackPressed();
    }

    @SuppressWarnings("StatementWithEmptyBody") @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            if (busesMapa != null) busesMapa.detener();
            Intent intent = new Intent(MapsActivity.this, MapsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_contactos) {
            Intent intent = new Intent(MapsActivity.this, IniciaSesion.class);
            startActivity(intent);
        } else if (id == R.id.nav_ayuda) {
            Intent intent = new Intent(MapsActivity.this, CardHelp.class);
            startActivity(intent);
        } else if (id == R.id.nav_7) {
            String lineaBus = "7";
            Intent intent = new Intent(MapsActivity.this, LineaBus.class);
            intent.putExtra("linea", lineaBus);
            startActivity(intent);
        } else if (id == R.id.nav_8) {
            String lineaBus = "8";
            Intent intent = new Intent(MapsActivity.this, LineaBus.class);
            intent.putExtra("linea", lineaBus);
            startActivity(intent);
        } else if (id == R.id.nav_11) {
            String lineaBus = "11";
            Intent intent = new Intent(MapsActivity.this, LineaBus.class);
            intent.putExtra("linea", lineaBus);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-2.221538, -80.909117), 14.f));
       // currentLocation();
        PlaceAutocompleteFragment placeAutoCompleteOrigenDestino = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_Origendestino);
        placeAutoCompleteOrigenDestino.setBoundsBias(new LatLngBounds( //PERMITE QUE MIS PRIMERAS OPCIONES DE BUSQUEDA SEAN EN LA PENIN
                new LatLng(-2.291430, -81.008619), new LatLng(-2.162431, -80.851164)));
        placeAutoCompleteOrigenDestino.getView().setBackgroundColor(Color.WHITE);
        placeAutoCompleteOrigenDestino.setHint("¿A dónde deseas ir?");
        ImageView searchIcon = (ImageView) ((LinearLayout) placeAutoCompleteOrigenDestino.getView()).getChildAt(0);
        searchIcon.setImageDrawable(getResources().getDrawable(R.drawable.menu));
        searchIcon.setOnClickListener(new View.OnClickListener() {@Override
        public void onClick(View view) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.openDrawer(GravityCompat.START); //Despliega el menu
        }});

        /////////////  BUSQUEDA DEL ORIGEN/DESTINO INGRESANDO POR TEXTO ///////////////////////
        placeAutoCompleteOrigenDestino.setOnPlaceSelectedListener(new PlaceSelectionListener() {@Override
        public void onPlaceSelected(Place place) {
            LatLng latlangBusquedaTextoOrigenDestino = place.getLatLng(); //recuepramos la LatLong de la busqueda
            if (markerPointsOrigenDestino.size() == 0)
                colocarEnMapaPuntoOrigenDestino(latlangBusquedaTextoOrigenDestino);
            else
                colocarEnMapaPuntoOrigenDestino(latlangBusquedaTextoOrigenDestino);
        }@Override
        public void onError(Status status) { Log.d("Maps", "An error occurred: " + status); }
        });
        /**
         * METODOD PARA AGREGAR EN MARKER EN EL MAPA PULSO EN PANTALLA
         */
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {@Override
        public void onMapClick(LatLng pointOri_Dest) {
            colocarEnMapaPuntoOrigenDestino(pointOri_Dest);
        }});
    }

    /**
     * METODO PARA POSICIONAR UN PUNTO EN EL MAPA Y BORRADO DE MARCADORES Y METODO USADO PARA BUSQUEDAS POR TEXTO
     * @param pointOriDest
     */
    private void colocarEnMapaPuntoOrigenDestino(LatLng pointOriDest) {
        if (markerPointsOrigenDestino.size()==0)
             markerPointsOrigenDestino.add(markerOrigen.getPosition());
        //limpiamos el mapa y generamos un nuevo marcador si pulsamos o buscamos 3 veces en el map
        if (markerPointsOrigenDestino.size() > 1) { new Radio().continueNew(this, new Radio.OnOk() {@Override
        public void onOk(boolean onOk) {
            if (onOk) { //Si el usuario selecciona YES, se inicializara TodoDenuevo
                horizontalScrollView.setVisibility(View.INVISIBLE);
                if (busesMapa != null) busesMapa.detener();
                mMap.clear();
                markerPointsOrigenDestino = new ArrayList<>();
                rutaDisponible = false;
                origenRutaEncontrado = false;
                rutasDisponibleOrigenDestino = new ArrayList<>();
                markerPointsOrigenDestino.add(markerOrigen.getPosition());
                encuentraRutasAlPuntoOrigenOrigen(markerPointsOrigenDestino.get(0),200);
                markerPointsOrigenDestino.add(pointOriDest);
                encuentraRutaAlPuntoDestino(markerPointsOrigenDestino.get(1),200);
            } }});
        }

        if (markerPointsOrigenDestino.size()==1)
            encuentraRutasAlPuntoOrigenOrigen(markerPointsOrigenDestino.get(0),200);
        // agregamos punto origen/destino al array list
        markerPointsOrigenDestino.add(pointOriDest);
        if (markerPointsOrigenDestino.size()==2)
            encuentraRutaAlPuntoDestino(markerPointsOrigenDestino.get(1),200);

    }

    /**
     * Método que dibuja la ruta seleccionada de una lista
     * @param seleccion la ruta que elegimos
     * @param latLngPuntoCercanoAtomarElBus
     * @param latLngPuntoCercanoBajadaDelBus
     */
    private void mostrarRutaSeleccionada(int seleccion, LatLng latLngPuntoCercanoAtomarElBus, LatLng latLngPuntoCercanoBajadaDelBus) {
        horizontalScrollView.setVisibility(View.VISIBLE);
        markerFindelViajeEnbus = colocarMarker.colocarMarcadorRutaBusMasCercanaDetino(latLngPuntoCercanoBajadaDelBus, "Llegue hasta aqui", mMap, this);

        markerPointsOrigenDestino.add(latLngPuntoCercanoAtomarElBus);
        markerPointsOrigenDestino.add(latLngPuntoCercanoBajadaDelBus);

        new MostrarRutaSeleccion().mostrarRutaSeleccionada(seleccion, this, mMap, markerPointsOrigenDestino.get(0), latLngPuntoCercanoAtomarElBus, latLngPuntoCercanoBajadaDelBus, markerPointsOrigenDestino.get(1), listaRutasPuntosLatLng);
        new SearchPlacesParaderosNear().getParadasCercanasAPuntoWS(nombreRutasTodasLineas.get(seleccion), mMap, latLngPuntoCercanoAtomarElBus, this, 200);
        new SearchPlacesParaderosNear().getParadasCercanasAPuntoWS(nombreRutasTodasLineas.get(seleccion), mMap, latLngPuntoCercanoBajadaDelBus, this, 200);

        busesMapa = new BusesMapa(mMap, this, nombreRutasTodasLineas.get(seleccion));
        if (busesMapa != null) busesMapa.ini();
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(this,LayoutInflater.from(this), nombreRutasTodasLineas.get(seleccion), busesMapa, mMap)); //MUESTRA INFORMACION EN EL MARCADOR
    }

    private void currentLocation() {
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        /*if ( !lm.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            AlertNoGps();
        }*/

          if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
              ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 500);
              return;
          } else {
              if (!mMap.isMyLocationEnabled())
                  mMap.setMyLocationEnabled(true);

              Location myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

              if (myLocation == null) {
                  Criteria criteria = new Criteria();
                  criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                  String provider = lm.getBestProvider(criteria, true);
                  myLocation = lm.getLastKnownLocation(provider);
              }

              if (myLocation != null) {
                  origen = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                  markerOrigen = colocarMarker.colocarMarcadorPuntoOrigenEnMapa(origen, "Ud. está aquí", mMap, this);
              }
          }
    }
    @SuppressLint("MissingPermission") @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 500:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    mMap.setMyLocationEnabled(true);
                break;
        }
    }

    /**
     * Método que elimina un LatLng si no se encuentra una ruta
     * @param eliminarPunto punto que se eliminara y posteriormete sera ampliiado su radio de busqueda
     * @param puntosCercanos amplia el radio cuando el punto origen y destino estan distantes a 1100mtrs
     */
    private void noDetectaRuta(LatLng eliminarPunto, boolean puntosCercanos) { //ELIMINA UN LATLONG SI NO ESTA DENTRO DEL ALCNCE DE UNA RUTA o menor 1000MTS

        colocarMarker.msmOrigenDestino(markerPointsOrigenDestino,distanciaOrigenDestino,mMap,this);
        markerPointsOrigenDestino.remove(eliminarPunto);

        if (!puntosCercanos) {
            new Radio().radioPickSelectorAmpliarBusquedaRutas(this, new Radio.OnOkRadio() {
                @Override
                public void radio(int radio) {
                    markerPointsOrigenDestino.add(eliminarPunto);
                    if (markerPointsOrigenDestino.size()==1)
                        encuentraRutasAlPuntoOrigenOrigen(eliminarPunto, radio);
                    else
                        encuentraRutaAlPuntoDestino(eliminarPunto,radio);
                }
            });
        }
    }

    public void onClick(View v) { new SearchPlacesParaderosNear().onClick(v, mMap, markerPointsOrigenDestino, this, markerOrigen, markerFindelViajeEnbus,markerFindelViajeEnbus,markerFindelViajeEnbus); }

    private void encuentraRutaAlPuntoDestino(LatLng pointDestinoMapa, int radio){
        distanciaOrigenDestino = encuentraRuta.distanciasEntrePuntos(markerPointsOrigenDestino.get(0), pointDestinoMapa);
        if (distanciaOrigenDestino > 1100) {
            detectarRutaCercanaPuntoDestino = new boolean[listaRutasPuntosLatLng.size()];

            for (int i = 0; i < listaRutasPuntosLatLng.size(); i++)
                detectarRutaCercanaPuntoDestino[i] = encuentraRuta.encuentraRutaBus(pointDestinoMapa, listaRutasPuntosLatLng.get(i), false, radio, mMap);

            List<LatLng> listPointMasCercanoOrigenRuta = new ArrayList<>();
            List<LatLng> listPointMasCercanoDestiRuta = new ArrayList<>();
            List<Double> listDistanciasOrigenRuta = new ArrayList<>();
            List<Double> listDistanciasRutaDestino = new ArrayList<>();

            for (int i = 0; i < detectarRutaCercanaPuntoDestino.length; i++) {
                if (detectarRutaCercanaPuntoDestino[i] && detectarRutaCercanaPuntoOrigen[i]) {
                    rutaDisponible = true;
                    rutasDisponibleOrigenDestino.add(nombreRutasTodasLineas.get(i));

                    LatLng findPtoOrigenRuta = new EncPuntoCerPoli().findNearestPoint(markerPointsOrigenDestino.get(0), listaRutasPuntosLatLng.get(i));//Encontramos el punto mas cercano Mi Ubi - Poli
                    listPointMasCercanoOrigenRuta.add(findPtoOrigenRuta);

                    double distUbiRuta = encuentraRuta.distanciasEntrePuntos(findPtoOrigenRuta, markerPointsOrigenDestino.get(0));
                    distUbiRuta = Double.parseDouble(new DecimalFormat("##,###").format(distUbiRuta));
                    listDistanciasOrigenRuta.add(distUbiRuta);

                    LatLng findPtoDesRuta = new EncPuntoCerPoli().findNearestPoint(pointDestinoMapa, listaRutasPuntosLatLng.get(i));//Encontramos el punto mas cercano Dest-Poli
                    listPointMasCercanoDestiRuta.add(findPtoDesRuta);

                    double distDestRuta = encuentraRuta.distanciasEntrePuntos(findPtoDesRuta, pointDestinoMapa);
                    distDestRuta = Double.parseDouble(new DecimalFormat("##,###").format(distDestRuta));
                    listDistanciasRutaDestino.add(distDestRuta);
                }
            }

            if (!rutaDisponible)
                noDetectaRuta(pointDestinoMapa,false);
            else {
                new Buses_disponibles().alertLineas(rutasDisponibleOrigenDestino, nombreRutasTodasLineas, this, listDistanciasOrigenRuta, listDistanciasRutaDestino, listPointMasCercanoOrigenRuta, listPointMasCercanoDestiRuta, new Buses_disponibles.OnOklineaSeleccionada() {
                    @Override
                    public void seleccionada(int seleccion, LatLng puntoCercanoATomarElBus, LatLng puntoCercanoBajarElBus) {
                        mostrarRutaSeleccionada(seleccion, puntoCercanoATomarElBus, puntoCercanoBajarElBus);
                    }});
            }
        } else
            noDetectaRuta(pointDestinoMapa,true);
    }
    private void encuentraRutasAlPuntoOrigenOrigen(LatLng pointOrigenMapa, int radio){

        detectarRutaCercanaPuntoOrigen = new boolean[listaRutasPuntosLatLng.size()];
        for (int i = 0; i < listaRutasPuntosLatLng.size(); i++)
            detectarRutaCercanaPuntoOrigen[i] = encuentraRuta.encuentraRutaBus(pointOrigenMapa, listaRutasPuntosLatLng.get(i), false, radio, mMap);

        for (int i = 0; i < detectarRutaCercanaPuntoOrigen.length; i++)
            if (detectarRutaCercanaPuntoOrigen[i]) {
                origenRutaEncontrado = true;
                break;
            }
        if (!origenRutaEncontrado)
            noDetectaRuta(pointOrigenMapa,false);
        if (markerPointsOrigenDestino.size()!=0)
            markerOrigen = colocarMarker.colocarMarcadorPuntoOrigenEnMapa(origen, "Ud. está aquí", mMap, this);
    }

    private void AlertNoGps() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("El sistema GPS esta desactivado, ¿Desea activarlo?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                        alert.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();

                        alert.dismiss();
                    }
                });
        alert = builder.create();
        alert.show();
    }

}