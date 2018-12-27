package com.controlhouse.utopiasoft.controlhouse.Cuentas;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.controlhouse.utopiasoft.controlhouse.Entidades.CCategorias;
import com.controlhouse.utopiasoft.controlhouse.Entidades.CCuenta;
import com.controlhouse.utopiasoft.controlhouse.R;

import java.util.List;

public class AdaptadorCuentas extends RecyclerView.Adapter<AdaptadorCuentas.HolderCuentas> {
    List<CCuenta> listCuentas;


    public AdaptadorCuentas(List<CCuenta> listCuentas)
    {
        this.listCuentas= listCuentas;
    }

    @NonNull
    @Override
    public AdaptadorCuentas.HolderCuentas onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.cuentas_item_list,null, false);

        return new HolderCuentas(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorCuentas.HolderCuentas holderCuentas, int position) {
        if(listCuentas.size()>0) {
            holderCuentas.txtcuentas.setText(listCuentas.get(position).getNombre());
            holderCuentas.txtSaldo.setText(String.valueOf(listCuentas.get(position).getSaldo()));
        }
    }

    @Override
    public int getItemCount() {
        return listCuentas.size();
    }

    public class HolderCuentas extends RecyclerView.ViewHolder {
        TextView txtcuentas, txtSaldo;
        ImageButton btnEdit, btnDetalle, btnEliminar;


        public HolderCuentas(@NonNull View itemView) {

            super(itemView);
            txtcuentas=itemView.findViewById(R.id.txtNombre);
            txtSaldo=itemView.findViewById(R.id.txtSaldo);
            btnDetalle=itemView.findViewById(R.id.btn_detalle);
            btnEliminar=itemView.findViewById(R.id.btn_eliminar);
            btnEdit=itemView.findViewById(R.id.btn_editar);
        }
    }
}
