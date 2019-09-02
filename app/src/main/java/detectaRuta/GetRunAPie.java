package detectaRuta;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.rutas.santaelena.app.rutas.R;

import java.util.List;

import PolyineBased.MapAnimator;
import interfaceClass.RetrofitMaps;
import recorridosDistancia.ListRoute;
import recorridosDistancia.Polydecode;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class GetRunAPie {
    Polyline poliRuDes;
    String recorridoOriRuta = "";
    String recorridoDestRuta = "";
    int c = 0;
    Marcador marcador = new Marcador();
    List<LatLng> list,list2;
    public void getRecorridoAPie(String type, LatLng puntoa, LatLng puntob, GoogleMap mMap, Context context) {


        String url = context.getString(R.string.url_directions);
        Retrofit retrofit = new Retrofit.Builder().baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitMaps service = retrofit.create(RetrofitMaps.class);

        Call<ListRoute> call = service.getDistanceDuration("metric", puntoa.latitude + "," + puntoa.longitude, puntob.latitude + "," + puntob.longitude, type);
        call.enqueue(new Callback<ListRoute>() {
            @Override
            public void onResponse(Response<ListRoute> response, Retrofit retrofit) {
                try {
                    for (int i = 0; i < response.body().getRoutes().size(); i++) {
                        String distance = response.body().getRoutes().get(i).getLegs().get(i).getDistance().getText();
                        String time = response.body().getRoutes().get(i).getLegs().get(i).getDuration().getText();

                        if (c == 0) {
                            recorridoOriRuta = ("Distancia:" + distance + ", Duracion:" + time);
                            marcador.colocarMarcadorRutaBusMasCercanaOrigen(puntob, recorridoOriRuta, mMap, context);

                            String encodedString = response.body().getRoutes().get(0).getOverviewPolyline().getPoints();
                            Polydecode polydecode = new Polydecode();

                            list = polydecode.decodePoly(encodedString);
                            poliRuDes = mMap.addPolyline(
                                    new PolylineOptions()
                                            .addAll(list)
                                            .width(4)
                                            .color(Color.BLACK)
                                            .geodesic(true)
                            );
                            animate(puntoa,puntob,mMap,list,context,c);
                            c = 1;
                        } else {
                            recorridoDestRuta = ("Distancia:" + distance + ", Duracion:" + time);
                            marcador.colocarMarcadorRecorridoaPieDestino(puntob, recorridoDestRuta, mMap, context); //este estaba

                            String encodedString = response.body().getRoutes().get(0).getOverviewPolyline().getPoints();
                            Polydecode polydecode = new Polydecode();

                            list2 = polydecode.decodePoly(encodedString);
                            poliRuDes = mMap.addPolyline(
                                    new PolylineOptions()
                                            .addAll(list2)
                                            .width(4)
                                            .color(Color.BLACK)
                                            .geodesic(true)
                            );
                            animate(puntoa,puntob,mMap,list2,context,c);
                            c = 0;
                        }

                        /*String encodedString = response.body().getRoutes().get(0).getOverviewPolyline().getPoints();
                        Polydecode polydecode = new Polydecode();

                        list = polydecode.decodePoly(encodedString);
                        poliRuDes = mMap.addPolyline(
                                    new PolylineOptions()
                                            .addAll(list)
                                            .width(3)
                                            .color(Color.BLACK)
                                            .geodesic(true)
                        );
                        animate(puntoa,puntob,mMap,list,context); */
                    }


                } catch (Exception e) {
                    Log.d("onResponse", "There is an error");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("onFailure", t.toString());
            }
        });

    }


    private void startAnim(GoogleMap mMap,List<LatLng> list,Context context) {
        if (mMap != null) {
            MapAnimator.getInstance().animateRoute(mMap, list);
        } else {
            Toast.makeText(context, "Map not ready", Toast.LENGTH_LONG).show();
       }
    }



    private void animate(LatLng ini , LatLng fin, GoogleMap mMap,List<LatLng> list, Context context, int c)
    {
        System.out.print("valor c  : " +c);
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(ini);
                builder.include(fin);
                LatLngBounds bounds = builder.build();
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200);

                mMap.moveCamera(cu);
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

                startAnim(mMap,list,context);

            }
        });
    }

}
