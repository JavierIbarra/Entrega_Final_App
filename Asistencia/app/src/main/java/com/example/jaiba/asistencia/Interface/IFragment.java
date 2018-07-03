package com.example.jaiba.asistencia.Interface;

import com.example.jaiba.asistencia.Fragment.AssistanceFragment;
import com.example.jaiba.asistencia.Fragment.ListParticipantsFragment;
import com.example.jaiba.asistencia.Fragment.MapFragment;
import com.example.jaiba.asistencia.Fragment.ProfileFragment;

public interface IFragment extends ListParticipantsFragment.OnFragmentInteractionListener,
        AssistanceFragment.OnFragmentInteractionListener,
        MapFragment.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener{
}
