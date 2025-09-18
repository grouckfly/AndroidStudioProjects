package com.example.praktikum2;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    EditText inputTinggi, inputBerat;
    Button hitungButton;
    TextView hasilText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();

        hitungButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hitungBMI();

                double hasilKonversi = hitungTinggi();

                if (hasilKonversi != -1) {
                    tampilHasil(hasilKonversi);
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void findViews() {
        inputTinggi = findViewById(R.id.input_tinggi);
        inputBerat = findViewById(R.id.input_berat);
        hitungButton = findViewById(R.id.hitung_button);
        hasilText = findViewById(R.id.hasil_text);
    }

    private double hitungTinggi() {
        String tinggiStr = inputTinggi.getText().toString();

        try {
            double tinggiCm = Double.parseDouble(tinggiStr);
            return tinggiCm / 3.28;
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Input tidak valid, harap masukkan angka", Toast.LENGTH_SHORT).show();
            return -1;
        }
    }

    private void tampilHasil(double hasilfeet) {
        String hasilFormatted = String.format("%.2f", hasilfeet);
        hasilText.setText(hasilFormatted + " feet");
    }

    private void hitungBMI() {
        String tinggiStr = inputTinggi.getText().toString();
        String beratStr = inputBerat.getText().toString();

        if (tinggiStr.isEmpty() || beratStr.isEmpty()) {
            Toast.makeText(this,
                    "Tinggi dan Berat Badan harus diisi",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double tinggiCm = Double.parseDouble(tinggiStr);
            double beratKg = Double.parseDouble(beratStr);

            if (tinggiCm == 0) {
                Toast.makeText(this,
                        "Tinggi badan tidak boleh nol",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            double tinggiM = tinggiCm / 100.0;
            double bmi = beratKg / (tinggiM * tinggiM);

            String kategori;
            if (bmi < 18.5) {
                kategori = "Kurus (Underweight)";
            } else if (bmi < 24.9) {
                kategori = "Normal (Ideal)";
            } else if (bmi < 29.9) {
                kategori = "Gemuk (Overweight)";
            } else {
                kategori = "Obesitas (Obesity)";
            }

            DecimalFormat df = new DecimalFormat("0.0");
            String hasilBmiFormatted = df.format(bmi);
            String pesan = "BMI Anda: " + hasilBmiFormatted + "\nKategori: " + kategori;

            Toast.makeText(this, pesan, Toast.LENGTH_LONG).show();

        } catch (NumberFormatException e) {
            Toast.makeText(this,
                    "Input tidak valid, harap masukkan angka",
                    Toast.LENGTH_SHORT).show();
        }
    }
}