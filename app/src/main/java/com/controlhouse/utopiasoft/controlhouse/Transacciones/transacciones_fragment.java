package com.controlhouse.utopiasoft.controlhouse.Transacciones;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.controlhouse.utopiasoft.controlhouse.Cuentas.AdaptadorCuentas;
import com.controlhouse.utopiasoft.controlhouse.Entidades.CConeccion;
import com.controlhouse.utopiasoft.controlhouse.Entidades.CCuenta;
import com.controlhouse.utopiasoft.controlhouse.Entidades.CMovimiento;
import com.controlhouse.utopiasoft.controlhouse.Entidades.CTransaciones;
import com.controlhouse.utopiasoft.controlhouse.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class transacciones_fragment extends DialogFragment implements Response.Listener<JSONObject>, Response.ErrorListener, transacciones_fragment_ingreso.OnActualizaTransacciones {
    ArrayList<CTransaciones> listaTransacciones;
    ArrayList<CCuenta> listaCuentas;
    RecyclerView recyclerView;
    LinearLayout linearLayoutRecycler;
    TextView txtTotal;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    ImageButton btnClose, btnAgregar, btnFiltrar;

    int _id;
    String _titulo;

    public transacciones_fragment(){}

    public transacciones_fragment(ArrayList<CCuenta> listaCuentas, int id, String titulo){
        this.listaCuentas=listaCuentas;
        this._id=id;
        this._titulo=titulo;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.transacciones_fragment,container,false);

        //getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        listaTransacciones= new ArrayList<>();
        recyclerView=view.findViewById(R.id.idListaTransacciones);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        TextView titulo = view.findViewById(R.id.txtTituloDialogFragment);

        txtTotal = view.findViewById(R.id.txtSaldoTotalTransaccion);

        if(_id>0)
        {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            titulo.setText(_titulo);
            //linearLayoutRecycler.setVisibility(View.GONE);
        }
        else
        {
            txtTotal.setVisibility(View.GONE);
            titulo.setText("Transacciones");
            btnAgregar= view.findViewById(R.id.btnAgregarTransacciones);
            btnAgregar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    transacciones_fragment_ingreso frag_ingreso= new transacciones_fragment_ingreso(listaCuentas);
                    frag_ingreso.show(getActivity().getSupportFragmentManager(),"IngresoTransaccion");
                }
            });
        }
        btnClose=view.findViewById(R.id.btnCloseTransacciones);

        request= Volley.newRequestQueue(getContext());

        CargarTransacciones();

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });



        return view;
    }

    public void CargarTransacciones(){
        if(_id>0)
            jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, CConeccion.Url_Listar_Transacciones + CConeccion.bd + "&id=" + _id, null, this, this);
        else
            jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, CConeccion.Url_Listar_Transacciones + CConeccion.bd, null, this, this);
        request.add(jsonObjectRequest);
    }

    private void CargarAdapterRecylerView() {
        Collections.sort(listaTransacciones, new Comparator<CTransaciones>() {
            @Override
            public int compare(CTransaciones o1, CTransaciones o2) {
                int result = o2.getFecha().compareTo(o1.getFecha());

                if (result != 0)
                {
                    return result;
                }
                return String.valueOf(o2.getId()).compareTo(String.valueOf(o1.getId()));

            }
        });
        AdaptadorTransacciones adapter = new AdaptadorTransacciones(listaTransacciones,_id);

        recyclerView.setAdapter(adapter);

        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));

    }


    @Override
    public void onResponse(JSONObject response) {
        CTransaciones tran;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        JSONArray jsonTransacciones= response.optJSONArray("transaccionModel");

        if(jsonTransacciones!=null) {
            try {
                listaTransacciones.clear();
                for (int i = 0; i < jsonTransacciones.length(); i++) {
                    tran = new CTransaciones();
                    JSONObject jsonObject = null;

                    jsonObject = jsonTransacciones.getJSONObject(i);


                    tran.setId(jsonObject.optInt("id"));
                    tran.setCuentaDestino(jsonObject.optString("cuentaDestino"));
                    tran.setCuentaOrigen(jsonObject.optString("cuentaOrigen"));
                    tran.setMonto(jsonObject.optDouble("Monto"));
                    JSONObject jsonObjectFecha = jsonObject.getJSONObject("fecha");
                    String sFecha = jsonObjectFecha.optString("date");
                    sFecha = sFecha.substring(0, 10);
                    tran.setFecha(formatter.parse(sFecha));
                    tran.setCuentaIdDestino(jsonObject.optInt("cuentaIdDestino"));
                    tran.setCuentaIdOrigen(jsonObject.optInt("cuentaIdOrigen"));
                    tran.setDescripcion(jsonObject.optString("descripcion"));

                    listaTransacciones.add(tran);

                }
                if(_id>0)
                    CalcularTotal();
                CargarAdapterRecylerView();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void CalcularTotal() {
        Double total=0.0;
        for(CTransaciones c:listaTransacciones)
        {
            if(c.getCuentaIdOrigen()==_id)
                total-=c.getMonto();
            else if(c.getCuentaIdDestino()==_id)
                total+=c.getMonto();
        }
        txtTotal.setText(String.format("TOTAL: %.2f", total));
        if(total<0)
            txtTotal.setBackgroundColor(getResources().getColor(R.color.colorSub));
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getContext(), "Error de coneccion", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActualiza() {

    }
}
