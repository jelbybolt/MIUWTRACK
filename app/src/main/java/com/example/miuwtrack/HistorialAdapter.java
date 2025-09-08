package com.example.miuwtrack;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder> {
    private List<Paciente> pacientes;

    public HistorialAdapter(List<Paciente> pacientes) {
        this.pacientes = pacientes;
    }

    @NonNull
    @Override
    public HistorialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_historial_card, parent, false);
        return new HistorialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistorialViewHolder holder, int position) {
        Paciente paciente = pacientes.get(position);

        holder.tvPaciente.setText("Paciente: " + paciente.getNombreDueno());
        holder.tvMascota.setText("Mascota: " + paciente.getEspecie());
        holder.tvRegistro.setText("Registro: " + paciente.getNumeroRegistro());

        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), DetalleHistorialActivity.class);
            intent.putExtra("pacienteId", paciente.getId());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return pacientes.size();
    }

    public static class HistorialViewHolder extends RecyclerView.ViewHolder {
        TextView tvPaciente, tvMascota, tvRegistro;
        CardView cardView;

        public HistorialViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPaciente = itemView.findViewById(R.id.tvPaciente);
            tvMascota = itemView.findViewById(R.id.tvMascota);
            tvRegistro = itemView.findViewById(R.id.tvRegistro);
            cardView = (CardView) itemView;
        }
    }
}
