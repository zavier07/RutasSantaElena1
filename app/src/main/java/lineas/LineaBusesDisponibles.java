package lineas;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.rutas.santaelena.app.rutas.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import entities.EstadoBusTemporal;

public class LineaBusesDisponibles extends FragmentActivity {
    private List<String> ubicacionBuses;
    List<EstadoBusTemporal> numBuses;
    LatLng punto;
    Dialog dialog;
    private List<String> discoBuses = new ArrayList<>();
    private List<String> numPasajerosActual = new ArrayList<>();
    private List<String> tiempoMinutos = new ArrayList<>();
    private List<String> tiempobuses = new ArrayList<>();
    private List<LatLng> busPunto = new ArrayList<>();

    public void busesCirculando(String linea, Context context, GoogleMap map) {

        numBuses = AnimateBusPosicion.numBuses;
        if (numBuses != null) {
            for (int i = 0; i < numBuses.size(); i++) {
                double lat = numBuses.get(i).getPosicionActual().getY();
                double lo = numBuses.get(i).getPosicionActual().getX();

                punto = new LatLng(lat, lo);

                int numPasajeros = numBuses.get(i).getCantidadUsuarios();
                numPasajerosActual.add(i, String.valueOf(numPasajeros));

                String discoBus = numBuses.get(i).getPlaca();
                discoBuses.add(i, String.valueOf(discoBus)); // HAY QUE CAMBIAR por disco, no existe

                busPunto.add(punto);
            }
        }
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        // final Dialog dialog = new Dialog(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = (View) inflater.inflate(R.layout.activity_lineas_buses_disponibles, null);


        alertDialog.setView(convertView);


        TextView textViewLinea = (TextView) convertView.findViewById(R.id.idlinea);
        textViewLinea.setText(linea);

        ListView listViewPasajeros = (ListView) convertView.findViewById(R.id.id_num_Pasajeros);
        ArrayAdapter<String> nuMPasajeros = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, numPasajerosActual);
        listViewPasajeros.setAdapter(nuMPasajeros);

        ListView listViewBusesDisco = (ListView) convertView.findViewById(R.id.id_lineas_buses_disco);
        ArrayAdapter<String> adapterDisco = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_single_choice, discoBuses);
        listViewBusesDisco.setAdapter(adapterDisco);

        listViewBusesDisco.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object lisItem = listViewBusesDisco.getItemAtPosition(position);

                Toast.makeText(context, "Selecciono  " + lisItem, Toast.LENGTH_SHORT).show();
                CameraUpdate irPosicionBus = CameraUpdateFactory.newLatLngZoom(busPunto.get(position), 16);
                map.animateCamera(irPosicionBus);
                dialog.dismiss();
            }
        });

        listViewBusesDisco.setChoiceMode(listViewBusesDisco.CHOICE_MODE_SINGLE);
        dialog = alertDialog.show();
    }

    public void tiempoBusParaderos(Map tiempo, Context context) {
        double min,hor,seg;
        double segundos;
        int segundos2,minutos;
        if (tiempo!=null) {
            Iterator it = tiempo.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry p = (Map.Entry) it.next();
                System.out.println("Placa = " + p.getKey() + " , Tiempo SEGUNDOS = " + p.getValue());

                tiempobuses.add((String) p.getKey());
                segundos = (double) p.getValue();

                hor=  (segundos)/3600;
                hor = Double.parseDouble(new DecimalFormat("#").format(hor));
                min=(segundos-(3600*hor))/60;
                min = Double.parseDouble(new DecimalFormat("#").format(min));
                seg=segundos-((hor*3600)+(min*60));
                seg = Double.parseDouble(new DecimalFormat("#").format(seg));
                segundos2 = (int) seg;
                minutos = (int) min;
                tiempoMinutos.add(String.valueOf((minutos + ":" + segundos2)));
                it.remove();

            }
        }
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        // final Dialog dialog = new Dialog(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = (View) inflater.inflate(R.layout.activity_buses_paraderos, null);

        alertDialog.setView(convertView);


        /*TextView textViewLinea = (TextView) convertView.findViewById(R.id.idlinea);
        textViewLinea.setText(linea);*/

        ListView listViewBusesDisco = (ListView) convertView.findViewById(R.id.id_busesParaderos);
        ArrayAdapter<String> adapterDisco = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, tiempobuses);
        listViewBusesDisco.setAdapter(adapterDisco);


        ListView listViewPasajeros = (ListView) convertView.findViewById(R.id.id_tiempo);
        ArrayAdapter<String> nuMPasajeros = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, tiempoMinutos);
        listViewPasajeros.setAdapter(nuMPasajeros);

        alertDialog.setNegativeButton("VOLVER", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        dialog = alertDialog.show();


        /*alertDialog.setNegativeButton(android.R.string.cancel, null);
        AlertDialog alert = alertDialog.create();
        alert.show();*/

    }
}
