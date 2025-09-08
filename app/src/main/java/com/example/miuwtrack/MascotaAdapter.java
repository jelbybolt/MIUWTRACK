package com.example.miuwtrack;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MascotaAdapter extends RecyclerView.Adapter<MascotaAdapter.MascotaViewHolder> {

    private List<Mascota> mascotaList;
    private final OnMascotaClickListener clickListener;

    public interface OnMascotaClickListener {
        void onMascotaClick(String mascotaId);
    }

    public MascotaAdapter(List<Mascota> mascotaList, OnMascotaClickListener clickListener) {
        this.mascotaList = mascotaList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public MascotaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mascota, parent, false);
        return new MascotaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MascotaViewHolder holder, int position) {
        Mascota mascota = mascotaList.get(position);
        holder.bind(mascota);
    }

    @Override
    public int getItemCount() {
        return mascotaList.size();
    }

    public void updateData(List<Mascota> newMascotaList) {
        mascotaList = newMascotaList;
        notifyDataSetChanged();
    }

    class MascotaViewHolder extends RecyclerView.ViewHolder {
        private TextView tvID, tvEspecie, tvUltimoProcedimiento;

        public MascotaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvID = itemView.findViewById(R.id.tvID);
            tvEspecie= itemView.findViewById(R.id.tvEspecie);
            tvUltimoProcedimiento = itemView.findViewById(R.id.tvUltimoProcedimiento);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    clickListener.onMascotaClick(mascotaList.get(position).getId());
                }
            });
        }

        public void bind(Mascota mascota) {
            tvID.setText(mascota.getNombreDueno());
            tvEspecie.setText(mascota.getEspecie() + " - " + mascota.getRaza());

            if (mascota.getUltimoProcedimiento() != null && !mascota.getUltimoProcedimiento().isEmpty()) {
                tvUltimoProcedimiento.setText("Último procedimiento: " + mascota.getUltimoProcedimiento() + " (" + mascota.getFechaUltimoProcedimiento() + ")");
            } else {
                tvUltimoProcedimiento.setText("No hay procedimientos registrados");
            }
        }
    }
}