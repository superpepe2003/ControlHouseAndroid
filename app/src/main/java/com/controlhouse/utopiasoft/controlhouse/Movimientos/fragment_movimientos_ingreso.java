package com.controlhouse.utopiasoft.controlhouse.Movimientos;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.controlhouse.utopiasoft.controlhouse.Entidades.CCategorias;
import com.controlhouse.utopiasoft.controlhouse.Entidades.CConeccion;
import com.controlhouse.utopiasoft.controlhouse.Entidades.CCuenta;
import com.controlhouse.utopiasoft.controlhouse.Entidades.CMovimiento;
import com.controlhouse.utopiasoft.controlhouse.Entidades.IFiltro;
import com.controlhouse.utopiasoft.controlhouse.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
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
    int tipo, cateId, cuentaId;
    CMovimiento movModifica;
    boolean isModificacion; //false es ingreso true es modificacion

    HashMap<Integer, String> cCuenta = new HashMap<Integer, String>();
    HashMap<Integer, String> cCategoria = new HashMap<Integer, String>();

    ScrollView scrollIngreso;

    Button btnFecha, btnIngreso, btnCategorias;
    ImageButton btnClose;
    Spinner cboCuenta;
    TextInputEditText txtDescripcion, txtHashtag, txtMonto;

    Drawable drawableIngreso, drawableIngresoSelec, drawableEgreso, drawableEgresoSelec;

    RadioButton rdbIngreso,rdbEgreso;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    public fragment_movimientos_ingreso(int tipo, ArrayList<CMovimiento> listaMovimientos, ArrayList<CCuenta> listaCuentas, ArrayList<CCategorias> listaCategorias, int cuentaId, int cateId, CMovimiento mov) {
        // Required empty public constructor
        this.listaMovimientos= listaMovimientos;
        this.listaCuentas=listaCuentas;
        this.listaCategorias=listaCategorias;
        this.tipo=tipo;
        this.cateId=cateId;
        this.cuentaId=cuentaId;
        this.movModifica=mov;
        if(mov==null)
            isModificacion=false;
        else
            isModificacion=true;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.mov_dialog_fragment_ingreso, container, false);

        btnClose = v.findViewById(R.id.btnClose);
        btnIngreso = v.findViewById(R.id.btn_aplicar);
        btnFecha = v.findViewById(R.id.btn_fecha);
        cboCuenta = v.findViewById(R.id.cboCuenta);
        btnCategorias = v.findViewById(R.id.btnCategorias);
        /*rdbIngreso= v.findViewById(R.id.rdb_Ingreso);
        rdbEgreso=v.findViewById(R.id.rdb_Egreso);
        rdbEgreso.setChecked(true);*/
        txtHashtag=v.findViewById(R.id.txthashtag);
        scrollIngreso = v.findViewById(R.id.ScrollIngreso);
        txtMonto = v.findViewById(R.id.txtMonto);
        txtDescripcion = v.findViewById(R.id.txtDescripcion);

        /* CargarImagenesDeRadioButton();
        CargarRadioButtonIngresoEgreso();*/
        CargarCombos();

        btnFecha.setText(fechaNow());
        btnCategorias.setText(getCategoriaId(cateId));

        request = Volley.newRequestQueue(getContext());

        if(isModificacion)
            CargarDatos();

        btnFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowCalendario();
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

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
                    if(isModificacion)
                        ModificarDatos();
                    else
                        GrabarDatos();
                }
                else
                {
                    Toast.makeText(getContext(),"No puede dejar el campo Monto vacio", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCategorias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment_lista_categorias frg = new fragment_lista_categorias(listaCategorias, -1, tipo,true);
                frg.setTargetFragment(fragment_movimientos_ingreso.this,1);
                frg.show(getActivity().getSupportFragmentManager(), "dialogListaCategoria");
            }
        });

        return v;
    }

    private void CargarDatos(){
        String d,m,a;
        d =  DateFormat.format("dd",movModifica.getFecha()).toString();
        m = DateFormat.format("MM",movModifica.getFecha()).toString();
        a = DateFormat.format("yy",movModifica.getFecha()).toString();
        txtMonto.setText(Double.toString(movModifica.getMonto()));
        txtHashtag.setText(movModifica.getHashtag());
        txtDescripcion.setText(movModifica.getDescripcion());
        btnFecha.setText(d +"/"+ m +"/"+a);
        cboCuenta.setId(movModifica.getIdCuenta());
        btnCategorias.setText(getCategoriaId(movModifica.getIdCategoria()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1)
        {
            int _idCate= data.getIntExtra("id", -1);
            if(_idCate!=-1) {
                cateId=_idCate;
                btnCategorias.setText(getCategoriaId(cateId));
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

    private boolean VerificarDatos()
    {
        if(txtMonto.getText().length()>0) {
            double p = Double.parseDouble(txtMonto.getText().toString());
            if(!Double.isNaN(p)) {
                if(btnCategorias.getText().toString().length()>0)
                    return true;
            }
        }

        return false;
    }


    private void GrabarDatos()
    {

        //SpinnerDatos categoriaSeleccionada = (SpinnerDatos) cboCategoria.getSelectedItem();
        SpinnerDatos cuentaSeleccionada= (SpinnerDatos) cboCuenta.getSelectedItem();

        String _fecha= "&fecha=" + btnFecha.getText().toString();
        String _monto = "&monto=" + txtMonto.getText().toString();
        String _descripcion= "&descripcion=" + txtDescripcion.getText().toString().replace(" ","%20");
        String _hashtag= "&hashtag=" + traerHashtag();
        String _categoria="&categoriaid=" + cateId;
        //String _categoria="&categoriaid=" + categoriaSeleccionada.getValor();
        //String _categoria = "&categoriaid=" + listaCategorias.get(cboCategoria.getSelectedItemPosition()).getId();

        /*if(_subCatId!=-1) {
            _subcategoria += _subCatId;
        }*/
        //String _cuenta= "&cuentaid=" + listaCuentas.get(cboCuenta.getSelectedItemPosition()).getId();

        String _cuenta="&cuentaid=" + cuentaSeleccionada.getValor();
        String _tipo = "&tipo=" + tipo;


        String Url2 = CConeccion.URL_Ingreso_Movimiento + CConeccion.bd + _fecha + _cuenta + _categoria + _monto + _tipo + _descripcion + _hashtag ;
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,Url2, null, this, this);
        request.add(jsonObjectRequest);
    }

    private void ModificarDatos()
    {
        SpinnerDatos cuentaSeleccionada= (SpinnerDatos) cboCuenta.getSelectedItem();

        String _id="&id=" + movModifica.getId();
        String _fecha= "&fecha=" + btnFecha.getText().toString();
        String _monto = "&monto=" + txtMonto.getText().toString();
        String _descripcion= "&descripcion=" + txtDescripcion.getText().toString().replace(" ","%20");
        String _hashtag= "&hashtag=" + traerHashtag();
        String _categoria="&categoriaid=" + cateId;
        String _cuenta="&cuentaid=" + cuentaSeleccionada.getValor();
        String _tipo = "&tipo=" + tipo;
        String _cuentavieja="&cuentaidvieja=" + movModifica.getIdCuenta();
        String _montoviejo="&montoviejo=" + movModifica.getMonto();


        String Url2 = CConeccion.URL_Modificar_Movimiento + CConeccion.bd + _id + _fecha + _cuenta + _categoria + _monto + _tipo + _hashtag + _descripcion + _cuentavieja + _montoviejo;
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
        ArrayList<SpinnerDatos> lCuentas = new ArrayList<SpinnerDatos>();
        int i=0, pos=0;
        //HashMap<Integer , String> lCuentas = new HashMap<Integer, String>();
        //HashMap<Integer , String> lCategorias = new HashMap<Integer, String>();


        for(CCuenta rcuenta :listaCuentas) {
            lCuentas.add(new SpinnerDatos(rcuenta.getNombre().toUpperCase(),rcuenta.getId()));
            if(rcuenta.getId()==cuentaId)
                pos=i;
            i++;
        }

        /*for(CCategorias rCategoria :listaCategorias) {
            lCategorias.add(new SpinnerDatos(rCategoria.getNombre(),rCategoria.getId()));
        }*/

        if(lCuentas.isEmpty())
        {
            Toast.makeText(getContext(),"No existen cuentas, no puede agregar Movimientos", Toast.LENGTH_SHORT).show();
            dismiss();
        }

        /*if(lCategorias.isEmpty())
        {
            Toast.makeText(getContext(),"No existen categorias, no puede agregar Movimientos", Toast.LENGTH_SHORT).show();
            dismiss();
        }*/
        ArrayAdapter<SpinnerDatos> adapterCuenta;
        adapterCuenta = new ArrayAdapter<SpinnerDatos>(getContext(), R.layout.spinnerpersonalizado,lCuentas);

        //ArrayAdapter<SpinnerDatos> adapterCategoria;
        //adapterCategoria = new ArrayAdapter<SpinnerDatos>(getContext(), R.layout.spinnerpersonalizado, lCategorias);

        cboCuenta.setAdapter(adapterCuenta);
        //cboCategoria.setAdapter(adapterCategoria);

        cboCuenta.setSelection(pos);
    }


    public String fechaNow()
    {
        Calendar c = Calendar.getInstance();

        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        int year= c.get(Calendar.YEAR);

       return Integer.toString(day) + "/" + Integer.toString(month) + "/" + Integer.toString(year);
    }

/*    public void CargarRadioButtonIngresoEgreso()
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
    }*/

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

        //Toast.makeText(getContext(),"ah ocurrido un error al guardar, " + error.getMessage().toString() , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            JSONObject jsonObject = response.getJSONObject("resultado");


            int id= jsonObject.optInt("valor");


            if(id>0) {
                if(isModificacion)
                    Toast.makeText(getContext(), "Se ha Modificado Correctamente", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getContext(), "Se ha Grabado Correctamente", Toast.LENGTH_SHORT).show();

                ((IFiltro)getActivity()).cargarWSMovimientos();

                if(isModificacion)
                    dismiss();
                LimpiarCampos();
            }
            else
                Toast.makeText(getContext(),"El dato no pudo Grabarse", Toast.LENGTH_SHORT).show();

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
