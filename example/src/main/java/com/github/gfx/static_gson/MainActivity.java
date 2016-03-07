package com.github.gfx.static_gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.bluelinelabs.logansquare.LoganSquare;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    static final int N = 2000;

    final Gson dynamicGson = new GsonBuilder()
            .create();

    final Gson staticGson = new GsonBuilder()
            .registerTypeAdapterFactory(new StaticGsonTypeAdapterFactory())
            .create();

    final Moshi moshi = new Moshi.Builder()
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                start("Dynamic Gson", dynamicGson);
                start("Static Gson", staticGson);
                start("Moshi", moshi);
                start("LoganSquare");
            }
        }, 1000);
    }

    void start(final String label, final Gson gson) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Session session = new Session();
                String serialized = gson.toJson(session);
                System.gc();

                Log.d("XXX", "start benchmarking " + label);

                long t0 = System.currentTimeMillis();
                for (int i = 0; i < N; i++) {
                    @SuppressWarnings("unused")
                    String s = gson.toJson(session);
                }
                long elapsed = System.currentTimeMillis() - t0;
                Log.d("XXX", label + " in serialization: " + elapsed + "ms");
                System.gc();

                t0 = System.currentTimeMillis();
                for (int i = 0; i < N; i++) {
                    @SuppressWarnings("unused")
                    Session s = gson.fromJson(serialized, Session.class);
                }
                elapsed = System.currentTimeMillis() - t0;
                Log.d("XXX", label + " in deserialization: " + elapsed + "ms");
                return null;
            }
        }.execute();
    }

    void start(final String label, final Moshi moshi) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                JsonAdapter<Session> adapter = moshi.adapter(Session.class);
                Session session = new Session();
                String serialized = adapter.toJson(session);
                System.gc();

                Log.d("XXX", "start benchmarking " + label);

                long t0 = System.currentTimeMillis();
                for (int i = 0; i < N; i++) {
                    @SuppressWarnings("unused")
                    String s = adapter.toJson(session);
                }
                long elapsed = System.currentTimeMillis() - t0;
                Log.d("XXX", label + " in serialization: " + elapsed + "ms");
                System.gc();

                t0 = System.currentTimeMillis();
                for (int i = 0; i < N; i++) {
                    try {
                        @SuppressWarnings("unused")
                        Session s = adapter.fromJson(serialized);
                    } catch (IOException e) {
                        throw new AssertionError(e);
                    }
                }
                elapsed = System.currentTimeMillis() - t0;
                Log.d("XXX", label + " in deserialization: " + elapsed + "ms");
                return null;
            }
        }.execute();
    }

    void start(final String label) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Session session = new Session();
                String serialized;
                try {
                    serialized = LoganSquare.serialize(session);
                } catch (IOException e) {
                    throw new AssertionError(e);
                }
                System.gc();

                Log.d("XXX", "start benchmarking " + label);

                long t0 = System.currentTimeMillis();
                for (int i = 0; i < N; i++) {
                    try {
                        @SuppressWarnings("unused")
                        String s = LoganSquare.serialize(session);
                    } catch (IOException e) {
                        throw new AssertionError(e);
                    }
                }
                long elapsed = System.currentTimeMillis() - t0;
                Log.d("XXX", label + " in serialization: " + elapsed + "ms");
                System.gc();

                t0 = System.currentTimeMillis();
                for (int i = 0; i < N; i++) {
                    try {
                        @SuppressWarnings("unused")
                        Session s = LoganSquare.parse(serialized, Session.class);
                    } catch (IOException e) {
                        throw new AssertionError(e);
                    }
                }
                elapsed = System.currentTimeMillis() - t0;
                Log.d("XXX", label + " in deserialization: " + elapsed + "ms");
                return null;
            }
        }.execute();
    }

}
