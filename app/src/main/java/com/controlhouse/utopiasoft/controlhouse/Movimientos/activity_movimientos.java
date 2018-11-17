package com.controlhouse.utopiasoft.controlhouse.Movimientos;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.controlhouse.utopiasoft.controlhouse.Entidades.CFiltroMovimientos;
import com.controlhouse.utopiasoft.controlhouse.Entidades.CMovimiento;
import com.controlhouse.utopiasoft.controlhouse.Entidades.IFiltro;
import com.controlhouse.utopiasoft.controlhouse.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class activity_movimientos extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Response.Listener<JSONObject>, Response.ErrorListener, IFiltro {

    ArrayList<CMovimiento> listaMovimientos, listaMovimientosFiltrado;
    RecyclerView recyclerPersonajes;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    //FILTRO QUE VAMOS A PASAR
    final CFiltroMovimientos filtroMovimiento = new CFiltroMovimientos();

    //Montos
    double _minimo, _maximo, _total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movimientos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        _minimo=0.0;
        _maximo=0.0;
        _total=0.0;

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        listaMovimientos =  new ArrayList<>();
        listaMovimientosFiltrado = new ArrayList<>();
        request = Volley.newRequestQueue(getApplicationContext());
        cargarWS();

        recyclerPersonajes=findViewById(R.id.idListaMovimientos);

        recyclerPersonajes.setLayoutManager(new LinearLayoutManager(this));

        //llenarMovimientos();

    }

    private void cargarWS() {

        String URL="http://utopiasoft.duckdns.org:8080/wscontrol/servicemovimientos.php?modo=L";

        jsonObjectRequest= new JsonObjectRequest(Request.Method.GET, URL, null, this,this);
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
        int cboolean;

        JSONArray json = response.optJSONArray("movimientosmodel");

        try{
            for(int i=0; i<json.length();i++){
                mov= new CMovimiento();
                JSONObject jsonObject=null;
                jsonObject= json.getJSONObject(i);


                mov.setId(jsonObject.optInt("id"));
                mov.setCategoria(jsonObject.optString("Categoria"));
                mov.setSubCategoria(jsonObject.optString("SubCategoria"));
                mov.setCuenta(jsonObject.optString("Cuenta"));
                mov.setMonto(jsonObject.optDouble("Monto"));
                JSONObject jsonObjectFecha = jsonObject.getJSONObject("fecha");
                String sFecha= jsonObjectFecha.optString("date");
                sFecha = sFecha.substring(0,10);
                mov.setFecha(formatter.parse(sFecha));
                mov.setHashtag(jsonObject.optString("HashTag"));
                mov.setDescripcion(jsonObject.optString("Descripcion"));
                cboolean = (jsonObject.optInt("Tipo"));
                if(cboolean==1)
                    mov.setTipo(true);
                else
                    mov.setTipo(false);

                listaMovimientos.add(mov);

                if(mov.getMonto()>_maximo)
                    _maximo=mov.getMonto();
                if(mov.getMonto()<_minimo)
                    _minimo=mov.getMonto();

                _total+=mov.getMonto();
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        AdaptadorMovimientos adapter = new AdaptadorMovimientos(listaMovimientos);

        recyclerPersonajes.setAdapter(adapter);

        recyclerPersonajes.addItemDecoration(
                new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
    }

    private void llenarMovimientos() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        try {
            listaMovimientos.add(new CMovimiento(1, formatter.parse("12/10/2018"), "Ventas", "Productos", "Caja", 100, "", "", true));
            listaMovimientos.add(new CMovimiento(1, formatter.parse("13/10/2018"), "Mercaderia", "Almacen", "Caja", 150, "", "#Pan #cafe #facturas", false));
            listaMovimientos.add(new CMovimiento(1, formatter.parse("14/10/2018"), "Mercaderia", "Chinos", "Caja", 200, "", "", false));
            listaMovimientos.add(new CMovimiento(1, formatter.parse("14/10/2018"), "Gastos Fijos", "Luz", "Caja", 1600, "", "", false));
            listaMovimientos.add(new CMovimiento(1, formatter.parse("14/10/2018"), "Gastos Fijos", "Telefono", "Caja", 700, "", "", false));
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
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

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_movimientos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_movimientos_ingresar) {
            // Handle the camera action
        } else if (id == R.id.nav_movimientos_actualizar) {

        } else if (id == R.id.nav_movimientos_eliminar) {

        } else if (id == R.id.nav_movimientos_listar) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void clickFiltro(View view) {
        FragmentManager fr =  getSupportFragmentManager();
        fragment_movimientos_filtros fragment_filtro =  new fragment_movimientos_filtros(filtroMovimiento,_minimo,_maximo);
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
                        return o2.getFecha().compareTo(o1.getFecha());
                    }
                });
                break;
            }
            case 2:{
                Collections.sort(listaMovimientosFiltrado, new Comparator<CMovimiento>() {
                    @Override
                    public int compare(CMovimiento o1, CMovimiento o2) {
                        return o1.getFecha().compareTo(o2.getFecha());
                    }
                });
                break;
            }
        }

        AdaptadorMovimientos adapter = new AdaptadorMovimientos(listaMovimientosFiltrado);

        recyclerPersonajes.setAdapter(adapter);
    }

    private ArrayList<CMovimiento> filtrosRestantes(ArrayList<CMovimiento> listaMovimientosFiltrado, CFiltroMovimientos filtro) {
        ArrayList<CMovimiento> temp =  new ArrayList<CMovimiento>();
        String con="";
        CMovimiento movTemp = new CMovimiento();

        if(filtro.getContenido()!=null || filtro.getContenido()!="")
        {
            con=filtro.getContenido().toLowerCase();
        }

        for(CMovimiento mov: listaMovimientosFiltrado)
        {
            movTemp = mov;
            if(!con.isEmpty())
            {
                if(mov.getCategoria().toLowerCase().contains(con) ||
                        mov.getSubCategoria().contains(con) ||
                        mov.getHashtag().contains(con) ||
                        mov.getDescripcion().contains(con)||
                        mov.getCuenta().contains(con))
                {
                    movTemp=mov;
                }
                else
                    movTemp=null;
            }

            if(movTemp!=null)
                if((mov.getMonto()>=filtro.getMontoMinimo())&& (mov.getMonto()<=filtro.getMontoMaximo()))
                {
                    movTemp=mov;
                }
                else
                {
                    movTemp=null;
                }

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
                    mov.getSubCategoria().contains(con) ||
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
}
