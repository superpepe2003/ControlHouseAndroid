package com.controlhouse.utopiasoft.controlhouse.Transacciones;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.controlhouse.utopiasoft.controlhouse.Entidades.CConeccion;
import com.controlhouse.utopiasoft.controlhouse.Entidades.CCuenta;
import com.controlhouse.utopiasoft.controlhouse.R;
import com.controlhouse.utopiasoft.controlhouse.Utilidades.DatePickerSinDia;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class transacciones_fragment_ingreso extends DialogFragment implements Response.Listener<JSONObject>, Response.ErrorListener {
    ArrayList<CCuenta> listaCuentas;

    ImageButton btnClose;
    Button btnGrabar, btnFecha;
    Spinner spinCuentaOrigen, spinCuentaDestino;
    EditText txtDescripcion, txtMonto;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;


    public interface OnActualizaTransacciones{
        void onActualiza();
    }

    public transacciones_fragment_ingreso(){}

    public transacciones_fragment_ingreso(ArrayList<CCuenta> listaCuentas){
        this.listaCuentas=listaCuentas;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.transacciones_fragment_ingreso,container,false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        btnClose=view.findViewById(R.id.btnCloseTransacciones);
        btnGrabar=view.findViewById(R.id.btnGuardarTransacciones);
        btnFecha=view.findViewById(R.id.btnFechaTransacciones);
        spinCuentaDestino= view.findViewById(R.id.cboCuentaDestino);
        spinCuentaOrigen= view.findViewById(R.id.cboCuentaOrigen);
        txtDescripcion=view.findViewById(R.id.txtDescripcionTransaccion);
        txtMonto=view.findViewById(R.id.txtMontoTransaccion);

        request=Volley.newRequestQueue(getContext());

        CargarSpin();
        btnFecha.setText(FechaNow());

        btnFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalendario();

            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnGrabar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validar())
                {
                    grabarTransaccion();
                }
            }
        });


        return view;
    }

    private boolean validar(){
        if((!txtMonto.getText().toString().isEmpty())&&(txtMonto.getText().length()>0))
        {
            return true;
        }
        return false;
    }

    private String FechaNow() {
        Calendar c= Calendar.getInstance();

        String dia = Integer.toString(c.get(Calendar.DAY_OF_MONTH)) + "/" +
                     Integer.toString(c.get(Calendar.MONTH)+1) + "/" +
                     Integer.toString(c.get(Calendar.YEAR));

        return dia;
    }

    private void showCalendario(){
        SimpleDateFormat format= new SimpleDateFormat("dd/MM/yyyy");
        Date fecha= new Date();

        try{
            fecha=format.parse(btnFecha.getText().toString());
        }catch (ParseException e){
            Toast.makeText(getContext(), "Error en conversion de fechas: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        final int d=Integer.parseInt(android.text.format.DateFormat.format("dd",fecha).toString());
        final int m=Integer.parseInt(android.text.format.DateFormat.format("MM",fecha).toString())-1;
        final int a=Integer.parseInt(android.text.format.DateFormat.format("yyyy",fecha).toString());

        DatePickerDialog dialog = new DatePickerDialog(getActivity(),AlertDialog.THEME_HOLO_DARK, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String fecha= Integer.toString(dayOfMonth) +"/" + Integer.toString(month+1) + "/" + Integer.toString(year);
                btnFecha.setText(fecha);
            }
        },a,m,d);

        ((ViewGroup) dialog.getDatePicker()).findViewById(Resources.getSystem().getIdentifier("day", "id", "android")).setVisibility(View.GONE);
        dialog.show();
    }

    private void CargarSpin() {
        ArrayList<SpinnerDatos> lCuentas= new ArrayList<>();

        for(CCuenta c:listaCuentas){
            lCuentas.add(new SpinnerDatos(c.getNombre().toUpperCase(),c.getId()));
        }

        if(lCuentas.isEmpty())
        {
            Toast.makeText(getContext(),"No existen cuentas, no puede agregar Movimientos", Toast.LENGTH_SHORT).show();
            dismiss();
        }

        ArrayAdapter<SpinnerDatos> adapter;
        adapter= new ArrayAdapter<>(getContext(),R.layout.spinnerpersonalizado,lCuentas);
        spinCuentaOrigen.setAdapter(adapter);
        spinCuentaDestino.setAdapter(adapter);
    }

    private void grabarTransaccion()
    {
        String fecha= "&fecha=" + btnFecha.getText().toString();
        String monto= "&monto=" + txtMonto.getText().toString();
        String descripcion = "&descripcion=" + txtDescripcion.getText().toString();
        String cuentaidorigen= "&cuentaidorigen=" + ((SpinnerDatos)spinCuentaOrigen.getSelectedItem()).getValor();
        String cuentaiddestino="&cuentaiddestino=" + ((SpinnerDatos)spinCuentaDestino.getSelectedItem()).getValor();

        String url = CConeccion.Url_Ingresar_Transacciones + CConeccion.bd + fecha + cuentaidorigen + cuentaiddestino + monto + descripcion;

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url,null, this, this);
        request.add(jsonObjectRequest);
    }


    @Override
    public void onResponse(JSONObject response) {
        JSONArray jsonArray = response.optJSONArray("resultadoTransaccion");

        if(jsonArray!=null)
        {
            JSONObject jsonObject=null;
            try {
                jsonObject = jsonArray.getJSONObject(0);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            int valor= jsonObject.optInt("valor");

            if(valor==1)
            {
                Toast.makeText(getContext(),"La Transaccion se grabo correctamente!", Toast.LENGTH_SHORT).show();

            }
            else
            {
                Toast.makeText(getContext(),"Error al querer grabar Transaccion: ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getContext(),"Error al querer grabar Transaccion: " + error.getMessage(), Toast.LENGTH_SHORT).show();
    }

}

class SpinnerDatos
{
    private String text;
    private int valor;

    public SpinnerDatos(String text, int valor) {
        this.text = text;
        this.valor = valor;
    }

    public int getValor()
    {
        return valor;
    }

    public String getTexto()
    {
        return text;
    }

    public String toString()
    {
        return text;
    }
}
