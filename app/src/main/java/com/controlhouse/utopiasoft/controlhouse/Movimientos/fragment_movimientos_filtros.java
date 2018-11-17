package com.controlhouse.utopiasoft.controlhouse.Movimientos;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.controlhouse.utopiasoft.controlhouse.Entidades.CFiltroMovimientos;
import com.controlhouse.utopiasoft.controlhouse.Entidades.IFiltro;
import com.controlhouse.utopiasoft.controlhouse.R;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class fragment_movimientos_filtros extends DialogFragment {

    RadioButton btnSortActual, btnSortFechaVieja;
    ImageButton btnClose;
    Button btnAplicar, btnFechaInicial, btnFechaFinal;
    CheckBox chkFechas, chkIngreso, chkEgreso;
    LinearLayout lnFechas;
    TextView txtMaximo, txtMinimo;
    TextInputEditText txtContenido;
    TextInputLayout txtLayoutContenido;

    Drawable drawableFecha, drawableFechaSelec, drawableActual, drawableActualSelec, drawableViejo, drawableViejoSelec, drawableIngreso, drawableIngresoSelec, drawableEgreso, drawableEgresoSelec;


    boolean actuales = false;
    boolean viejas = false;

    Calendar calendario;
    String fechaNow;

    //FILTRO QUE VAMOS A PASAR A LA ACTIVIDAD
    final CFiltroMovimientos filtro;
    final float _min, _max;

    CrystalRangeSeekbar seekbar;

    public fragment_movimientos_filtros(CFiltroMovimientos filtro, double _min, double _max) {
        this.filtro = filtro;
        this._max=(float)_max;
        this._min=(float) _min;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflo el fragmento con el layout creado
        View v =  inflater.inflate(R.layout.fragment_fragment_movimientos_filtros, container, false);

        btnSortActual = v.findViewById(R.id.btn_filtrar_fecha_actuales);
        btnSortFechaVieja = v.findViewById(R.id.btn_filtrar_fechas_viejas);
        btnClose = v.findViewById(R.id.btnClose);
        btnAplicar = v.findViewById(R.id.btn_aplicar);

        chkFechas =  v.findViewById(R.id.chkFechas);
        chkIngreso=v.findViewById(R.id.chkIngresos);
        chkEgreso= v.findViewById(R.id.chkEgresos);

        lnFechas = v.findViewById(R.id.lnFechas);
        btnFechaInicial = v.findViewById(R.id.btn_fecha_inicial);
        btnFechaFinal = v.findViewById(R.id.btn_fecha_final);

        seekbar = v.findViewById(R.id.rangeSeekbar1);
        txtMaximo = v.findViewById(R.id.txtMaximo);
        txtMinimo =v.findViewById(R.id.txtMinimo);

        txtContenido = v.findViewById(R.id.txtMovimientoFiltroContenido);
        txtLayoutContenido = v.findViewById(R.id.txtContenidoLayout);

        calendario = Calendar.getInstance();

        int month = calendario.get(Calendar.MONTH) + 1;
        int day = calendario.get(Calendar.DAY_OF_MONTH);
        int year= calendario.get(Calendar.YEAR);

        fechaNow = Integer.toString(day) + "/" + Integer.toString(month) + "/" + Integer.toString(year);

        btnFechaInicial.setText(fechaNow);
        btnFechaFinal.setText(fechaNow);





        CargarFiltro();


        //Imagen en RadioButton

        CargarImagenes();

        CargarRadioButtonFechas();
        CargarCheckbokFecha();
        CargarCheckbokIngresoEgreso();

        btnSortActual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnSortActual.isChecked()) {
                    actuales = true;
                    viejas = false;
                    CargarRadioButtonFechas();
                }
            }
        });

        btnSortFechaVieja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnSortFechaVieja.isChecked()) {
                    actuales = false;
                    viejas = true;
                    CargarRadioButtonFechas();
                }
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                dismiss();
            }
        });

        btnAplicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(actuales){filtro.setFecha(1);}
                if(viejas){filtro.setFecha(2);}

                filtro.setFechaInicial(btnFechaInicial.getText().toString());
                filtro.setFechaFinal(btnFechaFinal.getText().toString());

                filtro.setMontoMinimo(Double.parseDouble(txtMinimo.getText().toString()));
                filtro.setMontoMaximo(Double.parseDouble(txtMaximo.getText().toString()));

                filtro.setContenido(txtContenido.getText().toString());

                try {
                    ((IFiltro) getActivity()).setFiltro(filtro);
                }
                catch (ParseException e)
                {
                    Toast.makeText(getContext(),"Error en convertir fechas: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                dismiss();
            }
        });

        chkFechas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CargarCheckbokFecha();
                filtro.setFiltroPorFecha(chkFechas.isChecked());
            }
        });

        chkIngreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chkIngreso.isChecked())
                {
                    if(filtro.getTipo()==-1)
                    {
                        filtro.setTipo(0);
                        chkIngreso.setChecked(false);
                        chkEgreso.setChecked(false);
                    }
                    else
                    {
                        filtro.setTipo(1);
                    }
                }
                else
                {
                    filtro.setTipo(0);
                }
                CargarCheckbokIngresoEgreso();
            }
        });

        chkEgreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chkEgreso.isChecked())
                {
                    if(filtro.getTipo()==1)
                    {
                        filtro.setTipo(0);
                        chkIngreso.setChecked(false);
                        chkEgreso.setChecked(false);
                    }
                    else
                    {
                        filtro.setTipo(-1);
                    }
                }
                else
                {
                    filtro.setTipo(0);
                }
                CargarCheckbokIngresoEgreso();
            }
        });

        btnFechaInicial.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                ShowCalendario(1);
            }
        });

        btnFechaFinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowCalendario(2);
            }
        });

        seekbar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                    txtMinimo.setText(String.valueOf(minValue));
                    txtMaximo.setText(String.valueOf(maxValue));
            }
        });

        return v;
    }

    //SI HAY FILTRO LO CARGO
    private void CargarFiltro() {
        if(filtro!=null)
        {
            if(filtro.getFecha()==1)
                btnSortActual.setChecked(true);
            else if(filtro.getFecha()==2)
                btnSortFechaVieja.setChecked(true);

            if(filtro.isFiltroPorFecha())
            {
                chkFechas.setChecked(true);
                btnFechaFinal.setText(filtro.getFechaFinal());
                btnFechaInicial.setText(filtro.getFechaInicial());
            }
        }

        seekbar.setMaxValue(_max);
        seekbar.setMinValue(_min);
        /*if(filtro.getMontoMinimo()!=null) {
            txtMinimo.setText(String.valueOf(filtro.getMontoMinimo()));
            seekbar.setMinStartValue(filtro.getMontoMinimo().floatValue()).apply();
        }
        else {
            txtMinimo.setText(String.valueOf(_min));
        }*/
        if((filtro.getMontoMaximo()!=null) && (filtro.getMontoMinimo()!=null)) {
            txtMaximo.setText(String.valueOf(filtro.getMontoMaximo()));
            txtMinimo.setText(String.valueOf(filtro.getMontoMinimo()));
            seekbar.setMinStartValue(filtro.getMontoMinimo().floatValue())
                   .setMaxStartValue(filtro.getMontoMaximo().floatValue())
                   .apply();
        }
        else {
            txtMaximo.setText(String.valueOf(_max));
            txtMinimo.setText(String.valueOf(_min));
        }

        if(filtro.getContenido()!=null)
            txtLayoutContenido.setHint(filtro.getContenido());
    }

    private void ShowCalendario(final int i) {
        SimpleDateFormat sdf =  new SimpleDateFormat("dd/MM/yyyy");
        Date botonDate =  new Date();
        try{
            if(i==1)
                botonDate = sdf.parse(btnFechaInicial.getText().toString());
            else if(i==2)
                botonDate = sdf.parse(btnFechaFinal.getText().toString());
        }
        catch (ParseException e)
        {
            Toast.makeText(getContext(), "Error en conversion de fechas: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        final int dia = Integer.parseInt(DateFormat.format("dd", botonDate).toString());
        final int mes = Integer.parseInt(DateFormat.format("MM", botonDate).toString()) - 1;
        final int anio = Integer.parseInt(DateFormat.format("yyyy", botonDate).toString());
        final int boton = i;

        DatePickerDialog pickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String fecha = Integer.toString(dayOfMonth) + "/" + Integer.toString(month + 1) + "/" + Integer.toString(year);
                if(boton==1)
                    btnFechaInicial.setText(fecha);
                else if(boton==2)
                    btnFechaFinal.setText(fecha);
            }
        }, anio, mes, dia);

        pickerDialog.show();

    }


    public Drawable resizeImage(Context ctx, int resId, int w, int h) {

        // cargamos la imagen de origen
        Bitmap BitmapOrg = BitmapFactory.decodeResource(ctx.getResources(),
                resId);

        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = (int)Math.round(w * getResources().getDisplayMetrics().density);
        int newHeight = (int)Math.round(h * getResources().getDisplayMetrics().density);

        // calculamos el escalado de la imagen destino
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // para poder manipular la imagen
        // debemos crear una matriz

        Matrix matrix = new Matrix();
        // resize the Bitmap
        matrix.postScale(scaleWidth, scaleHeight);

        // volvemos a crear la imagen con los nuevos valores
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0,
                width, height, matrix, true);

        // si queremos poder mostrar nuestra imagen tenemos que crear un
        // objeto drawable y así asignarlo a un botón, imageview...
        return new BitmapDrawable(resizedBitmap);

    }


    //@Override
    /*public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());

//        LayoutInflater inflater = getActivity().getLayoutInflater();
//        View v = inflater.inflate(R.layout.fragment_fragment_movimientos_filtros, null);

        //builder.setView(v);

        return builder.create();

    }*/


    public void CargarRadioButtonFechas()
    {
        Drawable top = resizeImage(getContext(), R.drawable.icono_fecha,(int)Math.round(getResources().getDimension(R.dimen.icono_filtrar)), (int)Math.round(getResources().getDimension(R.dimen.icono_filtrar)));
        Drawable topChecked = resizeImage(getContext(), R.drawable.icono_fecha_seleccionado,(int)Math.round(getResources().getDimension(R.dimen.icono_filtrar)), (int)Math.round(getResources().getDimension(R.dimen.icono_filtrar)));
        //RadioButton
        if(btnSortActual.isChecked())
            btnSortActual.setCompoundDrawablesWithIntrinsicBounds(null, drawableActualSelec , null, null);
        else
            btnSortActual.setCompoundDrawablesWithIntrinsicBounds(null, drawableActual, null, null);
        if(btnSortFechaVieja.isChecked())
            btnSortFechaVieja.setCompoundDrawablesWithIntrinsicBounds(null, drawableActualSelec, null , null);
        else
            btnSortFechaVieja.setCompoundDrawablesWithIntrinsicBounds(null,drawableActual,null,null);
    }


    private void CargarCheckbokFecha() {

        //RadioButton
        if(chkFechas.isChecked()) {
            chkFechas.setCompoundDrawablesWithIntrinsicBounds(null, drawableFechaSelec, null, null);
            lnFechas.setVisibility(View.VISIBLE);
            btnFechaInicial.setEnabled(true);
            btnFechaFinal.setEnabled(true);

        }
        else {
            chkFechas.setCompoundDrawablesWithIntrinsicBounds(null, drawableFecha, null, null);
            lnFechas.setVisibility(View.GONE);
            btnFechaInicial.setEnabled(false);
            btnFechaFinal.setEnabled(false);
        }

    }


    private void CargarCheckbokIngresoEgreso()
    {
        if(chkIngreso.isChecked())
        {
            chkIngreso.setCompoundDrawablesWithIntrinsicBounds(null,drawableIngresoSelec,null,null);
        }
        else
        {
            chkIngreso.setCompoundDrawablesWithIntrinsicBounds(null,drawableIngreso,null,null);
        }
        if(chkEgreso.isChecked())
        {
            chkEgreso.setCompoundDrawablesWithIntrinsicBounds(null,drawableEgresoSelec,null,null);
        }
        else
        {
            chkEgreso.setCompoundDrawablesWithIntrinsicBounds(null,drawableEgreso,null,null);
        }
    }

    private void CargarImagenes()
    {
        drawableActual = resizeImage(getContext(), R.drawable.icono_fecha,(int)Math.round(getResources().getDimension(R.dimen.icono_filtrar)), (int)Math.round(getResources().getDimension(R.dimen.icono_filtrar)));
        drawableActualSelec = resizeImage(getContext(), R.drawable.icono_fecha_seleccionado,(int)Math.round(getResources().getDimension(R.dimen.icono_filtrar)), (int)Math.round(getResources().getDimension(R.dimen.icono_filtrar)));

        drawableIngreso= resizeImage(getContext(), R.drawable.icono_ingreso_checkbok,(int)Math.round(getResources().getDimension(R.dimen.icono_filtrar)),(int)Math.round(getResources().getDimension(R.dimen.icono_filtrar)));
        drawableIngresoSelec = resizeImage(getContext(), R.drawable.icono_ingreso_checkbok_seleccionado,(int)Math.round(getResources().getDimension(R.dimen.icono_filtrar)),(int)Math.round(getResources().getDimension(R.dimen.icono_filtrar)));

        drawableEgreso= resizeImage(getContext(), R.drawable.icono_egreso_checkbok,(int)Math.round(getResources().getDimension(R.dimen.icono_filtrar)),(int)Math.round(getResources().getDimension(R.dimen.icono_filtrar)));
        drawableEgresoSelec = resizeImage(getContext(), R.drawable.icono_egreso_checkbok_seleccionado,(int)Math.round(getResources().getDimension(R.dimen.icono_filtrar)),(int)Math.round(getResources().getDimension(R.dimen.icono_filtrar)));


        drawableFecha = resizeImage(getContext(), R.drawable.icono_fecha_checkbok,(int)Math.round(getResources().getDimension(R.dimen.icono_filtrar)), (int)Math.round(getResources().getDimension(R.dimen.icono_filtrar)));
        drawableFechaSelec = resizeImage(getContext(), R.drawable.icono_fecha_seleccionado_checkbok,(int)Math.round(getResources().getDimension(R.dimen.icono_filtrar)), (int)Math.round(getResources().getDimension(R.dimen.icono_filtrar)));
    }

}