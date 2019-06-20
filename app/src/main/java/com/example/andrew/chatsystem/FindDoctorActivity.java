package com.example.andrew.chatsystem;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindDoctorActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView findDoctorRecyclerView;
    private EditText et_Search;
    private ImageButton btn_search;
    private ImageView img ;
    private TextView search_text ;
    private Switch search_switch ;

    private DatabaseReference usersRef;
    private FirebaseRecyclerAdapter<Doctor, FindDoctorViewHolder> adapter ;

    private String speciality , query ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_doctor);

        speciality = Objects.requireNonNull(getIntent().getExtras()).getString("Doctor_Speciality");

        initializeFields();

        if(!speciality.equals("NULL"))
        {
            Query searchQueryStatus = usersRef.orderByChild("uStatus").startAt(speciality).endAt(speciality + "\uf8ff");
            fireBaseDoctorSearch(searchQueryStatus);
            search_switch.setChecked(true);
        }

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportActionBar().setTitle("");

                Animation animation = AnimationUtils.loadAnimation(FindDoctorActivity.this , R.anim.lefttoright);
                et_Search.setVisibility(View.VISIBLE);
                et_Search.setAnimation(animation);

                et_Search.setEnabled(true);
                et_Search.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(et_Search, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        et_Search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                 query = s.toString().trim() ;

                Query searchQueryStatus = usersRef.orderByChild("uStatus").startAt(query).endAt(query + "\uf8ff");
                Query searchQueryName = usersRef.orderByChild("uName").startAt(query).endAt(query + "\uf8ff");
                Query searchQueryOnline= usersRef.orderByChild("state").startAt("online").endAt("online" + "\uf8ff");

                if(search_switch.isChecked())
                    fireBaseDoctorSearch(searchQueryName);
               else
                    fireBaseDoctorSearch(searchQueryStatus);

            }
        });
    }

    private void initializeFields() {
        toolbar = (Toolbar) findViewById(R.id.findDoctor_toolBar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find Doctor");

        et_Search = (EditText) findViewById(R.id.et_findDoctor_search);
        btn_search = (ImageButton) findViewById(R.id.btn_findDoctor_search);
        img = (ImageView)findViewById(R.id.default_search_bg2);
        search_text = (TextView) findViewById(R.id.text_search_bg2);
        search_switch = (Switch)findViewById(R.id.search_switch);

        findDoctorRecyclerView = (RecyclerView) findViewById(R.id.find_doctor_recyclerView);
        findDoctorRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        usersRef = FirebaseDatabase.getInstance().getReference().child("Doctors");
    }

    private void fireBaseDoctorSearch(Query searchQuery) {

        FirebaseRecyclerOptions<Doctor> options = new FirebaseRecyclerOptions.Builder<Doctor>()
                .setQuery(searchQuery, Doctor.class).build();

        adapter = new FirebaseRecyclerAdapter<Doctor, FindDoctorViewHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull final FindDoctorViewHolder holder, final int position, @NonNull Doctor model) {

                        final String userID = getRef(position).getKey();

                        img.setVisibility(View.INVISIBLE);
                        search_text.setVisibility(View.INVISIBLE);

                        holder.rate.setVisibility(View.VISIBLE);
                        holder.rateStar.setVisibility(View.VISIBLE);

                        holder.userName.setText(model.getuName());
                        holder.userStatus.setText(model.getuStatus());
                        Picasso.get().load(model.getImage()).placeholder(R.drawable.profile_image)
                                .error(R.drawable.no_image).into(holder.profileImage);

                        usersRef.child(userID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists())
                                {
                                    if (dataSnapshot.child("userState").hasChild("state"))
                                    {
                                        String state = dataSnapshot.child("userState").child("state").getValue().toString();

                                        if (state.equals("online"))
                                            holder.onlineIcon.setVisibility(View.VISIBLE);

                                        else if (state.equals("offline"))
                                            holder.onlineIcon.setVisibility(View.INVISIBLE);
                                    }
                                    else
                                        holder.userStatus.setText("update the app");

                                    if(dataSnapshot.hasChild("doctorRate"))
                                    {
                                        String rate = dataSnapshot.child("doctorRate").getValue().toString();

                                        float DoctorRate = Float.parseFloat(rate) ;
                                        DoctorRate = Math.round(DoctorRate * 10) / 10 ;

                                        holder.rate.setText(String.valueOf(DoctorRate));
                                        holder.rate.setTextSize(20);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(FindDoctorActivity.this, ProfileActivity.class);
                                intent.putExtra("visit_user_id", userID);
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade_in , R.anim.fade_out);
                            }
                        });
                    }


                    @NonNull
                    @Override
                    public FindDoctorViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout, viewGroup, false);
                        return new FindDoctorViewHolder(view);
                    }
                };

        findDoctorRecyclerView.setAdapter(adapter);
        adapter.startListening();

        if (adapter.getItemCount()==0)
        {
            img.setVisibility(View.VISIBLE);
            search_text.setVisibility(View.VISIBLE);
            search_text.setText("No result found !");
        }
    }

    public static class FindDoctorViewHolder extends RecyclerView.ViewHolder {
        TextView userName, userStatus , rate;
        CircleImageView profileImage;
        ImageView onlineIcon , rateStar;

        public FindDoctorViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.tv_displayUsers_userName);
            userStatus = itemView.findViewById(R.id.tv_displayUsers_userStatus);
            profileImage = itemView.findViewById(R.id.displayUsers_profileImage);
            onlineIcon = itemView.findViewById(R.id.iv_user_online_status);
            rate = itemView.findViewById(R.id.tv_displayUsers_rate) ;
            rateStar = itemView.findViewById(R.id.rate_star);
        }
    }
}
