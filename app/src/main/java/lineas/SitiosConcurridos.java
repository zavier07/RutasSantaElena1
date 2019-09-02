package lineas;

import android.content.Context;
import android.support.constraint.ConstraintLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import detectaRuta.Marcador;

public class SitiosConcurridos {

    public void sitiosConcurridos(GoogleMap mMap, Context context){
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
            new Marcador().colocarMarcadorSitiod(listaPuntosSitios.get(i),listanameSitios.get(i) ,mMap,context);

        //    paraderosWpt();
    }

}
