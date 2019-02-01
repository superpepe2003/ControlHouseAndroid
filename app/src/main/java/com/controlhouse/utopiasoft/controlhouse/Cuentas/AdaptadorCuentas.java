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
    OnItemButtonClick listener;

    public interface OnItemButtonClick{
        void onItemEdita(int id);
        void onItemDetalla(int id);
        void onItemElimina(int id);
    }

    public void setOnItemButtonClick(OnItemButtonClick onItemButtonClick){listener=onItemButtonClick;}


    public AdaptadorCuentas(List<CCuenta> listCuentas)
    {
        this.listCuentas= listCuentas;
    }

    @NonNull
    @Override
    public AdaptadorCuentas.HolderCuentas onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.cuentas_item_list,null, false);

        return new HolderCuentas(view, listener);
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


        public HolderCuentas(@NonNull View itemView, final OnItemButtonClick listener) {

            super(itemView);
            txtcuentas=itemView.findViewById(R.id.txtNombre);
            txtSaldo=itemView.findViewById(R.id.txtSaldo);
            btnDetalle=itemView.findViewById(R.id.btn_detalle);
            btnEliminar=itemView.findViewById(R.id.btn_eliminar);
            btnEdit=itemView.findViewById(R.id.btn_editar);

            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position!=RecyclerView.NO_POSITION)
                    {
                        int id= listCuentas.get(position).getId();
                        listener.onItemEdita(id);
                    }
                }
            });

            btnEliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view){
                    int position= getAdapterPosition();
                    if(position!=RecyclerView.NO_POSITION)
                    {
                        int id= listCuentas.get(position).getId();
                        listener.onItemElimina(id);
                    }
                }
            });

            btnDetalle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position=getAdapterPosition();
                    if(position!=RecyclerView.NO_POSITION)
                    {
                        int id= listCuentas.get(position).getId();
                        listener.onItemDetalla(id);
                    }
                }
            });
        }
    }
}
