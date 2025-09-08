package com.example.miuwtrack;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VeterinariosActivity extends AppCompatActivity {

    private ImageButton btnCerrarSesion;
    private ImageButton btnModificarPassword;
    private ImageButton btnVolver; // Declarar el nuevo botón "Volver"

    private TextView userFullNameTextView;
    private TextView userEmailTextView;
    private TextView userPasswordPlaceholderTextView;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_veterinarios);

        auth = FirebaseAuth.getInstance();

        // Inicializar TextViews
        userFullNameTextView = findViewById(R.id.user_full_name);
        userEmailTextView = findViewById(R.id.user_email);
        userPasswordPlaceholderTextView = findViewById(R.id.user_password_placeholder);

        // Obtener datos del Intent (pasados desde LoginActivity, si aplica)
        Intent intent = getIntent();
        if (intent != null) {
            String userName = intent.getStringExtra("USER_NAME");
            String userEmail = intent.getStringExtra("USER_EMAIL");

            if (userName != null && !userName.isEmpty()) {
                userFullNameTextView.setText(userName);
            } else {
                userFullNameTextView.setText("Usuario Veterinario"); // Valor por defecto
            }
            if (userEmail != null && !userEmail.isEmpty()) {
                userEmailTextView.setText(userEmail);
            } else {
                userEmailTextView.setText("email@example.com"); // Valor por defecto
            }
        }

        // Inicializar los ImageButtons
        btnCerrarSesion = findViewById(R.id.btn_cerrar_sesion);
        btnModificarPassword = findViewById(R.id.btn_modificar_password);
        btnVolver = findViewById(R.id.btn_volver); // Inicializar el botón "Volver"

        // Configurar OnClickListener para el botón de cerrar sesión
        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut(); // Cerrar sesión de Firebase
                Toast.makeText(VeterinariosActivity.this, "Sesión cerrada.", Toast.LENGTH_SHORT).show();

                // Navegar a LoginActivity
                Intent logoutIntent = new Intent(VeterinariosActivity.this, LoginActivity.class);
                logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(logoutIntent);
                finish(); // Cerrar VeterinariosActivity
            }
        });

        // Configurar OnClickListener para el botón de modificar contraseña
        btnModificarPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangePasswordDialog();
            }
        });

        // --- NUEVA FUNCIONALIDAD: OnClickListener para el botón "Volver" ---
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar a MenuActivity
                Intent backToMenuIntent = new Intent(VeterinariosActivity.this, MenuActivity.class);
                // Las siguientes banderas son opcionales, pero pueden ser útiles
                // para limpiar la pila de actividades si es necesario.
                // Si solo quieres volver a una instancia existente de MenuActivity, usa FLAG_ACTIVITY_CLEAR_TOP
                // o si MenuActivity ya está en la pila y quieres volver a ella sin crear una nueva.
                // backToMenuIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(backToMenuIntent);
                finish(); // Cerrar VeterinariosActivity para que no se acumulen en la pila
            }
        });
        // --- FIN NUEVA FUNCIONALIDAD ---

        // Aplicar insets del sistema (configuración de EdgeToEdge)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Modificar Contraseña");

        // Configurar los campos de entrada
        final EditText oldPasswordInput = new EditText(this);
        oldPasswordInput.setHint("Contraseña actual");
        oldPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        final EditText newPasswordInput = new EditText(this);
        newPasswordInput.setHint("Nueva contraseña");
        newPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        final EditText confirmNewPasswordInput = new EditText(this);
        confirmNewPasswordInput.setHint("Confirmar nueva contraseña");
        confirmNewPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        // Crear un LinearLayout para contener los EditTexts
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20); // Añadir algo de padding

        layout.addView(oldPasswordInput);
        layout.addView(newPasswordInput);
        layout.addView(confirmNewPasswordInput);
        builder.setView(layout);

        // Configurar los botones del diálogo
        builder.setPositiveButton("Cambiar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String oldPassword = oldPasswordInput.getText().toString().trim();
                String newPassword = newPasswordInput.getText().toString().trim();
                String confirmNewPassword = confirmNewPasswordInput.getText().toString().trim();

                if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
                    Toast.makeText(VeterinariosActivity.this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!newPassword.equals(confirmNewPassword)) {
                    Toast.makeText(VeterinariosActivity.this, "Las nuevas contraseñas no coinciden.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (newPassword.length() < 8) {
                    Toast.makeText(VeterinariosActivity.this, "La nueva contraseña debe tener al menos 8 caracteres.", Toast.LENGTH_SHORT).show();
                    return;
                }

                changePassword(oldPassword, newPassword);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void changePassword(String oldPassword, String newPassword) {
        FirebaseUser user = auth.getCurrentUser();

        if (user != null && user.getEmail() != null) {
            // Re-autenticar al usuario con su contraseña actual
            auth.signInWithEmailAndPassword(user.getEmail(), oldPassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Si la re-autenticación es exitosa, actualizar la contraseña
                                user.updatePassword(newPassword)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(VeterinariosActivity.this, "Contraseña actualizada exitosamente.", Toast.LENGTH_SHORT).show();
                                                    Log.d("VeterinariosActivity", "Password updated for user: " + user.getEmail());
                                                } else {
                                                    Toast.makeText(VeterinariosActivity.this, "Error al actualizar contraseña: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                    Log.e("VeterinariosActivity", "Error updating password: " + task.getException().getMessage());
                                                }
                                            }
                                        });
                            } else {
                                Toast.makeText(VeterinariosActivity.this, "Contraseña actual incorrecta o error de re-autenticación.", Toast.LENGTH_LONG).show();
                                Log.e("VeterinariosActivity", "Re-authentication failed: " + task.getException().getMessage());
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "No se pudo obtener el usuario actual o su correo electrónico.", Toast.LENGTH_SHORT).show();
        }
    }
}