package com.stanleyj.android.food_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

public class DetailActivity extends AppCompatActivity {

    TextView TtextView, DtextView, PtextView, StextView, qty;
    ImageView img1, img2, img3;
    String ans= "", price;
    int h20s = 0, a = 0, quant;
    EditText editText;
    String ids, title;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        pd = new ProgressDialog(this);

        try {
            qty =  findViewById(R.id.quantity_text);
//            android.support.v7.app.ActionBar actionBar = getSupportActionBar();
            getSupportActionBar().setTitle("Food Details"); // set back button to action bar
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            TtextView =  findViewById(R.id.dTitle);
            DtextView =  findViewById(R.id.dDescription);
            PtextView =  findViewById(R.id.dPrice);
            StextView =  findViewById(R.id.dStatus);
            img1 =  findViewById(R.id.dimg1);
            img2 =  findViewById(R.id.dimg2);
            img3 =  findViewById(R.id.dimg3);
            editText =  findViewById(R.id.address);
            editText.setEnabled(false);


//            getting intent from previous activity
            ids = getIntent().getStringExtra("foodID");

            // send query to firebase
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mDatabaseReference = mFirebaseDatabase.getReference("Menu Item").child(ids);
            pd.setMessage(" Loading Data Please wait...");
            pd.setCanceledOnTouchOutside(false);
            pd.show();
            mDatabaseReference.child("Menu Item").child(ids);
            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                //                                getting clicked data from database
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    title = dataSnapshot.child("title").getValue(String.class);
                    String imh1 = dataSnapshot.child("image1").getValue(String.class);
                    String imh2 = dataSnapshot.child("image2").getValue(String.class);
                    String imh3 = dataSnapshot.child("image3").getValue(String.class);
                    String des = dataSnapshot.child("description").getValue(String.class);
                    String sta = dataSnapshot.child("food_status").getValue(String.class);
                    price = dataSnapshot.child("price").getValue(String.class);

                    TtextView.setText(title);
                    DtextView.setText(des);
                    StextView.setText(sta);
                    PtextView.setText("₦" + price);
                    try {
                        img1.setImageBitmap(decodeFromFirebaseBase64(imh1));
                        img2.setImageBitmap(decodeFromFirebaseBase64(imh2));
                        img3.setImageBitmap(decodeFromFirebaseBase64(imh3));

                    } catch (Exception f) {
                        AlertDialog alertDialog = new AlertDialog.Builder(DetailActivity.this).create();
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

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception f) {
            AlertDialog alertDialog = new AlertDialog.Builder(DetailActivity.this).create();
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
        pd.dismiss();
    }

    // handles the on back press { goes to the previuos activity }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    //this method is called when the + button is clicked.
    public void increment(View view) {
        a = a + 1;
        // displayprice(a);
        display(a);
    }

    //this method is called when the - button is clicked.
    public void decrement(View view) {
        if (a <= 0) {

        } else {
            a = a - 1;
            display(a);
        }

        //  displayprice(a);
    }

    //this method is called when any of the radio button is clicked
    public void radio(View view) {
        boolean ticked = ((RadioButton) view).isChecked();

        int i = view.getId();
        switch (i) {
            case R.id.pickup:
                if (ticked) {
                    ans = "Pick up";
                    editText.setEnabled(false);
                    editText.setText("");

                }
                break;
            case R.id.deliver:
                if (ticked) {
                    editText.setEnabled(ticked);
                    ans = "Deliver";
                }
                break;
        }
    }

    //    this method is called to show the result
    private void display(int number) {
        TextView qtv =  findViewById(R.id.quantity_text);
        qtv.setText("" + number);
    }

    //this method is called when the clear button is clicked.
    public void clear(View view) {
        a = 0;
        display(a);
    }

    //    this method is called to decode image string from firebase
    public static Bitmap decodeFromFirebaseBase64(String image) throws IOException {
        byte[] decodedByteArray = android.util.Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }

    //this method is called when the check button is clicked.
    public void check(View view) {
        int dev = 0;
        try {
            quant = Integer.parseInt(qty.getText().toString());
            if (qty.getText().equals("0")) {
                Toast.makeText(getApplicationContext(), "Please choose the food quantity", Toast.LENGTH_SHORT).show();
                return;
            } if (ans.equals("")) {
                Toast.makeText(getApplicationContext(), "Please Select Collection mode", Toast.LENGTH_SHORT).show();
                return;
            }
            String addess = editText.getText().toString();
            if (ans.equals("Deliver") && addess.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please Enter a delivery address", Toast.LENGTH_SHORT).show();
                return;
            }else {
                pd.setMessage("Please wait...");
                pd.setCanceledOnTouchOutside(false);
                pd.show();
                CheckBox h2o = findViewById(R.id.checkbox);
                boolean hash20 = h2o.isChecked();

                if (hash20) {
                    h20s = 100;
                }
                if (ans.equals("Deliver")) {
                    dev = 50;
                }

                int total;
                int pri = Integer.parseInt(price);
                total = (pri * quant) + h20s + dev;
//                Toast.makeText(DetailActivity.this, "Total = " + total, Toast.LENGTH_SHORT).show();
                pd.dismiss();
                Intent zz = new Intent(DetailActivity.this, SummaryActivity.class);
                zz.putExtra("FoodID", ids);
                zz.putExtra("price", price);
                zz.putExtra("topping", h20s);
                zz.putExtra("checktop", hash20);
                zz.putExtra("title", title);
                zz.putExtra("collection", ans);
                zz.putExtra("address", addess);
                zz.putExtra("Food ID", ids);
                zz.putExtra("Total", total);
                zz.putExtra("Quan", qty.getText().toString());
                startActivity(zz);

            }
        } catch (Exception f) {
            AlertDialog alertDialog = new AlertDialog.Builder(DetailActivity.this).create();
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
        pd.dismiss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Menu inflater
        getMenuInflater().inflate(R.menu.details, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
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