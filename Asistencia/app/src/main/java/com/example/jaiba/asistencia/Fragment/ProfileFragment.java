package com.example.jaiba.asistencia.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import com.android.volley.toolbox.Volley;
import com.example.jaiba.asistencia.R;
import com.example.jaiba.asistencia.Fragment.ProfileFragment;
import com.example.jaiba.asistencia.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProfileFragment extends Fragment implements Response.Listener<JSONObject>,Response.ErrorListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    JsonObjectRequest jsonObjectRequest;
    RequestQueue request;
    private ProgressDialog progress;
    private TextView name;
    private TextView email;
    private TextView comunity;
    private TextView active;
    private ImageView profile;

    private OnFragmentInteractionListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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

        View vista =  inflater.inflate(R.layout.fragment_profile, container, false);
        name = (TextView)vista.findViewById(R.id.textViewName);
        email = (TextView)vista.findViewById(R.id.textViewEmail);
        active = (TextView)vista.findViewById(R.id.textViewActive);
        comunity = (TextView)vista.findViewById(R.id.textViewCompany);
        profile = (ImageView) vista.findViewById(R.id.Profile);

        request= Volley.newRequestQueue(getContext());
        cargarWebService();

        return vista;
    }

    private void cargarWebService() {

        progress=new ProgressDialog(getContext());
        progress.setMessage("Consultando...");
        progress.show();

        SharedPreferences preferences = getContext().getSharedPreferences("Login", Context.MODE_PRIVATE);
        String email = preferences.getString("email", "");

        String url="http://javieribarra.cl/wsJSON.php?email_trabajador="+email+"";

        jsonObjectRequest=new JsonObjectRequest(Request.Method.GET,url,null,this,this);
        VolleySingleton.getIntanciaVolley(getContext()).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getActivity(), "No se puede conectar "+error.toString(), Toast.LENGTH_LONG).show();
        System.out.println();
        Log.d("ERROR: ", error.toString());
        progress.hide();
    }

    @Override
    public void onResponse(JSONObject response) {
        progress.hide();
        JSONArray json=response.optJSONArray("Perfil");

        try {
            JSONObject jsonObject=null;
            jsonObject=json.getJSONObject(0);

            email.setText(email.getText()+" "+jsonObject.optString("email_trabajador"));
            name.setText(name.getText()+" "+jsonObject.optString("nombre"));
            comunity.setText(comunity.getText()+""+jsonObject.optString("empresa"));
            active.setText(active.getText()+" "+jsonObject.optString("entrada"));
            String rutaImagen = jsonObject.optString("imagen");

            if (rutaImagen!=null){
                cargarImagenWebService(rutaImagen);
            }else{
                profile.setImageResource(R.drawable.img_base);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "No se ha podido establecer conexión con el servidor" +
                    " "+response, Toast.LENGTH_LONG).show();
        }

    }


    private void cargarImagenWebService(String rutaImagen) {

        String urlImagen="http://javieribarra.cl/"+rutaImagen;
        urlImagen=urlImagen.replace(" ","%20");

        ImageRequest imageRequest=new ImageRequest(urlImagen, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                profile.setImageBitmap(response);
            }
        }, 0, 0, ImageView.ScaleType.CENTER, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        VolleySingleton.getIntanciaVolley(getContext()).addToRequestQueue(imageRequest);
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
