package lineas;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

import detectaRuta.Marcador;
import entities.EstadoBusTemporal;
import models.HttpGetbus;

public class AnimateBusPosicion {

    Marcador marcador = new Marcador();
    LatLng punto;
    private Marker markerBus;
    private List<Marker> busMarker = new ArrayList<>();
    private List<LatLng> busPunto = new ArrayList<>();
    private List<String> discoBuses = new ArrayList<>();
    private List<String> numPasajerosActual = new ArrayList<>();


    public void getBusesMapa(GoogleMap mMap, Context context, String linea) {

        AsyncTask<Object, Void, List<EstadoBusTemporal>> httpGetbus = new HttpGetbus(new HttpGetbus.Posicionbus() {
            @Override
            public void busPosicion(List<EstadoBusTemporal> estadoBus) {
                if (estadoBus!=null)
                    mostrarBusesLista(mMap, context, linea, estadoBus);
            }
        }).execute(context, linea);

    }

    public void mostrarBusesLista(final GoogleMap mMap, Context context, String linea, List<EstadoBusTemporal> estadoBus) {

        for (int i = 0; i < estadoBus.size(); i++) {
            double lat = estadoBus.get(i).getPosicionActual().getY();
            double lo = estadoBus.get(i).getPosicionActual().getX();

            punto = new LatLng(lat, lo);

            int numPasajeros = estadoBus.get(i).getCantidadUsuarios();
            numPasajerosActual.add(i, String.valueOf(numPasajeros));

            String discoBus = estadoBus.get(i).getPlaca();
            discoBuses.add(i, String.valueOf(discoBus)); // HAY QUE CAMBIAR por disco, no existe

            String dato = String.valueOf("# Pasajeros " + numPasajeros + ", Disco : " + discoBus );
            System.out.println("dato : " + dato + "latlong : " + punto);

            markerBus = marcador.colocarMarcadorBusesRutadelMapa(punto, dato, mMap, context); //marcador.colocarMarcador(punto, dato, icon,mMap,context);
            busMarker.add(markerBus);
            busPunto.add(punto);

        }

        for (int i = 0; i < busMarker.size(); i++)
            animateMarker(busMarker.get(i), busPunto.get(i), true, mMap);


    }

    public void animateMarker(final Marker marker, final LatLng toPosition,
                              final boolean hideMarker, final GoogleMap mMap) {

        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        android.graphics.Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);


                double lng = toPosition.longitude;
                double lat = toPosition.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    handler.postDelayed(this, 5000);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });

    }

}
