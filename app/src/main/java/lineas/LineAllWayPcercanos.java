package lineas;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.rutas.santaelena.app.rutas.R;

import java.util.ArrayList;
import java.util.List;

import detectaRuta.Marcador;
import entities.Parada;
import entities.Point;
import entities.Ruta;

public class LineAllWayPcercanos extends Application {

    Marcador marcador = new Marcador();
    BitmapDescriptor icon;
    private List<String> lineabus = new ArrayList<>();
    private ArrayList<LatLng> listaPuntosRutasTodas = new ArrayList<>();
    private ArrayList<String> nombreParadero = new ArrayList<>();
    private List<ArrayList<LatLng>> listaRutas = new ArrayList<>();
    //  HashMap<String, Float> markerIcons = new HashMap<>();

    /**
     * @param lineasTodas :
     * @return lista de todas las rutas existentes
     */


    public List<ArrayList<LatLng>> getRecorridoLineas(List<Ruta> lineasTodas) {

        for (int i = 0; i < lineasTodas.size(); i++) {
            Ruta rutaModels = lineasTodas.get(i);
            List<Point> ruta = rutaModels.getListasPuntos();
            for (int j = 0; j < ruta.size(); j++) {
                double latitud = ruta.get(j).getY();
                double longitud = ruta.get(j).getX();
                LatLng punto = new LatLng(latitud, longitud);
                listaPuntosRutasTodas.add(punto);
            }
            listaRutas.add(i, new ArrayList<LatLng>(listaPuntosRutasTodas));
            listaPuntosRutasTodas.clear();
        }

        return listaRutas;
    }

    public List<String> getNombreLineas(List<Ruta> lineasTodas) {
        for (int i = 0; i < lineasTodas.size(); i++) {
            Ruta r = lineasTodas.get(i);
            lineabus.add(i, r.getLinea());
        }
        return lineabus;
    }


    /**
     *
     * @param listParadas
     * @param mMap
     * @return lista de paraderos cercanos .
     */

    public List<Marker> paraderosWpt(List<Parada> listParadas, final GoogleMap mMap, Context context) {

        List<Marker> markers = new ArrayList<>();
        String id_marker ="";
      // icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN);
        icon = BitmapDescriptorFactory.fromResource(R.drawable.autobusstop);
        for (int i = 0; i < listParadas.size(); i++) {

            nombreParadero.add(listParadas.get(i).getNombre());
            Parada coord = listParadas.get(i);
            double latitud = coord.getCoordenada().getY();
            double longitud = coord.getCoordenada().getX();
            id_marker = listParadas.get(i).getId();

            LatLng puntoMarcadores = new LatLng(latitud, longitud);

            markers.add(i,marcador.parderosCercanos(puntoMarcadores,nombreParadero.get(i),id_marker,icon,mMap,context));

        }


        return  markers;

    }



}