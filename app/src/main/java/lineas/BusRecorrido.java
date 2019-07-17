package lineas;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;

public class BusRecorrido {
    GoogleMap mMap;
    Context context;
    String linea = "";
    String idParadero;
    AnimateBusPosicion animateBusPosicion = new AnimateBusPosicion();

    Thread tThread = new Thread(new Runnable() {

        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    //animateBusPosicion.getBusesMapa(map, contextt, linea);
                    System.out.println("iniciando ");
                    Thread.sleep(5000);

                } catch (InterruptedException ex) {

                    break;
                }
            }

        }

    });

    public BusRecorrido(GoogleMap mMap, Context context, String linea,String idParadero) {
        this.mMap = mMap;
        this.context = context;
        this.linea = linea;
        this.idParadero = idParadero;

    }

    public void ini() {
        tThread.start();
    }

    public void detener() {
        tThread.interrupt();
        System.out.println("detenido");
    }

}
