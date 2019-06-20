package com.example.andrew.chatsystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class PhoneRegistrationActivity extends AppCompatActivity {

    private Button sendCode, verify;
    private EditText et_phoneNumber, et_verificationCode;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId , phoneNumber = "+20";
    boolean exist = false;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_registration);

        initializeFields();

        sendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 phoneNumber = et_phoneNumber.getText().toString();
                if (TextUtils.isEmpty(phoneNumber))
                    Toast.makeText(PhoneRegistrationActivity.this, "phone number", Toast.LENGTH_SHORT).show();
                else {

                    if (verifyUserPhone(phoneNumber))
                        Toast.makeText(PhoneRegistrationActivity.this, "This phone number exists", Toast.LENGTH_SHORT).show();
                    else {
                        loadingBar.setTitle("Number Verification");
                        loadingBar.setMessage("Verifying phone number ...");
                        loadingBar.setCanceledOnTouchOutside(false);
                        loadingBar.show();

                        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber,
                                60, TimeUnit.SECONDS, PhoneRegistrationActivity.this, mCallbacks);
                    }

                }
            }
        });

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCode.setVisibility(View.INVISIBLE);
                et_phoneNumber.setVisibility(View.INVISIBLE);

                String vCode = et_verificationCode.getText().toString();

                if (TextUtils.isEmpty(vCode))
                    Toast.makeText(PhoneRegistrationActivity.this, "Enter the code first ...", Toast.LENGTH_SHORT).show();

                else {

                    loadingBar.setTitle("Code Verification");
                    loadingBar.setMessage("Verifying code ...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, vCode);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                loadingBar.dismiss();
                Toast.makeText(PhoneRegistrationActivity.this, "Invalid", Toast.LENGTH_SHORT).show();

                sendCode.setVisibility(View.VISIBLE);
                et_phoneNumber.setVisibility(View.VISIBLE);
                verify.setVisibility(View.INVISIBLE);
                et_verificationCode.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                mVerificationId = verificationId;
                mResendToken = token;

                loadingBar.dismiss();
                Toast.makeText(PhoneRegistrationActivity.this, "code has been sent , chick it ", Toast.LENGTH_SHORT).show();

                sendCode.setVisibility(View.INVISIBLE);
                et_phoneNumber.setVisibility(View.INVISIBLE);
                verify.setVisibility(View.VISIBLE);
                et_verificationCode.setVisibility(View.VISIBLE);
            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            usersRef.child(mAuth.getCurrentUser().getUid()).child("PhoneNumber").setValue(phoneNumber)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                loadingBar.dismiss();
                                                sendUserToMainActivity();
                                            } else
                                                Toast.makeText(PhoneRegistrationActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        } else
                            Toast.makeText(PhoneRegistrationActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initializeFields() {
        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        sendCode = (Button) findViewById(R.id.sendVerificationCode_btn);
        verify = (Button) findViewById(R.id.verify_btn);
        et_phoneNumber = (EditText) findViewById(R.id.et_phoneNumber_input);
        et_verificationCode = (EditText) findViewById(R.id.et_verificationCode_input);
        loadingBar = new ProgressDialog(PhoneRegistrationActivity.this);
    }

    private void sendUserToMainActivity() {
        Toast.makeText(PhoneRegistrationActivity.this, "Registered successfully", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(PhoneRegistrationActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private boolean verifyUserPhone(final String phone) {
        usersRef.child(mAuth.getCurrentUser().getUid()).child("PhoneNumber")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String phoneNumber = dataSnapshot.getValue().toString();
                            if (phoneNumber.equals(phone))
                                exist = true;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        if (exist)
            return true;

        return false;
    }
}
