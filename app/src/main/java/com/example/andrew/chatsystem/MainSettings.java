package com.example.andrew.chatsystem;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainSettings extends AppCompatActivity {

    private CircleImageView profilePicture ;
    private TextView userName , status;
    private RelativeLayout rBasic , r1 , r2 , r3 , r4 ;

    private DatabaseReference userRoot ;

    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_settings);

         userID = Objects.requireNonNull(getIntent().getExtras()).getString("sUserID") ;

        initializeFields() ;

        rBasic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainSettings.this , SettingActivity.class));
            }
        });

        r1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainSettings.this , PreviewMHActivity.class);
                intent.putExtra("userID_MH" ,userID);
                startActivity(intent);
            }
        });

        r2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainSettings.this, "Under development ...", Toast.LENGTH_SHORT).show();
            }
        });

        r3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainSettings.this, "Under development ...", Toast.LENGTH_SHORT).show();
            }
        });

        r4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainSettings.this, "Under development ...", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        userRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    if(dataSnapshot.hasChild("image"))
                        Picasso.get().load(dataSnapshot.child("image").getValue().toString()).placeholder(R.drawable.profile_image)
                                .error(R.drawable.no_image).into(profilePicture);

                    userName.setText(dataSnapshot.child("uName").getValue().toString());
                    status.setText(dataSnapshot.child("uStatus").getValue().toString());
                }
                else
                    Toast.makeText(MainSettings.this, "This error ! will be fixed in next update", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initializeFields() {
        userRoot = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);

        Toolbar toolbar = (Toolbar)findViewById(R.id.MainSettings_appBar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Settings");


        profilePicture = (CircleImageView)findViewById(R.id.MainSettings_circleImageView) ;
        userName = (TextView)findViewById(R.id.MainSettings_userName);
        status = (TextView)findViewById(R.id.MainSettings_userStatus);

        rBasic = (RelativeLayout)findViewById(R.id.MainSettings_basics) ;
        r1 = (RelativeLayout)findViewById(R.id.MainSettings_R1) ;
        r2 = (RelativeLayout)findViewById(R.id.MainSettings_R2) ;
        r3 = (RelativeLayout)findViewById(R.id.MainSettings_R3) ;
        r4 = (RelativeLayout)findViewById(R.id.MainSettings_R4) ;
    }
}
