package com.controlhouse.utopiasoft.controlhouse.Movimientos;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.controlhouse.utopiasoft.controlhouse.Categorias.activity_categorias;
import com.controlhouse.utopiasoft.controlhouse.Cuentas.activity_cuentas;
import com.controlhouse.utopiasoft.controlhouse.Entidades.CCategorias;
import com.controlhouse.utopiasoft.controlhouse.Entidades.CConeccion;
import com.controlhouse.utopiasoft.controlhouse.Entidades.CCuenta;
import com.controlhouse.utopiasoft.controlhouse.Entidades.CFiltroMovimientos;
import com.controlhouse.utopiasoft.controlhouse.Entidades.CMovimiento;
import com.controlhouse.utopiasoft.controlhouse.Entidades.IFiltro;
import com.controlhouse.utopiasoft.controlhouse.R;
import com.controlhouse.utopiasoft.controlhouse.Utilidades.DatePickerSinDia;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static java.lang.Math.round;

public class activity_movimientos extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Response.Listener<JSONObject>, Response.ErrorListener, IFiltro {

    ArrayList<CMovimiento> listaMovimientos, listaMovimientosFiltrado;
    ArrayList<CCategorias> listaCategorias;
    ArrayList<CCuenta> listaCuentas;
    RecyclerView recyclerPersonajes;

    MenuItem toolbarbtnCalendar, toolbarbtnFiltro, toolbarbtnChk, toolbarbtnfiltrochk, toolbarbtnBorrar, toolbarbtnCalendarMes;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    FloatingActionButton fab_ingreso, fab_pago;

    //FILTRO QUE VAMOS A PASAR
    final CFiltroMovimientos filtroMovimiento = new CFiltroMovimientos();

    //Montos
    double _minimo, _maximo, _totalIngreso, _totalEgreso;
    TextView txtTotales, txtEgreso, txtIngreso;
    View viewTotales;

    //Id para poner los mas usados o si es una modificacion
    int _idCategoriaIngreso, _idCategoriaEgreso,_idCuenta;

    //Si esta o no el calendario
    Calendar calendario;
    boolean isCalendario, isMes;
    ConstraintLayout layoutCalendario;
    //MaterialCalendarView almanaque;
    Button btnElijeFechaoMes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mov_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnElijeFechaoMes= findViewById(R.id.btnElijeFechaoMes);
        //almanaque = findViewById(R.id.calendario);

        isCalendario=false;
        _minimo=0.0;
        _maximo=0.0;
        _totalIngreso=0.0;
        _totalEgreso=0.0;

        fab_ingreso = findViewById(R.id.fabAgregar);
        fab_pago=findViewById(R.id.fabSub);

        txtTotales=findViewById(R.id.txtTotales);
        txtEgreso=findViewById(R.id.txtEgreso);
        txtIngreso=findViewById(R.id.txtIngreso);

        layoutCalendario=findViewById(R.id.linearLayout6);
        //layoutCalendarioMes=findViewById(R.id.linearLayout6);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        listaMovimientos =  new ArrayList<>();
        listaMovimientosFiltrado = new ArrayList<>();
        listaCategorias =  new ArrayList<>();
        listaCuentas =  new ArrayList<>();
        request = Volley.newRequestQueue(getApplicationContext());
        cargarWSMovimientos();
        cargarWsCategorias();
        cargarWsCuentas();
        cargarWsRanking();

        recyclerPersonajes=findViewById(R.id.idListaMovimientos);

        recyclerPersonajes.setLayoutManager(new LinearLayoutManager(this));


