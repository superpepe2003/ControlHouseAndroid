package com.controlhouse.utopiasoft.controlhouse.Categorias;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.controlhouse.utopiasoft.controlhouse.Entidades.CCategorias;
import com.controlhouse.utopiasoft.controlhouse.Entidades.CConeccion;
import com.controlhouse.utopiasoft.controlhouse.Entidades.claveValor;
import com.controlhouse.utopiasoft.controlhouse.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;


public class fragment_lista_categorias extends DialogFragment {

    ExpandableListView lv;
    int _id;
    List<CCategorias> _listaCategorias = new ArrayList<>();
    ImageButton btnClose;
    int lugarDeLlamado;
    private onClickListCategoriaListener listener;
    private onMenuListCategoriaListener listenerMenu;

    LinearLayout llCabecderaBotones;

    List<claveValor> padre;
    Map<Integer, List<claveValor>> hijos;
    int _valor;
    Boolean _tipo;


    public interface onClickListCategoriaListener{
        void onItemClick(int id, String nombre);
    }

    public interface onMenuListCategoriaListener{
        void onMenuEliminarClick(int id);
    }

    public void setOnClickListCategoriaListener(onClickListCategoriaListener listener){ this.listener=listener;}
    public void setOnMenuListCategoriaListener(onMenuListCategoriaListener listener){this.listenerMenu=listener;}


    public fragment_lista_categorias()
    {}

    //lugarDeLLamado 0 = Ingresar movimiento muestro categorias - 1 = Ingresar Categorias muestro solo padres - 3 = Lista de Categorias en las solapas
    public fragment_lista_categorias(List<CCategorias> lCategorias, int id, int valor, int lugarDeLLamado) {
        _id=id;
        setPonerListaCategorias(lCategorias);
        _valor=valor;
        this.lugarDeLlamado=lugarDeLLamado;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.categorias_dialog_fragment_lista, container, false);
        if(getDialog()!=null)
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);


        if(_valor==1)
            _tipo=true;
        else
            _tipo=false;

        btnClose= view.findViewById(R.id.btnClose);

        padre= new ArrayList<>();
        hijos = new HashMap<>();
        lv = view.findViewById(R.id.listv);

        llCabecderaBotones =  view.findViewById(R.id.llCabeceraBotones);

        if(lugarDeLlamado==2)
            llCabecderaBotones.setVisibility(View.GONE);

        CargarExpandLisView();

        lv.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return false;

            }
        });

        lv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                return false;
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

    public void CargarExpandLisView()
    {
        if(padre.size()>0)
            padre.clear();
        if(hijos.size()>0)
            hijos.clear();

        padre=CargarLista(0,_listaCategorias);


        for(claveValor papa:padre)
        {
            hijos.put(papa.getId(), CargarLista(papa.getId(),_listaCategorias));
        }


        adapter_categorias_arbol adaptarbol;
        if(lugarDeLlamado==2)
            adaptarbol= new adapter_categorias_arbol(getContext(),padre,hijos, _id, true);
        else
            adaptarbol= new adapter_categorias_arbol(getContext(),padre,hijos, _id);
        adaptarbol.setOnClickListener(new adapter_categorias_arbol.OnClickListener() {
            @Override
            public void onClick(int id) {
                if(id==-1)
                {
                    if((lugarDeLlamado==0) || (lugarDeLlamado==2)) {
                        fragment_categorias_ingreso frg = new fragment_categorias_ingreso(_listaCategorias, _valor, true);
                        frg.setOnActualizarListaCategoriaListener(new fragment_categorias_ingreso.onActualizarListaCategoriaListener() {
                            @Override
                            public void onActualizarListaCategoria(List<CCategorias> listaCategorias) {
                                setPonerListaCategorias(listaCategorias);
                                CargarExpandLisView();
                            }
                        });
                        frg.show(getActivity().getSupportFragmentManager(), "Ingreso Categoria");
                    }
                    else if(lugarDeLlamado==1)
                    {
                        /*Intent intent = new Intent();
                        intent.putExtra("id", id);
                        getTargetFragment().onActivityResult(getTargetRequestCode(), 1, intent);*/
                        if(listener!=null)
                            listener.onItemClick(id, getCategoriaId(id));
                        dismiss();
                    }
                }
                else {
                    /*Intent intent = new Intent();
                    intent.putExtra("id", id);

                    getTargetFragment().onActivityResult(getTargetRequestCode(), 1, intent);*/

                    if(listener!=null)
                        listener.onItemClick(id, getCategoriaId(id));

                    if(lugarDeLlamado!=2)
                        dismiss();
                }
            }

            @Override
            public void onClickEliminar(int id) {
                if(listenerMenu!=null)
                    listenerMenu.onMenuEliminarClick(id);
            }
        });
        lv.setAdapter(adaptarbol);
    }

    public void setPonerListaCategorias(List<CCategorias> listaCategorias) {
        this._listaCategorias=listaCategorias;
    }

    public String getCategoriaId(int id)
    {
        for(CCategorias cat:_listaCategorias)
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

    public List<claveValor> CargarLista(int id, List<CCategorias> categoriasApasar)
    {
        List<claveValor> listTemp= new ArrayList<>();

        //SI PONER CATEGORIA PONE PARA INGRESAR UNA NUEVA CATEGORIA, EN EL CASO QUE SEA DESDE AGREGAR CATEGORIA VA A PONER PARA BORRAR SI HAY ALGUN PADRE SELECCIONADO
        if(id==0)
        {
            if((lugarDeLlamado==0)||(lugarDeLlamado==2)){
                claveValor ingreso = new claveValor();
                ingreso.setId(-1);
                ingreso.setNombre("Ingresar nueva categoria");
                listTemp.add(ingreso);
            }
            else if(lugarDeLlamado==1)
            {
                claveValor ingreso = new claveValor();
                ingreso.setId(-1);
                ingreso.setNombre("Ninguna Categoria Padre");
                listTemp.add(ingreso);
            }
        }

        for(CCategorias cat:categoriasApasar)
        {
           if((cat.getIdpadre()== id) && (cat.getTipo()== _tipo))
           {
               claveValor temp= new claveValor();
               temp.setId(cat.getId());
               temp.setNombre(cat.getNombre());
               listTemp.add(temp);
           }
        }

        return listTemp;
    }

    /*public List<claveValor> CargarListaPadres(int id) {
        List<claveValor> listTemp = new ArrayList<>();

        return listTemp;
    }

    public void CargarCategorias()
    {
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, CConeccion.URL_Listar_Categorias + CConeccion.bd, null, this, this);
        request.add(jsonObjectRequest);
    }*/

   /* @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getContext(), "Error: " + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(JSONObject response) {
        CCategorias cat;
        _listaCategorias.clear();
        JSONArray jsonArray= response.optJSONArray("categoriamodel");

        if(jsonArray!=null)
        {
            for (int i = 0; i < jsonArray.length(); i++) {
                cat = new CCategorias();
                JSONObject jsonObject = null;
                try {
                    jsonObject = jsonArray.getJSONObject(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

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

                _listaCategorias.add(cat);
            }

            CargarExpandLisView();
        }

    }

    private List<CCategorias> soloPadres()
    {
        List<CCategorias> listTemp= new ArrayList<>();

        for(CCategorias c:_listaCategorias)
        {
            if(c.getIdpadre()==0)
            {
                listTemp.add(c);
            }
        }
        return  listTemp;
    }*/

}
