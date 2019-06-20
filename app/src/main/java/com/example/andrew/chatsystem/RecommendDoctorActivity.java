package com.example.andrew.chatsystem;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecommendDoctorActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView familyRecyclerView;

    private String currentUserID, doctorID, accessType;

    private DatabaseReference rootRef, RecommendsRef, userContactsRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_doctor);

        accessType = Objects.requireNonNull(getIntent().getExtras()).getString("accessTypeID");
        doctorID = Objects.requireNonNull(getIntent().getExtras()).getString("RecommendedDoctorID");

        initializeFields();

        fireBaseUserRetrieve();

    }

    private void initializeFields() {
        toolbar = (Toolbar) findViewById(R.id.recommendDoctor_toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Your Contacts");

        familyRecyclerView = (RecyclerView) findViewById(R.id.recommend_doctor_recyclerView);
        familyRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        RecommendsRef = FirebaseDatabase.getInstance().getReference().child("Recommendations");
        userContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
    }

    private void fireBaseUserRetrieve() {
        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(userContactsRef, Contacts.class).build();

        FirebaseRecyclerAdapter<Contacts, RecommendDoctorActivity.FindUserViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, RecommendDoctorActivity.FindUserViewHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull final RecommendDoctorActivity.FindUserViewHolder holder, final int position, @NonNull Contacts model) {

                        final String userID = getRef(position).getKey();

                        rootRef.child("Users").child(userID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    if (dataSnapshot.child("userState").hasChild("state")) {
                                        String state = dataSnapshot.child("userState").child("state").getValue().toString();

                                        if (state.equals("online"))
                                            holder.onlineIcon.setVisibility(View.VISIBLE);

                                        else if (state.equals("offline"))
                                            holder.onlineIcon.setVisibility(View.INVISIBLE);
                                    } else
                                        holder.userStatus.setText("update the app");

                                    String uPicture;
                                    if (dataSnapshot.hasChild("image")) {
                                        uPicture = dataSnapshot.child("image").getValue().toString();
                                        Picasso.get().load(uPicture).placeholder(R.drawable.profile_image).into(holder.profileImage);
                                    }

                                    final String uName = dataSnapshot.child("uName").getValue().toString();
                                    String uStatus = dataSnapshot.child("uStatus").getValue().toString();

                                    holder.userName.setText(uName);
                                    holder.userStatus.setText(uStatus);

                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Toast.makeText(RecommendDoctorActivity.this, "Long press to make a recommendation",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    });

                                    holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                                        @Override
                                        public boolean onLongClick(View v) {
                                            if (accessType.equals("recommend")) {
                                                sendRecommendation(getRef(position).getKey(), doctorID);
                                                return true;
                                            }
                                            return false;
                                        }
                                    });

                                } else {
                                    rootRef.child("Doctors").child(userID).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists())
                                            {
                                                if (dataSnapshot.child("userState").hasChild("state")) {
                                                    String state = dataSnapshot.child("userState").child("state").getValue().toString();

                                                    if (state.equals("online"))
                                                        holder.onlineIcon.setVisibility(View.VISIBLE);

                                                    else if (state.equals("offline"))
                                                        holder.onlineIcon.setVisibility(View.INVISIBLE);
                                                } else
                                                    holder.userStatus.setText("update the app");

                                                String uPicture;
                                                if (dataSnapshot.hasChild("image")) {
                                                    uPicture = dataSnapshot.child("image").getValue().toString();
                                                    Picasso.get().load(uPicture).placeholder(R.drawable.profile_image).into(holder.profileImage);
                                                }

                                                final String uName = dataSnapshot.child("uName").getValue().toString();
                                                String uStatus = dataSnapshot.child("uStatus").getValue().toString();

                                                holder.userName.setText(uName);
                                                holder.userStatus.setText(uStatus);

                                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Toast.makeText(RecommendDoctorActivity.this,
                                                                "You can NOT recommend doctor to a doctor", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                                                    @Override
                                                    public boolean onLongClick(View v) {
                                                        Toast.makeText(RecommendDoctorActivity.this, "You can NOT recommend doctor to a doctor", Toast.LENGTH_SHORT).show();
                                                        return true;
                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public RecommendDoctorActivity.FindUserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout, viewGroup, false);
                        return new RecommendDoctorActivity.FindUserViewHolder(view);
                    }
                };

        familyRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void sendRecommendation(final String receivedUserID, final String doctorID) {
        RecommendsRef.child(currentUserID).child(doctorID).child("request_type")
                .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    RecommendsRef.child(receivedUserID).child(doctorID).child("request_type")
                            .setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                RecommendsRef.child(receivedUserID).child(doctorID).child("from")
                                        .setValue(currentUserID).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(RecommendDoctorActivity.this, "Recommendation has been sent", Toast.LENGTH_SHORT).show();
                                            /*
                                            HashMap<String,String> chatNotificationMap = new HashMap<>();
                                            chatNotificationMap.put("from" , currentUserID);
                                            chatNotificationMap.put("type" , "request");

                                            notificationRef.child(receivedUserID).push()
                                                    .setValue(chatNotificationMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful())
                                                    {
                                                        sendRequestMessage.setEnabled(true);
                                                        currentState = "request_sent";
                                                        sendRequestMessage.setText("Cancel Chat");
                                                    }
                                                }
                                            });
                                            */
                                        }
                                    }
                                });
                            } else
                                Toast.makeText(RecommendDoctorActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else
                    Toast.makeText(RecommendDoctorActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class FindUserViewHolder extends RecyclerView.ViewHolder {
        TextView userName, userStatus;
        CircleImageView profileImage;
        ImageView onlineIcon;

        public FindUserViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.tv_displayUsers_userName);
            userStatus = itemView.findViewById(R.id.tv_displayUsers_userStatus);
            profileImage = itemView.findViewById(R.id.displayUsers_profileImage);
            onlineIcon = itemView.findViewById(R.id.iv_user_online_status);
        }
    }
}
