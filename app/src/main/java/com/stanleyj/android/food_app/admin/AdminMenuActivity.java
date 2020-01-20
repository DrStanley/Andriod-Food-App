package com.stanleyj.android.food_app.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stanleyj.android.food_app.R;
import com.stanleyj.android.food_app.Updatefoodctivity;

public class AdminMenuActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    LinearLayout layoutAdd, layoutRemove, layoutUpdate, layoutOrder, layoutFeedback, layoutNumber;
    TextView number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu);
        getSupportActionBar().setTitle("Administrator");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        number = findViewById(R.id.number);
        layoutAdd = findViewById(R.id.add_food);
        layoutRemove = findViewById(R.id.remove_food);
        layoutUpdate = findViewById(R.id.update_food);
        layoutOrder = findViewById(R.id.food_orders);
        layoutFeedback = findViewById(R.id.feedback);
        layoutNumber = findViewById(R.id.user_count);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        databaseReference = firebaseDatabase.getReference("Users");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long num = +dataSnapshot.getChildrenCount();
                Toast.makeText(AdminMenuActivity.this, num + " and " + dataSnapshot.getValue(), Toast.LENGTH_SHORT).show();
                number.setText("" + num);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        layoutAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminMenuActivity.this, AddfoodActivity.class));
            }
        });
        layoutRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminMenuActivity.this, RemovefoodActivity.class));
            }
        });
        layoutUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminMenuActivity.this, Updatefoodctivity.class));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                firebaseAuth.signOut();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
