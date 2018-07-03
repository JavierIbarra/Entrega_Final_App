package com.example.jaiba.asistencia.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.example.jaiba.asistencia.Adapter.TrabajadoresAdapter;
import com.example.jaiba.asistencia.Entities.Trabajadores;
import com.example.jaiba.asistencia.Fragment.ProfileFragment;
import com.example.jaiba.asistencia.VolleySingleton;
import com.example.jaiba.asistencia.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ListParticipantsFragment extends Fragment implements Response.Listener<JSONObject>,Response.ErrorListener{

    private OnFragmentInteractionListener mListener;
    Button btnfilter;
    String ESTADO;
    RecyclerView recyclerTrabajadores;
    ArrayList<Trabajadores> listaTrabajadores;
    ProgressDialog progress;
    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    public ListParticipantsFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista=inflater.inflate(R.layout.fragment_list_participants, container, false);

        btnfilter=(Button)vista.findViewById(R.id.filter);
        listaTrabajadores=new ArrayList<>();
        recyclerTrabajadores= (RecyclerView) vista.findViewById(R.id.RecyclerView);
        recyclerTrabajadores.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerTrabajadores.setHasFixedSize(true);

        btnfilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDialogOpciones();
            }
        });

        request= Volley.newRequestQueue(getContext());
        cargarWebService();

        return vista;
    }

    private void mostrarDialogOpciones() {
        final CharSequence[] opciones={"Activos","Inactivos","Todos"};
        final AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setTitle("Filtrar busqueda por");
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("Activos")){
                    ESTADO="Activo";
                }else{
                    if (opciones[i].equals("Inactivos")){
                        ESTADO="Inactivo";

                    }else{
                        ESTADO="";
                    }
                }
                cargarWebService();
            }
        });
        builder.show();
    }
    private void cargarWebService() {

        progress=new ProgressDialog(getContext());
        progress.setMessage("Consultando...");
        progress.show();

        SharedPreferences preferences = getContext().getSharedPreferences("Login", Context.MODE_PRIVATE);
        String ID_EMPRESA = preferences.getString("id_empresa","");

        String url="http://javieribarra.cl/wsJSON.php?id_empresa="+ID_EMPRESA+"&estado="+ESTADO+"";

        jsonObjectRequest=new JsonObjectRequest(Request.Method.GET,url,null,this,this);
        VolleySingleton.getIntanciaVolley(getContext()).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getContext(), "No se puede conectar "+error.toString(), Toast.LENGTH_LONG).show();
        System.out.println();
        Log.d("ERROR: ", error.toString());
        progress.hide();
    }

    @Override
    public void onResponse(JSONObject response) {
        Trabajadores trabajadores=null;
        listaTrabajadores = new ArrayList<>();
        JSONArray json=response.optJSONArray("Trabajador");

        try {

            for (int i=0;i<json.length();i++){
                trabajadores=new Trabajadores();
                JSONObject jsonObject=null;
                jsonObject=json.getJSONObject(i);

                trabajadores.setEmail(jsonObject.optString("email_trabajador"));
                trabajadores.setName(jsonObject.optString("nombre"));
                trabajadores.setEntry(jsonObject.optInt("entrada"));
                trabajadores.setRutaImagen(jsonObject.getString("imagen"));

                listaTrabajadores.add(trabajadores);
            }
            progress.hide();
            TrabajadoresAdapter adapter=new TrabajadoresAdapter(listaTrabajadores, getContext());

            adapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String email = listaTrabajadores.get(recyclerTrabajadores.getChildAdapterPosition(view)).getEmail();
                    Fragment nuevoFragmento = new ProfileFragment();

                    Bundle args = new Bundle();
                    args.putString("email", email);
                    nuevoFragmento.setArguments(args);

                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.content_admin, nuevoFragmento);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });

            recyclerTrabajadores.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "No se ha podido establecer conexión con el servidor" +
                    " "+response, Toast.LENGTH_LONG).show();
            progress.hide();
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
