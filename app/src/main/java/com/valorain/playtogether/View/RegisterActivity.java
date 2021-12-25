package com.valorain.playtogether.View;


import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import android.widget.Button;
import android.widget.CheckBox;

import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.valorain.playtogether.Model.dbUser;
import com.valorain.playtogether.R;

public class RegisterActivity extends AppCompatActivity {


    private ProgressDialog mProgress;
    private dbUser mDbUser;  // Kullanıcı = User
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private Button btnCreate;
    private EditText edtUsername, edtEmail, edtPassword;
    private String txtUsername, txtEmail, txtPassword;
    private CheckBox btnCheck;
    private RadioButton radioE;
    private RadioButton radioK;
    private String gender;

    private void init() {

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        edtUsername = findViewById(R.id.edtUserName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnCreate = findViewById(R.id.btnCreateAcc);
        btnCheck = findViewById(R.id.checkBoxS);
        radioE = findViewById(R.id.radioErkek);
        radioK = findViewById(R.id.radioKadın);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();

        //Gender Code

        radioE.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b)
                gender = "male";

        });
        //Kadın ise
        radioK.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b)
                gender = "female";

        });

        //is checbox checked?
        btnCheck.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                btnCreate.setEnabled(true);
            } else
                btnCreate.setEnabled(false);
        });

        // Register button functions
        btnCreate.setOnClickListener(view -> {
            txtUsername = edtUsername.getText().toString();
            txtEmail = edtEmail.getText().toString();
            txtPassword = edtPassword.getText().toString();


            if (!TextUtils.isEmpty(txtUsername)) {
                if (!TextUtils.isEmpty(txtEmail)) {
                    if (!TextUtils.isEmpty(txtPassword)) {
                        //

                        mAuth.createUserWithEmailAndPassword(txtEmail, txtPassword).addOnCompleteListener(RegisterActivity.this, task -> {
                            if (task.isSuccessful()) {
                                //startprogress
                                mProgress = new ProgressDialog(RegisterActivity.this);
                                mProgress.setTitle("Registering...");
                                mProgress.show();
                                //endprogress
                                mUser = mAuth.getCurrentUser();
                                String Status = "Free";


                                if (mUser != null) {
                                    mDbUser = new dbUser(txtUsername, txtEmail, mUser.getUid(), gender, Status, false, false, "default",500,0,0,"default","","Hi I'm " + txtUsername +"! I'm a new PlayTogether user. Let's play together!");
                                    mFirestore.collection("UserList").document(mUser.getUid())
                                            .set(mDbUser)
                                            .addOnCompleteListener(RegisterActivity.this, task1 -> {
                                                if (task1.isSuccessful()) {
                                                    progressAyar();
                                                    Toast.makeText(RegisterActivity.this, "Register is successful!", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                    startActivity(new Intent(RegisterActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                                } else {
                                                    progressAyar();
                                                    Toast.makeText(RegisterActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }

                            } else {
                                progressAyar();
                                Toast.makeText(RegisterActivity.this, "This e-mail address is already used", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else
                        Toast.makeText(RegisterActivity.this, "Password field cannot be left blank.", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(RegisterActivity.this, "E-mail field cannot be left blank.", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(RegisterActivity.this, "Username field cannot be left blank.", Toast.LENGTH_SHORT).show();


        });


    }

    private void progressAyar() {  //Progress Settings

        if (mProgress.isShowing())

            //Close dialog
            mProgress.dismiss();
    }


}