package com.example.andrew.chatsystem;

import android.content.Context;
import android.content.Intent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity   implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference rootRef;
    private FirebaseUser currentUser ;

    private FloatingActionButton fab ;

    private String currentUserID ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeFields();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent findDoctorIntent = new Intent(MainActivity.this, FindDoctorActivity.class);
                findDoctorIntent.putExtra("Doctor_Speciality" , "NULL");
                startActivity(findDoctorIntent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() != NetworkInfo.State.CONNECTED &&
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() != NetworkInfo.State.CONNECTED)
        {

            getSupportActionBar().setIcon(R.drawable.ic_warning_black_24dp);
            getSupportActionBar().setTitle("   No connection");

            /*Toast toast = Toast.makeText(this, "NO internet connection ! some features will be not available",
                    Toast.LENGTH_LONG);

            toast.getView().setBackgroundColor(Color.rgb(100,100,100));
            TextView view = (TextView) toast.getView().findViewById(android.R.id.message);
            view.setTextColor(Color.WHITE);

            toast.show();*/
        }
        else
        {
            getSupportActionBar().setIcon(null);
            getSupportActionBar().setTitle("Consultation chat");
        }

        if (currentUser == null)
            SendUserToLogInActivity();

        else {
            updateUserStatus("online");
            verifyUserExistence();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null)
            updateUserStatus("offline");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null)
            updateUserStatus("offline");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null)
            updateUserStatus("offline");

        finish();
        moveTaskToBack(true) ;
    }

    private void initializeFields() {

        Toolbar mtoolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Consultation chat");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mtoolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        fab = (FloatingActionButton) findViewById(R.id.fab);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View view= navigationView.getHeaderView(0);
        TextView title=(TextView)view.findViewById(R.id.navtile);
        title.setText("old value");
        navigationView.setNavigationItemSelectedListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();
        currentUser = firebaseAuth.getCurrentUser();
        currentUserID = currentUser.getUid() ;

        ViewPager myViewPager = (ViewPager) findViewById(R.id.main_tabs_pager);
        TabsAccessorAdapter myTabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabsAccessorAdapter);

        TabLayout myTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);
    }

    private void verifyUserExistence() {
        //final String currentUserID = firebaseAuth.getCurrentUser().getUid();
        rootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (!(dataSnapshot.child("uName").exists()))
                    {
                        SendUserToSettingsActivity();
                        overridePendingTransition(R.anim.fade_in , R.anim.fade_out);
                    }

                    else if (!(dataSnapshot.hasChild("Medical History")))
                    {
                        SendUserToMHActivity();
                        overridePendingTransition(R.anim.fade_in , R.anim.fade_out);
                    }

                    else
                    {
                        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                        View view= navigationView.getHeaderView(0);

                        if(dataSnapshot.hasChild("image"))
                        {
                            CircleImageView navImage = (CircleImageView)view.findViewById(R.id.nav_user_image);
                            Picasso.get().load(dataSnapshot.child("image").getValue().toString())
                                    .error(R.drawable.no_image).placeholder(R.drawable.profile_image).into(navImage);
                        }

                        TextView title=(TextView)view.findViewById(R.id.navtile);
                        title.setText(dataSnapshot.child("uName").getValue().toString().trim());

                        TextView subTitle=(TextView)view.findViewById(R.id.navsubtitle);
                        subTitle.setText(dataSnapshot.child("uStatus").getValue().toString().trim());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SendUserToMHActivity() {
        Intent intent = new Intent(MainActivity.this, MedicalHistory.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("currentUserID" , firebaseAuth.getCurrentUser().getUid());
        intent.putExtra("has_MH","no");
        startActivity(intent);
    }

    private void SendUserToLogInActivity() {
        Intent intent = new Intent(MainActivity.this, log_in.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void SendUserToSettingsActivity() {
        Intent intent = new Intent(MainActivity.this, SettingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.menuItem_findDoctor:
                Intent findDoctorIntent = new Intent(MainActivity.this, FindDoctorActivity.class);
                findDoctorIntent.putExtra("Doctor_Speciality" , "NULL");
                startActivity(findDoctorIntent);
                return true;

            case R.id.menuItem_findFamily:
                startActivity(new Intent(MainActivity.this , FindFriendsActivity.class));
                return true;

            case R.id.menuItem_settings:
                Intent settingIntent = new Intent(MainActivity.this, MainSettings.class) ;
                settingIntent.putExtra("sUserID" ,currentUserID);
                startActivity(settingIntent);
                return true;

            case R.id.menuItem_LogOut:
                updateUserStatus("offline");
                firebaseAuth.signOut();
                Toast.makeText(MainActivity.this, "consider as a pleasure to serve you", Toast.LENGTH_SHORT).show();
                SendUserToLogInActivity();
                return true;

            default:
                return false;
        }

    }

    private void updateUserStatus(String state) {
        String currentTime, currentDate;
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        currentDate = dateFormat.format(calendar.getTime());

        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        currentTime = timeFormat.format(calendar.getTime());

        HashMap<String, Object> onlineStateMap = new HashMap<>();
        onlineStateMap.put("time", currentTime);
        onlineStateMap.put("date", currentDate);
        onlineStateMap.put("state", state);

        String currentUserID = firebaseAuth.getCurrentUser().getUid();

        rootRef.child("Users").child(currentUserID).child("userState")
                .updateChildren(onlineStateMap);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        switch (item.getItemId()) {
            case R.id.location:
               startActivity(new Intent(MainActivity.this, InfoWindowSymbolLayerActivity.class));
                break;

            case R.id.diagnose:
                startActivity(new Intent(MainActivity.this, DiagnosisInput.class));
                break;

            case R.id.history:
                Intent intent = new Intent(MainActivity.this , PreviewMHActivity.class);
                intent.putExtra("userID_MH" ,currentUserID);
                startActivity(intent);
                break;

            case R.id.settings:
                Intent intent1 = new Intent(MainActivity.this, MainSettings.class);
                intent1.putExtra("sUserID" ,currentUserID);
                startActivity(intent1);
                break;

            case R.id.first:
                startActivity(new Intent(MainActivity.this , Aids_List.class));
                break;

            case R.id.edit:
                startActivity(new Intent(MainActivity.this, SettingActivity.class) );
                break;

            case R.id.logout:
                updateUserStatus("offline");
                firebaseAuth.signOut();
                Toast.makeText(MainActivity.this, "consider as a pleasure to serve you", Toast.LENGTH_SHORT).show();
                SendUserToLogInActivity();
                break;
                //-------------------------------------------------------------------------------------------

            case R.id.Internal:
                Intent findDoctorIntent1 = new Intent(MainActivity.this, FindDoctorActivity.class);
                findDoctorIntent1.putExtra("Doctor_Speciality" , "Internal Medicine");
                startActivity(findDoctorIntent1);
                break;

            case R.id.Dermatology:
                Intent findDoctorIntent2 = new Intent(MainActivity.this, FindDoctorActivity.class);
                findDoctorIntent2.putExtra("Doctor_Speciality" , "Dermatology");
                startActivity(findDoctorIntent2);
                break;

            case R.id.Allergy:
                Intent findDoctorIntent3 = new Intent(MainActivity.this, FindDoctorActivity.class);
                findDoctorIntent3.putExtra("Doctor_Speciality" , "Allergy and Immunology");
                startActivity(findDoctorIntent3);
                break;

            case R.id.Obstetrics:
                Intent findDoctorIntent4 = new Intent(MainActivity.this, FindDoctorActivity.class);
                findDoctorIntent4.putExtra("Doctor_Speciality" , "Obstetrics and Gynecology");
                startActivity(findDoctorIntent4);
                break;

            case R.id.Family:
                Intent findDoctorIntent5 = new Intent(MainActivity.this, FindDoctorActivity.class);
                findDoctorIntent5.putExtra("Doctor_Speciality" , "Family Medicine");
                startActivity(findDoctorIntent5);
                break;

            case R.id.Ophthalmology:
                Intent findDoctorIntent6 = new Intent(MainActivity.this, FindDoctorActivity.class);
                findDoctorIntent6.putExtra("Doctor_Speciality" , "Ophthalmology");
                startActivity(findDoctorIntent6);
                break;

            case R.id.Physical:
                Intent findDoctorIntent7 = new Intent(MainActivity.this, FindDoctorActivity.class);
                findDoctorIntent7.putExtra("Doctor_Speciality" , "Physical Medicine");
                startActivity(findDoctorIntent7);
                break;

            case R.id.Dental:
                Intent findDoctorIntent8 = new Intent(MainActivity.this, FindDoctorActivity.class);
                findDoctorIntent8.putExtra("Doctor_Speciality" , "Dental Medicine");
                startActivity(findDoctorIntent8);
                break;

            default:
                return false;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
