package com.example.marcadoresinterfazojcs;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.marcadoresinterfazojcs.Adaptador.Datos;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener {

    String llave = "AIzaSyAZmpF3k0bcm-3c-f_0feLZQZRwYu-gdr0";
    GoogleMap mapa;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {

        Double latitud = latLng.latitude;
        Double longitud = latLng.longitude;

        String api_map_alrededor = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?fields=name&location=" + latitud + "," + longitud + "&radius=1500" + "&type=bar&key=" + llave;

        get_lugares_cercanos_mapa(api_map_alrededor);

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mapa = googleMap;

        mapa.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mapa.getUiSettings().setZoomControlsEnabled(true);

        CameraUpdate camUpd1 = CameraUpdateFactory.newLatLngZoom(new LatLng(-1.0243965508659039, -79.46594409916771), 18);

        mapa.setInfoWindowAdapter(new Datos(this, "", null,null));
        mapa.moveCamera(camUpd1);

        mapa.setOnMapClickListener(this);
        mapa.setOnMarkerClickListener(this);


    }

    public void get_lugares_cercanos_mapa(String url){
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject alrededor = new JSONObject(response);
                            JSONArray resultsa = alrededor.getJSONArray("results");

                            for (int i = 0; i < resultsa.length(); i++) {

                                JSONObject objecto = resultsa.getJSONObject(i);
                                String lugar = objecto.getString("name");
                                String place_id = objecto.getString("place_id");
                                JSONObject jObjectGeometry = objecto.getJSONObject("geometry");
                                JSONObject jObjectLocation = jObjectGeometry.getJSONObject("location");


                                MarkerOptions markerOptions = new MarkerOptions();

                                markerOptions.position(new LatLng(jObjectLocation.getDouble("lat"), jObjectLocation.getDouble("lng")));
                                markerOptions.title(lugar);
                                markerOptions.snippet(place_id);

                                mapa.addMarker(markerOptions);
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error.networkResponse.statusCode == 400){

                        }
                    }
                });
        queue.add(stringRequest);
    }

    private static final Map<String, String> ENGLISH_TO_SPANISH_DAYS = new HashMap<String, String>() {{
        put("Monday", "Lunes");
        put("Tuesday", "Martes");
        put("Wednesday", "Miércoles");
        put("Thursday", "Jueves");
        put("Friday", "Viernes");
        put("Saturday", "Sábado");
        put("Sunday", "Domingo");
    }};

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {

        String place_id = marker.getSnippet();


        String api_map_detalles = "https://maps.googleapis.com/maps/api/place/details/json?fields=name,photo,opening_hours,address_components,formatted_phone_number&place_id=" + place_id + "&key=" + llave;


        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, api_map_detalles,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject objecto = new JSONObject(response);
                            JSONObject  resul = objecto.getJSONObject("result");

                            String horario = "";
                            JSONObject jObjectOpeningHours = resul.getJSONObject("opening_hours");
                            JSONArray jArrayWeekdayText = jObjectOpeningHours.getJSONArray("weekday_text");
                            for (int i = 0; i < jArrayWeekdayText.length(); i++) {
                                String englishDay = jArrayWeekdayText.getString(i);
                                String[] parts = englishDay.split(": ");
                                if (parts.length == 2) {
                                    String englishDayName = parts[0].trim();
                                    String openingHours = parts[1].trim();

                                    if (ENGLISH_TO_SPANISH_DAYS.containsKey(englishDayName)) {
                                        String spanishDayName = ENGLISH_TO_SPANISH_DAYS.get(englishDayName);
                                        horario += spanishDayName + ": " + openingHours + "\n";
                                    } else {
                                        horario += englishDayName + ": " + openingHours + "\n";
                                    }
                                }
                            }
                            ImageView imgView = findViewById(R.id.img);
                            mapa.setInfoWindowAdapter(new Datos(MainActivity.this, horario, marker,imgView));
                            marker.showInfoWindow();
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        queue.add(stringRequest);
        return false;
    }
}