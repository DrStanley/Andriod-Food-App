package com.stanleyj.android.food_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Updatefoodctivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    LinearLayoutManager mLinearLayoutManager;
    private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatefood);
        pd = new ProgressDialog(this);

        getSupportActionBar().setTitle("Update Food"); // set back button to action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mRecyclerView = findViewById(R.id.recyclerviewU);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        // send query to firebase
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("Menu Item");
    }

    //    load data to recycler view
    @Override
    protected void onStart() {
        pd.setMessage("Loading Data Please wait...");
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        try {
            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChildren()) {
                        Toast.makeText(Updatefoodctivity.this, "No result found", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                        return;
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            FirebaseRecyclerAdapter<Model, ViewHolder> firebaseRecyclerAdapter =
                    new FirebaseRecyclerAdapter<Model, ViewHolder>(
                            Model.class,
                            R.layout.view_food,
                            ViewHolder.class,
                            mDatabaseReference
                    ) {
                        @Override
                        protected void populateViewHolder(ViewHolder viewHolder, Model model, int position) {
                            viewHolder.setDetailView(getApplicationContext(), model.getTitle(), model.getImage1(),
                                    model.getDescription(), model.getPrice(), model.getFoodID());
                            pd.dismiss();

                        }

                        @Override
                        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                            ViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
                            viewHolder.setOnClickListener(new ViewHolder.ClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                    //Views
                                    try {
                                        TextView ids = view.findViewById(R.id.viewfoodID);
                                        Intent intent = new Intent(view.getContext(), UpdateRemoveActivity.class);
                                        intent.putExtra("foodID", ids.getText().toString());
                                        intent.putExtra("title", "Update");
                                        startActivity(intent);
                                    } catch (Exception f) {
                                        AlertDialog alertDialog = new AlertDialog.Builder(Updatefoodctivity.this).create();
                                        alertDialog.setTitle("Alert");
                                        alertDialog.setMessage("Alert " + f.getMessage() + " " + f.getCause());
                                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                        alertDialog.show();
                                    } finally {
                                        pd.dismiss();
                                    }
                                }

                                @Override
                                public void onItemLongClick(View view, int position) {
//                          TODO
                                }
                            });

                            return viewHolder;
                        }
                    };
            // set adapter to recycler view
            mRecyclerView.setAdapter(firebaseRecyclerAdapter);
        } catch (Exception f) {
            Toast.makeText(getApplicationContext(), "Please wait for Image to Load", Toast.LENGTH_SHORT).show();
            AlertDialog alertDialog = new AlertDialog.Builder(Updatefoodctivity.this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("Alert " + f.getMessage() + " " + f.getCause());
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
        super.onStart();
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
