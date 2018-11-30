package com.controlhouse.utopiasoft.controlhouse.Movimientos;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.controlhouse.utopiasoft.controlhouse.Entidades.CCategorias;
import com.controlhouse.utopiasoft.controlhouse.Entidades.CCuenta;
import com.controlhouse.utopiasoft.controlhouse.Entidades.CMovimiento;
import com.controlhouse.utopiasoft.controlhouse.Entidades.CSubCategorias;
import com.controlhouse.utopiasoft.controlhouse.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 */
public class fragment_movimientos_ingreso extends DialogFragment implements Response.ErrorListener, Response.Listener<JSONObject> {

    ArrayList<CMovimiento> listaMovimientos;
    ArrayList<CCuenta> listaCuentas;
    ArrayList<CCategorias> listaCategorias;
    ArrayList<CSubCategorias> listaSubCategorias;

    HashMap<Integer, String> cCuenta = new HashMap<Integer, String>();
    HashMap<Integer, String> cCategoria = new HashMap<Integer, String>();
    HashMap<Integer, String> cSubCategoria = new HashMap<Integer, String>();

    ScrollView scrollIngreso;

    Button btnFecha, btnIngreso;
    ImageButton btnClose;
    Spinner cboCategoria, cboCuenta, cboSubCategoria;
    TextInputEditText txtDescripcion, txtHashtag, txtMonto;

    Drawable drawableIngreso, drawableIngresoSelec, drawableEgreso, drawableEgresoSelec;

    RadioButton rdbIngreso,rdbEgreso;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    public fragment_movimientos_ingreso(ArrayList<CMovimiento> listaMovimientos, ArrayList<CCuenta> listaCuentas, ArrayList<CCategorias> listaCategorias, ArrayList<CSubCategorias> listaSubCategorias) {
        // Required empty public constructor
        this.listaMovimientos= listaMovimientos;
        this.listaCuentas=listaCuentas;
        this.listaSubCategorias=listaSubCategorias;
        this.listaCategorias=listaCategorias;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_movimientos_ingreso, container, false);

        btnClose = v.findViewById(R.id.btnClose);
        btnIngreso = v.findViewById(R.id.btn_aplicar);
        btnFecha = v.findViewById(R.id.btn_fecha);
        cboCuenta = v.findViewById(R.id.cboCuenta);
        cboCategoria = v.findViewById(R.id.cboCategoria);
        cboSubCategoria = v.findViewById(R.id.cboSubCategoria);
        rdbIngreso= v.findViewById(R.id.rdb_Ingreso);
        rdbEgreso=v.findViewById(R.id.rdb_Egreso);
        rdbEgreso.setChecked(true);
        txtHashtag=v.findViewById(R.id.txthashtag);
        scrollIngreso = v.findViewById(R.id.ScrollIngreso);
        txtMonto = v.findViewById(R.id.txtMonto);
        txtDescripcion = v.findViewById(R.id.txtDescripcion);

        CargarImagenesDeRadioButton();
        CargarRadioButtonIngresoEgreso();
        CargarCombos();


        btnFecha.setText(fechaNow());

        request = Volley.newRequestQueue(getContext());

        btnFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowCalendario();
            }
        });

        rdbIngreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rdbIngreso.isChecked())
                {
                    CargarRadioButtonIngresoEgreso();
                }
            }
        });

        rdbEgreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rdbEgreso.isChecked())
                {
                    CargarRadioButtonIngresoEgreso();
                }
            }
        });

        cboCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CargarSubCategoria(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });



        /*txtHashtag.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction()==KeyEvent.ACTION_DOWN) && (keyCode==KeyEvent.KEYCODE_SPACE))
                {
                    txtHashtag.setText(PonerHash());
                    txtHashtag.setSelection(txtHashtag.getText().length());
                    return true;
                }
                return false;
            }
        });*/

        txtHashtag.addTextChangedListener(new TextWatcher() {
            Boolean editado=true;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
               /* String c = s.toString().substring(s.toString().length()-1);
                if(c== " ") {
                    if (editado) {
                        editado = false;
                        txtHashtag.setText(PonerHash());
                        txtHashtag.setSelection(txtHashtag.getText().length());
                    }
                    else
                        editado = true;
                }*/
            }
        });

        txtHashtag.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    if(txtHashtag.getText().length()>0)
                        txtHashtag.setText(PonerHash());
                }
            }
        });

        btnIngreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtHashtag.getText().length()>0)
                    txtHashtag.setText(PonerHash());
                if(VerificarDatos())
                {
                    GrabarDatos();
                }
            }
        });

        return v;
    }


    private boolean VerificarDatos()
    {
        if(txtDescripcion.getText()==null)
        {

        }
        if(txtMonto.getText()!=null) {
            return true;
        }

        return false;
    }


    private void GrabarDatos()
    {
        String URL = "http://utopiasoft.duckdns.org:8080/wscontrol/servicemovimientos.php?modo=C";
        String _fecha= "&fecha=" + btnFecha.getText().toString();
        String _monto = "&monto=" + txtMonto.getText().toString();
        String _descripcion= "&descripcion=" + txtDescripcion.getText().toString();
        String _hashtag= "&hashtag=" + traerHashtag();
        String _categoria = "&categoriaid=" + listaCategorias.get(cboCategoria.getSelectedItemPosition()).getId();
        int _subCatId = DevolverIdSubCategoria(listaCategorias.get(cboCategoria.getSelectedItemPosition()).getId());
        String _subcategoria ="&subcategoriaid=";
        if(_subCatId!=-1) {
            _subcategoria += _subCatId;
        }
        String _cuenta= "&cuentaid=" + listaCuentas.get(cboCuenta.getSelectedItemPosition()).getId();
        String _tipo;
        if(rdbIngreso.isChecked())
            _tipo="&tipo=1";
        else
            _tipo="&tipo=0";


        String Url2 = URL + _fecha + _cuenta + _categoria + _subcategoria + _monto + _tipo + _hashtag + _descripcion;
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,Url2, null, this, this);
        request.add(jsonObjectRequest);
    }


    private String traerHashtag()
    {
        String _hashtag = "";
        String Temp= txtHashtag.getText().toString();
        Temp.trim();
        String[] _hashs = Temp.split("#");
        for(int i=1;i<_hashs.length;i++)
        {
            if(i==1)
                _hashtag += _hashs[i];
            else
                _hashtag += "-" + _hashs[i];
        }

        return _hashtag;
    }


    private String PonerHash()
    {
        String palabra = txtHashtag.getText().toString();
        String t = "#";
        String t1;
        String[] temp = palabra.split(" ");
        String[] temp2 = new String[temp.length];
        int i = 0;
        for (String c : temp) {
            if(c.length()>0) {
                if (!(c.startsWith("#"))) // && (!(c.startsWith(" ")) || (c.toString().length() > 1)))
                    c = t.concat(c);
                temp2[i] = c;
                i++;
            }
        }
        String r = "";
        for(i=0;i<temp2.length;i++) {
            if(temp2[i]!=null)
                r += temp2[i];
        }

        return r;
    }

    private void ShowCalendario() {
        SimpleDateFormat sdf =  new SimpleDateFormat("dd/MM/yyyy");
        Date botonDate =  new Date();
        try{
            botonDate = sdf.parse(btnFecha.getText().toString());
        }
        catch (ParseException e)
        {
            Toast.makeText(getContext(), "Error en conversion de fechas: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        final int dia = Integer.parseInt(DateFormat.format("dd", botonDate).toString());
        final int mes = Integer.parseInt(DateFormat.format("MM", botonDate).toString()) - 1;
        final int anio = Integer.parseInt(DateFormat.format("yyyy", botonDate).toString());

        DatePickerDialog pickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String fecha = Integer.toString(dayOfMonth) + "/" + Integer.toString(month + 1) + "/" + Integer.toString(year);
                    btnFecha.setText(fecha);
            }
        }, anio, mes, dia);

        pickerDialog.show();

    }

    private void CargarCombos() {
        ArrayList<String> lCuentas = new ArrayList<String>();
        ArrayList<String> lCategorias= new ArrayList<String>();
        //HashMap<Integer , String> lCuentas = new HashMap<Integer, String>();
        //HashMap<Integer , String> lCategorias = new HashMap<Integer, String>();


        for(CCuenta rcuenta :listaCuentas) {
            lCuentas.add(rcuenta.getNombre());
        }

        for(CCategorias rCategoria :listaCategorias) {
            lCategorias.add(rCategoria.getNombre());
        }

        if(lCuentas.isEmpty())
        {
            Toast.makeText(getContext(),"No existen cuentas, no puede agregar Movimientos", Toast.LENGTH_SHORT).show();
            dismiss();
        }

        if(lCategorias.isEmpty())
        {
            Toast.makeText(getContext(),"No existen categorias, no puede agregar Movimientos", Toast.LENGTH_SHORT).show();
            dismiss();
        }
        ArrayAdapter<String> adapterCuenta;
        adapterCuenta = new ArrayAdapter<String>(getContext(), R.layout.spinnerpersonalizado,lCuentas);

        ArrayAdapter<String> adapterCategoria;
        adapterCategoria = new ArrayAdapter<String>(getContext(), R.layout.spinnerpersonalizado, lCategorias);

        cboCuenta.setAdapter(adapterCuenta);
        cboCategoria.setAdapter(adapterCategoria);
        CargarSubCategoria(0);

    }

    public void CargarSubCategoria(int i)
    {
        ArrayList<String> lSubCategorias= new ArrayList<String>();
        for(CSubCategorias rSubCategorias: listaSubCategorias)
        {
            if (rSubCategorias.getIdCategoria() == listaCategorias.get(i).getId()) {
                lSubCategorias.add(rSubCategorias.getNombre());
            }
        }

        ArrayAdapter<String> adapterSubCategoria;
        adapterSubCategoria = new ArrayAdapter<String>(getContext(), R.layout.spinnerpersonalizado,lSubCategorias);

        cboSubCategoria.setAdapter(adapterSubCategoria);
    }

    public int DevolverIdSubCategoria(int id)
    {
        int position = cboSubCategoria.getSelectedItemPosition();
        int index=0;

        for(CSubCategorias rSubCategorias: listaSubCategorias)
        {
            if (rSubCategorias.getIdCategoria() == id) {
                if(index==position)
                    return rSubCategorias.getId();
                index++;
            }
        }
        return -1;
    }

    public String fechaNow()
    {
        Calendar c = Calendar.getInstance();

        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        int year= c.get(Calendar.YEAR);

       return Integer.toString(day) + "/" + Integer.toString(month) + "/" + Integer.toString(year);
    }

    public void CargarRadioButtonIngresoEgreso()
    {
        //Drawable top = resizeImage(getContext(), R.drawable.icono_fecha,(int)Math.round(getResources().getDimension(R.dimen.icono_filtrar)), (int)Math.round(getResources().getDimension(R.dimen.icono_filtrar)));
        //Drawable topChecked = resizeImage(getContext(), R.drawable.icono_fecha_seleccionado,(int)Math.round(getResources().getDimension(R.dimen.icono_filtrar)), (int)Math.round(getResources().getDimension(R.dimen.icono_filtrar)));
        //RadioButton
        if(rdbIngreso.isChecked())
            rdbIngreso.setCompoundDrawablesWithIntrinsicBounds(null, drawableIngresoSelec , null, null);
        else
            rdbIngreso.setCompoundDrawablesWithIntrinsicBounds(null, drawableIngreso, null, null);
        if(rdbEgreso.isChecked())
            rdbEgreso.setCompoundDrawablesWithIntrinsicBounds(null, drawableEgresoSelec, null , null);
        else
            rdbEgreso.setCompoundDrawablesWithIntrinsicBounds(null,drawableEgreso,null,null);
    }

    public void CargarImagenesDeRadioButton(){
        drawableIngreso= resizeImage(getContext(), R.drawable.icono_ingreso_checkbok,(int)Math.round(getResources().getDimension(R.dimen.icono_filtrar)),(int)Math.round(getResources().getDimension(R.dimen.icono_filtrar)));
        drawableIngresoSelec = resizeImage(getContext(), R.drawable.icono_ingreso_checkbok_seleccionado,(int)Math.round(getResources().getDimension(R.dimen.icono_filtrar)),(int)Math.round(getResources().getDimension(R.dimen.icono_filtrar)));

        drawableEgreso= resizeImage(getContext(), R.drawable.icono_egreso_checkbok,(int)Math.round(getResources().getDimension(R.dimen.icono_filtrar)),(int)Math.round(getResources().getDimension(R.dimen.icono_filtrar)));
        drawableEgresoSelec = resizeImage(getContext(), R.drawable.icono_egreso_checkbok_seleccionado,(int)Math.round(getResources().getDimension(R.dimen.icono_filtrar)),(int)Math.round(getResources().getDimension(R.dimen.icono_filtrar)));
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


    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getContext(),"ah ocurrido un error al guardar, " + error.getMessage().toString() , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
        JSONArray jsonArray = response.optJSONArray("idMovimiento");
        JSONObject jsonObject = null;

            jsonObject = jsonArray.getJSONObject(0);


            int id= jsonObject.optInt("id");

            if(id>0)
                Toast.makeText(getContext(),"Se ha Grabado Correctamente", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getContext(),"El dato no pudo Grabarse", Toast.LENGTH_SHORT).show();

            LimpiarCampos();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void LimpiarCampos()
    {
        txtDescripcion.setText("");
        txtHashtag.setText("");
        txtMonto.setText("");
        btnFecha.setText(fechaNow());
        rdbEgreso.setChecked(true);
    }
}
