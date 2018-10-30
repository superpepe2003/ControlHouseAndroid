package com.controlhouse.utopiasoft.controlhouse;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void Ingresar(View view)
    {
        if(view.getId() == R.id.btnIngresar)
        {
            Intent in =  new Intent(this, menuPrincipal.class);
            startActivity(in);
        }
    }
}
