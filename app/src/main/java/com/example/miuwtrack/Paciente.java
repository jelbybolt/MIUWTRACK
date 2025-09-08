package com.example.miuwtrack;

public class Paciente {
    private String id;
    private String nombreDueno;
    private String telefono;
    private String especie;
    private String numeroRegistro;
    private String nombrePaciente;

    public Paciente(String id, String nombreDueno, String telefono, String especie, String numeroRegistro, String nombrePaciente) {
        this.id = id;
        this.nombreDueno = nombreDueno;
        this.nombrePaciente = nombrePaciente;
        this.telefono = telefono;
        this.especie = especie;
        this.numeroRegistro = numeroRegistro;
}

// Getters
public String getId() { return id; }
public String getNombreDueno() { return nombreDueno; }
public String getTelefono() { return telefono; }
public String getEspecie() { return especie; }
    public String getNombrePaciente() { return nombrePaciente; }
public String getNumeroRegistro() { return numeroRegistro; }
}
