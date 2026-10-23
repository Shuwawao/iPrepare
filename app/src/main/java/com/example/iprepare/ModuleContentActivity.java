package com.example.iprepare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ModuleContentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module_content);

        TextView title = findViewById(R.id.moduleTitle);
        TextView content = findViewById(R.id.moduleContent);

        String topicName = getIntent().getStringExtra("TOPIC_NAME");
        int moduleNumber = getIntent().getIntExtra("MODULE_NUMBER", 1);

        title.setText(topicName + " - Module " + moduleNumber);

        // Set brief description per topic (optional)
        String description;
        switch (topicName) {
            case "Earthquake":
                description = "Learn earthquake preparedness, safety, and post-disaster response.";
                break;
            case "Flood":
                description = "Understand flood prevention, evacuation, and recovery.";
                break;
            case "Fire":
                description = "Learn fire prevention, safe evacuation, and recovery steps.";
                break;
            case "Typhoon":
                description = "Prepare for typhoons — from early warnings to recovery planning.";
                break;
            default:
                description = "Module details not available.";
        }
        content.setText(description);

        // 🔹 Directly open the PDF
        openPDF(topicName, moduleNumber);
    }

    private void openPDF(String topic, int moduleNumber) {
        String pdfPath = topic.toLowerCase() + "/module" + moduleNumber + ".pdf";

        Intent intent = new Intent(this, PDFViewerActivity.class);
        intent.putExtra("PDF_FILE", pdfPath);
        intent.putExtra("TOPIC_NAME", topic);
        startActivity(intent);

        // Optional: close this activity so it doesn’t stack
        finish();
    }
}
