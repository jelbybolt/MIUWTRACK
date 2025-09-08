package com.example.miuwtrack;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private ImageButton btnComenzar;
    private ImageButton btnRegistrar;
    private FirebaseAuth auth;

    private static final String TAG = "LoginActivity";
    private static final String ERROR_WEAK_PASSWORD = "La contraseña es muy débil. Debe tener al menos 6 caracteres.";
    private static final String ERROR_INVALID_EMAIL_FORMAT = "El formato del correo electrónico es inválido.";
    private static final String ERROR_WRONG_PASSWORD = "Contraseña incorrecta. Inténtelo de nuevo.";
    private static final String ERROR_EMAIL_ALREADY_IN_USE = "Este correo electrónico ya está registrado.";
    private static final String ERROR_USER_NOT_FOUND = "Usuario no encontrado. Por favor, regístrese.";
    private static final String ERROR_USER_DISABLED = "Su cuenta ha sido deshabilitada.";
    private static final String ERROR_INVALID_CREDENTIALS = "Credenciales inválidas.";


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();

        // --- LÓGICA DE PERSISTENCIA DE SESIÓN ---
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            // Si ya hay un usuario logueado, navega directamente a MenuActivity
            // y cierra LoginActivity para que el usuario no pueda volver con el botón de atrás
            navigateToMenuActivity(currentUser.getEmail(), currentUser.getDisplayName());
            finish(); // Cierra LoginActivity
            return; // Detiene la ejecución del resto de onCreate
        }
        // --- FIN LÓGICA DE PERSISTENCIA ---

        // Si no hay usuario logueado, procede con la UI normal de LoginActivity
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnComenzar = findViewById(R.id.btnComenzar);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtemail = etEmail.getText().toString().trim();
                String txtpassword = etPassword.getText().toString().trim();

                if (txtemail.isEmpty() || txtpassword.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Por favor llene todos los campos", Toast.LENGTH_SHORT).show();
                } else if (txtpassword.length() < 8) {
                    Toast.makeText(LoginActivity.this, "La contraseña debe tener al menos 8 caracteres.", Toast.LENGTH_SHORT).show();
                } else {
                    registrarUsuario(txtemail, txtpassword);
                }
            }
        });

        btnComenzar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtemail = etEmail.getText().toString().trim();
                String txtpassword = etPassword.getText().toString().trim();
                loginUser(txtemail, txtpassword);
            }
        });
    }

    private void registrarUsuario(String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new
                        OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "Usuario creado exitosamente.", Toast.LENGTH_SHORT).show();
                                    FirebaseUser user = auth.getCurrentUser();
                                    if (user != null) {
                                        // Navigate to MenuActivity, passing user data
                                        navigateToMenuActivity(user.getEmail(), user.getDisplayName());
                                    }
                                } else {
                                    manejarError(task.getException());
                                }
                            }
                        });
    }

    private void loginUser(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(LoginActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            // Navigate to MenuActivity, passing user data
                            navigateToMenuActivity(user.getEmail(), user.getDisplayName());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        manejarError(e);
                    }
                });
    }

    // Renamed and updated method to navigate to MenuActivity
    private void navigateToMenuActivity(String email, String displayName) {
        Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
        intent.putExtra("USER_EMAIL", email);
        if (displayName != null && !displayName.isEmpty()) {
            intent.putExtra("USER_NAME", displayName);
        } else {
            // If display name is not set, use part before @ as a default name
            String defaultName = email.split("@")[0];
            // Capitalize first letter of the default name
            intent.putExtra("USER_NAME", defaultName.substring(0, 1).toUpperCase() + defaultName.substring(1));
        }
        startActivity(intent);
        finish(); // Cierra LoginActivity después de navegar
    }

    private void manejarError(Exception e) {
        String errorMessage = "Ocurrió un error desconocido.";

        if (e instanceof FirebaseAuthWeakPasswordException) {
            errorMessage = ERROR_WEAK_PASSWORD;
        } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
            if (e.getMessage() != null && e.getMessage().contains("invalid-email")) {
                errorMessage = ERROR_INVALID_EMAIL_FORMAT;
            } else if (e.getMessage() != null && e.getMessage().contains("wrong-password")) {
                errorMessage = ERROR_WRONG_PASSWORD;
            } else {
                errorMessage = ERROR_INVALID_CREDENTIALS;
            }
        } else if (e instanceof FirebaseAuthUserCollisionException) {
            errorMessage = ERROR_EMAIL_ALREADY_IN_USE;
        } else if (e instanceof FirebaseAuthInvalidUserException) {
            if (e.getMessage() != null && e.getMessage().contains("user-not-found")) {
                errorMessage = ERROR_USER_NOT_FOUND;
            } else if (e.getMessage() != null && e.getMessage().contains("user-disabled")) {
                errorMessage = ERROR_USER_DISABLED;
            } else {
                errorMessage = "El usuario no es válido.";
            }
        } else if (e != null && e.getMessage() != null) {
            errorMessage = "Error: " + e.getMessage();
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }
}