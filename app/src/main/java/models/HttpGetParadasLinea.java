package models;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.rutas.santaelena.app.rutas.HeadersAuth;
import com.rutas.santaelena.app.rutas.R;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import entities.Parada;
import lineas.LineaBus;

/*******CLASE QUE ME DEVUELVE UNA RUTA DE BUS ESPECIFICA ****/

public class HttpGetParadasLinea extends AsyncTask<Object, Void, Parada> {

    Context context;
    private String userMovil = "";
    private String passMovil = "";
    private String idParadas;

    private LineaBus lineaBus;

    public HttpGetParadasLinea(LineaBus lineaBus) {
        this.lineaBus = lineaBus;
    }

    RestTemplate restTemplate = new RestTemplate() ;

    public interface AsyncResponse2 {
        void processFinish(Parada parada);
    }

    public AsyncResponse2 delegate = null;

    public HttpGetParadasLinea(AsyncResponse2 delegate){
        this.delegate = delegate;
    }

    @Override
    protected Parada doInBackground(Object... params) {

        idParadas = (String) params[0];
        context = (Context) params[1];


        userMovil = context.getString(R.string.user_movil);
        passMovil = context.getString(R.string.pass_movil);

        HeadersAuth headersAuth = new HeadersAuth();

        HttpHeaders headers = headersAuth.createHttpHeaders(userMovil, passMovil);

        HttpEntity<String> entity = new HttpEntity<String>(headers);


            try {

                    final String getUrl = context.getString(R.string.url_paradas_lineas) + idParadas;

                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                ResponseEntity<Parada> responseParadasLineas = restTemplate.exchange(getUrl, HttpMethod.GET, entity, Parada.class);
                System.out.println("Result - status (" + responseParadasLineas.getStatusCode() + ") has body: " + responseParadasLineas.hasBody());


                return responseParadasLineas.getBody();

            } catch (Exception ex) {
                Log.e("", ex.getMessage());
            }

            return null;

    }

    @Override
    protected void onPostExecute(Parada parada) {
        super.onPostExecute(parada);
        delegate.processFinish(parada);
    }


}
