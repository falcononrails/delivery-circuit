package com.deliverycircuit.ehtp.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.deliverycircuit.ehtp.myapplication.database.DatabaseQueryClass;
import com.deliverycircuit.ehtp.myapplication.model.Route;

import java.util.ArrayList;
import java.util.List;

public class SavedRoutesActivity extends AppCompatActivity {

    private DatabaseQueryClass databaseQueryClass = new DatabaseQueryClass(this);
    private List<Route> routesList = new ArrayList<Route>();
    private RecyclerView recyclerView;
    private CheminEnregistreAdapter cheminEnregistreAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_routes);
        recyclerView = findViewById(R.id.my_recycler_view);
        routesList.addAll(databaseQueryClass.getAllRoutes());
        cheminEnregistreAdapter = new CheminEnregistreAdapter(this,routesList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(cheminEnregistreAdapter);

    }
}
