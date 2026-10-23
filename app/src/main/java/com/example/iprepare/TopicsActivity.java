package com.example.iprepare;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

public class TopicsActivity extends AppCompatActivity {

    private String topicName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topics);

        // 🟢 Setup Toolbar
        Toolbar toolbar = findViewById(R.id.topicsToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // 🟢 Get topic name
        topicName = getIntent().getStringExtra("TOPIC_NAME");

        // 🟢 Set toolbar title dynamically
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(topicName + " Modules");
        }

        // 🟢 Set topic title text
        TextView topicTitle = findViewById(R.id.topicTitle);
        topicTitle.setText(topicName + " Preparedness Topics");

        // 🟢 Reference the cards instead of buttons
        CardView module1Card = findViewById(R.id.module1Card);
        CardView module2Card = findViewById(R.id.module2Card);
        CardView module3Card = findViewById(R.id.module3Card);

        // 🟢 Add click listeners
        module1Card.setOnClickListener(v -> openModule(1));
        module2Card.setOnClickListener(v -> openModule(2));
        module3Card.setOnClickListener(v -> openModule(3));
    }

    // 🟢 Open module content
    private void openModule(int moduleNumber) {
        Intent intent = new Intent(this, ModuleContentActivity.class);
        intent.putExtra("TOPIC_NAME", topicName);
        intent.putExtra("MODULE_NUMBER", moduleNumber);
        startActivity(intent);
    }

    // 🟢 Handle back arrow
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
