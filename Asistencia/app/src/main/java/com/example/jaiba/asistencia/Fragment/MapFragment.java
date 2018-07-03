package com.example.jaiba.asistencia.Fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.example.jaiba.asistencia.Entities.Ubicaciones;
import com.example.jaiba.asistencia.VolleySingleton;
import com.example.jaiba.asistencia.R;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MapFragment extends Fragment implements OnMapReadyCallback {

    private View vista;
    private GoogleMap mGoogleMap;
    SupportMapFragment supportMapFragment;
    ProgressDialog progress;
    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;
    ArrayList<Ubicaciones> listaUbicaciones;
    String ID_EMPRESA;
    int largo;

    private OnFragmentInteractionListener mListener;

    public MapFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        SharedPreferences preferences = getContext().getSharedPreferences("Login", Context.MODE_PRIVATE);
        ID_EMPRESA = preferences.getString("id_empresa","");

        listaUbicaciones = new ArrayList<>();
        request = Volley.newRequestQueue(getContext());
       // cargarWebService();

        vista = inflater.inflate(R.layout.fragment_map, container, false);

        return vista;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getContext());

        if (status== ConnectionResult.SUCCESS){
            supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            supportMapFragment.getMapAsync(this);
        }
        else{
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status,(Activity) getContext(),10);
            dialog.show();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;


        progress=new ProgressDialog(getContext());
        progress.setMessage("Consultando...");
        progress.show();

        String url="http://javieribarra.cl/wsJSON.php?id_empresa="+ID_EMPRESA+"&ubicacion=1";

        jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Ubicaciones ubicaciones = null;

                JSONArray json = response.optJSONArray("Ubicacion");

                try {

                    for (int i = 0; i < json.length(); i++) {
                        ubicaciones = new Ubicaciones();
                        JSONObject jsonObject = null;
                        jsonObject = json.getJSONObject(i);

                        ubicaciones.setLatitud((float) jsonObject.optDouble("latitud"));
                        ubicaciones.setLongitud((float) jsonObject.optDouble("longitud"));
                        ubicaciones.setNombre(jsonObject.optString("nombre"));
                        ubicaciones.setHora(jsonObject.optString("hora"));

                        listaUbicaciones.add(ubicaciones);
                    }
                    progress.hide();
                    largo = listaUbicaciones.size();

                    float lat;
                    float log;
                    String nomb;

                    float zoom = 13;
                    for (int x=0; x<largo;x++) {
                        lat = listaUbicaciones.get(x).getLatitud();
                        log = listaUbicaciones.get(x).getLongitud();
                        nomb = listaUbicaciones.get(x).getNombre();
                        LatLng agregar = new LatLng(lat, log);
                        mGoogleMap.addMarker(new MarkerOptions().position(agregar).title(nomb).snippet("Hora de ingreso: "+ listaUbicaciones.get(x).getHora()));
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(agregar, zoom));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "No se ha podido establecer conexión con el servidor" +
                            " " + response, Toast.LENGTH_LONG).show();
                    progress.hide();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "No se puede conectar "+error.toString(), Toast.LENGTH_LONG).show();
                System.out.println();
                Log.d("ERROR: ", error.toString());
                progress.hide();
            }
        });
        VolleySingleton.getIntanciaVolley(getContext()).addToRequestQueue(jsonObjectRequest);


    }



    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
