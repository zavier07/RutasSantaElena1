package placesNearPoint;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.rutas.santaelena.app.rutas.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import detectaRuta.Marcador;

/**
 * https://www.youtube.com/watch?v=_Oljjn1fIAc
 */

public class GetNearbyPlacesData extends AsyncTask<Object, String, String> {
    String googlePlacesData;
    GoogleMap mMap;
    String url;
    LatLng latLng;
    Marker markerOptions;
    Context context;
    Marcador marcador = new Marcador();
    BitmapDescriptor icon;
    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap)objects[0];
        url = (String)objects[1];
        context = (Context)objects[2];
        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googlePlacesData = downloadUrl.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String s) {
        List<HashMap<String, String>> nearbyPlaceList = null;
        DataParser parser = new DataParser();
        nearbyPlaceList = parser.parse(s);
        showNearbyPlaces(nearbyPlaceList);
    }

    private void showNearbyPlaces(List<HashMap<String,String>> nearbyPlaceList)
    {
        if (nearbyPlaceList!=null) {
            Toast.makeText(context, "SE ENCONTRARON " + nearbyPlaceList.size() + " RESULTADOS ", Toast.LENGTH_SHORT).show();
            icon = BitmapDescriptorFactory.fromResource(R.drawable.circle);
            for (int i = 0; i < nearbyPlaceList.size(); i++) {
                HashMap<String, String> googlePlace = nearbyPlaceList.get(i);
                Log.d("onPostExecute", "Entered into showing locations");

                String placeName = googlePlace.get("place_name");
                String vicinity = googlePlace.get("vicinity");

                double lat = Double.parseDouble(googlePlace.get("lat"));
                double lng = Double.parseDouble(googlePlace.get("lng"));

                latLng = new LatLng(lat, lng);

                markerOptions = marcador.colocarMarcador(latLng, placeName + " : " + vicinity, icon, mMap, context);
            /*CameraUpdate orig = CameraUpdateFactory.newLatLngZoom(latLng, 17f);
            mMap.animateCamera(orig);*/

                if (i > 4) //presentara maximo 5 resultados
                    break;
            }

        }  else
            Toast.makeText(context, "NO HAY RSULTADOS ", Toast.LENGTH_SHORT).show();

    }

    public String getUrl(double latitude, double longitude, String nearbyPlace, int radio)
    {
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + radio);
        googlePlacesUrl.append("&type=" + nearbyPlace);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + "AIzaSyAcdDIpWjo3D68HFvOp70UBLqh6uxlhdiU");
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }
}
