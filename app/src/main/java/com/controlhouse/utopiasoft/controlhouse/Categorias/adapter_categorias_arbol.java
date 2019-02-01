package com.controlhouse.utopiasoft.controlhouse.Categorias;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.controlhouse.utopiasoft.controlhouse.Entidades.claveValor;
import com.controlhouse.utopiasoft.controlhouse.R;

import java.util.List;
import java.util.Map;

public class adapter_categorias_arbol extends BaseExpandableListAdapter {
    Context contexto;

    List<claveValor> padre;
    Map<Integer, List<claveValor>> hijos;
    int id;
    OnClickListener listener;
    int _posicionGroup, _posicionChildren;
    boolean mostrarBotones;

    public interface OnClickListener{
        void onClick(int id);
        //void onClickEditar(int id);
        void onClickEliminar(int id);
    }

    public void setOnClickListener(OnClickListener listener){this.listener=listener;}



    public adapter_categorias_arbol(Context context, List<claveValor> padre, Map<Integer, List<claveValor>> hijo, int id, boolean... mostrarBotones)
    {
        contexto=context;
        this.padre=padre;
        this.hijos=hijo;
        this.id=id;
        this.mostrarBotones = (mostrarBotones.length >= 1) ? mostrarBotones[0] : false;
    }


    @Override
    public int getGroupCount() {
        return padre.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        //if(hijos.get(padre.get(groupPosition).getId())!=null)
            return hijos.get(padre.get(groupPosition).getId()).size();
        /*else
            return 0;*/
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupPosition;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return padre.get(groupPosition).getId();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return hijos.get(padre.get(groupPosition).getId()).get(childPosition).getId();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View item = convertView;
        final DataHolder holder;
        if(item==null) {
            LayoutInflater inflater = ((Activity)contexto).getLayoutInflater();
            item = inflater.inflate(R.layout.categorias_expand_list_view_padre, null);
            holder= new DataHolder();
            holder.textView=item.findViewById(R.id.txtCategoria);
            holder.textViewId=item.findViewById(R.id.txtCategoriId);
            holder.textViewMenu=item.findViewById(R.id.textViewOptions);
            if((!mostrarBotones)||(this.padre.get(groupPosition).getId()==-1))
                holder.textViewMenu.setVisibility(View.GONE);

            item.setTag(holder);
        }
        else
        {
            holder=(DataHolder)item.getTag();
        }
        if(this.padre.get(groupPosition).getId()==-1) {
            holder.textView.setText(Html.fromHtml("<u>" + this.padre.get(groupPosition).getNombre() + "</u>"));
        }
        else
        {
            holder.textView.setText(this.padre.get(groupPosition).getNombre());
        }
        holder.textViewId.setText(String.valueOf(this.padre.get(groupPosition).getId()));

        ExpandableListView mExpandableListView = (ExpandableListView) parent;
        mExpandableListView.expandGroup(groupPosition);

        if(mostrarBotones) {
            holder.textViewMenu.setOnClickListener(new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(contexto,holder.textViewMenu, Gravity.CENTER_VERTICAL);
                    popup.inflate(R.menu.categoria_options);

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId())
                            {
                                case R.id.categoria_option_eliminar:
                                    if(listener!=null)
                                        listener.onClickEliminar(padre.get(groupPosition).getId());
                            }
                            return true;
                        }
                    });
                    popup.show();
                }
            });
        }

        /*if(this.padre.get(groupPosition).getId()==this.id)
            holder.textView.setBackgroundColor(ContextCompat.getColor(contexto, R.color.colorPrimary));
        else
            holder.textView.setBackgroundColor(ContextCompat.getColor(contexto, R.color.colorPrimaryLight));*/

        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null)
                    listener.onClick(Integer.parseInt(((DataHolder)v.getTag()).textViewId.getText().toString()));
            }
        });

        return item;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View item = convertView;
        final DataHolder holder;
        if(item==null) {
            LayoutInflater inflater = ((Activity)contexto).getLayoutInflater();
            item = inflater.inflate(R.layout.categorias_expand_list_view_hijo, null);
            holder= new DataHolder();
            holder.textView=item.findViewById(R.id.txtCategoria);
            holder.textViewId=item.findViewById(R.id.txtCategoriId);
            holder.textViewMenu=item.findViewById(R.id.textViewOptions);
            if(!mostrarBotones)
                holder.textViewMenu.setVisibility(View.GONE);
            item.setTag(holder);
        }
        else
        {
            holder=(DataHolder)item.getTag();
        }
        holder.textView.setText(this.hijos.get(padre.get(groupPosition).getId()).get(childPosition).getNombre());
        holder.textViewId.setText(String.valueOf(this.hijos.get(padre.get(groupPosition).getId()).get(childPosition).getId()));

        if(mostrarBotones)
            holder.textViewMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(contexto,holder.textViewMenu);
                    popup.inflate(R.menu.categoria_options);

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId())
                            {
                                case R.id.categoria_option_eliminar:
                                    if(listener!=null)
                                        listener.onClickEliminar(hijos.get(padre.get(groupPosition).getId()).get(childPosition).getId());
                            }
                            return true;
                        }
                    });
                    popup.show();
                }
            });

        /*if(this.hijos.get(padre.get(groupPosition).getId()).get(childPosition).getId()==this.id)
            holder.textView.setBackgroundColor(ContextCompat.getColor(contexto, R.color.colorPrimary));
        else
            holder.textView.setBackgroundColor(ContextCompat.getColor(contexto, R.color.colorPrimaryLight));*/

        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null)
                    listener.onClick(Integer.parseInt(((DataHolder)v.getTag()).textViewId.getText().toString()));
            }
        });

        return item;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    static class DataHolder{
        TextView textView, textViewId, textViewMenu;

    }
}
