package com.example.miuwtrack;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistorialActivity extends AppCompatActivity {

    private RecyclerView rvPacientes;
    private ImageButton btnVolver;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        // Configuración Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://miuwtrackp-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseRef = database.getReference("registros_mascotas");

        // Inicializar vistas
        rvPacientes = findViewById(R.id.rvPacientes);
        btnVolver = findViewById(R.id.btnVolver);

        // Configurar RecyclerView
        rvPacientes.setLayoutManager(new LinearLayoutManager(this));
        cargarPacientes();

        btnVolver.setOnClickListener(v -> finish());
    }

    private void cargarPacientes() {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Paciente> pacientes = new ArrayList<>();

                for (DataSnapshot pacienteSnapshot : snapshot.getChildren()) {
                    String id = pacienteSnapshot.getKey();
                    String nombre = pacienteSnapshot.child("dueno/nombre").getValue(String.class);
                    String telefono = pacienteSnapshot.child("dueno/telefono").getValue(String.class);
                    String mascota = pacienteSnapshot.child("mascota/nombre_paciente").getValue(String.class);
                    String especie = pacienteSnapshot.child("mascota/especie").getValue(String.class);
                    String registro = pacienteSnapshot.child("mascota/numero_registro").getValue(String.class);

                    if (nombre != null && registro != null) {
                        pacientes.add(new Paciente(id, nombre, telefono, mascota, especie, registro));
                    }
                }

                rvPacientes.setAdapter(new PacienteAdapter(pacientes));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HistorialActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}