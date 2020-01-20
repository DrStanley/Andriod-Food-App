package com.stanleyj.android.food_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UpdateRemoveActivity extends AppCompatActivity {
    EditText editTextName, editTextPrice, editTextDescrp;
    String ids, bar;
    ImageView imageView1, imageView2, imageView3;
    Spinner spinner1;
    FirebaseDatabase mFirebaseDatabase;
    ImageButton edImageButton;
    DatabaseReference mDatabaseReference;
    Button text;
    ArrayList<String> list2 = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_remove);
        //            getting intent from previous activity
        ids = getIntent().getStringExtra("foodID");
        bar = getIntent().getStringExtra("title");
        getSupportActionBar().setTitle(bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        text = (Button) findViewById(R.id.UPRE);
        edImageButton = (ImageButton) findViewById(R.id.editable);
        spinner1 = (Spinner) findViewById(R.id.UR_spinner);
        addStatus();

        text.setText(bar);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bar.equals("Remove")) {
                    remove();
                } else if (bar.equals("Update")) {
                    update();
                } else {
                    Toast.makeText(UpdateRemoveActivity.this, "Please wait...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        edImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enab(true);
            }
        });
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, list2);
        // Apply the adapter to the spinner
        spinner1.setAdapter(adapter);

        editTextName = (EditText) findViewById(R.id.UR_foodname);
        editTextDescrp = (EditText) findViewById(R.id.UR_descrip);
        editTextPrice = (EditText) findViewById(R.id.UR_price);
        imageView1 = (ImageView) findViewById(R.id.URimg1);
        imageView2 = (ImageView) findViewById(R.id.URimg2);
        imageView3 = (ImageView) findViewById(R.id.URimg3);
        enab(false);
        if (bar.equals("Remove")) {
            edImageButton.setVisibility(View.INVISIBLE);
            LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) edImageButton.getLayoutParams();
            params2.height = 0;
            edImageButton.setLayoutParams(params2);
        }


        // send query to firebase
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("Menu Item").child(ids);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            // getting clicked data from database
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String title = dataSnapshot.child("title").getValue(String.class);
                String imh1 = dataSnapshot.child("image1").getValue(String.class);
                String imh2 = dataSnapshot.child("image2").getValue(String.class);
                String imh3 = dataSnapshot.child("image3").getValue(String.class);
                String des = dataSnapshot.child("description").getValue(String.class);
                String sta = dataSnapshot.child("food_status").getValue(String.class);
                String price = dataSnapshot.child("price").getValue(String.class);

                editTextName.setText(title);
                editTextDescrp.setText(des);
                setSpinnerText(spinner1, sta);
                editTextPrice.setText("₦" + price);
                try {
                    imageView1.setImageBitmap(decodeFromFirebaseBase64(imh1));
                    imageView2.setImageBitmap(decodeFromFirebaseBase64(imh2));
                    imageView3.setImageBitmap(decodeFromFirebaseBase64(imh3));

                } catch (Exception f) {
                    AlertDialog alertDialog = new AlertDialog.Builder(UpdateRemoveActivity.this).create();
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


    }


    void addStatus() {
        list2.add("Select Food Status");
        list2.add("Available");
        list2.add("Unavailable");
        list2.add("Finished");
    }

    //    this method is called to decode image string from firebase
    public static Bitmap decodeFromFirebaseBase64(String image) throws IOException {
        byte[] decodedByteArray = android.util.Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }

    //    this method is called to enable or disable the views in the layout
    private void enab(boolean x) {
        editTextName.setEnabled(x);
        editTextDescrp.setEnabled(x);
        editTextPrice.setEnabled(x);
        imageView1.setEnabled(x);
        spinner1.setEnabled(x);
        imageView2.setEnabled(x);
        imageView3.setEnabled(x);
    }

    private void update() {

        if (TextUtils.isEmpty(editTextName.getText().toString())) {
            //product name is empty
            Toast.makeText(this, "Please Enter Food Name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(editTextDescrp.getText().toString())){
            //product name is empty
            Toast.makeText(this, "Please Enter Food Description", Toast.LENGTH_SHORT).show();
            return;
        }

        if (spinner1.getSelectedItem().toString().equals("Select Food Status")) {
            Toast.makeText(getApplicationContext(), "Please Select Food Status ", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(editTextPrice.getText().toString())){
            //product name is empty
            Toast.makeText(this, "Please Enter Food price", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(UpdateRemoveActivity.this, "Updating...", Toast.LENGTH_SHORT).show();
        Map<String, Object> updates = new HashMap<String, Object>();

        Drawable mDrawable1 = imageView1.getDrawable();
        Drawable mDrawable2 = imageView2.getDrawable();
        Drawable mDrawable3 = imageView3.getDrawable();
        Bitmap mBitmap1 = ((BitmapDrawable) mDrawable1).getBitmap();
        Bitmap mBitmap2 = ((BitmapDrawable) mDrawable2).getBitmap();
        Bitmap mBitmap3 = ((BitmapDrawable) mDrawable3).getBitmap();
        String imgUri1 = encodeBitmapAndSaveToFirebase(mBitmap1);
        String imgUri2 = encodeBitmapAndSaveToFirebase(mBitmap2);
        String imgUri3 = encodeBitmapAndSaveToFirebase(mBitmap3);

        updates.put("title", editTextName.getText().toString());
        updates.put("image1", imgUri1);
        updates.put("image2", imgUri2);
        updates.put("image3", imgUri3);
        updates.put("description", editTextDescrp.getText().toString());
        updates.put("food_status", spinner1.getSelectedItem().toString());
        updates.put("price", editTextPrice.getText().toString().replace("₦", ""));


        mDatabaseReference.updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                AlertDialog alertDialog = new AlertDialog.Builder(getApplicationContext()).create();
                alertDialog.setTitle("Update " + ids);
                alertDialog.setMessage("The product  has been Updated");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                AlertDialog alertDialog = new AlertDialog.Builder(getApplicationContext()).create();
                alertDialog.setTitle("Error Alert");
                alertDialog.setMessage("The following Error occurred\n" + e.getMessage());
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
            }
        });

    }

    public String encodeBitmapAndSaveToFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        return imageEncoded;
    }

    private void remove() {
        Toast.makeText(UpdateRemoveActivity.this, "Removing...", Toast.LENGTH_SHORT).show();
        mDatabaseReference.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                AlertDialog alertDialog = new AlertDialog.Builder(UpdateRemoveActivity.this).create();
                alertDialog.setTitle(ids + " Deleted");
                alertDialog.setMessage("Food has been deleted");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
            }
        });


    }

    private void setSpinnerText(Spinner spin, String txt) {
        for (int o = 0; o < spin.getAdapter().getCount(); o++) {
            if (spin.getAdapter().getItem(o).toString().contains(txt)) {
                spin.setSelection(o);
            }
        }
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

}
