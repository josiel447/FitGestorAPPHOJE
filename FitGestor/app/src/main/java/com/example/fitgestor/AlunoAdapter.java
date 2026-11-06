package com.example.fitgestor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AlunoAdapter extends RecyclerView.Adapter<AlunoAdapter.ViewHolder> {

    private final Context context;
    private final List<Aluno> lista;
    private final OnAlunoActionListener listener;

    public interface OnAlunoActionListener {
        void onBaixarPagamento(Aluno aluno);
        void onCompartilharComprovante(Aluno aluno);
        void onAlternarStatus(Aluno aluno);
    }

    public AlunoAdapter(Context context, List<Aluno> lista, OnAlunoActionListener listener) {
        this.context = context;
        this.lista = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_aluno, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Aluno aluno = lista.get(position);

        holder.tvNome.setText(aluno.getNome());
        holder.tvCpfTelefone.setText("CPF: " + aluno.getCpfFormatado() + " • " + aluno.getTelefone());
        holder.tvDetalhes.setText("Plano " + aluno.getPlano() + " • " + aluno.getValorFormatado() + " • Dia " + aluno.getDiaVencimento());
        holder.tvUltimoPagamento.setText("Último pagamento: " + aluno.getUltimoPagamento());

        if (!aluno.isAtivo()) {
            holder.tvStatusBadge.setText("INATIVO");
            holder.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_inativo);
            holder.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.gray_status));
            holder.btnLiquidar.setVisibility(View.GONE);
            holder.btnInativar.setText("Reativar");
        } else if (aluno.isPendente()) {
            holder.tvStatusBadge.setText("INADIMPLENTE");
            holder.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_inadimplente);
            holder.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.red_status));
            holder.btnLiquidar.setVisibility(View.VISIBLE);
            holder.btnLiquidar.setText("Baixar");
            holder.btnInativar.setText("Arquivar");
        } else {
            holder.tvStatusBadge.setText("REGULAR");
            holder.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_regular);
            holder.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.green_status));
            holder.btnLiquidar.setVisibility(View.GONE);
            holder.btnInativar.setText("Arquivar");
        }

        holder.btnLiquidar.setOnClickListener(v -> {
            if (listener != null) listener.onBaixarPagamento(aluno);
        });

        holder.btnComprovante.setOnClickListener(v -> {
            if (listener != null) listener.onCompartilharComprovante(aluno);
        });

        holder.btnInativar.setOnClickListener(v -> {
            if (listener != null) listener.onAlternarStatus(aluno);
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNome, tvCpfTelefone, tvDetalhes, tvUltimoPagamento, tvStatusBadge;
        Button btnLiquidar, btnComprovante, btnInativar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNome = itemView.findViewById(R.id.tvNomeAluno);
            tvCpfTelefone = itemView.findViewById(R.id.tvCpfTelefone);
            tvDetalhes = itemView.findViewById(R.id.tvDetalhesPlano);
            tvUltimoPagamento = itemView.findViewById(R.id.tvUltimoPagamento);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge);
            btnLiquidar = itemView.findViewById(R.id.btnLiquidar);
            btnComprovante = itemView.findViewById(R.id.btnComprovante);
            btnInativar = itemView.findViewById(R.id.btnInativar);
        }
    }
}