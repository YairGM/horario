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

public class login extends AppCompatActivity{

    private static final String TAG = " ";
    private EditText usuario,contraseña;
    private Button entrar,registro;
    private ProgressDialog progressDialog;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private FirebaseAuth mAuth;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.inicio);

        mAuth = FirebaseAuth.getInstance();

        entrar = (Button) findViewById(R.id.btnenvia);
        usuario = (EditText) findViewById(R.id.txtusu);
        contraseña = (EditText) findViewById(R.id.txtcontra);
        registro=(Button)findViewById(R.id.btnregistro);

        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(login.this,registro.class));
            }
        });

        entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                iniciar();
            }
        });

    }

    private void iniciar(){
        String email, password;
        email= usuario.getText().toString();
        password=contraseña.getText().toString();
        if(TextUtils.isEmpty(email)){
            Toast.makeText(login.this, "El campo correo no puede estar vacio",
                    Toast.LENGTH_SHORT).show();

            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(login.this, "El campo contraseña no puede estar vacio",
                    Toast.LENGTH_SHORT).show();

            return;
        }
        if (password.length()<=5){
            Toast.makeText(login.this, "Contraseña incorrecta",
                    Toast.LENGTH_SHORT).show();
            contraseña.setText("");
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            startActivity(new Intent(login.this,form.class));
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            startActivity(new Intent(login.this,login.class));

                            Toast.makeText(login.this, "Autentificacion fallida.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
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
            Intent intent = new Intent(login.this, form.class);
            startActivity(intent);
            Toast.makeText(login.this, "Ya iniciaste sesion",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
    }

}
