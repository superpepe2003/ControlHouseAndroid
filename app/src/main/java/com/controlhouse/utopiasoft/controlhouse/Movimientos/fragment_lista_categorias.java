package com.controlhouse.utopiasoft.controlhouse.Movimientos;

import android.content.Intent;
import android.net.sip.SipSession;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ExpandableListView;
import android.widget.ImageButton;

import com.controlhouse.utopiasoft.controlhouse.Categorias.fragment_categorias_ingreso;
import com.controlhouse.utopiasoft.controlhouse.Entidades.CCategorias;
import com.controlhouse.utopiasoft.controlhouse.Entidades.claveValor;
import com.controlhouse.utopiasoft.controlhouse.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class fragment_lista_categorias extends DialogFragment {

    ExpandableListView lv;
    int _id;
    List<CCategorias> _listaCategorias;
    ImageButton btnClose;
    boolean ponerCateNueva;

    List<claveValor> padre;
    Map<Integer, List<claveValor>> hijos;
    int _valor;
    Boolean _tipo;

    public void setPonerListaCategorias(List<CCategorias> lCategorias)
    {
        this._listaCategorias=lCategorias;
    }

    public fragment_lista_categorias()
    {}

    public fragment_lista_categorias(List<CCategorias>lCategorias, int id, int valor, boolean ponerCateNueva) {
        _id=id;
        setPonerListaCategorias(lCategorias);
        _valor=valor;
        this.ponerCateNueva=ponerCateNueva;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.mov_dialog_fragment_lista_categorias, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        if(_valor==1)
            _tipo=true;
        else
            _tipo=false;

        btnClose= view.findViewById(R.id.btnClose);

        padre= new ArrayList<>();
        hijos = new HashMap<>();
        lv = view.findViewById(R.id.listv);

        CargarExpandLisView();

        /*lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), "el id es: " + id, Toast.LENGTH_SHORT).show();
            }
        });*/

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

        padre=CargarLista(0);

        for(claveValor papa:padre)
        {
            //if(papa.getId()!=-1)
            hijos.put(papa.getId(), CargarLista(papa.getId()));
        }

        adapter_categorias_arbol adaptarbol= new adapter_categorias_arbol(getContext(),padre,hijos, _id);
        adaptarbol.setOnClickListener(new adapter_categorias_arbol.OnClickListener() {
            @Override
            public void onClick(int id) {
                if(id==-1)
                {
                    if(ponerCateNueva) {
                        fragment_categorias_ingreso frg = new fragment_categorias_ingreso(_listaCategorias, _valor);
                        frg.show(getActivity().getSupportFragmentManager(), "Ingreso Categoria");
                    }
                    else
                    {
                        Intent intent = new Intent();
                        intent.putExtra("id", id);
                        getTargetFragment().onActivityResult(getTargetRequestCode(), 1, intent);
                        dismiss();
                    }
                }
                else {
                    Intent intent = new Intent();
                    intent.putExtra("id", id);
                    getTargetFragment().onActivityResult(getTargetRequestCode(), 1, intent);
                    dismiss();
                }
            }
        });
        lv.setAdapter(adaptarbol);
    }

    public List<claveValor> CargarLista(int id)
    {
        List<claveValor> listTemp= new ArrayList<>();

        //SI PONER CATEGORIA PONE PARA INGRESAR UNA NUEVA CATEGORIA, EN EL CASO QUE SEA DESDE AGREGAR CATEGORIA VA A PONER PARA BORRAR SI HAY ALGUN PADRE SELECCIONADO
        if(id==0)
        {
            if(ponerCateNueva){
                claveValor ingreso = new claveValor();
                ingreso.setId(-1);
                ingreso.setNombre("Ingresar nueva categoria");
                listTemp.add(ingreso);
            }
            else
            {
                claveValor ingreso = new claveValor();
                ingreso.setId(-1);
                ingreso.setNombre("Ninguna Categoria Padre");
                listTemp.add(ingreso);
            }
        }

        for(CCategorias cat:_listaCategorias)
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

 }
