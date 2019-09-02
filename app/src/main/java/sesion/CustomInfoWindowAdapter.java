package sesion;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.rutas.santaelena.app.rutas.R;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import detectaRuta.Buses_disponibles;
import detectaRuta.Marcador;
import entities.EstadoBusTemporal;
import lineas.BusesMapa;
import lineas.LineaBusesDisponibles;
import models.HttpGetTiempoBuses;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private static final String TAG = "CustomInfoWindowAdapter";
    private LayoutInflater inflater;
    private String linea;
    private Context context;
    private BusesMapa busesMapa;


    public CustomInfoWindowAdapter(Context context,LayoutInflater inflater, String linea, BusesMapa busesMapa) {
        this.inflater = inflater;
        this.linea = linea;
        this.context = context;
        this.busesMapa = busesMapa;
    }

    public CustomInfoWindowAdapter() {

    }

    public View getInfoContents(final Marker m) {

        String info1 = m.getTitle();
        String info2 = m.getSnippet();
        LatLng posicion = m.getPosition();

        View v = inflater.inflate(R.layout.infowindow_layout, null);

        if (m.getTag() == "TAG_WAYPOINT") {// MOSTRAR LOS BUSES QUE PASARAN POR ESE PARADERI

            ((TextView) v.findViewById(R.id.info_window_title)).setText(new Marcador().getNombreCalles(posicion, inflater.getContext()));
            ((TextView) v.findViewById(R.id.info_window_snniple)).setText("Paradero Linea " + linea);
            AsyncTask<Object, Void, Map<Void, EstadoBusTemporal>> httpGetTiempoBuses = new HttpGetTiempoBuses(new HttpGetTiempoBuses.AsynGetBusTime() {
                @Override
                public void timeBusParadero(Map hashMap) {
                    new LineaBusesDisponibles().tiempoBusParaderos(hashMap,context);
                }
            }).execute(context,info2,linea);

        } else {

            ((TextView) v.findViewById(R.id.info_window_title)).setText(info1);
            ((TextView) v.findViewById(R.id.info_window_snniple)).setText(info2);
        }

        return v;
    }

    @Override
    public View getInfoWindow(Marker m) {
        return null;
    }


}