package com.controlhouse.utopiasoft.controlhouse.Cuentas;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.controlhouse.utopiasoft.controlhouse.Categorias.activity_categorias;
import com.controlhouse.utopiasoft.controlhouse.Entidades.CCategorias;
import com.controlhouse.utopiasoft.controlhouse.Entidades.CConeccion;
import com.controlhouse.utopiasoft.controlhouse.Entidades.CCuenta;
import com.controlhouse.utopiasoft.controlhouse.Entidades.CMovimiento;
import com.controlhouse.utopiasoft.controlhouse.Movimientos.AdaptadorMovimientos;
import com.controlhouse.utopiasoft.controlhouse.Movimientos.activity_movimientos;
import com.controlhouse.utopiasoft.controlhouse.Movimientos.fragment_movimientos_filtros;
import com.controlhouse.utopiasoft.controlhouse.R;
import com.controlhouse.utopiasoft.controlhouse.Transacciones.transacciones_fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class activity_cuentas extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Response.ErrorListener, Response.Listener<JSONObject>, cuentas_fragment_ingreso.iActualiza {

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
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                CargarDialogIngreso();
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
        txtSaldoTotal.setText(String.format("TOTAL: %.2f", saldo));
        if(saldo<0)
            txtSaldoTotal.setBackgroundColor(getResources().getColor(R.color.colorSub));
    }

    private void cargarWsCuentas()
    {
        jsonObjectRequest= new JsonObjectRequest(Request.Method.GET, CConeccion.URL_Listar_Cuentas + CConeccion.bd, null, this,this);
        request.add(jsonObjectRequest);
    }

    public void modificarWsCuentas(int id)
    {
        CCuenta cue=getCuenta(id);
        if(cue!=null) {
            cuentas_fragment_ingreso frg = new cuentas_fragment_ingreso(true, cue.getNombre(), cue.getSaldo(), id);
            frg.show(getSupportFragmentManager(), "modificar");
        }
    }

    public void eliminarWsCuentas(int id)
    {
        String Url= CConeccion.Url_Eliminar_Cuentas + CConeccion.bd + "&id=" + id;
        jsonObjectRequest= new JsonObjectRequest(Request.Method.GET, Url, null, this,this);
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

        if(id==R.id.nav_cuentas_nueva){
            CargarDialogIngreso();
        }
        else if(id==R.id.nav_cuentas_transacciones){
            FragmentManager fr =  getSupportFragmentManager();
            transacciones_fragment fragment_transacciones =  new transacciones_fragment(listaCuentas,0,"");
            fragment_transacciones.setCancelable(false);

            // The device is smaller, so show the fragment fullscreen
            FragmentTransaction transaction = fr.beginTransaction();
            // For a little polish, specify a transition animation
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            // To make it fullscreen, use the 'content' root view as the container
            // for the fragment, which is always the root view for the activity
            transaction.add(android.R.id.content, fragment_transacciones)
                    .addToBackStack(null).commit();


        }
        else if (id == R.id.nav_movimientos) {
            Intent in =  new Intent(this, activity_movimientos.class);
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

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this, "Error al acceder a la Bd", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(JSONObject response) {
        CCuenta cuent = null;

        JSONArray jsonCuentas = response.optJSONArray("cuentasmodel");
        JSONArray jsonEliminarCuentas= response.optJSONArray("resultadoEliminar");

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
        else if(jsonEliminarCuentas!=null)
        {
            JSONObject jsonObject= null;
            try {
                jsonObject = jsonEliminarCuentas.getJSONObject(0);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            int valor =  jsonObject.optInt("valor");

            if(valor==1)
            {
                Toast.makeText(this, "La cuenta se elimino satisfactoriamente!", Toast.LENGTH_SHORT).show();
                onActualizaCuentas();
            }
            else if(valor==0)
            {
                Toast.makeText(this, "La cuenta no pudo eliminarse!", Toast.LENGTH_SHORT).show();
            }
            else if(valor==-1){
                Toast.makeText(this, "La cuenta tiene movimientos o transacciones asociados, no se puede realizar la operacion hasta que no los elimine", Toast.LENGTH_LONG).show();
            }
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

        adapter.setOnItemButtonClick(new AdaptadorCuentas.OnItemButtonClick() {
            @Override
            public void onItemEdita(int id) {
                modificarWsCuentas(id);
            }

            @Override
            public void onItemDetalla(int id) {
                transacciones_fragment tranDialog = new transacciones_fragment(null,id, "Transacciones de " + getCuenta(id).getNombre());
                tranDialog.show(getSupportFragmentManager(),"TransaccionesDeCuenta");
            }

            @Override
            public void onItemElimina(final int id) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(activity_cuentas.this, R.style.MyAlertDialogStyle);
                dialog.setTitle("Confirmar");
                dialog.setMessage("¿Desea Eliminar la Cuenta?");
                dialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        eliminarWsCuentas(id);
                    }
                });
                dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();
            }
        });

        recyclerCuentas.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
    }

    public CCuenta getCuenta(int id)
    {
        for(CCuenta cue:listaCuentas){
            if(cue.getId()==id)
                return cue;
        }
        return null;
    }

    @Override
    public void onActualizaCuentas() {
        cargarWsCuentas();
    }

    public void CargarDialogIngreso()
    {
        cuentas_fragment_ingreso frg = new cuentas_fragment_ingreso(false, "", 0, 0);
        frg.show(getSupportFragmentManager(),"ingreso");
    }

}
