package com.controlhouse.utopiasoft.controlhouse.Cuentas;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.controlhouse.utopiasoft.controlhouse.Entidades.CConeccion;
import com.controlhouse.utopiasoft.controlhouse.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class cuentas_fragment_ingreso extends DialogFragment implements Response.ErrorListener, Response.Listener<JSONObject> {

    boolean _isModificacion;

    Button btnAplicar;
    ImageButton btnClose;
    TextView txtNombre, txtSaldo;
    String _nombre;
    double _saldo;
    int _id;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    public interface iActualiza{
        void onActualizaCuentas();
    }

    public cuentas_fragment_ingreso(){
    }

    public cuentas_fragment_ingreso(boolean isModificacion, String nombre, double saldo, int id)
    {
        this._isModificacion=isModificacion;
        this._nombre=nombre;
        this._saldo=saldo;
        this._id=id;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.cuentas_fragment_ingreso,container);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        request = Volley.newRequestQueue(getContext());

        btnAplicar=view.findViewById(R.id.btn_aplicar_cuentas);
        btnClose=view.findViewById(R.id.btnCloseCuentas);
        txtNombre=view.findViewById(R.id.txtNombreCuenta);
        txtSaldo=view.findViewById(R.id.txtSaldoCuenta);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        if(_isModificacion)
            CargarDatos();

        btnAplicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ValidarDatos())
                {
                    if(!_isModificacion)
                        GrabarDatos();
                    else
                        ModificaDatos();
                }
            }
        });

        return view;
    }

    private void CargarDatos() {
        txtNombre.setText(_nombre);
        txtSaldo.setText(String.valueOf(_saldo));
    }

    private void GrabarDatos() {
        String _saldo;
        if(txtSaldo.getText().toString().isEmpty())
            _saldo="0";
        else
            _saldo=txtSaldo.getText().toString();
        String nombre = "&nombre=" + txtNombre.getText().toString().replace(" ", "%20");
        String saldo= "&saldo=" + _saldo;
        String url = CConeccion.URL_Ingresar_Cuentas + CConeccion.bd + nombre +saldo;

        jsonObjectRequest= new JsonObjectRequest(Request.Method.GET, url,null, this, this);
        request.add(jsonObjectRequest);
    }

    private void ModificaDatos(){
        String _s;
        if(txtSaldo.getText().toString().isEmpty())
            _s="0";
        else
            _s=txtSaldo.getText().toString();
        String nombre = "&nombre=" + txtNombre.getText().toString().replace(" ", "%20");
        String saldo= "&saldo=" + _s;
        String id="&id=" + _id;
        String url = CConeccion.URL_Modificar_Cuentas + CConeccion.bd + id + nombre +saldo;

        jsonObjectRequest= new JsonObjectRequest(Request.Method.GET, url,null, this, this);
        request.add(jsonObjectRequest);
    }

    private boolean ValidarDatos() {
        if(!txtNombre.getText().toString().isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getContext(), "Error al intentar grabar los datos: " + error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(JSONObject response) {
        JSONArray jsonGrabar = response.optJSONArray("resultadoGrabar");
        JSONArray jsonModificar = response.optJSONArray("resultadoModificar");

        if (jsonGrabar != null) {
            JSONObject jsonObject = null;
            try {
                jsonObject = jsonGrabar.getJSONObject(0);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            int id = jsonObject.optInt("valor");


            if (id > 0) {
                Toast.makeText(getContext(), "Se ha Grabado Correctamente", Toast.LENGTH_SHORT).show();
                ((iActualiza) getActivity()).onActualizaCuentas();
                limpiarCampos();
            } else {
                Toast.makeText(getContext(), "El Dato no pudo grabarse", Toast.LENGTH_SHORT).show();
            }
        }
        else if(jsonModificar!=null) {

            JSONObject jsonObject = jsonModificar.optJSONObject(0);

            int id= jsonObject.optInt("valor");

            if (id > 0) {
                Toast.makeText(getContext(), "Se ha Modificado Correctamente", Toast.LENGTH_SHORT).show();
                ((iActualiza) getActivity()).onActualizaCuentas();
                dismiss();
            } else
                Toast.makeText(getContext(), "El Dato no pudo modificar", Toast.LENGTH_SHORT).show();

        }
    }

    private void limpiarCampos () {
            txtNombre.setText("");
            txtSaldo.setText("");
    }

}