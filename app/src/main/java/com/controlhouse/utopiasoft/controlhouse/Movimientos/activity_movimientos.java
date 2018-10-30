package com.controlhouse.utopiasoft.controlhouse.Movimientos;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.controlhouse.utopiasoft.controlhouse.Entidades.CMovimiento;
import com.controlhouse.utopiasoft.controlhouse.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class activity_movimientos extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Response.Listener<JSONObject>, Response.ErrorListener {

    ArrayList<CMovimiento> listaMovimientos;
    RecyclerView recyclerPersonajes;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movimientos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        listaMovimientos =  new ArrayList<>();
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
                mov.setTipo(jsonObject.optBoolean("Tipo"));

                listaMovimientos.add(mov);
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


}