        fab_ingreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IngresarMovimientos(1, null);
            }
        });

        fab_pago.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IngresarMovimientos(0, null);
            }
        });

        btnElijeFechaoMes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int d = 0;
                int m = 0;
                int y = 0;
                try {
                    SimpleDateFormat dfFecha;
                    if(isMes)
                        dfFecha= new SimpleDateFormat("MM/yyyy");
                    else
                        dfFecha= new SimpleDateFormat("dd/MM/yyyy");
                    SimpleDateFormat dfDia = new SimpleDateFormat("dd");
                    SimpleDateFormat dfMes= new SimpleDateFormat("MM");
                    SimpleDateFormat dfAnio=new SimpleDateFormat("yyyy");
                    if(!isMes)
                        d = Integer.parseInt(dfDia.format(dfFecha.parse(btnElijeFechaoMes.getText().toString())));
                    m = Integer.parseInt(dfMes.format(dfFecha.parse(btnElijeFechaoMes.getText().toString())));
                    y = Integer.parseInt(dfAnio.format(dfFecha.parse(btnElijeFechaoMes.getText().toString())));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                DatePickerSinDia datePicker= new DatePickerSinDia(isMes, d,m,y);
                datePicker.setOnDateChangeListener(new DatePickerSinDia.OnDateChangeListener() {
                    @Override
                    public void onDateChange(int year, int month, int day) {
                        if(isCalendario)
                            btnElijeFechaoMes.setText(day + "/" + (month +1) + "/" + year);
                        else
                            btnElijeFechaoMes.setText((month +1) + "/" + year);
                        FiltrarxCalendarios(year,month + 1,day);
                    }
                });
                datePicker.show(getSupportFragmentManager(),"elijeFechaoMes");
            }
        });

        /*almanaque.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView materialCalendarView, @NonNull CalendarDay calendarDay, boolean b) {
                Calendar fecha = calendarDay.getCalendar();

                String d = fecha.get(Calendar.DAY_OF_MONTH) + "/" + (fecha.get(Calendar.MONTH)+1) + "/" + fecha.get(Calendar.YEAR);

                filtroMovimiento.setFiltroPorFecha(true);
                filtroMovimiento.setFechaInicial(d);
                filtroMovimiento.setFechaFinal(d);

                try {
                    setFiltro(filtroMovimiento);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });*/

    }

    private void FiltrarxCalendarios(int year, int month, int day) {
        String d1,d2;
        if(isCalendario) {
            d1 = day + "/" + month + "/" + year;
            d2 = d1;
        }
        else
        {
            Calendar calFin = Calendar.getInstance();
            calFin.set(year, month, 1);
            calFin.set(year, month, calFin.getActualMaximum(Calendar.DAY_OF_MONTH));
            int DiaFin = calFin.get(Calendar.DAY_OF_MONTH);

            d1= 1+ "/" + month + "/" + year;
            d2= DiaFin + "/" + month + "/" + year;
        }
        filtroMovimiento.setFiltroPorFecha(true);
        filtroMovimiento.setFechaInicial(d1);
        filtroMovimiento.setFechaFinal(d2);

        try {
            setFiltro(filtroMovimiento);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void CargarParaFiltrarCalendarios(){
        String []fech= btnElijeFechaoMes.getText().toString().split("/");
        int d=0;
        int m=0;
        int y=0;
        if(!isMes){
            d=Integer.parseInt(fech[0]);
            m=Integer.parseInt(fech[1]);
            y=Integer.parseInt(fech[2]);
        }
        else
        {
            m=Integer.parseInt(fech[0]);
            y=Integer.parseInt(fech[1]);
        }
        FiltrarxCalendarios(y,m,d);
    }

    public void cargarWSMovimientos() {

        jsonObjectRequest= new JsonObjectRequest(Request.Method.GET, CConeccion.URL_Listar_Movimientos + CConeccion.bd, null, this,this);
        request.add(jsonObjectRequest);
    }

    private void cargarWsCategorias(){

        jsonObjectRequest= new JsonObjectRequest(Request.Method.GET, CConeccion.URL_Listar_Categorias + CConeccion.bd, null, this,this);
        request.add(jsonObjectRequest);
    }

    private void cargarWsCuentas()
    {

        jsonObjectRequest= new JsonObjectRequest(Request.Method.GET, CConeccion.URL_Listar_Cuentas + CConeccion.bd, null, this,this);
        request.add(jsonObjectRequest);
    }

    private void cargarWsRanking()
    {
        jsonObjectRequest= new JsonObjectRequest(Request.Method.GET, CConeccion.URL_Listar_Ranking + CConeccion.bd, null, this,this);
        request.add(jsonObjectRequest);
    }

    private void eliminarWSMovimiento(int id, int idcuenta, double monto, boolean tipo)
    {
        int a = (tipo)?1:0;
        String url= CConeccion.URL_Eliminar_Movimientos + CConeccion.bd + "&id=" + id + "&idcuenta=" + idcuenta + "&monto=" + monto + "&tipo=" + a;
        jsonObjectRequest= new JsonObjectRequest(Request.Method.GET, url, null, this,this);
        request.add(jsonObjectRequest);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getApplicationContext(), "No se puede conectar", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onResponse(JSONObject response) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        CMovimiento mov = null;
        CCategorias cat = null;
        CCuenta cuent = null;
        int cboolean;

        JSONArray jsonCategorias = response.optJSONArray("categoriamodel");
        JSONArray jsonCuentas=response.optJSONArray("cuentasmodel");
        JSONArray jsonMovimientos = response.optJSONArray("movimientosmodel");
        JSONArray jsonRanking = response.optJSONArray("Ranking");
        JSONArray jsonEliminar= response.optJSONArray("resultado");

        try{
            if (jsonMovimientos!=null) {
                listaMovimientos.clear();
                for (int i = 0; i < jsonMovimientos.length(); i++) {
                    mov = new CMovimiento();
                    JSONObject jsonObject = null;
                    jsonObject = jsonMovimientos.getJSONObject(i);


                    mov.setId(jsonObject.optInt("id"));
                    mov.setCategoria(jsonObject.optString("Categoria"));
                    mov.setCuenta(jsonObject.optString("Cuenta"));
                    mov.setMonto(jsonObject.optDouble("Monto"));
                    JSONObject jsonObjectFecha = jsonObject.getJSONObject("fecha");
                    String sFecha = jsonObjectFecha.optString("date");
                    sFecha = sFecha.substring(0, 10);
                    mov.setFecha(formatter.parse(sFecha));
                    mov.setHashtag(jsonObject.optString("HashTag"));
                    mov.setDescripcion(jsonObject.optString("Descripcion"));
                    cboolean = (jsonObject.optInt("Tipo"));
                    mov.setIdCategoria(jsonObject.optInt("CategoriaId"));
                    mov.setIdCuenta(jsonObject.optInt("CuentaId"));
                    String catePadre=jsonObject.optString("CategoriaPadre");
                    if((catePadre==null)||(catePadre.isEmpty())||(catePadre.equals("null")))
                        catePadre="";
                    mov.setCatePadre(catePadre);

                    if (cboolean == 1) {
                        mov.setTipo(true);
                        _totalIngreso += mov.getMonto();
                    }
                    else {
                        mov.setTipo(false);
                        _totalEgreso += mov.getMonto();
                    }

                    listaMovimientos.add(mov);

                    if (mov.getMonto() > _maximo)
                        _maximo = mov.getMonto();
                    if (mov.getMonto() < _minimo)
                        _minimo = mov.getMonto();

                    mov.setChecked(false);

                }

                CargarTotales();

                CargarAdapterRecyclerView();

                try {
                    setFiltro(filtroMovimiento);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
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
                    if(tipo==1)
                        cat.setTipo(true);
                    else
                        cat.setTipo(false);
                    tipo = jsonObject.optInt("FijaMensualmente");
                    if(tipo==1)
                        cat.setFija(true);
                    else
                        cat.setFija(false);
                    cat.setIdpadre(jsonObject.optInt("IdPadre"));
                    cat.setMonto(jsonObject.optDouble("Monto"));

                    listaCategorias.add(cat);
                }
            }

            else if(jsonCuentas!=null) {
                listaCuentas.clear();
                //Toast.makeText(getApplicationContext(), "HAY CUENTAS", Toast.LENGTH_LONG).show();
                for (int i = 0; i < jsonCuentas.length(); i++) {
                    cuent = new CCuenta();
                    JSONObject jsonObject = null;
                    jsonObject = jsonCuentas.getJSONObject(i);

                    cuent.setId(jsonObject.optInt("Id"));
                    cuent.setNombre(jsonObject.optString("Nombre"));
                    cuent.setSaldo((float)(jsonObject.optDouble("Saldo")));

                    listaCuentas.add(cuent);
                }
            }
            else if(jsonRanking!=null)
            {
                JSONObject jsonR= jsonRanking.getJSONObject(0);
                _idCuenta = jsonR.optInt("idCuenta");
                _idCategoriaIngreso = jsonR.optInt("idCategoriaIngreso");
                _idCategoriaEgreso = jsonR.optInt("idCategoriaEgreso");

            }
            else if(jsonEliminar!=null)
            {
                JSONObject json = jsonEliminar.getJSONObject(0);
                int resul= json.optInt("valor");
                if(resul==1) {
                    Toast.makeText(getApplicationContext(), "El registro se elimino satisfactoriamente!", Toast.LENGTH_SHORT).show();
                    cargarWSMovimientos();
                }
                else
                    Toast.makeText(getApplicationContext(),"El registro no se pudo eliminar!", Toast.LENGTH_SHORT).show();
            }
            /*else {
                JSONObject jsonRanking= response.getJSONObject("Ranking");
                _idCuenta = jsonRanking.optInt("idCuenta");
                _idCategoriaIngreso = jsonRanking.optInt("idCategoriaIngreso");
                _idCategoriaEgreso = jsonRanking.optInt("idCategoriaEgreso");
            }*/
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    private void CargarAdapterRecyclerView() {
        AdaptadorMovimientos adapter = new AdaptadorMovimientos(listaMovimientos,false);

        recyclerPersonajes.setAdapter(adapter);

        adapter.setOnClickItemAdapterListener(new AdaptadorMovimientos.onClickItemAdapterListener() {
            @Override
            public void onItemDetalle(int id) {
                Detalle(id); //Toast.makeText(getApplicationContext(), "has apretado en la posicion: " + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemEdita(int id) {
                CMovimiento mov= DevolverMovimiento(id);
                if(mov!=null) {
                    if (mov.getTipo())
                        IngresarMovimientos(1, mov);
                    else
                        IngresarMovimientos(0, mov);
                }
            }

            @Override
            public void onItemElimina(final int id) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(activity_movimientos.this, R.style.MyAlertDialogStyle);
                dialog.setTitle("Confirmar");
                dialog.setMessage("¿Desea Eliminar el movimiento?");
                dialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CMovimiento mov = DevolverMovimiento(id);
                        eliminarWSMovimiento(mov.getId(),mov.getIdCuenta(),mov.getMonto(),mov.getTipo());
                    }
                });
                dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();
            }

            //Selecciono algun checked
            @Override
            public void onItemSelecciona(ArrayList<CMovimiento> listMovi) {
                listaMovimientosFiltrado=listMovi;
            }

            //Apreto para que aparezcan los checked
            @Override
            public void onSelecciona() {
                if(!toolbarbtnChk.isVisible()) {
                    AdaptadorMovimientos adapter = new AdaptadorMovimientos(listaMovimientosFiltrado, true);

                    recyclerPersonajes.setAdapter(adapter);

                    toolbarbtnCalendar.setVisible(false);
                    toolbarbtnCalendarMes.setVisible(false);
                    toolbarbtnChk.setVisible(true);
                    toolbarbtnFiltro.setVisible(false);
                    toolbarbtnfiltrochk.setVisible(true);
                    toolbarbtnBorrar.setVisible(true);
                }
            }
        });

        recyclerPersonajes.addItemDecoration(
                new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
    }

    private void Detalle(int id) {
        fragment_movimientos_detalle fDetalle= new fragment_movimientos_detalle(DevolverMovimiento(id));
        fDetalle.show(this.getSupportFragmentManager(),"detalle");
    }

    private CMovimiento DevolverMovimiento(int id) {
        CMovimiento mov=null;
        for(CMovimiento movi: listaMovimientos)
        {
            if(movi.getId()==id)
                return movi;
        }
        return mov;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mov_activity_toolbar, menu);

        toolbarbtnFiltro= menu.findItem(R.id.bar_movimientos_filtro);
        toolbarbtnChk=menu.findItem(R.id.bar_movimientos_cerrarchecked);
        toolbarbtnCalendar=menu.findItem(R.id.bar_movimientos_calendarioLista);
        toolbarbtnCalendarMes= menu.findItem(R.id.bar_movimientos_calendarioMesLista);
        toolbarbtnBorrar=menu.findItem(R.id.bar_movimientos_borrar);
        toolbarbtnfiltrochk=menu.findItem(R.id.bar_movimientos_filtraporchecked);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.bar_movimientos_filtro) {
            FragmentManager fr =  getSupportFragmentManager();
            fragment_movimientos_filtros fragment_filtro =  new fragment_movimientos_filtros(filtroMovimiento,_minimo,_maximo,(isCalendario || isMes));
            fragment_filtro.setCancelable(false);

            // The device is smaller, so show the fragment fullscreen
            FragmentTransaction transaction = fr.beginTransaction();
            // For a little polish, specify a transition animation
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            // To make it fullscreen, use the 'content' root view as the container
            // for the fragment, which is always the root view for the activity
            transaction.add(android.R.id.content, fragment_filtro)
                    .addToBackStack(null).commit();
            return true;
        }
        else if (id==R.id.bar_movimientos_calendarioLista){
                if(!isCalendario)
                {
                    isCalendario=true;
                    item.setIcon(R.drawable.ic_movimientos_listarwhite);
                    btnElijeFechaoMes.setText(ponerFechaenBoton());
                    layoutCalendario.setVisibility(View.VISIBLE);
                    CargarParaFiltrarCalendarios();
                    BuscarTotales(listaMovimientos);
                    toolbarbtnCalendarMes.setVisible(false);
                }
                else
                {
                    isCalendario=false;
                    toolbarbtnCalendarMes.setVisible(true);
                    layoutCalendario.setVisibility(View.GONE);
                    filtroMovimiento.setFiltroPorFecha(false);
                    filtroMovimiento.setFechaInicial(null);
                    filtroMovimiento.setFechaFinal(null);
                    try {
                        setFiltro(filtroMovimiento);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    item.setIcon(R.drawable.ic_action_fecha);
                    BuscarTotales(listaMovimientos);
                }
        }
        else if(id==R.id.bar_movimientos_calendarioMesLista)
        {
            if(!isMes)
            {
                isMes=true;
                toolbarbtnCalendar.setVisible(false);
                item.setIcon(R.drawable.ic_movimientos_listarwhite);
                btnElijeFechaoMes.setText(ponerFechaenBoton());
                layoutCalendario.setVisibility(View.VISIBLE);
                CargarParaFiltrarCalendarios();
                //filtroxMes();
                BuscarTotales(listaMovimientos);
            }
            else
            {
                isMes=false;
                toolbarbtnCalendar.setVisible(true);
                layoutCalendario.setVisibility(View.GONE);
                filtroMovimiento.setFiltroPorFecha(false);
                filtroMovimiento.setFechaInicial(null);
                filtroMovimiento.setFechaFinal(null);
                try {
                    setFiltro(filtroMovimiento);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                item.setIcon(R.drawable.ic_action_fecha_mes);
                BuscarTotales(listaMovimientos);
            }
        }
        else if(id==R.id.bar_movimientos_cerrarchecked){
            try {
                setFiltro(filtroMovimiento);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            /*AdaptadorMovimientos adapter = new AdaptadorMovimientos(listaMovimientos,false);

            recyclerPersonajes.setAdapter(adapter);*/

            toolbarbtnCalendar.setVisible(true);
            toolbarbtnCalendarMes.setVisible(true);
            toolbarbtnChk.setVisible(false);
            toolbarbtnFiltro.setVisible(true);
            toolbarbtnfiltrochk.setVisible(false);
            toolbarbtnBorrar.setVisible(false);
        }
        else if(id==R.id.bar_movimientos_borrar){

        }
        else if(id==R.id.bar_movimientos_filtraporchecked)
        {
            FiltrarPorChecked();
        }

        return super.onOptionsItemSelected(item);
    }

    private String ponerFechaenBoton() {
        calendario = Calendar.getInstance();

        int month = calendario.get(Calendar.MONTH) + 1;
        int day = calendario.get(Calendar.DAY_OF_MONTH);
        int year= calendario.get(Calendar.YEAR);

        if(isCalendario)
            return Integer.toString(day) + "/" + Integer.toString(month) + "/" + Integer.toString(year);
        else
            return Integer.toString(month) + "/" + Integer.toString(year);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_movimientos_ingreso) {
            IngresarMovimientos(1, null);

        } else if (id == R.id.nav_movimientos_pago) {
            IngresarMovimientos(0, null);

        }
        else if(id==R.id.nav_cuentas){
            Intent in= new Intent(this, activity_cuentas.class);
            startActivity(in);
            finish();
        }
        else if(id==R.id.nav_categoria)
        {
            Intent in= new Intent(this, activity_categorias.class);
            startActivity(in);
            finish();
        }
        else if (id ==R.id.nav_salir){
            BorrarUsuario();
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void clickFiltro(View view) {
        FragmentManager fr =  getSupportFragmentManager();
        fragment_movimientos_filtros fragment_filtro =  new fragment_movimientos_filtros(filtroMovimiento,_minimo,_maximo,isCalendario);
        fragment_filtro.setCancelable(false);

        // The device is smaller, so show the fragment fullscreen
        FragmentTransaction transaction = fr.beginTransaction();
        // For a little polish, specify a transition animation
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        // To make it fullscreen, use the 'content' root view as the container
        // for the fragment, which is always the root view for the activity
        transaction.add(android.R.id.content, fragment_filtro)
                .addToBackStack(null).commit();

    }

    private void IngresarMovimientos(int tipo, CMovimiento mov)
    {
        FragmentManager fr =  getSupportFragmentManager();
        fragment_movimientos_ingreso fragment_ingreso;
        if(tipo==1)
        {
            fragment_ingreso = new fragment_movimientos_ingreso(tipo, listaMovimientos, listaCuentas, listaCategorias, _idCuenta, _idCategoriaIngreso, mov);
        }
        else {
            fragment_ingreso = new fragment_movimientos_ingreso(tipo, listaMovimientos, listaCuentas, listaCategorias, _idCuenta, _idCategoriaEgreso, mov);
        }
        // The device is smaller, so show the fragment fullscreen
        FragmentTransaction transaction = fr.beginTransaction();
        // For a little polish, specify a transition animation
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        // To make it fullscreen, use the 'content' root view as the container
        // for the fragment, which is always the root view for the activity
        transaction.add(android.R.id.content, fragment_ingreso)
                .addToBackStack(null).commit();
    }

    @Override
    public void setFiltro(CFiltroMovimientos filtro) throws ParseException {

        //FILTRA ENTRE 2 FECHAS
        if(filtro.isFiltroPorFecha())
        {
            listaMovimientosFiltrado= FiltroEntreFechas(filtro);
        }
        else
        {
            listaMovimientosFiltrado=listaMovimientos;
        }

        //FILTRO POR MONTOS
        //listaMovimientosFiltrado = FiltroEntreMontos(listaMovimientosFiltrado, filtro);

        //FILTRO POR CONTENIDO
        //listaMovimientosFiltrado = FiltroxContenido(listaMovimientosFiltrado, filtro);

        //FILTRO POR MONTOS / CONTENIDOS Y SI ES INGRESO O EGRESO
        listaMovimientosFiltrado= filtrosRestantes(listaMovimientosFiltrado,filtro);


        //ORDENA LA LISTA
        switch (filtro.getFecha()) {
            case 1:{
                Collections.sort(listaMovimientosFiltrado, new Comparator<CMovimiento>() {
                    @Override
                    public int compare(CMovimiento o1, CMovimiento o2) {
                        int result = o2.getFecha().compareTo(o1.getFecha());

                        if (result != 0)
                        {
                            return result;
                        }
                        return String.valueOf(o2.getId()).compareTo(String.valueOf(o1.getId()));

                    }
                });
                break;
            }
            case 2:{
                Collections.sort(listaMovimientosFiltrado, new Comparator<CMovimiento>() {
                    @Override
                    public int compare(CMovimiento o1, CMovimiento o2) {
                        int result = o1.getFecha().compareTo(o2.getFecha());
                        if (result != 0)
                        {
                            return result;
                        }
                        return String.valueOf(o1.getId()).compareTo(String.valueOf(o2.getId()));
                    }
                });
                break;
            }
        }

        AdaptadorMovimientos adapter = new AdaptadorMovimientos(listaMovimientosFiltrado, false);

        recyclerPersonajes.setAdapter(adapter);

        BuscarTotales(listaMovimientosFiltrado);
        CargarTotales();
    }

    private void FiltrarPorChecked()
    {
        ArrayList<CMovimiento> lTemp= new ArrayList<>();
        for(CMovimiento mov:listaMovimientosFiltrado)
        {
            if(mov.getChecked())
                lTemp.add(mov);
        }

        AdaptadorMovimientos adapter = new AdaptadorMovimientos(lTemp, false);

        recyclerPersonajes.setAdapter(adapter);

        BuscarTotales(lTemp);
        CargarTotales();
    }

    private ArrayList<CMovimiento> filtrosRestantes(ArrayList<CMovimiento> listaMovimientosFiltrado, CFiltroMovimientos filtro) {
        ArrayList<CMovimiento> temp =  new ArrayList<CMovimiento>();
        String con="";
        CMovimiento movTemp = new CMovimiento();

        if(!filtro.getContenido().isEmpty())
        {
            con=filtro.getContenido().toLowerCase();
        }

        for(CMovimiento mov: listaMovimientosFiltrado)
        {
            movTemp = mov;
            //FILTRO POR CONTENIDO
            if(!con.isEmpty())
            {
                if(mov.getCategoria().toLowerCase().contains(con) ||
                        mov.getHashtag().toLowerCase().contains(con) ||
                        mov.getDescripcion().toLowerCase().contains(con)||
                        mov.getCuenta().toLowerCase().contains(con) ||
                        mov.getCatePadre().toLowerCase().contains(con))
                {
                    movTemp=mov;
                }
                else
                    movTemp=null;
            }

            //FILTRO POR MONTO
            if(movTemp!=null)
                if(filtro.getMontoMinimo()!=null && filtro.getMontoMaximo()!=null) {
                    if ((mov.getMonto() >= filtro.getMontoMinimo()) && (mov.getMonto() <= filtro.getMontoMaximo())) {
                        movTemp = mov;
                    } else {
                        movTemp = null;
                    }
                }

            //FILTRO POR TIPO
            if(movTemp!=null)
            {
                if(filtro.getTipo()==0)
                    movTemp=mov;
                else if(filtro.getTipo()==1)
                {
                    if(mov.getTipo())
                        movTemp=mov;
                    else
                        movTemp=null;
                }
                else if(filtro.getTipo()==-1)
                {
                    if(!mov.getTipo())
                        movTemp=mov;
                    else
                        movTemp=null;
                }
            }

            if(movTemp!=null)
                temp.add(mov);
        }
        return temp;
    }

   /* private ArrayList<CMovimiento> FiltroxContenido(ArrayList<CMovimiento> listaMovimientosFiltrado, CFiltroMovimientos filtro) {
        ArrayList<CMovimiento> temp =  new ArrayList<CMovimiento>();
        if((filtro.getContenido()!=null)||(filtro.getContenido()!=""))
        {
            String con = filtro.getContenido().toLowerCase();
            for(CMovimiento mov: listaMovimientosFiltrado)
            {
                if(mov.getCategoria().toLowerCase().contains(con) ||
                    mov.getHashtag().contains(con) ||
                    mov.getDescripcion().contains(con)||
                    mov.getCuenta().contains(con) ||
                    mov.getCatePadre().contains(con))
                {
                    temp.add(mov);
                }
            }
            return temp;
        }
        else
            return listaMovimientosFiltrado;
    }

    private ArrayList<CMovimiento> FiltroEntreMontos(ArrayList<CMovimiento> listaMovimientosFiltrado, CFiltroMovimientos filtro) {
        ArrayList<CMovimiento> temp =  new ArrayList<CMovimiento>();
        for(CMovimiento mov: listaMovimientosFiltrado)
        {
            if((mov.getMonto()>=filtro.getMontoMinimo())&& (mov.getMonto()<=filtro.getMontoMaximo()))
            {
                temp.add(mov);
            }
        }
        return temp;
    }*/

    private ArrayList<CMovimiento> FiltroEntreFechas(CFiltroMovimientos filtro)  throws ParseException {
        CargarFiltro(filtro);
        ArrayList<CMovimiento> temp =  new ArrayList<CMovimiento>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try{
            //ARMO LA FECHA INICIAL CON UN OBJETO CALENDARIO RESTANDOLE 1 DIA
            Calendar c = Calendar.getInstance();
            c.setTime(sdf.parse(filtro.getFechaInicial()));
            c.add(Calendar.DAY_OF_YEAR, -1);
            Date di = c.getTime();

            //ARMO LA FECHA FINAL CON UN OBJETO CALENDARIO RESTANDOLE 1 DIA
            c.setTime(sdf.parse(filtro.getFechaFinal()));
            c.add(Calendar.DAY_OF_YEAR, 1);
            Date df = c.getTime();

            for(CMovimiento mov: listaMovimientos)
            {
                Date fech = mov.getFecha();
                if(fech.after(di) && (fech.before(df)))
                    temp.add(mov);
            }
        }
        catch(ParseException e)
        {
            throw e;
        }
        return temp;
    }

    private void CargarFiltro(CFiltroMovimientos filtro) {
        filtroMovimiento.setFechaInicial(filtro.getFechaInicial());
        filtroMovimiento.setFecha(filtro.getFecha());
        filtroMovimiento.setFiltroPorFecha(filtro.isFiltroPorFecha());
        filtroMovimiento.setFechaFinal(filtro.getFechaFinal());
        filtroMovimiento.setMontoMaximo(filtro.getMontoMaximo());
        filtroMovimiento.setMontoMinimo(filtro.getMontoMinimo());
    }

    private void BorrarUsuario() {

        SharedPreferences pref = getSharedPreferences("login", MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();

        edit.putString("usuario", "");
        edit.putString("password", "");

        edit.commit();
    }

    private void BuscarTotales(List<CMovimiento> lista){
        _totalEgreso=0.0;
        _totalIngreso=0.0;

        for(CMovimiento mov:lista)
        {
            if(mov.getTipo())
                _totalIngreso +=mov.getMonto();
            else
                _totalEgreso +=mov.getMonto();
        }
    }

    private void CargarTotales(){
        Double total= _totalIngreso-_totalEgreso;
        txtIngreso.setText(String.format("INGRESO: %.2f",_totalIngreso));
        txtEgreso.setText(String.format("EGRESO: %.2f",_totalEgreso));
        txtTotales.setText(String.format("TOTAL: %.2f",total));
        if(total<0)
            txtTotales.setBackgroundColor(getResources().getColor(R.color.colorSub));

        //viewTotales.setLayoutParams(new TableLayout.LayoutParams(txtTotales.getMinimumWidth(),2));
    }

    public void LimpiarFiltro() throws ParseException {
        filtroMovimiento.setFecha(1);
        filtroMovimiento.setContenido("");
        filtroMovimiento.setFiltroPorFecha(false);
        filtroMovimiento.setTipo(0);
        filtroMovimiento.setMontoMinimo(new Double(_minimo));
        filtroMovimiento.setMontoMaximo(new Double(_maximo));
        filtroMovimiento.setFechaInicial(FechaNow());
        filtroMovimiento.setFechaFinal(FechaNow());

        setFiltro(filtroMovimiento);
    }

    private String FechaNow()
    {
        calendario = Calendar.getInstance();

        int month = calendario.get(Calendar.MONTH) + 1;
        int day = calendario.get(Calendar.DAY_OF_MONTH);
        int year= calendario.get(Calendar.YEAR);

        return Integer.toString(day) + "/" + Integer.toString(month) + "/" + Integer.toString(year);

    }
}
