package com.controlhouse.utopiasoft.controlhouse.Categorias;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
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
import com.controlhouse.utopiasoft.controlhouse.Entidades.CCategorias;
import com.controlhouse.utopiasoft.controlhouse.Entidades.CConeccion;
import com.controlhouse.utopiasoft.controlhouse.Entidades.IFiltro;
import com.controlhouse.utopiasoft.controlhouse.Movimientos.fragment_lista_categorias;
import com.controlhouse.utopiasoft.controlhouse.Movimientos.fragment_movimientos_ingreso;
import com.controlhouse.utopiasoft.controlhouse.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class fragment_categorias_ingreso extends DialogFragment implements Response.ErrorListener, Response.Listener<JSONObject> {
    int tipo;
    List<CCategorias> listaCategorias, listapadres;

    ImageButton btnClose;
    Button btnAplicar;
    Button btnPadreCategorias;
    CheckBox chkIsFija;
    EditText txtNombre, txtMonto;
    LinearLayout linearLayoutMonto;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    int catePadreId=0;

    public fragment_categorias_ingreso(List<CCategorias> cates, int tipo) {
        this.listaCategorias= cates;
        this.tipo=tipo;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.categorias_dialog_fragment_ingreso, container, false);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        btnPadreCategorias= view.findViewById(R.id.btnCatePadreCategorias);
        btnClose= view.findViewById(R.id.btnCloseCategorias);
        btnAplicar=view.findViewById(R.id.btn_aplicar_categorias);
        txtMonto= view.findViewById(R.id.txtMontoCategoria);
        txtNombre=view.findViewById(R.id.txtNombreCategoria);
        chkIsFija=view.findViewById(R.id.chkIsfija);
        linearLayoutMonto =  view.findViewById(R.id.llMonto);

        request = Volley.newRequestQueue(getContext());

        listapadres =  soloPadres();

        btnPadreCategorias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment_lista_categorias frg = new fragment_lista_categorias(listapadres, -1, tipo,false);
                frg.setTargetFragment(fragment_categorias_ingreso.this,1);
                frg.show(getActivity().getSupportFragmentManager(), "dialog");
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnAplicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(VerificarDatos())
                {
                    GrabarWSCategoria();
                }
            }
        });

        chkIsFija.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    linearLayoutMonto.setVisibility(View.VISIBLE);
                else
                    linearLayoutMonto.setVisibility(View.GONE);
            }
        });

        return view;
    }

    private boolean VerificarDatos() {
        if(txtNombre.getText().length()>0)
        {
            return true;
        }

        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1)
        {
            int _idCate= data.getIntExtra("id", -1);
            if(_idCate!=-1) {
                catePadreId=_idCate;
                btnPadreCategorias.setText(getCategoriaId(catePadreId));
            }
            else
            {
                catePadreId=0;
                btnPadreCategorias.setText("?");
            }
        }
    }

    public String getCategoriaId(int id)
    {
        for(CCategorias cat:listaCategorias)
        {
            if(id!=-1) {
                if (cat.getId() == id)
                    return cat.getNombre();
            }
            else
                return cat.getNombre();
        }
        return "";
    }

    private List<CCategorias> soloPadres()
    {
        List<CCategorias> listTemp= new ArrayList<>();

        for(CCategorias c:listaCategorias)
        {
            if(c.getIdpadre()==0)
            {
                listTemp.add(c);
            }
        }
        return  listTemp;
    }


    private void GrabarWSCategoria(){
        String nombre= "&nombre=" + txtNombre.getText().toString().replace(" ", "%20");
        String tipo= "&tipo=" + this.tipo;
        String catePadre= "&idPadre=" + this.catePadreId;
        String monto,isFija, Url2;

        Url2=CConeccion.URL_Ingresar_Categorias + CConeccion.bd + nombre + tipo + catePadre ;

        if(chkIsFija.isChecked()) {
            monto = "&monto=" + txtMonto.getText().toString();
            isFija = "&FijaMensualmente=1";
            Url2 += monto + isFija;
        }

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,Url2, null, this, this);
        request.add(jsonObjectRequest);
    }


    @Override
    public void onErrorResponse(VolleyError error) {
        if (error == null || error.networkResponse == null) {
            return;
        }

        String body;
        //get status code here
        final String statusCode = String.valueOf(error.networkResponse.statusCode);
        //get response body and parse with appropriate encoding
        try {
            body = new String(error.networkResponse.data,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            // exception
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        CCategorias cat;
        JSONArray jsonArray = response.optJSONArray("resultado");
        JSONArray jsonCategorias = response.optJSONArray("categoriamodel");

        try {
            if(jsonArray!=null) {
                JSONObject jsonObject = jsonArray.getJSONObject(0);

                int id = jsonObject.optInt("valor");


                if (id > 0) {
                    Toast.makeText(getContext(), "Se ha Grabado Correctamente", Toast.LENGTH_SHORT).show();

                    CargarCategorias();

                    /*fragment_lista_categorias f = (fragment_lista_categorias) (getActivity()).getSupportFragmentManager().findFragmentByTag("dialogListaCategoria");
                    f.CargarExpandLisView();

                    dismiss();*/
                } else
                    Toast.makeText(getContext(), "El dato no pudo Grabarse", Toast.LENGTH_SHORT).show();
            }
            else if(jsonCategorias!=null) {
                listaCategorias.clear();
                //Toast.makeText(getApplicationContext(), "HAY CATEGORIAS", Toast.LENGTH_LONG).show();
                for (int i = 0; i < jsonCategorias.length(); i++) {
                    cat = new CCategorias();
                    JSONObject jsonObject = null;
                    jsonObject = jsonCategorias.getJSONObject(i);

                    cat.setId(jsonObject.optInt("Id"));
                    cat.setNombre(jsonObject.optString("Nombre"));
                    int tipo = jsonObject.optInt("Tipo");
                    if (tipo == 1)
                        cat.setTipo(true);
                    else
                        cat.setTipo(false);
                    tipo = jsonObject.optInt("FijaMensualmente");
                    if (tipo == 1)
                        cat.setFija(true);
                    else
                        cat.setFija(false);
                    cat.setIdpadre(jsonObject.optInt("IdPadre"));
                    cat.setMonto(jsonObject.optDouble("Monto"));

                    listaCategorias.add(cat);
                }
                fragment_lista_categorias f = (fragment_lista_categorias) (getActivity()).getSupportFragmentManager().findFragmentByTag("dialogListaCategoria");
                f.setPonerListaCategorias(listaCategorias);
                f.CargarExpandLisView();

                dismiss();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void CargarCategorias()
    {
        jsonObjectRequest= new JsonObjectRequest(Request.Method.GET, CConeccion.URL_Listar_Categorias + CConeccion.bd, null, this,this);
        request.add(jsonObjectRequest);
    }
}
