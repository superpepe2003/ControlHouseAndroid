package com.controlhouse.utopiasoft.controlhouse.Movimientos;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.controlhouse.utopiasoft.controlhouse.Entidades.CMovimiento;
import com.controlhouse.utopiasoft.controlhouse.R;



public class fragment_movimientos_detalle extends DialogFragment {

    CMovimiento mov;

    TextView txtfecha,txtcategoria, txtmonto, txtcuenta, txthashtag, txtdescripcion;
    ImageButton btnclose;

    public fragment_movimientos_detalle() {
        // Required empty public constructor
    }

    public fragment_movimientos_detalle(CMovimiento mov) {
        this.mov=mov;
    }

    @Override
    public void onResume() {
        super.onResume();

        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width= ViewGroup.LayoutParams.MATCH_PARENT;
        params.height=ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.mov_dialog_fragment_detalle, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        txtfecha = view.findViewById(R.id._txtfecha);
        txtcategoria = view.findViewById(R.id._txtCategoria);
        txtmonto = view.findViewById(R.id._txtMonto);
        txtcuenta = view.findViewById(R.id._txtCuenta);
        txthashtag = view.findViewById(R.id._txthashtag);
        txtdescripcion = view.findViewById(R.id._txtDescripcion);
        btnclose = view.findViewById(R.id._btnClose);

        CargarValores();

        btnclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

    private void CargarValores() {
        txtfecha.setText(DateFormat.format("dd/MM/yy",mov.getFecha()).toString());
        txtcategoria.setText(mov.getCategoria());
        txtmonto.setText(String.valueOf(mov.getMonto()));
        txtcuenta.setText(mov.getCuenta());
        if(mov.getHashtag().toLowerCase()!="null")
            txthashtag.setText(mov.getHashtag());
        if(mov.getDescripcion().toLowerCase()!="null")
            txtdescripcion.setText(mov.getDescripcion());
    }


}
