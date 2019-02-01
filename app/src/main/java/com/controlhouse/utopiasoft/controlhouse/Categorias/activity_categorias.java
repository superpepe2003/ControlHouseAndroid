package com.controlhouse.utopiasoft.controlhouse.Categorias;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
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
import com.controlhouse.utopiasoft.controlhouse.Cuentas.activity_cuentas;
import com.controlhouse.utopiasoft.controlhouse.Entidades.CCategorias;
import com.controlhouse.utopiasoft.controlhouse.Entidades.CConeccion;
import com.controlhouse.utopiasoft.controlhouse.Movimientos.activity_movimientos;
import com.controlhouse.utopiasoft.controlhouse.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class activity_categorias extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Response.ErrorListener, Response.Listener<JSONObject> {

    List<CCategorias> listaCategorias= new ArrayList<>();

    private AppBarLayout appBar;
    private TabLayout pestanas;
    private ViewPager viewPager;

    fragment_lista_categorias frgEgreso, frgIngreso;

    View vista;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categorias_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        request= Volley.newRequestQueue(this);

        LlenarCategorias();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }



    private void llenarViewPager(ViewPager viewPager) {
        SeccionesAdapter adapter= new SeccionesAdapter(getSupportFragmentManager());
        frgEgreso = new fragment_lista_categorias(listaCategorias,-1,0, 2);
        frgIngreso = new fragment_lista_categorias(listaCategorias,-1,1,2);

        frgEgreso.setOnClickListCategoriaListener(new fragment_lista_categorias.onClickListCategoriaListener() {
            @Override
            public void onItemClick(int id, String nombre) {
                Toast.makeText(getApplicationContext(),"El id es: " + id + " - de nombre: " + nombre, Toast.LENGTH_SHORT).show();
            }

        });

        frgEgreso.setOnMenuListCategoriaListener(new fragment_lista_categorias.onMenuListCategoriaListener() {
            @Override
            public void onMenuEliminarClick(final int id) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(activity_categorias.this, R.style.MyAlertDialogStyle);
                dialog.setTitle("Eliminar");
                dialog.setMessage("¿Desea eliminar la Categoria?");
                dialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        eliminarCategoria(id,0);
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

        frgIngreso.setOnClickListCategoriaListener(new fragment_lista_categorias.onClickListCategoriaListener() {
            @Override
            public void onItemClick(int id, String nombre) {
                Toast.makeText(getApplicationContext(),"El id es: " + id + " - de nombre: " + nombre, Toast.LENGTH_SHORT).show();
            }
        });

        frgIngreso.setOnMenuListCategoriaListener(new fragment_lista_categorias.onMenuListCategoriaListener() {
            @Override
            public void onMenuEliminarClick(int id) {
                eliminarCategoria(id,1);
            }
        });

        adapter.addFragment(frgEgreso, getResources().getString(R.string.nav_categorias_egreso));
        adapter.addFragment(frgIngreso,getResources().getString(R.string.nav_categorias_ingreso));

        viewPager.setAdapter(adapter);
    }



    private void ConfigurarPestanas()
    {
        if(appBar==null)
        {
            appBar = findViewById(R.id.appBar);
            pestanas= new TabLayout(this);
            appBar.addView(pestanas);

            viewPager =  findViewById(R.id.idViewPagerCategorias);
            llenarViewPager(viewPager);

            viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
            {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }
            });
            pestanas.setupWithViewPager(viewPager);
        }
        else
        {
            llenarViewPager(viewPager);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.categorias_activity_toolbar, menu);
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

    private void BorrarUsuario() {

        SharedPreferences pref = getSharedPreferences("login", MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();

        edit.putString("usuario", "");
        edit.putString("password", "");

        edit.commit();
    }

    private void eliminarCategoria(int id, int tipo) {
        if((id!=1000)&&(id!=1001)) {
            String Url = CConeccion.URL_Eliminar_Categorias + CConeccion.bd + "&id=" + id + "&tipo=" + tipo;
            jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Url, null, this, this);
            request.add(jsonObjectRequest);
        }
        else
        {
            Toast.makeText(this,"Esta Categoria no se puede borrar", Toast.LENGTH_SHORT).show();
        }
    }

    private void LlenarCategorias() {
        jsonObjectRequest =  new JsonObjectRequest(Request.Method.GET, CConeccion.URL_Listar_Categorias + CConeccion.bd, null, this, this);
        request.add(jsonObjectRequest);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getApplicationContext(), "No se puede conectar", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONObject response) {
        CCategorias cat;
        JSONArray jsonCategorias = response.optJSONArray("categoriamodel");
        JSONArray jsonEliminarCategoria = response.optJSONArray("resultadoEliminar");

        if(jsonCategorias!=null) {
            listaCategorias.clear();
            //Toast.makeText(getApplicationContext(), "HAY CATEGORIAS", Toast.LENGTH_LONG).show();
            for (int i = 0; i < jsonCategorias.length(); i++) {
                cat = new CCategorias();
                JSONObject jsonObject = null;
                try {
                    jsonObject = jsonCategorias.getJSONObject(i);
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

                listaCategorias.add(cat);
            }
            ConfigurarPestanas();
        }
        else if(jsonEliminarCategoria!=null)
        {
            JSONObject jsonObject = null;
            try {
                jsonObject = jsonEliminarCategoria.getJSONObject(0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            int valor= jsonObject.optInt("valor");
            if(valor>0) {
                Toast.makeText(this, "La categoria se borro satisfactoriamente", Toast.LENGTH_SHORT).show();
                LlenarCategorias();
            }
        }
    }
}
