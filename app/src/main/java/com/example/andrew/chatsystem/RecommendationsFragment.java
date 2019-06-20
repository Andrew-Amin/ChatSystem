package com.example.andrew.chatsystem;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecommendationsFragment extends Fragment {

    private View recommendsView;
    private RecyclerView recommends_recyclerView;

    private DatabaseReference RecommendsRef, rootRef;
    private FirebaseAuth mAuth;

    private String currentUserID;

    public RecommendationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        recommendsView = inflater.inflate(R.layout.fragment_recommendations, container, false);

        initializeFields();

        return recommendsView;
    }

    private void initializeFields() {
        recommends_recyclerView = (RecyclerView) recommendsView.findViewById(R.id.Recommendations_RecyclerView);
        recommends_recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        RecommendsRef = FirebaseDatabase.getInstance().getReference().child("Recommendations");
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(RecommendsRef.child(currentUserID), Contacts.class).build();

        FirebaseRecyclerAdapter<Contacts, RecommendsViewHolder> adapter = new FirebaseRecyclerAdapter
                <Contacts, RecommendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final RecommendsViewHolder holder, int position, @NonNull Contacts model) {

                holder.btn_Decline.setVisibility(View.VISIBLE);
                holder.btn_Accept.setVisibility(View.VISIBLE);
                holder.btn_Accept.setText("View profile");

                final String list_user_id = getRef(position).getKey();
                DatabaseReference request_type_ref = getRef(position).child("request_type").getRef();

                request_type_ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists())
                        {
                            String request_type = dataSnapshot.getValue().toString();

                            if (request_type.equals("received"))
                            {
                                rootRef.child("Doctors").child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists())
                                        {
                                            if (dataSnapshot.hasChild("image")) {

                                                final String request_userImage = dataSnapshot.child("image").getValue().toString();

                                                Picasso.get().load(request_userImage).placeholder(R.drawable.profile_image)
                                                        .into(holder.profileImage);
                                            }
                                            final String request_userName = dataSnapshot.child("uName").getValue().toString();

                                            holder.userName.setText(request_userName);
                                            holder.userStatus.setText("has been recommended for you ...");

                                            holder.btn_Accept.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent = new Intent(getContext() , ProfileActivity.class);
                                                    intent.putExtra("visit_user_id" , list_user_id) ;
                                                    startActivity(intent);
                                                }
                                            });

                                            holder.btn_Decline.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    declineRecommendation(list_user_id);
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                            else if (request_type.equals("sent"))
                            {
                                holder.btn_Accept.setText("Cancel");
                                holder.btn_Accept.setBackgroundResource(R.drawable.decline_btn_bg);

                                holder.btn_Decline.setVisibility(View.INVISIBLE);
                                holder.btn_Decline.setEnabled(false);


                                rootRef.child("Doctors").child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists())
                                        {
                                            if (dataSnapshot.hasChild("image")) {

                                                final String request_userImage = dataSnapshot.child("image").getValue().toString();

                                                Picasso.get().load(request_userImage).placeholder(R.drawable.profile_image)
                                                        .into(holder.profileImage);
                                            }
                                            final String request_userName = dataSnapshot.child("uName").getValue().toString();

                                            holder.userName.setText(request_userName);
                                            holder.userStatus.setText("You recommended " + request_userName+" to your family");

                                            holder.btn_Accept.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    declineRecommendation(list_user_id);
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
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public RecommendsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout, viewGroup, false);
                return new RecommendsViewHolder(view);
            }
        };
        recommends_recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class RecommendsViewHolder extends RecyclerView.ViewHolder {
        TextView userName, userStatus;
        CircleImageView profileImage;
        Button btn_Accept, btn_Decline;

        public RecommendsViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.tv_displayUsers_userName);
            userStatus = itemView.findViewById(R.id.tv_displayUsers_userStatus);
            profileImage = itemView.findViewById(R.id.displayUsers_profileImage);
            btn_Accept = itemView.findViewById(R.id.btn_AcceptRequest);
            btn_Decline = itemView.findViewById(R.id.btn_DeclineRequest);
        }
    }

    private void declineRecommendation(final String list_user_id) {
        RecommendsRef.child(currentUserID).child(list_user_id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Recommendation has been deleted", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(getContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }

                });
    }
}
