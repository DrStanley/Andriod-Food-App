package com.stanleyj.android.food_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stanleyj.android.food_app.admin.AdminMenuActivity;

public class LoginActivity extends AppCompatActivity {
    EditText editTextEm, editTextPa;
    Button login;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private ProgressDialog pd;
    private TextView paswrd;
    private TextView regis;
    private String em;
    private String pa;
    String admin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setTitle("Login Page");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        Shared preference to get the extra data stored;
        SharedPreferences.Editor editor = sharedPref.edit();
        boolean check_log = sharedPref.getBoolean("Login", false);
        String piss = sharedPref.getString("Username", null);
        editor.apply();

        editTextEm =  findViewById(R.id.usn);
        pd = new ProgressDialog(LoginActivity.this);
        editTextPa =  findViewById(R.id.lpwd);
        paswrd =  findViewById(R.id.f_pass);
        regis =  findViewById(R.id.regAct);
        firebaseAuth = FirebaseAuth.getInstance();

        Toast.makeText(LoginActivity.this, piss + " check " + check_log, Toast.LENGTH_SHORT).show();
        if (check_log) {
            editTextEm.setText(piss);
        }

        login =  findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
        regis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });
        paswrd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ForgotActivity.class));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loginUser() {

        em = editTextEm.getText().toString();
        pa = editTextPa.getText().toString();
        if (TextUtils.isEmpty(em)) {
            //email is empty
            Toast.makeText(this, "Please Enter email", Toast.LENGTH_SHORT).show();
            editTextEm.setError("*Required");
            editTextEm.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(pa)) {
            //password is empty
            Toast.makeText(this, "Please Enter password", Toast.LENGTH_SHORT).show();
            editTextPa.setError("*Required");
            editTextPa.requestFocus();
            return;
        }
        pd.setMessage("Logging User in...");
        pd.show();
        pd.setCanceledOnTouchOutside(false);

        firebaseAuth.signInWithEmailAndPassword(em, pa).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if (user.isEmailVerified()) {
                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        Shared preference to get the extra data stored;
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean("Login", true);
                        editor.putBoolean("Updated", true);
                        editor.putString("Username", em);
                        editor.apply();
                        editTextPa.setText(null);
                        firebaseDatabase = FirebaseDatabase.getInstance();
                        databaseReference = firebaseDatabase.getReference();
                        databaseReference = firebaseDatabase.getReference("Users").child(user.getUid()).child("Profile");
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                admin = dataSnapshot.child("type").getValue(String.class);
                                pd.dismiss();

                                Toast.makeText(LoginActivity.this, "Login successful!!", Toast.LENGTH_SHORT).show();

                                if (admin != null && admin.equals("admin")){
                                    pd.dismiss();
                                    startActivity(new Intent(getApplicationContext(), AdminMenuActivity.class));
                                    return;
                                }else {
                                    pd.dismiss();

                                    startActivity(new Intent(getApplicationContext(), MainMenuActivity.class));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    } else {
                        pd.dismiss();
                        // email not sent
                        final View view = findViewById(android.R.id.content);
                        Snackbar.make(view, "Please check your mail and verify your email", Snackbar.LENGTH_SHORT)
                                .setAction(null, null)
                                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                                .show();
                    }
                } else {
                    pd.dismiss();
                    AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("Couldn't Login User \n"
                            + task.getException().getCause()+"\n"+ task.getException().getMessage());
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
