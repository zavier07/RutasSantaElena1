package placesNearPoint;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.rutas.santaelena.app.rutas.R;

import java.util.ArrayList;
import java.util.List;

import detectaRuta.Buses_disponibles;
import detectaRuta.EncuentraRuta;
import detectaRuta.SeleccionUbicacion;
import entities.Parada;
import lineas.BusesMapa;
import lineas.LineAllWayPcercanos;
import lineas.LineaBusesDisponibles;
import models.HttpGetParadasCercanas;
import netDisponible.DisponibleNet;
import sesion.CustomInfoWindowAdapter;

public class SearchPlacesParaderosNear extends FragmentActivity {

    LineAllWayPcercanos lineAllWayPcercanos = new LineAllWayPcercanos();
    SeleccionUbicacion seleccionUbicacion = new SeleccionUbicacion();
    EncuentraRuta encuentraRuta = new EncuentraRuta();
    Radio radioIngresa = new Radio();
    Circle dibujaRadio;
    DisponibleNet disponibleNet = new DisponibleNet();
    boolean conectaNet;
    private BusesMapa busesMapa;

    public void onClick(View v, GoogleMap mMap, ArrayList<LatLng> opcionOrigenDestino, Context context,
                        Marker origen, Marker destino, Marker abordarBus, Marker bajadaBus, String lineaBus) {

        switch (v.getId()) {

            case R.id.flt_restaurant:
                conectaNet = disponibleNet.compruebaConexion(context);
                if (conectaNet == true) {
                    if (opcionOrigenDestino.size() > 1) {
                        seleccionUbicacion.SeleccionaOpcionUbiDest(context, new SeleccionUbicacion.OnOkOrigenDestino() {
                            @Override
                            public void seleccionadaUbicacion(int seleccionUbicacion) {


                                radioIngresa.radioPickSelector(context, new Radio.OnOkRadio() {
                                    @Override
                                    public void radio(int radio) {

                                        DataPlaces(mMap, opcionOrigenDestino.get(seleccionUbicacion), "restaurant", radio, context);

                                    }
                                });
                            }
                        });
                    } else
                        mensaje(context);
                } else
                    Toast.makeText(context, "Requiere conexion a Internet  ", Toast.LENGTH_SHORT).show();
                break;

            case R.id.fltTomarElBus:
                if (opcionOrigenDestino.size() > 1)
                    posicionElegida(opcionOrigenDestino.get(2), mMap, abordarBus, "Aqui tome el bus !Presione para mas informacion! ", context);
                else
                    mensaje(context);
                break;

            case R.id.fltBajadaDelBus:
                if (opcionOrigenDestino.size() > 1)
                    posicionElegida(opcionOrigenDestino.get(3), mMap, bajadaBus, "Pf siga la linea negra hasta llegar a su destino", context);
                else
                    mensaje(context);
                break;

            case R.id.fltDestino:
                if (opcionOrigenDestino.size() > 1)
                    posicionElegida(opcionOrigenDestino.get(1), mMap, destino, "Tú destino aquí !Presione para mas informacion!", context);
                else
                    mensaje(context);
                break;

            case R.id.idhibrido:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;

            case R.id.idnormal:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;

            case R.id.idVerbus:
                if (opcionOrigenDestino.size() > 1)
                    new LineaBusesDisponibles().busesCirculando(lineaBus, context, mMap);
                else
                    mensaje(context);
                break;

            case R.id.idinfo:
                new Buses_disponibles().infoMapsForever(context);
                break;

        }

    }

    private void DataPlaces(GoogleMap mMap, LatLng opcionOrigenDestino, String text, int radio, Context context) {

        Object dataTransfer[] = new Object[3];

        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();

        dataTransfer = new Object[3];
        String url = getNearbyPlacesData.getUrl(opcionOrigenDestino.latitude, opcionOrigenDestino.longitude, text, radio);
        getNearbyPlacesData = new GetNearbyPlacesData();
        dataTransfer[0] = mMap;
        dataTransfer[1] = url;
        dataTransfer[2] = context;
        getNearbyPlacesData.execute(dataTransfer);

        // encuentraRuta.dibujaRadio(radio,opcionOrigenDestino,mMap);

        CameraUpdate orig = CameraUpdateFactory.newLatLngZoom(opcionOrigenDestino, 17f);

        mMap.animateCamera(orig);
    }


    public void getParadasCercanasAPuntoWS(String linea, GoogleMap mMap, LatLng opcionOrigenDestino, Context context, int radio) {

        AsyncTask<Object, Void, List<Parada>> httpGetParadasCerca = new HttpGetParadasCercanas(new HttpGetParadasCercanas.AsynParadas() {
            @Override
            public void paradas(List<Parada> paradas) {
                if (paradas != null) {
                    Toast.makeText(context, "SE ENCONTRARON " + paradas.size() + " PARADEROS ", Toast.LENGTH_SHORT).show();
                    lineAllWayPcercanos.paraderosWpt(paradas, mMap, context);
                } else if (paradas == null) {
                    getParadasCercanasAPuntoWS(linea, mMap, opcionOrigenDestino, context, radio + 200);
                }

            }
        }).execute(linea, radio, opcionOrigenDestino, context);
    }


    private void posicionElegida(LatLng PuntoLatLong, GoogleMap mMap, Marker marcador, String title, Context context) {

        if (marcador != null) {
            CameraUpdate orig = CameraUpdateFactory.newLatLngZoom(PuntoLatLong, 17f);
            marcador.showInfoWindow();
            mMap.animateCamera(orig);
            Toast.makeText(context, title, Toast.LENGTH_SHORT).show();
        } else {
            CameraUpdate orig = CameraUpdateFactory.newLatLngZoom(PuntoLatLong, 17f);
            Toast.makeText(context, title, Toast.LENGTH_LONG).show();
            mMap.animateCamera(orig);
        }

    }

    private void mensaje(Context context) {
        Toast.makeText(context, context.getText(R.string.mensaje_hacer_primero), Toast.LENGTH_SHORT).show();
    }
}
