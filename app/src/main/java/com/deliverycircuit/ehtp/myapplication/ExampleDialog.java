package com.deliverycircuit.ehtp.myapplication;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class ExampleDialog extends AppCompatDialogFragment {
    private ExampleDialogListener listener;
    public AutoCompleteTextView stopText;
    private List<LocationItem> locationList;
    OkHttpClient client;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog, null);

        client = new OkHttpClient();

        builder.setView(view)
                .setTitle("Ajouter un arrêt")
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String stop = stopText.getText().toString();
                        listener.applyTexts(stop);
                    }
                });

        stopText = view.findViewById(R.id.added_stop);

        stopText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!stopText.getText().toString().isEmpty()) {

                    jsonParse(stopText);
                    AutoCompleteLocationAdapter adapter = new AutoCompleteLocationAdapter(getActivity(), locationList);
                    stopText.setAdapter(adapter);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (ExampleDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement ExampleDialogListener");
        }
    }

    public interface ExampleDialogListener {
        void applyTexts(String stop);
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
                        System.out.println("===========================================");
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

                            System.out.println(title);
                            System.out.println(vicinity);
                            System.out.println(position[0] + " " + position[1]);

                            locationList.add(new LocationItem(title, vicinity, position));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        });
    }
}