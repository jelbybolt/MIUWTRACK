package com.example.miuwtrack;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProcedimientoAdapter extends RecyclerView.Adapter<ProcedimientoAdapter.ProcedimientoViewHolder> {

    private List<Procedimiento> procedimientoList;

    public ProcedimientoAdapter(List<Procedimiento> procedimientoList) {
        this.procedimientoList = procedimientoList;
    }

    @NonNull
    @Override
    public ProcedimientoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_procedimiento, parent, false);
        return new ProcedimientoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProcedimientoViewHolder holder, int position) {
        Procedimiento procedimiento = procedimientoList.get(position);
        holder.tvFecha.setText("Fecha: " + procedimiento.getFecha());
        holder.tvTipo.setText("Tipo: " + procedimiento.getTipoProcedimiento());
        holder.tvDescripcion.setText("Descripción: " + procedimiento.getDescripcion());
        holder.tvMedicamentos.setText("Medicamentos: " + procedimiento.getMedicamentos());
        holder.tvDosificacion.setText("Dosificación " + procedimiento.getDosificacion());
    }

    @Override
    public int getItemCount() {
        return procedimientoList.size();
    }

    //Metodo para actualizar los datos en el adapter
    public void updateData(List<Procedimiento> newList) {
        procedimientoList.clear();
        procedimientoList.addAll(newList);
        notifyDataSetChanged();
    }

    //para mantener las referencias de los views
    public static class ProcedimientoViewHolder extends RecyclerView.ViewHolder {
        TextView tvFecha, tvTipo, tvDescripcion, tvMedicamentos, tvDosificacion;

        public ProcedimientoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFecha = itemView.findViewById(R.id.tvProcedimientoFecha);
            tvTipo = itemView.findViewById(R.id.tvProcedimientoTipo);
            tvDescripcion = itemView.findViewById(R.id.tvProcedimientoDescripcion);
            tvMedicamentos = itemView.findViewById(R.id.tvProcedimientoMedicamentos);
            tvDosificacion = itemView.findViewById(R.id.tvProcedimientoDosificacion);
        }
    }
    public void setProcedimientoList(List<Procedimiento> newList) {
        this.procedimientoList = newList;
        notifyDataSetChanged();

}
    }
