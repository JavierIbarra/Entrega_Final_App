package com.example.jaiba.asistencia.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import java.util.List;

import com.example.jaiba.asistencia.R;
import com.example.jaiba.asistencia.Entities.Trabajadores;
import com.example.jaiba.asistencia.VolleySingleton;

public class TrabajadoresAdapter extends RecyclerView.Adapter<TrabajadoresAdapter.TrabajadoresHolder> implements View.OnClickListener{

    List<Trabajadores> ListaTrabajadores;
    Context context;
    private View.OnClickListener listener;

    public TrabajadoresAdapter(List<Trabajadores> ListaTrabajadores, Context context){
        this.ListaTrabajadores=ListaTrabajadores;
        this.context=context;
    }

    @Override
    public TrabajadoresHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View vista= LayoutInflater.from(parent.getContext()).inflate(R.layout.trabajadores_list,parent,false);
        RecyclerView.LayoutParams layoutParams=new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        vista.setLayoutParams(layoutParams);

        vista.setOnClickListener(this);

        return new TrabajadoresHolder(vista);
    }

    @Override
    public void onBindViewHolder(TrabajadoresHolder holder, int position) {
        holder.email.setText(ListaTrabajadores.get(position).getEmail());
        holder.name.setText(ListaTrabajadores.get(position).getName());
        if (ListaTrabajadores.get(position).getEntry()==1){
            holder.entry.setBackgroundColor(Color.parseColor("#CEF6CE"));
        }
        else {
            holder.entry.setBackgroundColor(Color.parseColor("#F6CECE"));
        }
        if (ListaTrabajadores.get(position).getRutaImagen()!=null){
            //
            cargarImagenWebService(ListaTrabajadores.get(position).getRutaImagen(),holder);
        }else{
            holder.imagen.setImageResource(R.drawable.img_base);
        }
    }

    private void cargarImagenWebService(String rutaImagen, final TrabajadoresHolder holder) {

        String urlImagen="http://javieribarra.cl/"+rutaImagen;
        urlImagen=urlImagen.replace(" ","%20");

        ImageRequest imageRequest=new ImageRequest(urlImagen, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                holder.imagen.setImageBitmap(response);
            }
        }, 0, 0, ImageView.ScaleType.CENTER, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        //request.add(imageRequest);
        VolleySingleton.getIntanciaVolley(context).addToRequestQueue(imageRequest);
    }

    @Override
    public int getItemCount() {
        return ListaTrabajadores.size();
    }


    public void setOnClickListener(View.OnClickListener listener){
        this.listener=listener;
    }

    @Override
    public void onClick(View view) {
        if(listener!=null){
            listener.onClick(view);
        }
    }


    public class TrabajadoresHolder extends RecyclerView.ViewHolder {

        TextView name,email;
        LinearLayout entry;
        ImageView imagen;

        public TrabajadoresHolder(View itemView) {
            super(itemView);
            name= (TextView) itemView.findViewById(R.id.name);
            email= (TextView) itemView.findViewById(R.id.email);
            entry= (LinearLayout) itemView.findViewById(R.id.btnEntry);
            imagen=(ImageView) itemView.findViewById(R.id.imgProfile);
        }
    }
}
