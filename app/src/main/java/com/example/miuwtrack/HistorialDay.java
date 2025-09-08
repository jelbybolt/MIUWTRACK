package com.example.miuwtrack;

import java.util.Map;

public class HistorialDay {
    private String fecha;
    private Map<String, Task> tareas; // Map taskId to Task object

    public HistorialDay() {
        // Default constructor required for calls to DataSnapshot.getValue(HistorialDay.class)
    }

    public HistorialDay(String fecha, Map<String, Task> tareas) {
        this.fecha = fecha;
        this.tareas = tareas;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Map<String, Task> getTareas() {
        return tareas;
    }

    public void setTareas(Map<String, Task> tareas) {
        this.tareas = tareas;
    }

    // Inner class for Task
    public static class Task {
        private String texto;
        private boolean completada;

        public Task() {
            // Default constructor required
        }

        public Task(String texto, boolean completada) {
            this.texto = texto;
            this.completada = completada;
        }

        public String getTexto() {
            return texto;
        }

        public void setTexto(String texto) {
            this.texto = texto;
        }

        public boolean isCompletada() {
            return completada;
        }

        public void setCompletada(boolean completada) {
            this.completada = completada;
        }
    }
}
