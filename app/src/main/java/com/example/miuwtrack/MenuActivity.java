package com.example.miuwtrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    private String userEmail; // To store user email received from LoginActivity
    private String userName;  // To store user name received from LoginActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Retrieve user data from the Intent that started this activity (from LoginActivity)
        Intent incomingIntent = getIntent();
        if (incomingIntent != null) {
            userEmail = incomingIntent.getStringExtra("USER_EMAIL");
            userName = incomingIntent.getStringExtra("USER_NAME");
        }

        // Configure listeners for the image buttons
        ImageButton btnAgenda = findViewById(R.id.btnAgenda);
        ImageButton btnRegistro = findViewById(R.id.btnRegistro);
        ImageButton btnConsulta = findViewById(R.id.btnConsulta);
        ImageButton btnHistorial = findViewById(R.id.btnHistorial);
        ImageButton btnVeterinarios = findViewById(R.id.btnVeterinarios);

        // Set OnClickListener for Agenda button
        btnAgenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuActivity.this, AgendaActivity.class));
            }
        });

        // Set OnClickListener for Registro button
        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuActivity.this, RegistroActivity.class));
            }
        });

        // Set OnClickListener for Consulta button
        btnConsulta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuActivity.this, ConsultaActivity.class));
            }
        });

        // Set OnClickListener for Historial button
        btnHistorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuActivity.this, HistorialActivity.class));
            }
        });

        // Set OnClickListener for Veterinarios button
        btnVeterinarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a new Intent for VeterinariosActivity
                Intent veterinariosIntent = new Intent(MenuActivity.this, VeterinariosActivity.class);

                // Pass the user data received from LoginActivity to VeterinariosActivity
                veterinariosIntent.putExtra("USER_EMAIL", userEmail);
                veterinariosIntent.putExtra("USER_NAME", userName);

                startActivity(veterinariosIntent);
            }
        });
    }
}