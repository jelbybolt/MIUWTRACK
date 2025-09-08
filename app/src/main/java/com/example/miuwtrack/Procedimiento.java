package com.example.miuwtrack;

import com.google.firebase.database.PropertyName;

public class Procedimiento {

    private String descripcion;
    private String fecha; // This should store the actual date (e.g., "2025-06-05")
    private String medicamentos;
    private String dosificacion;
    private String tipoProcedimiento; // This should store the type (e.g., "Consulta General")

    public Procedimiento() {}

    // Constructor should match the order and names you use when creating new objects
    public Procedimiento(String fecha, String tipoProcedimiento, String descripcion, String medicamentos, String dosificacion) {
        this.fecha = fecha;
        this.tipoProcedimiento = tipoProcedimiento;
        this.descripcion = descripcion;
        this.medicamentos = medicamentos;
        this.dosificacion = dosificacion;
    }

    // Getters and Setters with @PropertyName if necessary for exact Firebase field names
    // No @PropertyName needed if your Firebase field names exactly match Java field names.
    // However, it's good practice for clarity and robustness if there's any ambiguity.

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getMedicamentos() { return medicamentos; }
    public void setMedicamentos(String medicamentos) { this.medicamentos = medicamentos; }

    public String getDosificacion() { return dosificacion; }
    public void setDosificacion(String dosificacion) { this.dosificacion = dosificacion; }

    // Use @PropertyName if the Firebase field name is "tipoProcedimiento" and your Java field is different (e.g., "type")
    // If your Java field is also 'tipoProcedimiento', then @PropertyName is optional but harmless.
    @PropertyName("tipoProcedimiento") // Ensures it maps to the Firebase field named "tipoProcedimiento"
    public String getTipoProcedimiento() { return tipoProcedimiento; }
    @PropertyName("tipoProcedimiento")
    public void setTipoProcedimiento(String tipoProcedimiento) { this.tipoProcedimiento = tipoProcedimiento; }
}