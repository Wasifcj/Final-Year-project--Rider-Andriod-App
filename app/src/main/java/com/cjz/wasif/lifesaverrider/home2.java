package com.cjz.wasif.lifesaverrider;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.github.glomadrian.materialanimatedswitch.MaterialAnimatedSwitch;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class home2 extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener
{
    SupportMapFragment mapFragment;

    private GoogleMap mMap;
    MaterialAnimatedSwitch location_switch;

    GoogleApiClient mGoogleApiClient;
    Location mlastlocation;
    LocationRequest mlocationRequest;
    FirebaseAuth auth;
    Boolean customer_online=false;
    Boolean newMapLocation=false;

    private Boolean request_bol=false;
    private  Marker pickupMarker;
    private Marker DriverLocationMarker;

    private Button cancel_emergency;



    FirebaseDatabase db;

    DatabaseReference users;
    private Button mrequest;

    private LatLng pickuplocation;
    // SupportMapFragment mapFragment;
    //location
    FirebaseAuth newauth;
    FirebaseUser current_user;

    private RadioButton bike,simple,advance;

    private String service;

    boolean MustPickService=false;

    //Driver Cardview

    private CardView driver_cardview;
    private TextView driver_name,driver_contact;
    private CircleImageView driver_profile_image;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);






        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        UpdateNavForCustomer();

        //Maps
        mapFragment= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this );


        //Geo fire
        //   auth = FirebaseAuth.getInstance();
        //   db = FirebaseDatabase.getInstance();
        //   Customer = db.getReference("Customers");

        mrequest=(Button)findViewById(R.id.request_driver);
        cancel_emergency=(Button)findViewById(R.id.cancel_ride);

        //handel for driver cardview
        driver_cardview=(CardView)findViewById(R.id.driver_cardView_id);
        driver_name=(TextView)findViewById(R.id.Driver_name);
        driver_contact=(TextView)findViewById(R.id.Driver_contact);
        driver_profile_image=(CircleImageView)findViewById(R.id.Driver_profile_image);



        //check for ambulance service

        bike=(RadioButton)findViewById(R.id.bike_ambulance);
        simple=(RadioButton)findViewById(R.id.simple_ambulance);
        advance=(RadioButton)findViewById(R.id.advance_ambulance);

        bike.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(bike.isChecked()){

                    service="bike";
                    MustPickService=true;
                }
            }
        });

        simple.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(simple.isChecked()){

                    service="Simple Ambulance";
                    MustPickService=true;
                }

            }
        });

        advance.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(advance.isChecked()){

                    service="Advance Ambulance";
                    MustPickService=true;

                }

            }
        });




        mrequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(customer_online==true){


                    //cancel ride



                    if(request_bol){




                        mrequest.setText("Get Ambulance");

                        request_bol=false;
                        geoQuery.removeAllListeners();
                        driverRefLocation.removeEventListener(driverLocationRefListner);

                        mrequest.setVisibility(View.VISIBLE);


                        if(driverFoundID !=null){

                           DatabaseReference Ref= FirebaseDatabase.getInstance().getReference("Users").child(driverFoundID);

                           Ref.child("Customer Rider ID").removeValue();

                           DatabaseReference tracking=FirebaseDatabase.getInstance().getReference("Tracking").child(driverFoundID);
                           tracking.removeValue();



                            driverFoundID=null;



                        }
                        driverFound=false;
                        radius=1;

                        String User_ID= FirebaseAuth.getInstance().getCurrentUser().getUid();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Customer Request");

                        GeoFire geoFire = new GeoFire(ref);
                        geoFire.removeLocation(User_ID);

                        //remove pickup location
                        if(pickupMarker != null){
                            Toast.makeText(home2.this, "Emergence Canceled Successfully", Toast.LENGTH_SHORT).show();
                            pickupMarker.remove();
                            driver_cardview.setVisibility(View.GONE);
                        }
                        //remover driver location;
                        if(mDriverMarker !=null){

                            mDriverMarker.remove();
                            newMapLocation=false;


                        }



                    }else{
                        if(MustPickService==true){
                        request_bol=true;

                        newMapLocation=true;

                        String User_ID = FirebaseAuth.getInstance().getCurrentUser().
                                getUid();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Customer Request");

                        GeoFire geoFire = new GeoFire(ref);
                        geoFire.setLocation(User_ID, new GeoLocation(mlastlocation.getLatitude(), mlastlocation.getLongitude()));

                        pickuplocation = new LatLng(mlastlocation.getLatitude(), mlastlocation.getLongitude());
                       pickupMarker= mMap.addMarker(new MarkerOptions().position(pickuplocation).title("Pickup Here"));

                        mrequest.setText("Finding Ambulance...");
                        mrequest.setTextColor(getResources().getColor(R.color.basePressColor));
                        mrequest.setBackgroundColor(getResources().getColor(R.color.crimson));

                        getcloserDriver();



                    }else {

                            Toast.makeText(home2.this, "Please select first the ambulance type! ", Toast.LENGTH_SHORT).show();
                        }

                    }



                }
                else {
                    Toast.makeText(home2.this, "You are not online", Toast.LENGTH_SHORT).show();

                }


            }
        });








        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(home2.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        } else {
            mapFragment.getMapAsync(this);
        }

        startService(new Intent(home2.this,OnAppKilled.class));
        location_switch = (MaterialAnimatedSwitch) findViewById(R.id.location_switch);

        location_switch.setOnCheckedChangeListener(new MaterialAnimatedSwitch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(boolean isChecked) {
                if (isChecked) {

                    ConnectDriver();
                    customer_online=true;
                    Toast.makeText(home2.this, "You are online", Toast.LENGTH_SHORT).show();

                    mrequest.setVisibility(View.VISIBLE);



                } else {

                    disconnectDriver();
                    Toast.makeText(home2.this, "You are offline", Toast.LENGTH_SHORT).show();
                   // mrequest.setBackgroundColor(getResources().getColor(R.color.BallReleaseColor));
                 //   mrequest.setTextColor(getResources().getColor(R.color.baseReleaseColor));
                 //   mrequest.setText("Get Driver");
                    mrequest.setVisibility(View.INVISIBLE);
                    cancel_emergency.setVisibility(View.INVISIBLE);


                    customer_online=false;
                }

            }
        });









    }



    // finding the driver
    private int radius=1;
    private Boolean driverFound=false;
    private String  driverFoundID;
    GeoQuery geoQuery;

    private void getcloserDriver() {

        final DatabaseReference driverLocation= FirebaseDatabase.getInstance().getReference().child("Driver available");
        GeoFire geoFire=new GeoFire(driverLocation);

        geoQuery=geoFire.queryAtLocation(new GeoLocation(pickuplocation.latitude,pickuplocation.longitude),radius);

        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {

                if(!driverFound && request_bol){

                  DatabaseReference customerDatabase=FirebaseDatabase.getInstance().getReference().child("Users").child(key);

                  customerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                      @Override
                      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                          if(dataSnapshot.exists()&& dataSnapshot.getChildrenCount()>0){

                           Map<String,Object> drivermap=(Map<String, Object>)dataSnapshot.getValue();

                           if(driverFound){
                               return;

                           }
                           if(drivermap.get("Service").equals(service)){

                               driverFound=true;
                               driverFoundID =dataSnapshot.getKey();

                               DatabaseReference driverRef= FirebaseDatabase.getInstance().getReference().child("Users").child(driverFoundID);
                               String customer_ID=FirebaseAuth.getInstance().getCurrentUser().getUid();
                               HashMap map = new HashMap();
                               map.put("Customer Rider ID",customer_ID);
                               driverRef.updateChildren(map);
                               getDriverInfo();
                               getDriverLocation();



                           }

                          }



                      }

                      @Override
                      public void onCancelled(@NonNull DatabaseError databaseError) {

                      }
                  });










                }

            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

                if(!driverFound){

                    radius++;
                    //Recursive Method calling itself Until driver found....

                   if(radius!=8){ // Searching area radius
                    getcloserDriver();
                }else{

                       request_bol=false;


                       mrequest.setVisibility(View.VISIBLE);
                       String User_ID= FirebaseAuth.getInstance().getCurrentUser().getUid();
                       DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Customer Request");

                       GeoFire geoFire = new GeoFire(ref);
                       geoFire.removeLocation(User_ID);
                       mrequest.setText("Get Ambulance");
                       Toast.makeText(home2.this, "Please try Again no Ambulance found!", Toast.LENGTH_SHORT).show();
                       pickupMarker.remove();
                       newMapLocation=false;
                       radius=1;
                   }

                }

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

    }

    private void getDriverInfo() {

        driver_cardview.setVisibility(View.VISIBLE);
        auth=FirebaseAuth.getInstance();
        // user_id=FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference  mCustomerDataBase= FirebaseDatabase.getInstance().getReference().child("Users").child(driverFoundID);


        //tracking
        if(driverFoundID!=null){

            DatabaseReference drivers=FirebaseDatabase.getInstance().getReference().child("Users").child(driverFoundID);
            drivers.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()&& dataSnapshot.getChildrenCount()>0){
                        Map<String,Object> map=(Map<String, Object>)dataSnapshot.getValue();
                        if(map.get("name")!=null && map.get("phone")!=null ) {
                        final   String  driver_namee = map.get("name").toString();
                        final   String  driver_contactee=map.get("phone").toString();
                        final String    driver_profilee=map.get("profileImageUrl").toString();



                            DatabaseReference drivertracking=FirebaseDatabase.getInstance().getReference("Tracking").child(driverFoundID);
                            Map DriverInfo=new HashMap();
                            DriverInfo.put("DriverID",driverFoundID);

                            DriverInfo.put("name",driver_namee);
                            DriverInfo.put("phone",driver_contactee);
                            DriverInfo.put("profileImageUrl",driver_profilee);


                            drivertracking.updateChildren(DriverInfo);

                        }
                      //  if(map.get("phone")!=null) {
                       //     driver_contact = map.get("phone").toString();
                         //   Driver_contact_edit.setText(driver_contact);
                      //  }

                      //  if(map.get("profileImageUrl")!=null) {
                       //     Driver_profile_image = map.get("profileImageUrl").toString();
                         //   Glide.with(getApplicationContext()).load(Driver_profile_image).into(Driver_profile);

                        }

                    }

               // }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



        }

        mCustomerDataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()&& dataSnapshot.getChildrenCount()>0){
                    Map<String,Object> map=(Map<String, Object>)dataSnapshot.getValue();
                    if(map.get("name")!=null) {
                        String  driver_name_store = map.get("name").toString();
                        driver_name.setText("Driver Name: "+driver_name_store);
                    }
                    if(map.get("phone")!=null) {
                        String   driver_contact_store = map.get("phone").toString();
                        driver_contact.setText("Driver Contact# "+driver_contact_store);
                    }

                    if(map.get("profileImageUrl")!=null) {
                        String   Driver_profile_image_store = map.get("profileImageUrl").toString();
                        Glide.with(getApplicationContext()).load(Driver_profile_image_store).into(driver_profile_image);

                    }

                    if(map.get("Customer Rider ID")==null){

                        request_bol=false;
                        radius=1;
                        driver_cardview.setVisibility(View.GONE);
                        mDriverMarker.remove();
                        pickupMarker.remove();
                        newMapLocation=false;
                        driverFound=false;

                        mrequest.setText("GET AMBULANCE");

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }







    private Marker mDriverMarker;

    private DatabaseReference driverRefLocation;

    private  ValueEventListener  driverLocationRefListner;

    private void getDriverLocation() {
        if (customer_online == true){
            mrequest.setBackgroundColor(R.color.crimson);
            driverRefLocation = FirebaseDatabase.getInstance().getReference().child("Driver Working").child(driverFoundID).child("l");
        driverLocationRefListner = driverRefLocation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && request_bol) {

                    final List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLng = 0;
                    mrequest.setBackgroundColor(R.color.crimson);
                    mrequest.setText("Cancel Emergency");
                    if (map.get(0) != null) {

                        locationLat = Double.parseDouble(map.get(0).toString());

                    }
                    if (map.get(1) != null) {

                        locationLng = Double.parseDouble(map.get(1).toString());

                    }

                    final LatLng driverLatlng = new LatLng(locationLat, locationLng);

                    if (mDriverMarker != null) {


                        mDriverMarker.remove();
                    }
                    Location loc1 = new Location("");
                    loc1.setLatitude(pickuplocation.latitude);
                    loc1.setLongitude(pickuplocation.longitude);

                    Location loc2 = new Location("");
                    loc2.setLatitude(driverLatlng.latitude);
                    loc2.setLongitude(driverLatlng.longitude);

                    double distance = loc1.distanceTo(loc2);
                    double Kilometers = distance * 0.001;
                    final double valueRounded = Math.round(Kilometers * 100D) / 100D;
                    if(valueRounded>0.03) {



                    }
                    else{

                     //   Toast.makeText(home2.this, "Ambulance Arrived", Toast.LENGTH_SHORT).show();


                      //  mrequest.setText("Ambulance Coming ");
                    }
                    //Cancel emergency with button
                    if(driverFoundID !=null){

                       // cancel_emergency.setVisibility(View.VISIBLE);
                        //cancel_emergency.setTextColor();
                    }


                    mDriverMarker = mMap.addMarker(new MarkerOptions().position(driverLatlng).title("Ambulance" + "\n" + "Total remaining distance" + "(" + valueRounded + ")" + "KM").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ambulancen)));
                    mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                        @Override
                        public void onCameraMove() {
                          //  newMapLocation=true;
                            if(newMapLocation==true){

                               // CameraPosition cameraPosition= new CameraPosition(driverLatlng,15f,15f,0f);
                           // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(driverLatlng,17));
                             //   CameraPosition cameraPosition= new CameraPosition(driverLatlng,15,0,0);
                             //  CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);

                               // CameraPosition cameraPosition= new CameraPosition.
                               // CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);

                             //  mMap.animateCamera(cameraUpdate);

                                mMap.moveCamera(CameraUpdateFactory.newLatLng(driverLatlng));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                                mMap.setMyLocationEnabled(true);
                                mMap.getUiSettings().setZoomControlsEnabled(true);



                            }
                        }
                    });




                    mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                        @Override
                        public View getInfoWindow(Marker marker) {
                            return null;
                        }

                        @Override
                        public View getInfoContents(Marker marker) {
                            Context mContext = getApplicationContext();
                            LinearLayout info = new LinearLayout(mContext);
                            info.setOrientation(LinearLayout.VERTICAL);

                            TextView title = new TextView(mContext);
                            title.setTextColor(Color.BLACK);
                            title.setGravity(Gravity.CENTER);
                            title.setTypeface(null, Typeface.BOLD);
                            title.setText(marker.getTitle());

                            TextView snippet = new TextView(mContext);
                            snippet.setTextColor(Color.GRAY);
                            snippet.setText(marker.getSnippet());

                            info.addView(title);
                            info.addView(snippet);
                            return info;
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }else{
            Toast.makeText(this, "You are not online", Toast.LENGTH_SHORT).show();
        
}
    
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.Logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(home2.this,MainActivity.class));



        } else if (id == R.id.nav_contact) {
            startActivity(new Intent(home2.this,contact_customer.class));

        } else if (id == R.id.nav_editProfile) {
            startActivity(new Intent(home2.this,EditProfileCustomer.class));

        } else if (id == R.id.nav_share) {
            Intent myintent = new Intent(Intent.ACTION_SEND);
            myintent.setType("text/plain");
            String shareBody= "L I F E SAVER";
            String shareSub=  "L I F E SAVER is basically first Android app in Pakistan which gives the online booking Ambulance and you can also facilitate with first aid at your door step! available on play store soon! ";
            myintent.putExtra(Intent.EXTRA_SUBJECT,shareBody);
            myintent.putExtra(Intent.EXTRA_TEXT,shareSub);
            startActivity(Intent.createChooser(myintent,"Share using"));

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;




    }


    public void UpdateNavForCustomer(){

        String id;
        newauth=FirebaseAuth.getInstance();
        id=newauth.getUid();
        current_user=newauth.getCurrentUser();
        users=FirebaseDatabase.getInstance().getReference().child("Customers").child(id);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView=navigationView.getHeaderView(0);
        final TextView customer_name_nav=headerView.findViewById(R.id.nav_Customer_Name);
        TextView customer_email_nav=headerView.findViewById(R.id.nav_Customer_Email);
        final CircleImageView Customer_image_nav=headerView.findViewById(R.id.nav_customer_image);



        customer_email_nav.setText(current_user.getEmail());


        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()&& dataSnapshot.getChildrenCount()>0){
                    Map<String,Object> map=(Map<String, Object>)dataSnapshot.getValue();
                    if(map.get("name")!=null) {
                        String   driver_name = map.get("name").toString();
                        customer_name_nav.setText(driver_name);
                    }

                    if(map.get("profileImageUrl")!=null) {
                        String   Customer_profile_image = map.get("profileImageUrl").toString();
                        Glide.with(getApplicationContext()).load(Customer_profile_image).into(Customer_image_nav);

                    }



                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }


    private void ConnectDriver()

    {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(home2.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mlocationRequest, this);

    }

    private void disconnectDriver() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
        String user_ID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref =FirebaseDatabase.getInstance().getReference("Driver available");

        GeoFire geoFire= new GeoFire(ref);
        geoFire.removeLocation(user_ID);


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);


        // Add a marker in Sydney and move the camera
        // LatLng sydney = new LatLng(-34, 151);
        // mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient= new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();


    }

    @Override
    public void onLocationChanged(Location location) {

        mlastlocation=location;
        LatLng latLng= new LatLng(location.getLatitude(),location.getLongitude());
        if(newMapLocation==false) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
        }
        //String user_ID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //  DatabaseReference ref =FirebaseDatabase.getInstance().getReference("Driver Availible");

        //  GeoFire geoFire= new GeoFire(ref);
        //  geoFire.setLocation(user_ID,new GeoLocation(location.getLatitude(),location.getLongitude()));

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mlocationRequest = new LocationRequest();
        mlocationRequest.setInterval(1000);
        mlocationRequest.setFastestInterval(1000);
        mlocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        //    ActivityCompat.requestPermissions(Welcome.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
        //   }
        //  LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mlocationRequest, this);


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }



    final  int LOCATION_REQUEST_CODE=1;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){

            case LOCATION_REQUEST_CODE:{
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){

                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    mapFragment.getMapAsync(this);


                }
                else {

                    Toast.makeText(this, "Please provide the permission", Toast.LENGTH_SHORT).show();
                }
                break;

                //if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            }
        }

    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            moveTaskToBack(true);

        }
    }
}
