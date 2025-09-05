package com.example.prak1;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private int clickCount = 0;
    private final String nama = "Afansyah Putra Siregar";
    private final String nrp = "3224600072";

    public void onBtnClick(View view) {
        TextView textView = findViewById(R.id.txtHello);
        clickCount++;

        if (clickCount == 1) {
            textView.setText("Hello Good Morning!");
        } else if (clickCount == 2) {
            String displayText = "Nama: " + nama + "\nNRP: " + nrp;
            textView.setText(displayText);

            clickCount = 0;
        }
    }
}