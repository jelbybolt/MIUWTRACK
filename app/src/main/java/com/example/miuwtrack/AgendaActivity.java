package com.example.miuwtrack;


import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.ImageButton; // Import ImageButton
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AgendaActivity extends AppCompatActivity {

    // Elementos de la interfaz
    private TextView textViewDayOfWeek, textViewDayNumber, textViewMonth;
    private EditText taskInput;
    private ImageButton agregarButton, finishDayButton, backButton;
    private LinearLayout taskList;
    // Firebase
    private DatabaseReference databaseReference;
    // Formato de fecha
    private SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", new Locale("es", "ES"));
    private SimpleDateFormat dayNumberFormat = new SimpleDateFormat("dd", new Locale("es", "ES"));
    private SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", new Locale("es", "ES"));
    private SimpleDateFormat dateKeyFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_agenda);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://miuwtrackp-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = database.getReference("agenda");
        // Inicializar vistas
        initViews();
        // Configurar fecha actual
        updateDate();
        // Configurar listeners
        setupListeners();
        // Cargar tareas
        loadTasks();
    }
    private void initViews() {
        textViewDayOfWeek = findViewById(R.id.textViewDayOfWeek);
        textViewDayNumber = findViewById(R.id.textViewDayNumber);
        textViewMonth = findViewById(R.id.textViewMonth);
        taskInput = findViewById(R.id.taskInput);
        agregarButton = findViewById(R.id.agregar);
        finishDayButton = findViewById(R.id.finishDayButton);
        backButton = findViewById(R.id.backButton);
        taskList = findViewById(R.id.taskList);
    }

    private void updateDate() {
        Date currentDate = new Date();
        // Actualizar UI
        textViewDayOfWeek.setText(capitalize(dayFormat.format(currentDate)));
        textViewDayNumber.setText(dayNumberFormat.format(currentDate));
        textViewMonth.setText(capitalize(monthFormat.format(currentDate)));
        // Guardar fecha en Firebase
        databaseReference.child("fecha_actual").setValue(dateKeyFormat.format(currentDate));
    }

    private String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private void setupListeners() {
        agregarButton.setOnClickListener(v -> addTask());
        finishDayButton.setOnClickListener(v -> finishDay());
        backButton.setOnClickListener(v -> finish());
    }
    private void addTask() {
        String taskText = taskInput.getText().toString().trim();
        if (!taskText.isEmpty()) {
            String taskId = databaseReference.child("tareas").push().getKey();

            Map<String, Object> task = new HashMap<>();
            task.put("texto", taskText);
            task.put("completada", false);

            databaseReference.child("tareas").child(taskId).setValue(task);
            taskInput.setText("");
        }
    }
    private void loadTasks() {
        databaseReference.child("tareas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                taskList.removeAllViews();

                for (DataSnapshot taskSnapshot : snapshot.getChildren()) {
                    String taskText = taskSnapshot.child("texto").getValue(String.class);
                    Boolean isCompleted = taskSnapshot.child("completada").getValue(Boolean.class);
                    String taskId = taskSnapshot.getKey();

                    if (taskText != null) {
                        addTaskToUI(taskText, isCompleted != null && isCompleted, taskId);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar error
            }
        });
    }
    private void addTaskToUI(String taskText, boolean isCompleted, String taskId) {
        CheckBox checkBox = new CheckBox(this);
        checkBox.setText(taskText);
        checkBox.setChecked(isCompleted);
        checkBox.setTextSize(16);
        checkBox.setPadding(0, 16, 0, 16);

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            databaseReference.child("tareas").child(taskId).child("completada").setValue(isChecked);
        });

        taskList.addView(checkBox);
    }
    private void finishDay() {
        // Eliminar tareas completadas
        databaseReference.child("tareas").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot taskSnapshot : snapshot.getChildren()) {
                    Boolean isCompleted = taskSnapshot.child("completada").getValue(Boolean.class);
                    if (isCompleted != null && isCompleted) {
                        taskSnapshot.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar error
            }
        });
        updateDate();
    }
}
