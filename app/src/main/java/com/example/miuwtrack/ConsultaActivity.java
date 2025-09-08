package com.example.miuwtrack;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ConsultaActivity extends AppCompatActivity {
    private SearchView searchViewRegistro;
    private TextView tvPacienteInfo, tvMascotaInfo, tvFecha;
    private AutoCompleteTextView actvTipoProcedimiento;
    private EditText etDescripcion, etMedicamentos, etDosificacion;
    private ImageButton btnGuardarProcedimiento, btnVolver;
    private RecyclerView rvProcedimientos;

    private DatabaseReference databaseReference;
    private String currentPacienteId = null; // Para almacenar el ID del paciente actualmente cargado
    private ProcedimientoAdapter procedimientoAdapter;
    private List<Procedimiento> procedimientoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_consulta);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar Firebase con la URL correcta
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://miuwtrackp-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = database.getReference("registros_mascotas");

        // Referencias a las vistas
        searchViewRegistro = findViewById(R.id.searchViewRegistro);
        tvPacienteInfo = findViewById(R.id.tvPacienteInfo);
        tvMascotaInfo = findViewById(R.id.tvMascotaInfo);
        tvFecha = findViewById(R.id.tvFecha);
        actvTipoProcedimiento = findViewById(R.id.actvTipoProcedimiento);
        etDescripcion = findViewById(R.id.etDescripcion);
        etMedicamentos = findViewById(R.id.etMedicamentos);
        etDosificacion = findViewById(R.id.etDosificacion);
        btnGuardarProcedimiento = findViewById(R.id.btnGuardarProcedimiento);
        btnVolver = findViewById(R.id.btnVolver); // Initialize btnVolver here
        rvProcedimientos = findViewById(R.id.rvProcedimientos);

        // Configurar RecyclerView
        rvProcedimientos.setLayoutManager(new LinearLayoutManager(this));
        procedimientoList = new ArrayList<>();
        procedimientoAdapter = new ProcedimientoAdapter(procedimientoList);
        rvProcedimientos.setAdapter(procedimientoAdapter);

        // Configurar AutoCompleteTextView para Tipo de Procedimiento
        String[] tiposProcedimiento = {"Consulta General", "Vacunación", "Desparasitación", "Cirugía", "Revisión Post-operatoria", "Análisis de Laboratorio", "Estilismo/Peluquería", "Control de Peso"};
        ArrayAdapter<String> tipoProcedimientoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, tiposProcedimiento);
        actvTipoProcedimiento.setAdapter(tipoProcedimientoAdapter);

        // Mostrar la fecha actual
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        tvFecha.setText("Fecha: " + currentDate);

        // Listener para el SearchView
        searchViewRegistro.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                buscarPacientePorNumeroRegistro(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        btnGuardarProcedimiento.setOnClickListener(v -> {
            if (currentPacienteId != null) {
                guardarProcedimiento();
            } else {
                Toast.makeText(ConsultaActivity.this, "Primero busca y selecciona un paciente", Toast.LENGTH_SHORT).show();
            }
        });

        // --- HERE IS THE FIX FOR THE "VOLVER" BUTTON ---
        btnVolver.setOnClickListener(v -> volver());
        // --- END OF FIX ---

        setFormEnabled(false);
    }

    private void buscarPacientePorNumeroRegistro(String numeroRegistro) {
        currentPacienteId = null;
        procedimientoList.clear();
        procedimientoAdapter.notifyDataSetChanged();
        setFormEnabled(false);

        if (numeroRegistro.isEmpty()) {
            Toast.makeText(this, "Ingrese un número de registro", Toast.LENGTH_SHORT).show();
            return;
        }

        // Consulta mejorada para buscar el número de registro
        databaseReference.orderByChild("mascota/numero_registro").equalTo(numeroRegistro)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot pacienteSnapshot : snapshot.getChildren()) {
                                currentPacienteId = pacienteSnapshot.getKey();

                                // Obtener datos del dueño
                                String nombreDueno = pacienteSnapshot.child("dueno/nombre").getValue(String.class);
                                String telefono = pacienteSnapshot.child("dueno/telefono").getValue(String.class);

                                // Obtener datos de la mascota
                                String especie = pacienteSnapshot.child("mascota/especie").getValue(String.class);
                                String raza = pacienteSnapshot.child("mascota/raza").getValue(String.class);

                                // Mostrar información
                                tvPacienteInfo.setText("Dueño: " + nombreDueno + " - Tel: " + telefono);
                                tvMascotaInfo.setText("Mascota: " + especie + " - Raza: " + raza);

                                // Habilitar formulario y cargar procedimientos
                                setFormEnabled(true);
                                cargarProcedimientosPaciente(currentPacienteId);
                                break; // Solo tomamos el primer resultado
                            }
                        } else {
                            Toast.makeText(ConsultaActivity.this, "No se encontró el registro", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ConsultaActivity.this, "Error en la búsqueda: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void cargarProcedimientosPaciente(String pacienteId) {
        databaseReference.child(pacienteId).child("procedimientos")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        procedimientoList.clear();
                        for (DataSnapshot procedimientoSnapshot : snapshot.getChildren()) {
                            Procedimiento procedimiento = procedimientoSnapshot.getValue(Procedimiento.class);
                            if (procedimiento != null) {
                                procedimientoList.add(procedimiento);
                            }
                        }
                        // Optionally sort procedures by date if needed
                        // Collections.sort(procedimientoList, (p1, p2) -> p2.getFecha().compareTo(p1.getFecha()));
                        procedimientoAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ConsultaActivity.this, "Error al cargar procedimientos: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void guardarProcedimiento() {
        String tipo = actvTipoProcedimiento.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String medicamentos = etMedicamentos.getText().toString().trim();
        String dosificacion = etDosificacion.getText().toString().trim();

        // Validación básica
        if (tipo.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, "Tipo y descripción son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear objeto procedimiento
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String fecha = sdf.format(new Date());

        Procedimiento nuevoProcedimiento = new Procedimiento(fecha, tipo, descripcion, medicamentos.isEmpty() ? "N/A" : medicamentos, dosificacion.isEmpty() ? "N/A" : dosificacion);

        // Guardar en Firebase
        DatabaseReference procedimientosRef = databaseReference.child(currentPacienteId).child("procedimientos");
        String procedimientoId = procedimientosRef.push().getKey();

        if (procedimientoId != null) {
            procedimientosRef.child(procedimientoId).setValue(nuevoProcedimiento)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(ConsultaActivity.this, "Procedimiento guardado exitosamente", Toast.LENGTH_SHORT).show();
                        limpiarCamposProcedimiento();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ConsultaActivity.this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void limpiarCamposProcedimiento() {
        actvTipoProcedimiento.setText("");
        etDescripcion.setText("");
        etMedicamentos.setText("");
        etDosificacion.setText("");
    }

    private void setFormEnabled(boolean enabled) {
        actvTipoProcedimiento.setEnabled(enabled);
        etDescripcion.setEnabled(enabled);
        etMedicamentos.setEnabled(enabled);
        etDosificacion.setEnabled(enabled);
        btnGuardarProcedimiento.setEnabled(enabled);
    }

    // New method for the "Volver" button
    private void volver() {
        finish(); // This will close the current activity and return to the previous one
    }
}