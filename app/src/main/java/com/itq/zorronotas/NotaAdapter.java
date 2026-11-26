package com.itq.zorronotas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Adaptador que enlaza la lista de notas con las tarjetas CardView en el RecyclerView.
 */
public class NotaAdapter extends RecyclerView.Adapter<NotaAdapter.NotaViewHolder> {

    private List<NotaEntity> notas = new ArrayList<>();
    private OnNotaClickListener clickListener;
    private OnNotaLongClickListener longClickListener;

    public interface OnNotaClickListener {
        void onNotaClick(NotaEntity nota);
    }

    public interface OnNotaLongClickListener {
        void onNotaLongClick(NotaEntity nota);
    }

    public void setOnNotaClickListener(OnNotaClickListener listener) {
        this.clickListener = listener;
    }

    public void setOnNotaLongClickListener(OnNotaLongClickListener listener) {
        this.longClickListener = listener;
    }

    public void setNotas(List<NotaEntity> notas) {
        this.notas = notas;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_nota, parent, false);
        return new NotaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NotaViewHolder holder, int position) {
        NotaEntity notaActual = notas.get(position);
        holder.tvTitulo.setText(notaActual.getTitulo());
        holder.tvContenido.setText(notaActual.getContenido());
        holder.tvFecha.setText(notaActual.getFechaCreacion());

        if (notaActual.getFechaRecordatorio() != null && notaActual.getFechaRecordatorio() > System.currentTimeMillis()) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            String recordatorioTexto = "⏰ " + sdf.format(new Date(notaActual.getFechaRecordatorio()));
            holder.tvRecordatorio.setText(recordatorioTexto);
            holder.tvRecordatorio.setVisibility(View.VISIBLE);
        } else {
            holder.tvRecordatorio.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onNotaClick(notaActual);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onNotaLongClick(notaActual);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return notas.size();
    }

    static class NotaViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitulo;
        private final TextView tvContenido;
        private final TextView tvFecha;
        private final TextView tvRecordatorio;

        public NotaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvItemTitulo);
            tvContenido = itemView.findViewById(R.id.tvItemContenido);
            tvFecha = itemView.findViewById(R.id.tvItemFecha);
            tvRecordatorio = itemView.findViewById(R.id.tvItemRecordatorio);
        }
    }
}