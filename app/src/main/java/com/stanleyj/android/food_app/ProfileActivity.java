package com.stanleyj.android.food_app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    EditText nam, phn, usd;
    ImageView imageView;
    TextView em;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setTitle("Profile"); // set back button to action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        em = (TextView) findViewById(R.id.mail);
        imageView = (ImageView) findViewById(R.id.profile_image);
        nam = (EditText) findViewById(R.id.nameP);
        usd = (EditText) findViewById(R.id.usd);
        phn = (EditText) findViewById(R.id.phnP);
        usd.setEnabled(false);
        nam.setEnabled(false);
        nam.setEnabled(false);
        phn.setEnabled(false);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        databaseReference = firebaseDatabase.getReference("Users").child(firebaseAuth.getUid()).child("Profile");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    String aha = dataSnapshot.child("name").getValue(String.class);
                    String img = dataSnapshot.child("image").getValue(String.class);
                    String phn2 = dataSnapshot.child("phone_number").getValue(String.class);
                    String ema = firebaseAuth.getCurrentUser().getEmail();

                    em.setText(ema);
                    phn.setText(phn2);
                    nam.setText(aha);
                    usd.setText(firebaseAuth.getUid());
                    Bitmap bb = decodeFromFirebaseBase64(img);
                    imageView.setImageBitmap(bb);
                } catch (Exception f) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static Bitmap decodeFromFirebaseBase64(String image) throws IOException {
        byte[] decodedByteArray = android.util.Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_reset) {
            recreate();
        }
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
