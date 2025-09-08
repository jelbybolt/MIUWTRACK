package com.example.miuwtrack;

public class Mascota {
    private String id;
    private String nombreDueno;
    private String especie;
    private String nombrePaciente;
    private String raza;
    private String numeroRegistro;
    private String ultimoProcedimiento;
    private String fechaUltimoProcedimiento;

    // Constructor vacío necesario para Firebase
    public Mascota() {
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getNombreDueno() {
        return nombreDueno;
    }

    public String getEspecie() {
        return especie;
    }
    public String getNombrePaciente() {
        return nombrePaciente;
    }

    public String getRaza() {
        return raza;
    }

    public String getNumeroRegistro() {
        return numeroRegistro;
    }

    public String getUltimoProcedimiento() {
        return ultimoProcedimiento;
    }

    public String getFechaUltimoProcedimiento() {
        return fechaUltimoProcedimiento;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setNombreDueno(String nombreDueno) {
        this.nombreDueno = nombreDueno;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }
    public void setNombrePaciente(String nombrePaciente) {
        this.nombrePaciente = nombrePaciente;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public void setNumeroRegistro(String numeroRegistro) {
        this.numeroRegistro = numeroRegistro;
    }

    public void setUltimoProcedimiento(String ultimoProcedimiento) {
        this.ultimoProcedimiento = ultimoProcedimiento;
    }

    public void setFechaUltimoProcedimiento(String fechaUltimoProcedimiento) {
        this.fechaUltimoProcedimiento = fechaUltimoProcedimiento;
    }
}
