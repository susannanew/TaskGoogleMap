package com.example.task;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    SupportMapFragment smf;
    FusedLocationProviderClient client;
    EditText YourLocation , destination;
    ImageButton menu;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    RecyclerView recyclerView;
    FirebaseFirestore db;
    ArrayList<SourceLocation> sourceLocationArrayList;
    MyAdabter myAdabter;
    RelativeLayout relativeLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        menu = findViewById(R.id.menuBtn);
        drawerLayout = findViewById(R.id.drawerLayout);
        YourLocation = findViewById(R.id.source);
        destination = findViewById(R.id.destination);
        smf = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);
        client = LocationServices.getFusedLocationProviderClient(this);
        navigationView=findViewById(R.id.nav_view);
        relativeLayout = findViewById(R.id.relative);

        recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        db = FirebaseFirestore.getInstance();
        sourceLocationArrayList = new ArrayList<SourceLocation>();
        myAdabter= new MyAdabter(MainActivity.this, sourceLocationArrayList, new MyAdabter.ItemClickListener() {
            @Override
            public void onItemClick(SourceLocation sourceLocation) {
                showToast(sourceLocation.getName() + "Clicked! ");
                getPlace(sourceLocation.getLatitude(), sourceLocation.getLongitude() , sourceLocation.getName());
                relativeLayout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
            }
        });



        Dexter.withContext(getApplicationContext())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        getmylocation();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                switch (item.getItemId()){
                case   R.id.firstItem:
                    {
                    Log.e("TAG", "onNavigationItemSelected:FFFF " );
                    Toast.makeText(getApplication(), "You Clicked First Item", Toast.LENGTH_SHORT).show();
                    break;
                    }
                case  R.id.secondItem :
                    {
                    Toast.makeText(getApplication(), "You Clicked Second Item", Toast.LENGTH_SHORT).show();
                    break;
                    }
                case  R.id.thirdItem :
                    {
                    Toast.makeText(getApplication(), "You Clicked Third Item", Toast.LENGTH_SHORT).show();
                    break;
                    }
            }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        recyclerView.setAdapter(myAdabter);
        EventChangeListener();

        YourLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                recyclerView.setVisibility(View.VISIBLE);
                relativeLayout.setVisibility(View.VISIBLE);
            }
        });

        YourLocation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b){
                    recyclerView.setVisibility(View.GONE);
                    relativeLayout.setVisibility(View.GONE);
                }
            }
        });

        destination.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
               recyclerView.setVisibility(View.VISIBLE);
               relativeLayout.setVisibility(View.VISIBLE);
            }
        });

        destination.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b){
                    recyclerView.setVisibility(View.GONE);
                    relativeLayout.setVisibility(View.GONE);
                }
            }
        });

    }


    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    private void EventChangeListener() {
        db.collection("Source").orderBy("name" , Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null){
                            Log.e("FireStore error", error.getMessage() );
                            return;
                        }

                        for (DocumentChange dc : value.getDocumentChanges()){
                            if (dc.getType() == DocumentChange.Type.ADDED){
                                sourceLocationArrayList.add(dc.getDocument().toObject(SourceLocation.class));
                            }
                            myAdabter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void getPlace(double location, double location2 , String name){
        smf.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                LatLng latLng=new LatLng(location, location2);

                MarkerOptions markerOptions=new MarkerOptions().position(latLng).title(name);

                googleMap.addMarker(markerOptions);
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));

            }
        });

    }


    public void getmylocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(final Location location) {
                smf.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
                        MarkerOptions markerOptions=new MarkerOptions().position(latLng).title("You are here...!!");

                        googleMap.addMarker(markerOptions);
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));
                    }
                });
            }
        });
    }


}