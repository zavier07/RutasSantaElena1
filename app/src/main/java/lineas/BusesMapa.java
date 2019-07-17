package lineas;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;

public class BusesMapa {
    GoogleMap map;
    Context contextt;
    String linea = "";
    AnimateBusPosicion animateBusPosicion = new AnimateBusPosicion();
    private  boolean detectaNet;

    Thread tThread = new Thread(new Runnable() {

        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                        animateBusPosicion.getBusesMapa(map, contextt, linea);
                        System.out.println("iniciando ");
                        Thread.sleep(5000);

                } catch (InterruptedException ex) {

                    break;
                }
            }

        }

    });

    public BusesMapa(GoogleMap mMap, Context context, String line) {
        this.map = mMap;
        this.contextt = context;
        this.linea = line;

    }

    public void ini() {
        tThread.start();
    }

    public void detener() {
        tThread.interrupt();
        System.out.println("detenido");
    }

}
