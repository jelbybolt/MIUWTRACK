package com.example.miuwtrack;



import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetalleHistorialActivity extends AppCompatActivity{ // Obtener el ID del paciente del Intent

private TextView tvHistorial;
private DatabaseReference dbRef;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_detalle_historial);

    String pacienteId = getIntent().getStringExtra("pacienteId");
    tvHistorial = findViewById(R.id.tvHistorial);

    dbRef = FirebaseDatabase.getInstance("https://miuwtrackp-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("registros_mascotas").child(pacienteId);

    cargarHistorial();
}

private void cargarHistorial() {
    dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            StringBuilder historial = new StringBuilder();

            // Datos del dueño
            DataSnapshot dueno = snapshot.child("dueno");
            historial.append("Dueño: ").append(dueno.child("nombre").getValue(String.class)).append("\n");
            historial.append("Teléfono: ").append(dueno.child("telefono").getValue(String.class)).append("\n\n");

            // Datos mascota
            DataSnapshot mascota = snapshot.child("mascota");
            historial.append("Nombre del paciente: ").append(mascota.child("nombre_paciente").getValue(String.class)).append("\n");
            historial.append("Especie: ").append(mascota.child("especie").getValue(String.class)).append("\n");
            historial.append("Raza: ").append(mascota.child("raza").getValue(String.class)).append("\n");
            historial.append("Registro: ").append(mascota.child("numero_registro").getValue(String.class)).append("\n\n");

            // Procedimientos
            historial.append("PROCEDIMIENTOS:\n\n");

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()); // Formato día/mes/año
            String fechaActual = sdf.format(new Date());

            if (snapshot.hasChild("procedimientos")) {
                for (DataSnapshot proc : snapshot.child("procedimientos").getChildren()) {
                    historial.append("Fecha: ").append(fechaActual).append("\n");
                    historial.append("Medicamento: ").append(proc.child("medicamentos").getValue(String.class)).append("\n");
                    historial.append("Tipo: ").append(proc.child("tipoProcedimiento").getValue(String.class)).append("\n");
                    historial.append("Dosificación: ").append(proc.child("descripcion").getValue(String.class)).append("\n\n");
                }
            } else {
                historial.append("No hay procedimientos registrados");
            }

            tvHistorial.setText(historial.toString());
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            tvHistorial.setText("Error al cargar historial");
        }
    });
}
}