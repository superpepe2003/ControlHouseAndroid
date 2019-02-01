package com.controlhouse.utopiasoft.controlhouse.Movimientos;

import android.support.annotation.NonNull;

import com.controlhouse.utopiasoft.controlhouse.Entidades.CMovimiento;
import com.controlhouse.utopiasoft.controlhouse.R;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class AdaptadorMovimientos extends RecyclerView.Adapter<AdaptadorMovimientos.HolderMovimientos> {

    ArrayList<CMovimiento> listMovimientos;
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    private static onClickItemAdapterListener mlistener;
    boolean selecciona;

    public interface onClickItemAdapterListener{
        void onItemDetalle(int id);
        void onItemEdita(int id);
        void onItemElimina(int id);
        void onItemSelecciona(ArrayList<CMovimiento> listMovi);
        void onSelecciona();
    }

    public void setOnClickItemAdapterListener(onClickItemAdapterListener listener)
    {
        mlistener=listener;
    }

    public AdaptadorMovimientos(ArrayList<CMovimiento> listMovimientos, boolean chk) {
        this.listMovimientos = listMovimientos;
        this.selecciona=chk;
    }

    @Override
    public AdaptadorMovimientos.HolderMovimientos onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.mov_item_list,null,false);

        //view.setOnClickItemAdapterListener(this);

        return new HolderMovimientos(view,mlistener,selecciona);
    }

    @Override
    public void onBindViewHolder(HolderMovimientos holder, int position) {
        holder.txtCategoria.setText(listMovimientos.get(position).getCategoria());
        holder.txtTipo.setText((listMovimientos.get(position).getTipo())?"Ingreso" : "Egreso");
        holder.txtMonto.setText(Double.toString(listMovimientos.get(position).getMonto()));
        holder.txtFecha.setText(formatter.format(listMovimientos.get(position).getFecha()));
        holder.txtCatePadre.setText(listMovimientos.get(position).getCatePadre());
        holder.chkSeleccionaMovimiento.setChecked(listMovimientos.get(position).getChecked());
        //holder.txtHashtag.setText(listMovimientos.get(position).getHashtag());
        if(selecciona)
            holder.chkSeleccionaMovimiento.setVisibility(View.VISIBLE);
        else
            holder.chkSeleccionaMovimiento.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return listMovimientos.size();
    }

    public class HolderMovimientos extends RecyclerView.ViewHolder {
        TextView txtCategoria, txtFecha, txtMonto, txtHashtag, txtTipo, txtCatePadre;
        ImageButton btnDetalle, btnEditar, btnEliminar;
        CheckBox chkSeleccionaMovimiento;

        public HolderMovimientos(View itemView, final onClickItemAdapterListener listener, final boolean selecciona) {
            super(itemView);
            txtCategoria =  itemView.findViewById(R.id.txtMovimientosCategoria);
            txtTipo =  itemView.findViewById(R.id.txtMovimientosTipo);
            txtMonto =  itemView.findViewById(R.id.txtMovimientosMonto);
            txtFecha =  itemView.findViewById(R.id.txtMovimientosFecha);
            txtCatePadre= itemView.findViewById(R.id.txtCategoriaPadre);
            //txtHashtag =  itemView.findViewById(R.id.txtMovimientosHashtag);
            btnDetalle = itemView.findViewById(R.id.btn_detalle);
            btnEditar= itemView.findViewById(R.id.btn_editar);
            btnEliminar=itemView.findViewById(R.id.btn_eliminar);

            chkSeleccionaMovimiento= itemView.findViewById(R.id.chkSeleccionarMovimiento);

            btnDetalle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null)
                    {
                        int position = getAdapterPosition();
                        if(position!= RecyclerView.NO_POSITION) {
                            int id= listMovimientos.get(position).getId();
                            listener.onItemDetalle(id);
                        }
                    }
                }
            });

            btnEditar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null)
                    {
                        int position = getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION)
                        {
                            int id= listMovimientos.get(position).getId();
                            listener.onItemEdita(id);
                        }
                    }
                }
            });

            btnEliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null)
                    {
                        int position = getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION)
                        {
                            int id= listMovimientos.get(position).getId();
                            listener.onItemElimina(id);
                        }
                    }
                }
            });

            chkSeleccionaMovimiento.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(selecciona) {
                        listMovimientos.get(getAdapterPosition()).setChecked(isChecked);
                        if (listener != null) {
                            listener.onItemSelecciona(listMovimientos);
                        }
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(listener!=null)
                    {
                        int position = getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION)
                        {
                            int id= listMovimientos.get(position).getId();
                            listener.onSelecciona();
                        }
                    }
                    return true;
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(selecciona) {
                        if (chkSeleccionaMovimiento.isChecked()) {
                            chkSeleccionaMovimiento.setChecked(false);
                        } else {
                            chkSeleccionaMovimiento.setChecked(true);
                        }
                    }
                }
            });
        }
    }
}
