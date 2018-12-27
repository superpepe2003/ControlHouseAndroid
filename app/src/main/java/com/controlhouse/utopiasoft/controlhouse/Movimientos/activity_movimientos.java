package com.controlhouse.utopiasoft.controlhouse.Movimientos;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.controlhouse.utopiasoft.controlhouse.Cuentas.activity_cuentas;
import com.controlhouse.utopiasoft.controlhouse.Entidades.CCategorias;
import com.controlhouse.utopiasoft.controlhouse.Entidades.CConeccion;
import com.controlhouse.utopiasoft.controlhouse.Entidades.CCuenta;
import com.controlhouse.utopiasoft.controlhouse.Entidades.CFiltroMovimientos;
import com.controlhouse.utopiasoft.controlhouse.Entidades.CMovimiento;
import com.controlhouse.utopiasoft.controlhouse.Entidades.IFiltro;
import com.controlhouse.utopiasoft.controlhouse.R;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class activity_movimientos extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Response.Listener<JSONObject>, Response.ErrorListener, IFiltro {

    ArrayList<CMovimiento> listaMovimientos, listaMovimientosFiltrado;
    ArrayList<CCategorias> listaCategorias;
    ArrayList<CCuenta> listaCuentas;
    RecyclerView recyclerPersonajes;

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
    boolean isCalendario;
    ConstraintLayout layoutCalendario;
    MaterialCalendarView almanaque;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mov_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        almanaque = findViewById(R.id.calendario);

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

        layoutCalendario=findViewById(R.id.linearLayout5);

        //cargarCheckbox();

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

        almanaque.setOnDateChangedListener(new OnDateSelectedListener() {
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
        });

    }

   /* private void cargarCheckbox() {
        Drawable drawableFecha = resizeImage(getApplicationContext(), R.drawable.icono_fecha_checkbok,(int)Math.round(getResources().getDimension(R.dimen.icono_filtrar)), (int)Math.round(getResources().getDimension(R.dimen.icono_filtrar)));
        Drawable drawableFechaSelec = resizeImage(getApplicationContext(), R.drawable.icono_fecha_seleccionado_checkbok,(int)Math.round(getResources().getDimension(R.dimen.icono_filtrar)), (int)Math.round(getResources().getDimension(R.dimen.icono_filtrar)));

        if(chkCalendario.isChecked()) {
            chkCalendario.setCompoundDrawablesWithIntrinsicBounds(null, drawableFechaSelec, null, null);
            layoutCalendario.setVisibility(View.VISIBLE);

        }
        else {
            chkCalendario.setCompoundDrawablesWithIntrinsicBounds(null, drawableFecha, null, null);
            layoutCalendario.setVisibility(View.GONE);
        }
    }*/

    /*public Drawable resizeImage(Context ctx, int resId, int w, int h) {

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

    }*/

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
                    mov.setCatePadre(jsonObject.optString("CategoriaPadre"));

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
        AdaptadorMovimientos adapter = new AdaptadorMovimientos(listaMovimientos);

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
            public void onItemElimina(int id) {
                CMovimiento mov = DevolverMovimiento(id);
                eliminarWSMovimiento(mov.getId(),mov.getIdCuenta(),mov.getMonto(),mov.getTipo());
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
            return true;
        }
        if (id==R.id.bar_movimientos_calendarioLista){
                if(!isCalendario)
                {
                    isCalendario=true;
                    item.setIcon(R.drawable.ic_movimientos_listarwhite);
                    layoutCalendario.setVisibility(View.VISIBLE);
                    BuscarTotales(listaMovimientos);
                }
                else
                {
                    isCalendario=false;
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

        return super.onOptionsItemSelected(item);
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

        AdaptadorMovimientos adapter = new AdaptadorMovimientos(listaMovimientosFiltrado);

        recyclerPersonajes.setAdapter(adapter);

        BuscarTotales(listaMovimientosFiltrado);
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
                        mov.getHashtag().contains(con) ||
                        mov.getDescripcion().contains(con)||
                        mov.getCuenta().contains(con))
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

    private ArrayList<CMovimiento> FiltroxContenido(ArrayList<CMovimiento> listaMovimientosFiltrado, CFiltroMovimientos filtro) {
        ArrayList<CMovimiento> temp =  new ArrayList<CMovimiento>();
        if((filtro.getContenido()!=null)||(filtro.getContenido()!=""))
        {
            String con = filtro.getContenido().toLowerCase();
            for(CMovimiento mov: listaMovimientosFiltrado)
            {
                if(mov.getCategoria().toLowerCase().contains(con) ||
                    mov.getHashtag().contains(con) ||
                    mov.getDescripcion().contains(con)||
                    mov.getCuenta().contains(con))
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
    }

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
        txtIngreso.setText("INGRESO: " + Double.toString(_totalIngreso));
        txtEgreso.setText("EGRESO : " + Double.toString(_totalEgreso));
        txtTotales.setText("TOTAL  : " + Double.toString(total));

        //viewTotales.setLayoutParams(new TableLayout.LayoutParams(txtTotales.getMinimumWidth(),2));
    }

    public void LimpiarFiltro() throws ParseException {
        filtroMovimiento.setFecha(0);
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
