package detectaRuta;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;

public class EncuentraRuta extends FragmentActivity {
    //isLocationOnEdge: DETERMINA SI UN PUNTO SE ENCUENTRA EN UNA POLILINEA CERCA DENTRO O FUERA DE ELLA CON UN RADIO DE TOLERANCIA
    Circle radioBusqueda;
    int seleccion=0;

    /**
     * Método que me devuelve si una ruta esta o se encuentra dentro de en un radio determinado
     * @param latLng referente a este punto es el radio de búsqueda
     * @param lista rutas con las que se comparara el punto
     * @param f
     * @param rad radio de búsqueda
     * @param mMap
     * @return si la ruta esta o no esta en el perimetro del radio retornara un false o true
     */
    public boolean encuentraRutaBus(LatLng latLng , ArrayList<LatLng> lista, boolean f, int rad, GoogleMap mMap){

        boolean encuentraBordePoli = PolyUtil.isLocationOnEdge(latLng, lista, f, rad);

        while (encuentraBordePoli != true) {

            while (rad < 1000) {

                rad = rad + 100;
                encuentraBordePoli = PolyUtil.isLocationOnEdge(latLng, lista, false, rad);

                if (encuentraBordePoli == true)
                    break;
            }
            break;
        }

        return encuentraBordePoli;
    }

    /**
     * dibuja el radio de busqueda
     * @param rad
     * @param marcador
     * @param mMap
     */
    public void dibujaRadio(int rad, LatLng marcador,GoogleMap mMap) {
        cleanCircle(radioBusqueda);
        radioBusqueda = mMap.addCircle(new CircleOptions()
                .center(marcador)
                .radius(rad)//metros
                .strokeWidth(5)
                .strokeColor(Color.GRAY)
        );
    }

    /**
     * limpia el radio
     * @param limpiRadioBusqueda
     */
    public void cleanCircle(Circle limpiRadioBusqueda) {
        if (limpiRadioBusqueda != null)
            limpiRadioBusqueda.remove();

    }


    /**
     *
     * @param pa
     * @param pb
     * @return distancia existente entre el pa y pb
     */
    public double distanciasEntrePuntos(LatLng pa , LatLng pb){

        double distanciaminima = SphericalUtil.computeDistanceBetween(pa, pb);

        return distanciaminima;

    }
}
