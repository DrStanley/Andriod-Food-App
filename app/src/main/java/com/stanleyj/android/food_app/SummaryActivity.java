package com.stanleyj.android.food_app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import co.paystack.android.PaystackSdk;
import co.paystack.android.model.Card;

public class SummaryActivity extends AppCompatActivity {
    TextView tv_food, tv_p, tv_foodQTy, tv_top, tv_cmode, tv_del, tv_tot;
    String food, qty;
    int t;
    int toping;
    String modes;
    String adde;
    boolean tops;
    Button pay;
    String pri;
    LinearLayout liS, liA, liAC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        // action bar for title
        getSupportActionBar().setTitle("Summary");
        // set back button to action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

//        getting intents
        food = getIntent().getStringExtra("title");
        qty = getIntent().getStringExtra("Quan");
        t = getIntent().getIntExtra("Total", 0);
        toping = getIntent().getIntExtra("topping", 0);
        modes = getIntent().getStringExtra("collection");
        adde = getIntent().getStringExtra("address");
        pri = getIntent().getStringExtra("price");
        tops = getIntent().getBooleanExtra("checktop", false);

//        defining all the views
        liA = findViewById(R.id.add);
        liAC = findViewById(R.id.addPay);
        liS = findViewById(R.id.soft);
        tv_food = findViewById(R.id.food);
        tv_tot = findViewById(R.id.tot);
        tv_foodQTy = findViewById(R.id.food_quantity);
        tv_p = findViewById(R.id.p);
        tv_top = findViewById(R.id.tp);
        tv_cmode = findViewById(R.id.c_mode);
        tv_del = findViewById(R.id.deli);
        pay = findViewById(R.id.payment);

        try {

            pay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    startActivity(new Intent(SummaryActivity.this,PaymentActivity.class).putExtra("amount",t+"0"));
                }
            });

//            Toast.makeText(SummaryActivity.this, t + " and " + tops, Toast.LENGTH_SHORT).show();
            if (!tops) {
                liS.setVisibility(View.INVISIBLE);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) liS.getLayoutParams();
                params.height = 0;
                liS.setLayoutParams(params);
            }

            if (modes.equals("Pick up")) {
                liA.setVisibility(View.INVISIBLE);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) liA.getLayoutParams();
                params.height = 0;
                liA.setLayoutParams(params);

                liAC.setVisibility(View.INVISIBLE);
                LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) liAC.getLayoutParams();
                params2.height = 0;
                liAC.setLayoutParams(params2);
            }

            tv_food.setText(food);
            tv_foodQTy.setText("x" + qty);
            tv_p.setText(pri);
            tv_top.setText("" + toping);
            tv_tot.setText("₦" + t);
            tv_cmode.setText(modes);
            tv_del.setText(adde);
        } catch (Exception f) {
            AlertDialog alertDialog = new AlertDialog.Builder(SummaryActivity.this).create();
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
