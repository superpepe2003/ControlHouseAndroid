package com.controlhouse.utopiasoft.controlhouse.Movimientos;

import android.support.annotation.NonNull;

import com.controlhouse.utopiasoft.controlhouse.Entidades.CMovimiento;
import com.controlhouse.utopiasoft.controlhouse.MainActivity;
import com.controlhouse.utopiasoft.controlhouse.R;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AdaptadorMovimientos extends RecyclerView.Adapter<AdaptadorMovimientos.HolderMovimientos> {

    ArrayList<CMovimiento> listMovimientos;
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

    public AdaptadorMovimientos(ArrayList<CMovimiento> listMovimientos) {
        this.listMovimientos = listMovimientos;
    }

    @Override
    public AdaptadorMovimientos.HolderMovimientos onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_list_movimientos,null,false);

        return new HolderMovimientos(view);
    }

    @Override
    public void onBindViewHolder(HolderMovimientos holder, int position) {
        holder.txtCategoria.setText(listMovimientos.get(position).getCategoria());
        holder.txtSubCategoria.setText(listMovimientos.get(position).getSubCategoria());
        holder.txtTipo.setText((listMovimientos.get(position).getTipo())?"Ingreso" : "Egreso");
        holder.txtMonto.setText(Double.toString(listMovimientos.get(position).getMonto()));
        holder.txtFecha.setText(formatter.format(listMovimientos.get(position).getFecha()));
        holder.txtHashtag.setText(listMovimientos.get(position).getHashtag());
    }

    @Override
    public int getItemCount() {
        return listMovimientos.size();
    }

    public class HolderMovimientos extends RecyclerView.ViewHolder {
        TextView txtCategoria,txtSubCategoria, txtFecha, txtMonto, txtHashtag, txtTipo;

        public HolderMovimientos(View itemView) {
            super(itemView);
            txtCategoria =  itemView.findViewById(R.id.txtMovimientosCategoria);
            txtSubCategoria = itemView.findViewById(R.id.txtMovimientosSubCategoria);
            txtTipo =  itemView.findViewById(R.id.txtMovimientosTipo);
            txtMonto =  itemView.findViewById(R.id.txtMovimientosMonto);
            txtFecha =  itemView.findViewById(R.id.txtMovimientosFecha);
            txtHashtag =  itemView.findViewById(R.id.txtMovimientosHashtag);
        }
    }
}
