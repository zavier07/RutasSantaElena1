package detectaRuta;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.rutas.santaelena.app.rutas.R;

import java.util.ArrayList;
import java.util.List;

public class SeleccionUbicacion{

    public interface OnOkOrigenDestino{
        void seleccionadaUbicacion (int seleccionUbicacion);
    }

    public void SeleccionaOpcionUbiDest(Context context, final
    OnOkOrigenDestino onOkOrigenDestino) {

        List<String> opcionOrigenDestino = new ArrayList<>();

        opcionOrigenDestino.add("Punto Origen");
        opcionOrigenDestino.add("Punto Destino");
        opcionOrigenDestino.add("Abordará el Bus");
        opcionOrigenDestino.add("Bajada del Bus");

        String[] arrayopcionOrigenDestino = opcionOrigenDestino.toArray(new String[0]);


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setTitle("Desea visualizar restaurantes alrrededor de : ");
        dialogBuilder.setIcon(R.drawable.llegadabus);
        dialogBuilder.setSingleChoiceItems(arrayopcionOrigenDestino,-1, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                String selectedOpcion = arrayopcionOrigenDestino[item].toString();  //Selected item in listview
                Toast.makeText(context, "Selecciono " + selectedOpcion, Toast.LENGTH_SHORT).show();

                for (int i=0;i<opcionOrigenDestino.size();i++)
                    if (selectedOpcion == opcionOrigenDestino.get(i)){
                        onOkOrigenDestino.seleccionadaUbicacion(i);
                        break;
                    }
                    dialog.dismiss();
            }});
dialogBuilder.setNegativeButton(android.R.string.cancel, null);

        AlertDialog alertDialogObject = dialogBuilder.create();
        alertDialogObject.show();

    }


    public void dialogSeleccionaOpcionUbiDestParaderos(Context context, final OnOkOrigenDestino onOkOrigenDestino){

        List<String> opcionOrigenDestino = new ArrayList<>();

        opcionOrigenDestino.add("Abordará el Bus");
        opcionOrigenDestino.add("Bajada del Bus");

        String[] arrayBusesDisponibles = opcionOrigenDestino.toArray(new String[0]);

        AlertDialog.Builder alt_bld = new AlertDialog.Builder(context);

        alt_bld.setTitle("Dondé desea visualizar?");
        alt_bld.setIcon(R.drawable.buspasajeros);
        alt_bld.setSingleChoiceItems(arrayBusesDisponibles, -1, new DialogInterface
                .OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                String selectedOpcion = arrayBusesDisponibles[item].toString();

                Toast.makeText(context, "Selecciono " + selectedOpcion, Toast.LENGTH_SHORT).show();

                for (int i=0;i<opcionOrigenDestino.size();i++)
                    if (selectedOpcion == opcionOrigenDestino.get(i)){
                        onOkOrigenDestino.seleccionadaUbicacion(i+2);
                        break;
                    }
                dialog.dismiss();// dismiss the alertbox after chose option

            }

        });

        alt_bld.setNegativeButton(android.R.string.cancel, null);
        AlertDialog alert = alt_bld.create();
        alert.show();

    }



}
