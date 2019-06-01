package com.deliverycircuit.ehtp.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.deliverycircuit.ehtp.myapplication.database.DatabaseQueryClass;
import com.deliverycircuit.ehtp.myapplication.model.Route;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

public class CheminEnregistreAdapter extends RecyclerView.Adapter<CustomViewHolder> {

    private Context context;
    private List<Route> routeList;
    private DatabaseQueryClass databaseQueryClass;

    public CheminEnregistreAdapter(Context context, List<Route> studentList) {
        this.context = context;
        this.routeList = studentList;
        databaseQueryClass = new DatabaseQueryClass(context);
        Logger.addLogAdapter(new AndroidLogAdapter());
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chemin, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        final int itemPosition = position;
        final Route route = routeList.get(position);

        holder.nameTextView.setText(route.getNameRoute());
        holder.arriveTextView.setText(String.valueOf(route.getEndStop()));
        holder.departTextView.setText(route.getStartStop());

        holder.crossButtonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setMessage("Voulez-vous supprimer ce chemin?");
                alertDialogBuilder.setPositiveButton("Oui",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                deleteStudent(itemPosition);
                            }
                        });

                alertDialogBuilder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MapsActivity.class);
                intent.putExtra("starting_place", route.getStartStop());
                intent.putExtra("ending_place", route.getEndStop());
                ArrayList<String> listArret = new ArrayList<String>();
                for (int i = 0; i < databaseQueryClass.getAllStops(route.getIdRoute()).size(); i++) {
                    listArret.add(databaseQueryClass.getAllStops(route.getIdRoute()).get(i).getStopName());
                }
                intent.putExtra("listArret", listArret);
                context.startActivity(intent);
            }
        });
    }

    private void deleteStudent(int position) {
        Route route = routeList.get(position);
        long count = databaseQueryClass.deleteRoute(route.getIdRoute());

        if (count > 0) {
            routeList.remove(position);
            notifyDataSetChanged();
            Toast.makeText(context, "Chemin supprimé avec succès", Toast.LENGTH_LONG).show();
        } else
            Toast.makeText(context, "Chemin non supprimé. Une erreur est survenue!", Toast.LENGTH_LONG).show();

    }

    @Override
    public int getItemCount() {
        return routeList.size();
    }
}
