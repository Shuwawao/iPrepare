package com.example.iprepare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    private TextView welcomeText;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigation;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;

    private int currentItemId = R.id.nav_home; // track current selected nav

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 🔹 Firebase setup
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // 🔹 UI setup
        toolbar = findViewById(R.id.topAppBar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigationView);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        // 🔹 Toolbar + Drawer setup
        setSupportActionBar(toolbar);
        drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // 🔹 Drawer Menu actions
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_profile) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            } else if (id == R.id.nav_emergency_info) {
                startActivity(new Intent(MainActivity.this, EmergencyActivity.class));
            } else if (id == R.id.nav_updates) {
                startActivity(new Intent(MainActivity.this, UpdatesActivity.class));
            } else if (id == R.id.nav_logout) {
                auth.signOut();
                Toast.makeText(MainActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // 🔹 Bottom Navigation → Animated fragment switch
        bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int newItemId = item.getItemId();

            if (newItemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (newItemId == R.id.nav_modules) {
                selectedFragment = new ModulesFragment();
            } else if (newItemId == R.id.nav_prepare) {
                selectedFragment = new PrepareFragment();
            } else if (newItemId == R.id.nav_quiz) {
                selectedFragment = new QuizFragment();
            }

            if (selectedFragment != null) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                // 🔸 Decide animation direction based on navigation
                if (newItemId > currentItemId) {
                    transaction.setCustomAnimations(
                            R.anim.slide_in_right, R.anim.slide_out_left
                    );
                } else if (newItemId < currentItemId) {
                    transaction.setCustomAnimations(
                            R.anim.slide_in_left, R.anim.slide_out_right
                    );
                }

                transaction.replace(R.id.fragment_container, selectedFragment);
                transaction.commit();
                currentItemId = newItemId; // update tracker
            }

            return true;
        });

        // 🔹 Redirect if not logged in
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

        // 🔹 Load user data safely and populate drawer header
        View headerView = navigationView != null && navigationView.getHeaderCount() > 0 ? navigationView.getHeaderView(0) : null;
        TextView navHeaderTitle = headerView != null ? headerView.findViewById(R.id.navHeaderTitle) : null;
        TextView navHeaderSubtitle = headerView != null ? headerView.findViewById(R.id.navHeaderSubtitle) : null;

        if (auth.getCurrentUser() != null) {
            String email = auth.getCurrentUser().getEmail();
            if (navHeaderSubtitle != null && email != null) {
                navHeaderSubtitle.setText(email);
            }

            String userId = auth.getCurrentUser().getUid();
            firestore.collection("users").document(userId)
                    .get()
                    .addOnSuccessListener(doc -> {
                        String name = "User";
                        if (doc.exists() && doc.getString("name") != null && !doc.getString("name").isEmpty()) {
                            name = doc.getString("name");
                        }
                        if (navHeaderTitle != null) {
                            navHeaderTitle.setText(name);
                        }
                    })
                    .addOnFailureListener(e -> {
                        if (navHeaderTitle != null) {
                            navHeaderTitle.setText("iPrepare User");
                        }
                    });
        }

        // 🧩 Upload preparedness data only once
        setupPreparednessData();

        // 🔹 Handle return from QuizResultActivity
        String openFragment = getIntent().getStringExtra("openFragment");
        if ("quiz".equals(openFragment)) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new QuizFragment())
                    .commit();
            bottomNavigation.setSelectedItemId(R.id.nav_quiz);
        } else if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
            bottomNavigation.setSelectedItemId(R.id.nav_home);
        }
    }

    // 🧠 FIRESTORE SETUP FOR PREPAREDNESS DATA
    private void setupPreparednessData() {
        firestore.collection("preparedness").get().addOnSuccessListener(query -> {
            if (query.isEmpty()) {
                Log.d("FirestoreSetup", "No preparedness data found, uploading defaults...");
                uploadPreparednessDefaults();
            } else {
                Log.d("FirestoreSetup", "Preparedness data already exists, skipping upload.");
            }
        }).addOnFailureListener(e -> Log.e("FirestoreSetup", "Error checking preparedness collection", e));
    }

    private void uploadPreparednessDefaults() {
        Map<String, Object> earthquake = new HashMap<>();
        earthquake.put("checklist", Arrays.asList(
                "Secure heavy furniture and appliances to walls",
                "Prepare an emergency bag with food, water, and medicine",
                "Identify safe spots under sturdy tables or inside corners",
                "Practice ‘Drop, Cover, and Hold On’ with family",
                "Keep shoes and flashlight near your bed",
                "Store important documents in a waterproof pouch"
        ));
        earthquake.put("tips", Arrays.asList(
                "During shaking: Stay calm, drop, cover, and hold on",
                "Avoid standing near windows or mirrors",
                "If outdoors, move to open areas away from power lines",
                "After shaking: Check for injuries and gas leaks",
                "Use texts instead of calls to avoid network congestion"
        ));
        earthquake.put("progress", 0);

        Map<String, Object> fire = new HashMap<>();
        fire.put("checklist", Arrays.asList(
                "Install smoke alarms in every room",
                "Test alarms monthly and replace batteries yearly",
                "Prepare a small fire extinguisher",
                "Identify 2 exits per room",
                "Practice fire evacuation drills",
                "Keep matches and lighters out of children’s reach",
                "Turn off appliances when not in use"
        ));
        fire.put("tips", Arrays.asList(
                "If trapped: Stay low and crawl under smoke",
                "Never use elevators during fire",
                "If clothes catch fire: Stop, Drop, and Roll",
                "Check doors with back of hand before opening",
                "Call emergency services only when safe"
        ));
        fire.put("progress", 0);

        Map<String, Object> flood = new HashMap<>();
        flood.put("checklist", Arrays.asList(
                "Store drinking water and ready-to-eat food",
                "Move valuables and electricals to higher ground",
                "Prepare waterproof bags for documents",
                "Check drainage and clean gutters regularly",
                "Know your local evacuation area",
                "Keep extra clothes and blankets ready"
        ));
        flood.put("tips", Arrays.asList(
                "Never walk or drive through floodwaters",
                "Stay updated with weather alerts",
                "Turn off main power if flooding starts",
                "Avoid contact with floodwater (can be contaminated)",
                "Boil or purify drinking water after flood"
        ));
        flood.put("progress", 0);

        Map<String, Object> typhoon = new HashMap<>();
        typhoon.put("checklist", Arrays.asList(
                "Reinforce windows and roofs if needed",
                "Store enough food, water, and batteries",
                "Charge phones and power banks",
                "Prepare candles, matches, and flashlights",
                "Bring pets and outdoor items inside",
                "Listen to weather bulletins",
                "Prepare a first-aid kit"
        ));
        typhoon.put("tips", Arrays.asList(
                "Stay indoors and away from windows",
                "Don’t cross flooded streets",
                "Wait for official ‘all clear’ before going outside",
                "Avoid rumors—follow only trusted sources (like PAGASA, NDRRMC)",
                "Prepare to evacuate early if you live in high-risk zones"
        ));
        typhoon.put("progress", 0);

        firestore.collection("preparedness").document("earthquake").set(earthquake);
        firestore.collection("preparedness").document("fire").set(fire);
        firestore.collection("preparedness").document("flood").set(flood);
        firestore.collection("preparedness").document("typhoon").set(typhoon)
                .addOnSuccessListener(aVoid -> Log.d("FirestoreSetup", "Preparedness data uploaded successfully!"))
                .addOnFailureListener(e -> Log.e("FirestoreSetup", "Error uploading preparedness data", e));
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }
}
