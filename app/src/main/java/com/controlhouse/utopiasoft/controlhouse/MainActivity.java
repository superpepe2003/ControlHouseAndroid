package com.controlhouse.utopiasoft.controlhouse;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.controlhouse.utopiasoft.controlhouse.Entidades.CConeccion;
import com.controlhouse.utopiasoft.controlhouse.Entidades.CUsuario;
import com.controlhouse.utopiasoft.controlhouse.Movimientos.activity_movimientos;

public class MainActivity extends AppCompatActivity {

    CUsuario _Usuario;
    TextView txtUsuario, txtPassword;
    CheckBox chkUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _Usuario= new CUsuario();

        CargarUsuario();
        if(_Usuario.getUsuario().length()>0)
            Ingreso(true);

        txtUsuario = findViewById(R.id.txtUsuario);
        txtPassword = findViewById(R.id.txtPassword);
        chkUsuario =  findViewById(R.id.chkUsuario);
    }

    public void Ingresar(View view)
    {
        if(view.getId() == R.id.btnIngresar)
        {
            _Usuario.setUsuario(txtUsuario.getText().toString());
            _Usuario.setPassword(txtPassword.getText().toString());
            Ingreso(false);
        }
    }


    public void Ingreso(boolean preingreso)
    {
        if((_Usuario.getUsuario().equals("admin")) && (_Usuario.getPassword().equals("admin")))
        {
            if(!preingreso)
                if(chkUsuario.isChecked())
                    GuardarUsuario();
            //Intent in = new Intent(this, menuPrincipal.class);
            //startActivity(in);

            CConeccion.bd=CConeccion.bdEjemplo;
            Intent in =  new Intent(this, activity_movimientos.class);
            startActivity(in);

            finish();
        }
        else if((_Usuario.getUsuario().equals("silpab")) && (_Usuario.getUsuario().equals("silpab")))
        {
            if(!preingreso)
                if(chkUsuario.isChecked())
                    GuardarUsuario();
            //Intent in = new Intent(this, menuPrincipal.class);
            //startActivity(in);

            CConeccion.bd=CConeccion.bdReal;
            Intent in =  new Intent(this, activity_movimientos.class);
            startActivity(in);

            finish();
        }
    }


    private void GuardarUsuario() {

        SharedPreferences pref = getSharedPreferences("login", MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();

        edit.putString("usuario", _Usuario.getUsuario());
        edit.putString("password", _Usuario.getPassword());

        edit.commit();
    }

    private void CargarUsuario(){
        SharedPreferences pref = getSharedPreferences("login", MODE_PRIVATE);

        _Usuario.setUsuario(pref.getString("usuario", ""));
        _Usuario.setPassword(pref.getString("password", ""));
    }
}
