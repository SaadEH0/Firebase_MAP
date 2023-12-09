package com.example.firebase_map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.widget.Toast;

import android.os.Bundle;
import android.view.MenuItem;
import android.content.Intent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar); //Ignore red line errors
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav,
                R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.nav_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        } else if (itemId == R.id.nav_settings) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
        } else if (itemId == R.id.nav_share) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ShareFragment()).commit();
        } else if (itemId == R.id.nav_about) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AboutFragment()).commit();
        } else if (itemId == R.id.nav_logout) {
            FirebaseAuth auth = FirebaseAuth.getInstance();

            // Check if the user is signed in with Firebase Email/Password
            if (auth.getCurrentUser() != null) {
                // Log out the user using Firebase Email/Password
                auth.signOut();
                // Display a message indicating successful logout
                Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            } else {
                // Handle Google Sign-In logout
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
                GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);

                googleSignInClient.signOut().addOnCompleteListener(this, task -> {
                    // Display a message indicating successful logout
                    Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

                    // You can perform any additional actions after Google Sign-Out here

                    // Redirect to the login screen or perform any other necessary actions
                    // For example, you can start a new LoginActivity using an Intent
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);

                    // Finish the current activity to prevent the user from navigating back
                    finish();
                });
            }

            // Redirect to the login screen or perform any other necessary actions
            // For example, you can start a new LoginActivity using an Intent
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);

            // Finish the current activity to prevent the user from navigating back
            finish();
        }




        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}