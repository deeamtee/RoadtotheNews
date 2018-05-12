package com.deeamtee.roadtothenews;

import android.os.AsyncTask;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextClock;
import android.widget.TextView;

import com.deeamtee.roadtothenews.Fragments.BasketFragment;
import com.deeamtee.roadtothenews.Fragments.FootballFragment;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ListView listView;
    private TextView textView;

    BasketFragment basketFragment;
    FootballFragment footballFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);
        textView = (TextView) findViewById(R.id.textView);

        ParseTitle parseTitle = new ParseTitle();
        parseTitle.execute();

        try {
            final HashMap<String, String> hashMap = parseTitle.get();

            final ArrayList<String> arrayList = new ArrayList<>();

            for (Map.Entry entry : hashMap.entrySet()){
                arrayList.add(entry.getKey().toString());
            }

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1, arrayList);

            listView.setAdapter(arrayAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ParseText parseText = new ParseText();
                    parseText.execute(hashMap.get(arrayList.get(position)));
                    try {
                        listView.setVisibility(View.GONE);
                        textView.setText(parseText.get());

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //   setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        footballFragment = new FootballFragment();
        basketFragment = new BasketFragment();
    }

    @Override
    public void onBackPressed() {
        listView.setVisibility(View.VISIBLE);
        textView.setVisibility(View.GONE);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (id == R.id.nav_camera) {
            fragmentTransaction.replace(R.id.container_fragment, footballFragment);

        } else if (id == R.id.nav_gallery) {
            fragmentTransaction.replace(R.id.container_fragment, basketFragment);
        }
        fragmentTransaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

class ParseText extends AsyncTask<String, Void, String>{

    @Override
    protected String doInBackground(String... strings) {
        String str = " ";
        try {
            Document document = Jsoup.connect(strings[0]).get();
            Element element = document.select(".itemFullText").first();
            str = element.text();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return str;
    }

}

    class ParseTitle extends AsyncTask<Void, Void, HashMap<String, String>> {

        @Override
        protected HashMap<String, String> doInBackground(Void... voids) {
            HashMap<String, String> hashMap = new HashMap<>();

            try {
                Document document = Jsoup.connect("http://www.ndu.edu.ua/index.php/ua/vsi-novuni").get();
                Elements elements = document.select(".catItemTitle");
                for (Element element : elements){
                    Element element1 = element.select("a[href]").first();
                    hashMap.put(element.text(), element.attr("abs:href"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return hashMap;
        }

    }

}
