package com.example.miuwtrack;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PacienteAdapter extends RecyclerView.Adapter<PacienteAdapter.ViewHolder>  {

    private final List<Paciente> pacientes;

    public PacienteAdapter(List<Paciente> pacientes) {
        this.pacientes = pacientes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_paciente, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Paciente paciente = pacientes.get(position);
        holder.tvNombre.setText(paciente.getNombreDueno());
        holder.tvMascota.setText("Número de registro: " + paciente.getNombrePaciente());
        holder.tvEspecie.setText("Nombre: " + paciente.getEspecie());
        holder.tvRegistro.setText("Especie: " + paciente.getNumeroRegistro());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), DetalleHistorialActivity.class);
            intent.putExtra("pacienteId", paciente.getId());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return pacientes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvNombre, tvMascota, tvRegistro, tvEspecie;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvMascota = itemView.findViewById(R.id.tvMascota);
            tvRegistro = itemView.findViewById(R.id.tvRegistro);
            tvEspecie = itemView.findViewById(R.id.tvEspecie);
        }
    }
}