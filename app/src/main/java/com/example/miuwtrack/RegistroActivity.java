package com.example.miuwtrack;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout; // Importar LinearLayout
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class RegistroActivity extends AppCompatActivity {

    private EditText etNombre, etDireccion, etTelefono, etEmail;
    private EditText etNumRegistro, etNombreMascota, etEdad, etEspecie, etRaza, etColor;
    private RadioGroup rgGenero, rgEsterilizado;
    private ImageButton btnGuardar, btnModificar, btnCancelar, btnEliminar, btnVolver;
    private SearchView searchView;
    private TextView textViewModo;

    // Referencias a los LinearLayouts que contienen los botones
    private LinearLayout layoutGuardarVolverButtons;
    private LinearLayout layoutModificarEliminarCancelarButtons;

    // Firebase
    private DatabaseReference databaseReference;
    private String registroIdActual = null; // Para almacenar el ID del registro a modificar


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://miuwtrackp-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = database.getReference("registros_mascotas");

        // Inicializar vistas
        initViews();

        // Configurar listeners
        setupListeners();

        // Iniciar en modo nuevo registro
        setModoNuevoRegistro();
    }

    private void initViews() {
        // Datos de la persona
        etNombre = findViewById(R.id.namePersona);
        etDireccion = findViewById(R.id.dirPersona);
        etTelefono = findViewById(R.id.telPersona);
        etEmail = findViewById(R.id.emailPersona);

        // Datos de la mascota
        etNumRegistro = findViewById(R.id.num_registro);
        etNombreMascota = findViewById(R.id.nombreMascota);
        etEdad = findViewById(R.id.edadMascota);
        etEspecie = findViewById(R.id.especie_mascota);
        etRaza = findViewById(R.id.raza_mascota);
        etColor = findViewById(R.id.color_mascota);
        rgGenero = findViewById(R.id.radioGroupGenero);
        rgEsterilizado = findViewById(R.id.radioGroupEsterilizado);

        // Botones
        btnGuardar = findViewById(R.id.boton_guardar);
        btnModificar = findViewById(R.id.boton_modificar);
        btnCancelar = findViewById(R.id.boton_cancelar);
        btnEliminar = findViewById(R.id.boton_eliminar);
        btnVolver = findViewById(R.id.btnVolver);
        searchView = findViewById(R.id.searchViewRegistro);
        textViewModo = findViewById(R.id.textViewModo);

        // Inicializar los LinearLayouts contenedores de botones
        layoutGuardarVolverButtons = findViewById(R.id.layout_guardar_volver_buttons);
        layoutModificarEliminarCancelarButtons = findViewById(R.id.layout_modificar_eliminar_cancelar_buttons);
    }

    private void setupListeners() {
        // Configurar SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                buscarRegistro(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // Listeners de botones
        btnGuardar.setOnClickListener(v -> {
            if (validarCampos()) {
                guardarDatos(false);
            }
        });

        btnModificar.setOnClickListener(v -> {
            Log.d("RegistroActivity", "ID actual al modificar: " + registroIdActual);
            if (validarCampos()) {
                guardarDatos(true);
            }
        });
        btnVolver.setOnClickListener(v -> volver());
        btnCancelar.setOnClickListener(v -> setModoNuevoRegistro());
        btnEliminar.setOnClickListener(v -> eliminarRegistro());
    }

    private void buscarRegistro(String numeroRegistro) {
        if (numeroRegistro.isEmpty()) {
            Toast.makeText(this, "Ingrese un número de registro", Toast.LENGTH_SHORT).show();
            return;
        }

        databaseReference.orderByChild("mascota/numero_registro").equalTo(numeroRegistro)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot registroSnapshot : snapshot.getChildren()) {
                                registroIdActual = registroSnapshot.getKey();
                                cargarDatosRegistro(registroIdActual);
                                setModoEdicion();
                            }
                        } else {
                            Toast.makeText(RegistroActivity.this, "No se encontró el registro", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(RegistroActivity.this, "Error en la búsqueda: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void cargarDatosRegistro(String registroId) {
        databaseReference.child(registroId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Cargar datos del dueño
                    DataSnapshot duenoSnapshot = snapshot.child("dueno");
                    etNombre.setText(duenoSnapshot.child("nombre").getValue(String.class));
                    etDireccion.setText(duenoSnapshot.child("direccion").getValue(String.class));
                    etTelefono.setText(duenoSnapshot.child("telefono").getValue(String.class));
                    etEmail.setText(duenoSnapshot.child("email").getValue(String.class));

                    // Cargar datos de la mascota
                    DataSnapshot mascotaSnapshot = snapshot.child("mascota");
                    etNumRegistro.setText(mascotaSnapshot.child("numero_registro").getValue(String.class));
                    etNombreMascota.setText(mascotaSnapshot.child("nombre_paciente").getValue(String.class));
                    etEdad.setText(mascotaSnapshot.child("edad").getValue(String.class));
                    etEspecie.setText(mascotaSnapshot.child("especie").getValue(String.class));
                    etRaza.setText(mascotaSnapshot.child("raza").getValue(String.class));
                    etColor.setText(mascotaSnapshot.child("color").getValue(String.class));

                    // Configurar RadioButtons
                    String genero = mascotaSnapshot.child("genero").getValue(String.class);
                    if (genero != null) {
                        if (genero.equals("Hembra")) {
                            rgGenero.check(R.id.radioHembra);
                        } else {
                            rgGenero.check(R.id.radioMacho);
                        }
                    }

                    String esterilizado = mascotaSnapshot.child("esterilizado").getValue(String.class);
                    if (esterilizado != null) {
                        if (esterilizado.equals("Sí")) {
                            rgEsterilizado.check(R.id.radioEsterilizadoSi);
                        } else {
                            rgEsterilizado.check(R.id.radioEsterilizadoNo);
                        }
                    }

                    Toast.makeText(RegistroActivity.this, "Registro cargado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RegistroActivity.this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setModoEdicion() {
        // Ocultar el LinearLayout de "Guardar" y "Volver"
        layoutGuardarVolverButtons.setVisibility(View.GONE);
        // Mostrar el LinearLayout de "Modificar", "Eliminar" y "Cancelar"
        layoutModificarEliminarCancelarButtons.setVisibility(View.VISIBLE);

        textViewModo.setText("Editando registro: " + registroIdActual);
        textViewModo.setTextColor(ContextCompat.getColor(this, android.R.color.holo_orange_dark));
    }

    private void setModoNuevoRegistro() {
        // Mostrar el LinearLayout de "Guardar" y "Volver"
        layoutGuardarVolverButtons.setVisibility(View.VISIBLE);
        // Ocultar el LinearLayout de "Modificar", "Eliminar" y "Cancelar"
        layoutModificarEliminarCancelarButtons.setVisibility(View.GONE);

        textViewModo.setText("Modo: Nuevo registro");
        textViewModo.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
        limpiarFormulario();
        searchView.setQuery("", false);
        searchView.clearFocus();
    }

    private boolean validarCampos() {
        boolean valido = true;

        if (etNombre.getText().toString().trim().isEmpty()) {
            etNombre.setError("Nombre obligatorio");
            valido = false;
        }

        if (etTelefono.getText().toString().trim().isEmpty()) {
            etTelefono.setError("Teléfono obligatorio");
            valido = false;
        }

        if (etEspecie.getText().toString().trim().isEmpty()) {
            etEspecie.setError("Especie obligatoria");
            valido = false;
        }

        if (rgGenero.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Seleccione el género", Toast.LENGTH_SHORT).show();
            valido = false;
        }

        if (rgEsterilizado.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Seleccione esterilización", Toast.LENGTH_SHORT).show();
            valido = false;
        }

        return valido;
    }

    private void guardarDatos(boolean esModificacion) {

        if (esModificacion && (registroIdActual == null || registroIdActual.isEmpty())) {
            Toast.makeText(this, "No hay un registro seleccionado para modificar", Toast.LENGTH_SHORT).show();
            return;
        }
        // Obtener valores de los RadioGroups
        String genero = ((RadioButton) findViewById(rgGenero.getCheckedRadioButtonId())).getText().toString();
        String esterilizado = ((RadioButton) findViewById(rgEsterilizado.getCheckedRadioButtonId())).getText().toString();

        // Crear estructura de datos
        Map<String, Object> registro = new HashMap<>();

        // Datos del dueño
        Map<String, Object> dueno = new HashMap<>();
        dueno.put("nombre", etNombre.getText().toString());
        dueno.put("direccion", etDireccion.getText().toString());
        dueno.put("telefono", etTelefono.getText().toString());
        dueno.put("email", etEmail.getText().toString());

        // Datos de la mascota
        Map<String, Object> mascota = new HashMap<>();
        mascota.put("numero_registro", etNumRegistro.getText().toString());
        mascota.put("nombre_paciente", etNombreMascota.getText().toString());
        mascota.put("edad", etEdad.getText().toString());
        mascota.put("especie", etEspecie.getText().toString());
        mascota.put("raza", etRaza.getText().toString());
        mascota.put("color", etColor.getText().toString());
        mascota.put("genero", genero);
        mascota.put("esterilizado", esterilizado);

        // Combinar ambos mapas
        registro.put("dueno", dueno);
        registro.put("mascota", mascota);

        if (esModificacion) {
            // Modificar registro existente
            databaseReference.child(registroIdActual).updateChildren(registro)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegistroActivity.this, "Registro actualizado", Toast.LENGTH_SHORT).show();
                            setModoNuevoRegistro();
                        } else {
                            Toast.makeText(RegistroActivity.this, "Error al actualizar", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Guardar nuevo registro
            String nuevoRegistroId = databaseReference.push().getKey();
            databaseReference.child(nuevoRegistroId).setValue(registro)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegistroActivity.this, "Registro guardado exitosamente", Toast.LENGTH_SHORT).show();
                            setModoNuevoRegistro(); // Reiniciar el formulario
                        } else {
                            Toast.makeText(RegistroActivity.this, "Error al guardar registro", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void eliminarRegistro() {
        if (registroIdActual != null && !registroIdActual.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle("Eliminar Registro")
                    .setMessage("¿Estás seguro de que quieres eliminar este registro?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        databaseReference.child(registroIdActual).removeValue()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegistroActivity.this, "Registro eliminado", Toast.LENGTH_SHORT).show();
                                        setModoNuevoRegistro();
                                    } else {
                                        Toast.makeText(RegistroActivity.this, "Error al eliminar", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    })
                    .setNegativeButton("No", null)
                    .show();
        } else {
            Toast.makeText(this, "No hay registro para eliminar", Toast.LENGTH_SHORT).show();
        }
    }

    private void limpiarFormulario() {
        etNombre.setText("");
        etDireccion.setText("");
        etTelefono.setText("");
        etEmail.setText("");
        etNumRegistro.setText("");
        etNombreMascota.setText("");
        etEdad.setText("");
        etEspecie.setText("");
        etRaza.setText("");
        etColor.setText("");
        rgGenero.clearCheck();
        rgEsterilizado.clearCheck();
        registroIdActual = null; // Limpiar el ID del registro actual al limpiar el formulario
    }

    private void volver() {
        // Vuelve a la actividad principal o anterior.
        Intent intent = new Intent(RegistroActivity.this, MenuActivity.class); //
        finish(); // Cierra esta actividad para que no se apile
    }
}