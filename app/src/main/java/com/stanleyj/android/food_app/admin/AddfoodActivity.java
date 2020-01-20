package com.stanleyj.android.food_app.admin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stanleyj.android.food_app.Model;
import com.stanleyj.android.food_app.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class AddfoodActivity extends AppCompatActivity {
    FirebaseDatabase mFirebaseDatabase;
    FirebaseAuth firebaseAuth;
    DatabaseReference mDatabaseReference;
    EditText editTextName, editTextPrice, editTextDescrp;
    ImageView imageView1, imageView2, imageView3;
    Button buttonAdd;
    ArrayList<String> list2 = new ArrayList<>();
    Spinner spinner;

    private static final int PICK_IMAGE = 100;
    boolean img1, img2, img3;
    String imgUri1, imgUri2, imgUri3, tag;
    Uri imgUri;
    private ProgressDialog pd;
    String nam, spin;

    String encodeBitmapAndSaveToFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfood);
        getSupportActionBar().setTitle("Add New Food");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        pd = new ProgressDialog(this);
        addStatus();
        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("Menu Item");
        spinner = (Spinner) findViewById(R.id.status_spinner);


        editTextName = (EditText) findViewById(R.id.foodname);
        editTextDescrp = (EditText) findViewById(R.id.food_descrip);
        editTextPrice = (EditText) findViewById(R.id.food_price);
        imageView1 = (ImageView) findViewById(R.id.img1);
        imageView2 = (ImageView) findViewById(R.id.img2);
        imageView3 = (ImageView) findViewById(R.id.img3);
        buttonAdd = (Button) findViewById(R.id.food_add);
        clicks();

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, list2);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);


    }

    private void openImage() {
        try {
            Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(gallery, PICK_IMAGE);
        } catch (Exception ignored) {

        }
    }

    private void clicks() {
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loads();
            }
        });
        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img1 = true;
                openImage();

            }
        });
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img2 = true;
                openImage();

            }
        });
        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img3 = true;
                openImage();

            }
        });
    }

    private void loads() {

        String des = editTextDescrp.getText().toString();
        String pri = editTextPrice.getText().toString();
        nam = editTextName.getText().toString();

        if (TextUtils.isEmpty(nam)) {
            //product name is empty
            Toast.makeText(this, "Please Enter Food Name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(des)) {
            //product name is empty
            Toast.makeText(this, "Please Enter Food Description", Toast.LENGTH_SHORT).show();
            return;
        }
        spin = spinner.getSelectedItem().toString();

        if (spin.equals("Select Food Status")) {
            Toast.makeText(getApplicationContext(), "Please Select Food Status ", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(pri)) {
            //product name is empty
            Toast.makeText(this, "Please Enter Food price", Toast.LENGTH_SHORT).show();
            return;
        }

        Drawable mDrawable1 = imageView1.getDrawable();
        Drawable mDrawable2 = imageView2.getDrawable();
        Drawable mDrawable3 = imageView3.getDrawable();
        Bitmap mBitmap1 = ((BitmapDrawable) mDrawable1).getBitmap();
        Bitmap mBitmap2 = ((BitmapDrawable) mDrawable2).getBitmap();
        Bitmap mBitmap3 = ((BitmapDrawable) mDrawable3).getBitmap();
        imgUri1 = encodeBitmapAndSaveToFirebase(mBitmap1);
        imgUri2 = encodeBitmapAndSaveToFirebase(mBitmap2);
        imgUri3 = encodeBitmapAndSaveToFirebase(mBitmap3);
        String tag = "FD" + System.currentTimeMillis();

        try {
            Model ss = new Model(nam, imgUri1, imgUri2, imgUri3, des, spin, pri, tag);

            pd.setMessage("Uploading Data...");
            pd.show();
            pd.setCanceledOnTouchOutside(false);
            mDatabaseReference.child(tag).setValue(ss).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    pd.dismiss();
                    if (task.isSuccessful()) {
                        AlertDialog alertDialog = new AlertDialog.Builder(AddfoodActivity.this).create();
                        alertDialog.setTitle("Upload Complete");
                        alertDialog.setMessage(nam + " has been uploaded successfully");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    } else {
                        pd.dismiss();
                        AlertDialog alertDialog = new AlertDialog.Builder(AddfoodActivity.this).create();
                        alertDialog.setTitle("Alert");
                        alertDialog.setMessage("Couldn't Upload Data \n"
                                + task.getException().getMessage());
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
        } catch (Exception f) {
            pd.dismiss();
            AlertDialog alertDialog = new AlertDialog.Builder(AddfoodActivity.this).create();
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

    void addStatus() {
        list2.add("Select Food Status");
        list2.add("Available");
        list2.add("Finished");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            imgUri = data.getData();

            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
            if (img1) {
                imageView1.setImageBitmap(bitmap);
                img1 = false;
            } else if (img2) {
                imageView2.setImageBitmap(bitmap);
                img2 = false;
            } else if (img3) {
                imageView3.setImageBitmap(bitmap);
                img3 = false;
            }

        } catch (Exception e) {
            AlertDialog alertDialog = new AlertDialog.Builder(AddfoodActivity.this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("Error Updating\n" + e.getMessage());
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    });
        }
        super.onActivityResult(requestCode, resultCode, data);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
