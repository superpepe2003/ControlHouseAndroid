package com.controlhouse.utopiasoft.controlhouse.Cuentas;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.controlhouse.utopiasoft.controlhouse.Entidades.CCategorias;
import com.controlhouse.utopiasoft.controlhouse.Entidades.CConeccion;
import com.controlhouse.utopiasoft.controlhouse.Entidades.CCuenta;
import com.controlhouse.utopiasoft.controlhouse.Movimientos.AdaptadorMovimientos;
import com.controlhouse.utopiasoft.controlhouse.Movimientos.activity_movimientos;
import com.controlhouse.utopiasoft.controlhouse.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class activity_cuentas extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Response.ErrorListener, Response.Listener<JSONObject> {

    RecyclerView recyclerCuentas;
    ArrayList<CCuenta> listaCuentas;
    TextView txtSaldoTotal;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cuentas_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        listaCuentas =  new ArrayList<>();
        request = Volley.newRequestQueue(getApplicationContext());
        cargarWsCuentas();

        txtSaldoTotal= findViewById(R.id.txtSaldoTotal);

        recyclerCuentas= findViewById(R.id.idListaCuentas);
        recyclerCuentas.setLayoutManager(new LinearLayoutManager(this));

    }

    private void CargarSaldoTotal() {
        Double saldo=0.0;
        for(CCuenta c:listaCuentas)
        {
            saldo+=c.getSaldo();
        }
        txtSaldoTotal.setText(String.valueOf(saldo));
        if(saldo<0)
            txtSaldoTotal.setTextColor(getResources().getColor(R.color.colorSub));
    }

    private void cargarWsCuentas()
    {
        jsonObjectRequest= new JsonObjectRequest(Request.Method.GET, CConeccion.URL_Listar_Cuentas + CConeccion.bd, null, this,this);
        request.add(jsonObjectRequest);
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
        //getMenuInflater().inflate(R.menu.cuentas_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       /* if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_movimientos) {
            Intent in =  new Intent(this, activity_movimientos.class);
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

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(JSONObject response) {
        CCuenta cuent = null;

        JSONArray jsonCuentas = response.optJSONArray("cuentasmodel");

        if (jsonCuentas != null) {
            listaCuentas.clear();
            //Toast.makeText(getApplicationContext(), "HAY CUENTAS", Toast.LENGTH_LONG).show();
            for (int i = 0; i < jsonCuentas.length(); i++) {
                try {
                cuent = new CCuenta();
                JSONObject jsonObject = null;

                jsonObject = jsonCuentas.getJSONObject(i);

                cuent.setId(jsonObject.optInt("Id"));
                cuent.setNombre(jsonObject.optString("Nombre"));
                cuent.setSaldo((float) (jsonObject.optDouble("Saldo")));

                listaCuentas.add(cuent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            CargarAdapterRecyclerView();
            CargarSaldoTotal();
        }
    }

    private void BorrarUsuario() {

        SharedPreferences pref = getSharedPreferences("login", MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();

        edit.putString("usuario", "");
        edit.putString("password", "");

        edit.commit();
    }

    private void CargarAdapterRecyclerView() {
        AdaptadorCuentas adapter = new AdaptadorCuentas(listaCuentas);

        recyclerCuentas.setAdapter(adapter);

        recyclerCuentas.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
    }
}
