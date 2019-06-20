package com.example.andrew.chatsystem;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private TextView userName, userStatus;
    private CircleImageView profileImage;
    private Button sendRequestMessage , declineChatRequest;
    private RatingBar doctor_rate ;

    private DatabaseReference rootRef, chatRequestRef , contactsRef , notificationRef;
    private FirebaseAuth mAuth;

    private String receivedUserID, senderUserID, currentState , img;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        receivedUserID = getIntent().getExtras().getString("visit_user_id");
        initializeFields();

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(img))
                    img= "default" ;

                Intent intent = new Intent(ProfileActivity.this, ImageFullScreenActivity.class);
                intent.putExtra("ImageUri" , img);
                startActivity(intent);
            }
        });
    }

    private void initializeFields() {

        rootRef = FirebaseDatabase.getInstance().getReference();
        chatRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat_requests");
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        notificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications");

        mAuth = FirebaseAuth.getInstance();

        senderUserID = mAuth.getCurrentUser().getUid();
        currentState = "new";

        userName = (TextView) findViewById(R.id.tv_profile_userName);
        userStatus = (TextView) findViewById(R.id.tv_profile_status);
        profileImage = (CircleImageView) findViewById(R.id.profile_ProfileImage);
        sendRequestMessage = (Button) findViewById(R.id.btn_profile_sendChatRequest);
        declineChatRequest = (Button) findViewById(R.id.btn_profile_declineChatRequest);
        doctor_rate = (RatingBar)findViewById(R.id.DoctorProfile_ratingBar);

        retrieveUserInfo();

    }

    private void retrieveUserInfo() {
        rootRef.child("Users").child(receivedUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    if (dataSnapshot.hasChild("image"))
                    {
                        img = dataSnapshot.child("image").getValue().toString();
                        Picasso.get().load(img).placeholder(R.drawable.profile_image).into(profileImage);
                    }

                    String name = dataSnapshot.child("uName").getValue().toString();
                    String status = dataSnapshot.child("uStatus").getValue().toString();

                    userName.setText(name);
                    userStatus.setText(status);

                    manageChatRequest();
                }
             else
                 retrieveDoctorInfo();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void retrieveDoctorInfo() {

        doctor_rate.setVisibility(View.VISIBLE);

        rootRef.child("Doctors").child(receivedUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    String rate ;
                    if (dataSnapshot.hasChild("image"))
                    {
                        img = dataSnapshot.child("image").getValue().toString();
                        Picasso.get().load(img).placeholder(R.drawable.profile_image).into(profileImage);
                    }

                    String name = dataSnapshot.child("uName").getValue().toString();
                    String status = dataSnapshot.child("uStatus").getValue().toString();

                    userName.setText(name);
                    userStatus.setText(status);
                    if(dataSnapshot.hasChild("doctorRate"))
                        rate = dataSnapshot.child("doctorRate").getValue().toString();

                    else
                    {
                        rate = "0" ;
                        doctor_rate.setVisibility(View.GONE);
                        Toast.makeText(ProfileActivity.this, name + "has not been rated yet ! , be the first", Toast.LENGTH_LONG).show();
                    }

                    float DoctorRate = Float.parseFloat(rate) ;
                    DoctorRate = Math.round(DoctorRate * 10) / 10 ;
                    doctor_rate.setRating(DoctorRate);

                    manageChatRequest();
                }
                else
                {
                    Toast.makeText(ProfileActivity.this, "This account has been deleted", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ProfileActivity.this , MainActivity.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void manageChatRequest() {

        chatRequestRef.child(senderUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(receivedUserID)) {

                    String request_type = dataSnapshot.child(receivedUserID).child("request_type").getValue().toString();
                    if (request_type.equals("sent")) {
                        sendRequestMessage.setEnabled(true);
                        currentState = "request_sent";
                        sendRequestMessage.setText("Cancel chat");
                        sendRequestMessage.setBackgroundResource(R.drawable.decline_btn_bg);
                    }

                    else if (request_type.equals("received"))
                    {
                        currentState = "request_received";
                        sendRequestMessage.setText("Accept chat");
                        sendRequestMessage.setBackgroundResource(R.drawable.accept_btn_bg);

                        declineChatRequest.setVisibility(View.VISIBLE);
                        declineChatRequest.setEnabled(true);

                        declineChatRequest.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cancelChatRequest();
                            }
                        });
                    }
                }
                else
                {
                    contactsRef.child(senderUserID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.hasChild(receivedUserID))
                            {
                                currentState = "friends";
                                sendRequestMessage.setText("Remove contact");
                                sendRequestMessage.setBackgroundResource(R.drawable.decline_btn_bg);
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


        if (!senderUserID.equals(receivedUserID)) {
            sendRequestMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendRequestMessage.setEnabled(false);
                    if (currentState.equals("new")) {
                        sendChatRequest();
                        //implement a notification way when user receive a new chat request
                    }
                    if (currentState.equals("request_sent")) {
                        cancelChatRequest();
                    }

                    if (currentState.equals("request_received")) {
                        acceptChatRequest();
                    }

                    if (currentState.equals("friends")) {
                        removeContact();
                    }

                }
            });
        } else {
            sendRequestMessage.setVisibility(View.INVISIBLE);
            sendRequestMessage.setEnabled(false);
        }
    }

    private void sendChatRequest() {
        chatRequestRef.child(senderUserID).child(receivedUserID).child("request_type")
                .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    chatRequestRef.child(receivedUserID).child(senderUserID).child("request_type")
                            .setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                HashMap <String,String> chatNotificationMap = new HashMap<>();
                                chatNotificationMap.put("from" , senderUserID);
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


                            } else
                                Toast.makeText(ProfileActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else
                    Toast.makeText(ProfileActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cancelChatRequest() {
        chatRequestRef.child(senderUserID).child(receivedUserID).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            chatRequestRef.child(receivedUserID).child(senderUserID).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                sendRequestMessage.setEnabled(true);
                                                currentState = "new";
                                                sendRequestMessage.setText("Request Chat");

                                                declineChatRequest.setVisibility(View.INVISIBLE);
                                                declineChatRequest.setEnabled(false);
                                            } else
                                                Toast.makeText(ProfileActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else
                            Toast.makeText(ProfileActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }

                });
    }

    private void acceptChatRequest() {
        contactsRef.child(senderUserID).child(receivedUserID)
                .child("contacts").setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    contactsRef.child(receivedUserID).child(senderUserID)
                            .child("contacts").setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                chatRequestRef.child(senderUserID).child(receivedUserID)
                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
                                            chatRequestRef.child(receivedUserID).child(senderUserID)
                                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful())
                                                    {
                                                        sendRequestMessage.setEnabled(true);
                                                        currentState="friends";
                                                        sendRequestMessage.setText("Remove Contact");

                                                        declineChatRequest.setVisibility(View.INVISIBLE);
                                                        declineChatRequest.setEnabled(false);
                                                        Toast.makeText(ProfileActivity.this, "New contact saved", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    private void confirmRemoveContact() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext() , R.style.AlertDialog);
        builder.setTitle("   Remove "+userName.getText().toString()+" from your contacts ?   ");

        builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeContact();
            }
        });

        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void removeContact() {
        contactsRef.child(senderUserID).child(receivedUserID).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            contactsRef.child(receivedUserID).child(senderUserID).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                sendRequestMessage.setEnabled(true);
                                                currentState = "new";
                                                sendRequestMessage.setBackgroundResource(R.drawable.btn_bg);
                                                sendRequestMessage.setText("Request Chat");


                                                declineChatRequest.setVisibility(View.INVISIBLE);
                                                declineChatRequest.setEnabled(false);
                                                Toast.makeText(ProfileActivity.this, "Contact has been removed", Toast.LENGTH_SHORT).show();
                                            } else
                                                Toast.makeText(ProfileActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else
                            Toast.makeText(ProfileActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }

                });
    }
}
