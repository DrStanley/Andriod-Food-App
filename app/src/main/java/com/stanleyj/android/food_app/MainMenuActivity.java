package com.stanleyj.android.food_app;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
//import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.stanleyj.android.food_app.admin.AddfoodActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainMenuActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle barDrawerToggle;
    RecyclerView mRecyclerView;

    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    LinearLayoutManager mLinearLayoutManager;
    SharedPreferences mSharedPreferences;
    private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pd = new ProgressDialog(this);
        setContentView(R.layout.activity_main_menu);
        drawerLayout =  findViewById(R.id.Drawer);
        barDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(barDrawerToggle);
        barDrawerToggle.syncState();
        NavigationView nV =  findViewById(R.id.nav_view);
        nV.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.hep:
                        return true;
                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                        return true;
                    case R.id.history:
                        startActivity(new Intent(getApplicationContext(),HistoryActivity.class));
                        return true;
                    case R.id.setting:
                        return true;
                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        finish();
                        return true;
                }
                return true;
            }
        });


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mSharedPreferences = getSharedPreferences("SortSettings", MODE_PRIVATE);
        String mSorting = mSharedPreferences.getString("Sort", "newest");
        if (mSorting.equals("newest")) {
            mLinearLayoutManager = new LinearLayoutManager(this);

            mLinearLayoutManager.setReverseLayout(true);
            mLinearLayoutManager.setStackFromEnd(true);
        } else if (mSorting.equals("oldest")) {
            mLinearLayoutManager = new LinearLayoutManager(this);

            mLinearLayoutManager.setReverseLayout(false);
            mLinearLayoutManager.setStackFromEnd(false);
        }

        // Action bar title
        getSupportActionBar();
        getSupportActionBar().setTitle("Main Menu");

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        // send query to firebase
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("Menu Item");
    }

    //    Method that convert first letter of each word in a string into an uppercase letter.
    @NonNull
    private String capitalize(String capString) {
        StringBuffer capBuffer = new StringBuffer();
        Matcher capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString);
        while (capMatcher.find()) {
            capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
        }
        return capMatcher.appendTail(capBuffer).toString();
    }

    // search firebase method
    private void firebaseSearch(String searchText) {
        searchText = capitalize(searchText);
        try {
            final Query firebaseQuery = mDatabaseReference.orderByChild("title").startAt(searchText).endAt(searchText + "\uf8ff");
            FirebaseRecyclerAdapter<Model, ViewHolder> firebaseRecyclerAdapter =
                    new FirebaseRecyclerAdapter<Model, ViewHolder>(
                            Model.class,
                            R.layout.row,
                            ViewHolder.class,
                            firebaseQuery
                    ) {
                        @Override
                        protected void populateViewHolder(ViewHolder viewHolder, Model model, int position) {
                            firebaseQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChildren()) {
                                        Toast.makeText(MainMenuActivity.this, "No result found", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            viewHolder.setDetails(getApplicationContext(), model.getTitle(), model.getImage1(),
                                    model.getImage2(), model.getImage3(), model.getDescription(), model.getFood_status(), model.getPrice(), model.getFoodID());
                            pd.dismiss();
                        }

                        @Override
                        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                            ViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);

                            viewHolder.setOnClickListener(new ViewHolder.ClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                    TextView ids =  view.findViewById(R.id.foodID);
                                    Intent intent = new Intent(view.getContext(), DetailActivity.class);
                                    intent.putExtra("foodID", ids.getText().toString());
                                    startActivity(intent);
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
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Please wait for Image to Load", Toast.LENGTH_SHORT).show();
        }
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
                        Toast.makeText(MainMenuActivity.this, "No result found", Toast.LENGTH_SHORT).show();
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
                            R.layout.row,
                            ViewHolder.class,
                            mDatabaseReference.orderByChild("title")
                    ) {
                        @Override
                        protected void populateViewHolder(ViewHolder viewHolder, Model model, int position) {

                            viewHolder.setDetails(getApplicationContext(), model.getTitle(), model.getImage1(),
                                    model.getImage2(), model.getImage3(), model.getDescription(), model.getFood_status(), model.getPrice(), model.getFoodID());
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
                                        TextView ids = (TextView) view.findViewById(R.id.foodID);
                                        Intent intent = new Intent(view.getContext(), DetailActivity.class);
                                        intent.putExtra("foodID", ids.getText().toString());
                                        startActivity(intent);
                                    } catch (Exception e) {
                                        Toast.makeText(getApplicationContext(), "Please wait for Image to Load", Toast.LENGTH_SHORT).show();
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
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Please wait for Image to Load", Toast.LENGTH_SHORT).show();
        }
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Menu inflater
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                firebaseSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                firebaseSearch(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_Sort) {
            // displays dailog box to select sort option
            showSortDialog();
            return true;
        }
//        if (id == R.id.action_add) {
//
//            Intent im = new Intent(getApplicationContext(), AddfoodActivity.class);
//            startActivity(im);
//            return true;
//        }

        if (barDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showSortDialog() {

        String[] sortOptions = {"Newest", "Oldest"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sort By").setIcon(R.drawable.ic_action_sort).setItems(sortOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                // the which arg contains index position of the sortOptions
                if (which == 0) {
                    //sort by newest
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putString("Sort", "newest");
                    editor.apply();
                    recreate(); // restart activity

                } else if (which == 1) {
                    //sort by oldest
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putString("Sort", "oldest");
                    editor.apply();
                    recreate();

                }
            }
        });
        builder.show();
    }
}
