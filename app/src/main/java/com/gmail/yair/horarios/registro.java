package com.gmail.yair.horarios;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;


public class registro extends AppCompatActivity {
    private static final String TAG = " ";
    private EditText usuario,contraseña;
    private Button registro;
    private ProgressDialog progressDialog;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        mAuth = FirebaseAuth.getInstance();

        usuario = (EditText) findViewById(R.id.txtusu);
        contraseña = (EditText) findViewById(R.id.txtcontra);
        registro=(Button)findViewById(R.id.btnregistro);

        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registro();
            }
        });
    }

    private void registro(){
        final String email, password;
        email= usuario.getText().toString();
        password=contraseña.getText().toString();
        if(TextUtils.isEmpty(email)){
            Toast.makeText(registro.this, "El campo usuario no puede estar vacio",
                    Toast.LENGTH_SHORT).show();

            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(registro.this, "El campo correo no puede estar vacio",
                    Toast.LENGTH_SHORT).show();

            return;
        }
        if (password.length()<=5){
            Toast.makeText(registro.this, "La contraseña ingresada es demasiado corta",
                    Toast.LENGTH_SHORT).show();
            contraseña.setText("");
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            startActivity(new Intent(registro.this, form.class));
                            Toast.makeText(registro.this, "Registro exitoso",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            startActivity(new Intent(registro.this,registro.class));
                            Toast.makeText(registro.this, "Fallo en el registro",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }


    private void updateUI(FirebaseUser user){
        if(user !=null){
            startActivity(new Intent(registro.this,form.class));
            finish();
        }
    }
}
