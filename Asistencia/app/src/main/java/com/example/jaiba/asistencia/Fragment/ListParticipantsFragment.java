package com.example.jaiba.asistencia.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.example.jaiba.asistencia.Adapter.TrabajadoresAdapter;
import com.example.jaiba.asistencia.Entities.Trabajadores;
import com.example.jaiba.asistencia.VolleySingleton;
import com.example.jaiba.asistencia.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListParticipantsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ListParticipantsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListParticipantsFragment extends Fragment implements Response.Listener<JSONObject>,Response.ErrorListener{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    RecyclerView recyclerTrabajadores;
    ArrayList<Trabajadores> listaTrabajadores;
    ProgressDialog progress;
    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    public ListParticipantsFragment() {
    }

    public static ListParticipantsFragment newInstance(String param1, String param2) {
        ListParticipantsFragment fragment = new ListParticipantsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista=inflater.inflate(R.layout.fragment_list_participants, container, false);

        listaTrabajadores=new ArrayList<>();
        recyclerTrabajadores= (RecyclerView) vista.findViewById(R.id.RecyclerView);
        recyclerTrabajadores.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerTrabajadores.setHasFixedSize(true);
        request= Volley.newRequestQueue(getContext());
        cargarWebService();

        return vista;
    }

    private void cargarWebService() {

        progress=new ProgressDialog(getContext());
        progress.setMessage("Consultando...");
        progress.show();

        SharedPreferences preferences = getContext().getSharedPreferences("Login", Context.MODE_PRIVATE);
        String ID_EMPRESA = preferences.getString("id_empresa","");

        String url="http://javieribarra.cl/wsJSON.php?id_empresa="+ID_EMPRESA+"";

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

        JSONArray json=response.optJSONArray("Trabajador");

        try {

            for (int i=0;i<json.length();i++){
                trabajadores=new Trabajadores();
                JSONObject jsonObject=null;
                jsonObject=json.getJSONObject(i);

                trabajadores.setEmail(jsonObject.optString("email_trabajador"));
                trabajadores.setName(jsonObject.optString("nombre"));

                listaTrabajadores.add(trabajadores);
            }
            progress.hide();
            TrabajadoresAdapter adapter=new TrabajadoresAdapter(listaTrabajadores);
            recyclerTrabajadores.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "No se ha podido establecer conexión con el servidor" +
                    " "+response, Toast.LENGTH_LONG).show();
            progress.hide();
        }

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
