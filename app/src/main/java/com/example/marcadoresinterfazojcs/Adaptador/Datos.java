package com.example.marcadoresinterfazojcs.Adaptador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.marcadoresinterfazojcs.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class Datos implements GoogleMap.InfoWindowAdapter{

    View mapa;
    Marker marcador;
    Context context;
    String horario;

    ImageView foto;




    public Datos(Context context, String horario, Marker marcador1,ImageView foto){
        this.context = context;

        this.marcador = marcador1;
        mapa = LayoutInflater.from(context).inflate(R.layout.mapainterfaces, null);
        this.horario = horario;
        this.foto=foto;
    }



    private void setInformacionEnLayout(Marker marker, View view){
        TextView txtLugar = (TextView) view.findViewById(R.id.Nombre);
        TextView txtHorario = (TextView) view.findViewById(R.id.Hour);

        if (this.marcador != null)
            if (this.marcador.getTitle() != null) txtLugar.setText(this.marcador.getTitle());
        txtHorario.setText(this.horario);
    }

    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        setInformacionEnLayout(marker, this.mapa);
        return this.mapa;
    }

    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        setInformacionEnLayout(marker, this.mapa);
        return this.mapa;
    }
}
