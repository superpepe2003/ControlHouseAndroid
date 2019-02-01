package com.controlhouse.utopiasoft.controlhouse.Transacciones;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.controlhouse.utopiasoft.controlhouse.Entidades.CTransaciones;
import com.controlhouse.utopiasoft.controlhouse.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AdaptadorTransacciones extends RecyclerView.Adapter<AdaptadorTransacciones.HolderTransacciones> {
    private ArrayList<CTransaciones> listaTransacciones;
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    int _id;

    public AdaptadorTransacciones(ArrayList<CTransaciones> listaTransacciones, int id)
    {
        this.listaTransacciones=listaTransacciones;
        this._id=id;
    }

    @NonNull
    @Override
    public HolderTransacciones onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.transacciones_item_list,null,false);

        return new HolderTransacciones(view, _id);
    }

    @Override
    public void onBindViewHolder(HolderTransacciones holderTransacciones, int position) {
        if(listaTransacciones.size()>0) {
            holderTransacciones.txtCuentaDestino.setText(listaTransacciones.get(position).getCuentaDestino());
            holderTransacciones.txtCuentaOrigen.setText(listaTransacciones.get(position).getCuentaOrigen());
            holderTransacciones.txtMonto.setText(String.valueOf(listaTransacciones.get(position).getMonto()));
            holderTransacciones.txtFecha.setText(formatter.format(listaTransacciones.get(position).getFecha()));
        }
    }

    @Override
    public int getItemCount() {
        return listaTransacciones.size();
    }

    public class HolderTransacciones extends RecyclerView.ViewHolder {
        TextView txtCuentaOrigen, txtCuentaDestino, txtFecha, txtMonto;
        ImageButton btnEditar, btnDetalle, btnEliminar;

        public HolderTransacciones(View itemView, int id) {
            super(itemView);

            txtCuentaOrigen= itemView.findViewById(R.id.txtCuentaOrigen);
            txtCuentaDestino=itemView.findViewById(R.id.txtCuentaDestino);
            txtFecha=itemView.findViewById(R.id.txtTransaccionFecha);
            txtMonto=itemView.findViewById(R.id.txtTransaccionMonto);

            btnEditar=itemView.findViewById(R.id.btn_editar_transacciones);
            btnDetalle=itemView.findViewById(R.id.btn_detalle_transacciones);
            btnEliminar=itemView.findViewById(R.id.btn_eliminar_transacciones);

            if(id>0)
            {
                btnDetalle.setVisibility(View.GONE);
                btnEliminar.setVisibility(View.GONE);
                btnEditar.setVisibility(View.GONE);
            }

        }
    }
}
