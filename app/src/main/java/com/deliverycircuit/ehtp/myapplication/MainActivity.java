package com.deliverycircuit.ehtp.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.AutoCompleteTextView;

import com.deliverycircuit.ehtp.myapplication.model.*;

import com.deliverycircuit.ehtp.myapplication.SaveStop.*;
import com.deliverycircuit.ehtp.myapplication.Util.*;

import static android.widget.AdapterView.*;

import android.view.Menu;

import android.content.DialogInterface;

import java.util.ArrayList;
import java.util.List;

import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ExampleDialog.ExampleDialogListener, RouteCreateListener {

    private List<LocationItem> locationList;
    private LinearLayout parentLinearLayout;
    public AutoCompleteTextView starting_place;
    public AutoCompleteTextView ending_place;

    private ListView listViewItem;
    private Button addStop;
    MyAdapter adapter;
    ArrayList<String> itemList;
    OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listViewItem = (ListView) findViewById(R.id.listitem);
        addStop = (Button) findViewById(R.id.add_stop);
        itemList = new ArrayList<String>();
        adapter = new MyAdapter(this.getApplicationContext(), itemList);
        ListView listV = (ListView) findViewById(R.id.listitem);
        listV.setAdapter(adapter);

        listV.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> a, View v, int position, long id) {
                AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
                adb.setTitle("Supprimer un arrêt");
                adb.setMessage("Voulez-vous supprimer l'arrêt ?");
                final int positionToRemove = position;
                adb.setNegativeButton("Annuler", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        itemList.remove(positionToRemove);
                        adapter.notifyDataSetChanged();
                    }
                });
                adb.show();
                return false;
            }
        });

        addStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemList.size() < 6)
                    openDialog();
                else
                    Toast.makeText(getApplicationContext(), "Nombre maximum d'arrêt atteint", Toast.LENGTH_LONG).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        client = new OkHttpClient();

        starting_place = findViewById(R.id.start_input);

        ending_place = findViewById(R.id.end_input);

        starting_place.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!starting_place.getText().toString().isEmpty()) {

                    jsonParse(starting_place);
                    AutoCompleteLocationAdapter adapter = new AutoCompleteLocationAdapter(getApplicationContext(), locationList);
                    starting_place.setAdapter(adapter);

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ending_place.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!ending_place.getText().toString().isEmpty()) {

                    jsonParse(ending_place);
                    AutoCompleteLocationAdapter adapter = new AutoCompleteLocationAdapter(getApplicationContext(), locationList);
                    ending_place.setAdapter(adapter);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        parentLinearLayout = (LinearLayout) findViewById(R.id.list_linear_layout);
    }

    /* ------------------------- End of onCreate -------------------- */

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            openRouteSaveDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            startActivity(new Intent(MainActivity.this, SavedRoutesActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void openDialog() {
        final ExampleDialog exampleDialog = new ExampleDialog();
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }

    @Override
    public void applyTexts(String stop) {
        if (!stop.isEmpty()) {
            itemList.add(stop);
            adapter.notifyDataSetChanged();
        }
    }

    private void jsonParse(AutoCompleteTextView place) {

        String url = "https://places.cit.api.here.com/places/v1/autosuggest?" +
                "app_id=N8JglbCybLCFfme63WAE" +
                "&app_code=r4rXKbsVEFpODcigDYfY7g" +
                "&in=33.573109,-7.589843;r=40000" +
                "&q=" + place.getText().toString() +
                "&pretty";

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        locationList = new ArrayList<>();
                        final String myResponse = response.body().string();
                        JSONObject Jobject = new JSONObject(myResponse);
                        JSONArray Jresults = Jobject.getJSONArray("results");

                        for (int i = 0; i < Jresults.length(); i++) {
                            JSONObject object = Jresults.getJSONObject(i);
                            String title = object.getString("title");
                            String vicinity = object.getString("vicinity");

                            //position_parsing
                            String raw_position = object.getString("position");

                            String[] str_position = raw_position
                                    .replace("[", "")
                                    .replace("]", "")
                                    .split(",");

                            double[] position = {Double.parseDouble(str_position[0]), Double.parseDouble(str_position[1])};

                            locationList.add(new LocationItem(title, vicinity, position));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        });
    }

    private void getPosition(final String stop, final ArrayList<Double> position_x, final ArrayList<Double> position_y) {
        String url = "https://places.cit.api.here.com/places/v1/autosuggest?" +
                "app_id=N8JglbCybLCFfme63WAE" +
                "&app_code=r4rXKbsVEFpODcigDYfY7g" +
                "&in=33.573109,-7.589843;r=40000" +
                "&q=" + stop +
                "&pretty";

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        final String myResponse = response.body().string();
                        JSONObject Jobject = new JSONObject(myResponse);
                        JSONArray Jresults = Jobject.getJSONArray("results");

                        JSONObject object = Jresults.getJSONObject(0);

                        //position_parsing
                        String raw_position = object.getString("position");


                        String[] str_position = raw_position
                                .replace("[", "")
                                .replace("]", "")
                                .split(",");

                        position_x.add(Double.parseDouble(str_position[0]));
                        position_y.add(Double.parseDouble(str_position[1]));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        });

    }

    public void onGo(View v) {
        if (!starting_place.getText().toString().isEmpty() && !ending_place.getText().toString().isEmpty() && itemList.size() > 0) {
            ArrayList<Double> position_x = new ArrayList<Double>();
            ArrayList<Double> position_y = new ArrayList<Double>();

            getPosition(starting_place.getText().toString(), position_x, position_y);

            for (int i = 0; i < itemList.size(); i++) {
                getPosition(itemList.get(i), position_x, position_y);
            }

            getPosition(ending_place.getText().toString(), position_x, position_y);

            Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
            intent.putExtra("listArret", itemList);
            intent.putExtra("starting_place", starting_place.getText().toString());
            intent.putExtra("ending_place", ending_place.getText().toString());

            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "Il faut spécifier les points de départ, d'arrivée, et les arrêts souhaités.", Toast.LENGTH_LONG).show();
        }

    }

    private void openRouteSaveDialog() {
        Bundle bundle = new Bundle();
        bundle.putString("starting_place", starting_place.getText().toString());
        bundle.putString("ending_place", ending_place.getText().toString());
        bundle.putStringArrayList("listArrets", itemList);
        RouteCreateDialogFragment routeCreateDialogFragment = RouteCreateDialogFragment.newInstance("Enregistrer chemin", this);
        routeCreateDialogFragment.setArguments(bundle);
        routeCreateDialogFragment.show(getSupportFragmentManager(), Config.CREATE_TABLE_ROUTE);
    }


    @Override
    public void onRouteCreated(Route route) {
    }

    public void onDelete(View v) {
        parentLinearLayout.removeView((View) v.getParent());
    }
}

class MyAdapter extends ArrayAdapter<String> {

    Context context;
    ArrayList<String> mitemList;

    MyAdapter(Context c, ArrayList<String> itemList) {
        super(c, R.layout.item_listview, R.id.stopÎtem, itemList);
        context = c;
        mitemList = itemList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.item_listview, parent, false);
        TextView myStop = row.findViewById(R.id.stopÎtem);

        // now set our resources on views
        myStop.setText(mitemList.get(position));
        return row;
    }
}

