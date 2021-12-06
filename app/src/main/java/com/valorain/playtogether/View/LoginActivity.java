package com.valorain.playtogether.View;


import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.valorain.playtogether.R;


public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText edtEmail, edtPassword;
    private Button btnLogin;
    private TextView signUp;
    private String txtEmail, txtPassword;
    private ProgressDialog mProgress;

    private void init() {
        mAuth = FirebaseAuth.getInstance();
        btnLogin = findViewById(R.id.btnLogin);
        signUp = findViewById(R.id.signUp);
        edtEmail = findViewById(R.id.edtLoginEmail);
        edtPassword = findViewById(R.id.edtLoginPass);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();


        //zaten giriş yaptıysa
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        }


        //üye ise giriş yap
        btnLogin.setOnClickListener(view -> {
            txtEmail = edtEmail.getText().toString();
            txtPassword = edtPassword.getText().toString();
            mProgress = new ProgressDialog(LoginActivity.this);
            mProgress.setTitle("Giriş Yapılıyor...");
            mProgress.show();
            if (!TextUtils.isEmpty(txtEmail)) {
                if (!TextUtils.isEmpty(txtPassword)) {

                    mAuth.signInWithEmailAndPassword(txtEmail, txtPassword)
                            .addOnCompleteListener(LoginActivity.this, task -> {
                                if (task.isSuccessful()) {

                                    progressAyar();
                                    Toast.makeText(LoginActivity.this, "Giriş Başarılı", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                    finish();
                                } else {
                                    progressAyar();
                                    Toast.makeText(LoginActivity.this, "Kullanıcı Adı veya Şifre yanlış", Toast.LENGTH_SHORT).show();
                                }
                            });


                } else {
                    progressAyar();
                    Toast.makeText(LoginActivity.this, "Şifre alanı boş bırakılamaz!", Toast.LENGTH_SHORT).show();
                }

            } else {
                progressAyar();
                Toast.makeText(LoginActivity.this, "Email alanı boş bırakılamaz!", Toast.LENGTH_SHORT).show();
            }
        });

        //üye değilse kayıt sayfasına gönder
        signUp.setOnClickListener(view -> {
            Intent register = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(register);

        });


    }

    private void progressAyar() {

        if (mProgress.isShowing())
            mProgress.dismiss();
    }

}