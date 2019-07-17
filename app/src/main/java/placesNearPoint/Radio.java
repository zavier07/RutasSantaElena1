package placesNearPoint;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.NumberPicker;

import com.rutas.santaelena.app.rutas.R;

public class Radio extends FragmentActivity {

    public interface OnOkRadio{
        void radio (int radio);
    }

    public interface OnOk{
        void onOk (boolean onOk);
    }

    public void ingresaRadio(Context context , final OnOkRadio onOkRadio){
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        final EditText edittextRadio = new EditText(context);

        edittextRadio.setInputType(InputType.TYPE_CLASS_NUMBER);
        edittextRadio.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(3) });//mmaximo 4 numeros
        edittextRadio.setKeyListener(DigitsKeyListener.getInstance(false,false));//false false desabilita numero decimales y negativos

        alert.setMessage("Ingrese el perimetro de busqueda en METROS");
        alert.setTitle("Busquedas Cercanas");

        alert.setView(edittextRadio);

        alert.setPositiveButton("Buscar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                Editable YouEditTextValue = edittextRadio.getText();
                Integer rad = Integer.valueOf(String.valueOf(YouEditTextValue)) ;

                onOkRadio.radio(rad);
            }
        });

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });

        alert.show();
    }

    public void continueNew(Context context, final OnOk onOk){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Esta Seguro?");
        builder.setMessage("Se borraran los datos del mapa");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                onOk.onOk(true);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                onOk.onOk(false);
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void radioPickSelector(Context context, final OnOkRadio onOkRadio){
        int start = 50;
        int step = 20;
        String[] numbers = new String[16];
        for(int i =0 ; i < numbers.length ; i++) {
            numbers[i] = start + "";
            start = start + step;
        }
        NumberPicker picker = new NumberPicker(context);
        picker.setMaxValue(16);
        picker.setMinValue(1);
        picker.setDisplayedValues(numbers);

        FrameLayout layout = new FrameLayout(context);
        layout.addView(picker, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER));

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setIcon(R.drawable.buspasajeros);
        alertDialogBuilder.setTitle("Radio de busqueda En Metros");
        alertDialogBuilder.setMessage("Seleccione");
        alertDialogBuilder.setView(layout);
        alertDialogBuilder.setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
            int ini = 50;
            int rango = 20;

            for(int j =1 ; j < picker.getValue() ; j++)
                    ini = ini + rango;

            onOkRadio.radio(ini);
        })
                .setNegativeButton(android.R.string.cancel, null)
                .show();


    }

    public void radioPickSelectorAmpliarBusquedaRutas(Context context, final OnOkRadio onOkRadio){
        int start = 1100;
        int step = 150;
        String[] numbers = new String[15];
        for(int i =0 ; i < numbers.length ; i++) {
            numbers[i] = start + "";
            start = start + step;
        }
        NumberPicker picker = new NumberPicker(context);
        picker.setMaxValue(15);
        picker.setMinValue(1);
        picker.setDisplayedValues(numbers);

        FrameLayout layout = new FrameLayout(context);
        layout.addView(picker, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER));

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setIcon(R.drawable.buspasajeros);
        alertDialogBuilder.setTitle("AMPLIE EL RADIO DE BÚSQUEDA");
        alertDialogBuilder.setMessage("No se detecto ninguna Ruta en un radio de 1000 mtrs");
        alertDialogBuilder.setView(layout);
        alertDialogBuilder.setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
            int ini = 1100;
            int rango = 150;

            for(int j =1 ; j < picker.getValue() ; j++)
                ini = ini + rango;

            onOkRadio.radio(ini);
        })
                .setNegativeButton(android.R.string.cancel, null)
                .show();


    }


}
