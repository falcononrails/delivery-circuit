package com.deliverycircuit.ehtp.myapplication.SaveStop;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.deliverycircuit.ehtp.myapplication.SavedRoutesActivity;

import com.deliverycircuit.ehtp.myapplication.R;
import com.deliverycircuit.ehtp.myapplication.model.*;
import com.deliverycircuit.ehtp.myapplication.database.DatabaseQueryClass;

import java.util.ArrayList;


public class RouteCreateDialogFragment extends DialogFragment {

    private static RouteCreateListener routeCreateListener;


    ArrayList<String> listArrets;
    private String starting_place;
    private String ending_place;

    private EditText nameEditText;
    private Button createButton;
    private Button cancelButton;

    private String nameString = "";

    public RouteCreateDialogFragment() {
        // Required empty public constructor
    }

    public static RouteCreateDialogFragment newInstance(String title, RouteCreateListener listener) {
        routeCreateListener = listener;
        RouteCreateDialogFragment routeCreateDialogFragment = new RouteCreateDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        routeCreateDialogFragment.setArguments(args);

        routeCreateDialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);

        return routeCreateDialogFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_route_create_dialog, container, false);

        nameEditText = view.findViewById(R.id.routeNameEditText);
        createButton = view.findViewById(R.id.createButton);
        cancelButton = view.findViewById(R.id.cancelButton);

        if (getArguments() != null) {
            starting_place = getArguments().getString("starting_place");
            ending_place = getArguments().getString("ending_place");
            listArrets = getArguments().getStringArrayList("listArrets");
        }
        getDialog().setTitle("Enregistrer un chemin");

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                nameString = nameEditText.getText().toString();
                if (!nameString.isEmpty() && !starting_place.isEmpty() && !ending_place.isEmpty() && listArrets.size() > 0) {
                    Route route = new Route(-1, nameString, starting_place, ending_place);

                    DatabaseQueryClass databaseQueryClass = new DatabaseQueryClass(getContext());

                    long route_id = databaseQueryClass.insertRoute(route);

                    if (route_id > 0) {
                        route.setIdRoute(route_id);
                        routeCreateListener.onRouteCreated(route);
                    }

                    for (int i = 0; i < listArrets.size(); i++) {
                        Stop stop = new Stop(-1, listArrets.get(i));
                        long stop_id = databaseQueryClass.insertStop(stop, route_id);

                        if (stop_id > 0) {
                            stop.setIdStop(stop_id);
                        }
                    }
                    getDialog().dismiss();
                    Intent intent = new Intent(getActivity().getApplicationContext(), SavedRoutesActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "Avant d'enregistrer le chemin, veuillez spécifier son nom, et les points d'arrêts.", Toast.LENGTH_LONG).show();
                }


            }

        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

}

