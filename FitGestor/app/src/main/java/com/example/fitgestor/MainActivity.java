package com.example.fitgestor;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AlunoAdapter.OnAlunoActionListener {

    private DatabaseHelper dbHelper;
    private RecyclerView rvAlunos;
    private AlunoAdapter adapter;
    private final List<Aluno> listaAlunos = new ArrayList<>();

    private TextView tvRegulares, tvInadimplentes, tvTotal, tvEmptyState;
    private EditText etBusca;
    private Button btnNovoAluno;
    private Button btnFiltroTodos, btnFiltroRegulares, btnFiltroInadimplentes, btnFiltroInativos;

    private String filtroAtual = "TODOS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = DatabaseHelper.getInstance(this);

        rvAlunos = findViewById(R.id.rvAlunos);
        tvRegulares = findViewById(R.id.tvRegulares);
        tvInadimplentes = findViewById(R.id.tvInadimplentes);
        tvTotal = findViewById(R.id.tvTotal);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        etBusca = findViewById(R.id.etBusca);
        btnNovoAluno = findViewById(R.id.btnNovoAluno);

        btnFiltroTodos = findViewById(R.id.btnFiltroTodos);
        btnFiltroRegulares = findViewById(R.id.btnFiltroRegulares);
        btnFiltroInadimplentes = findViewById(R.id.btnFiltroInadimplentes);
        btnFiltroInativos = findViewById(R.id.btnFiltroInativos);

        adapter = new AlunoAdapter(this, listaAlunos, this);
        rvAlunos.setLayoutManager(new LinearLayoutManager(this));
        rvAlunos.setAdapter(adapter);

        btnNovoAluno.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CadastroAlunoActivity.class);
            startActivity(intent);
        });

        configurarFiltros();
        configurarBusca();
        carregarDados();
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarDados();
    }

    private void configurarFiltros() {
        btnFiltroTodos.setOnClickListener(v -> aplicarFiltro("TODOS", btnFiltroTodos));
        btnFiltroRegulares.setOnClickListener(v -> aplicarFiltro("REGULARES", btnFiltroRegulares));
        btnFiltroInadimplentes.setOnClickListener(v -> aplicarFiltro("INADIMPLENTES", btnFiltroInadimplentes));
        btnFiltroInativos.setOnClickListener(v -> aplicarFiltro("INATIVOS", btnFiltroInativos));
    }

    private void aplicarFiltro(String novoFiltro, Button botaoAtivo) {
        filtroAtual = novoFiltro;

        int corAtiva = ContextCompat.getColor(this, R.color.primary_orange);
        int corInativa = ContextCompat.getColor(this, R.color.filter_chip_unselected);
        int textoAtivo = ContextCompat.getColor(this, R.color.white);
        int textoInativo = ContextCompat.getColor(this, R.color.text_muted);

        Button[] botoes = {btnFiltroTodos, btnFiltroRegulares, btnFiltroInadimplentes, btnFiltroInativos};
        for (Button btn : botoes) {
            if (btn == botaoAtivo) {
                btn.setBackgroundTintList(ColorStateList.valueOf(corAtiva));
                btn.setTextColor(textoAtivo);
            } else {
                btn.setBackgroundTintList(ColorStateList.valueOf(corInativa));
                btn.setTextColor(textoInativo);
            }
        }

        carregarDados();
    }

    private void configurarBusca() {
        etBusca.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                carregarDados();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void carregarDados() {
        String busca = etBusca.getText().toString().trim();
        List<Aluno> alunosCarregados = dbHelper.listarAlunos(busca, filtroAtual);

        listaAlunos.clear();
        listaAlunos.addAll(alunosCarregados);
        adapter.notifyDataSetChanged();

        if (listaAlunos.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
        }

        atualizarKPIs();
    }

    private void atualizarKPIs() {
        tvRegulares.setText(String.valueOf(dbHelper.contarRegulares()));
        tvInadimplentes.setText(String.valueOf(dbHelper.contarInadimplentes()));
        tvTotal.setText(String.valueOf(dbHelper.contarTotalAtivos()));
    }

    // RF04 & RN003: Baixar pagamento com seleção da forma
    @Override
    public void onBaixarPagamento(Aluno aluno) {
        String[] formasPagamento = {"PIX", "Cartão de Débito", "Cartão de Crédito", "Dinheiro"};

        new AlertDialog.Builder(this)
                .setTitle("Baixar Pagamento: " + aluno.getNome())
                .setMessage("Valor da Mensalidade: " + aluno.getValorFormatado() + "\nSelecione a forma de pagamento:")
                .setItems(formasPagamento, (dialog, which) -> {
                    String formaEscolhida = formasPagamento[which];
                    boolean sucesso = dbHelper.baixarPagamento(aluno.getId(), formaEscolhida);
                    if (sucesso) {
                        Toast.makeText(this, "✅ Pagamento registrado com sucesso via " + formaEscolhida + "!", Toast.LENGTH_SHORT).show();
                        carregarDados();
                    } else {
                        Toast.makeText(this, "Erro ao registrar baixa de pagamento.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // RF06: Compartilhamento digital de comprovante
    @Override
    public void onCompartilharComprovante(Aluno aluno) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, aluno.gerarComprovantePagamento());
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, "Enviar Comprovante de Pagamento");
        startActivity(shareIntent);
    }

    // RN004: Inativar / Arquivar aluno sem deletar histórico
    @Override
    public void onAlternarStatus(Aluno aluno) {
        boolean novoStatus = !aluno.isAtivo();
        String acao = novoStatus ? "reativar" : "arquivar/inativar";

        new AlertDialog.Builder(this)
                .setTitle("Confirmar Ação")
                .setMessage("Deseja " + acao + " a matrícula de " + aluno.getNome() + "?\nO histórico de pagamentos será preservado.")
                .setPositiveButton("Sim", (dialog, which) -> {
                    boolean sucesso = dbHelper.alternarStatusAtivo(aluno.getId(), novoStatus);
                    if (sucesso) {
                        Toast.makeText(this, "Aluno " + (novoStatus ? "reativado" : "arquivado") + " com sucesso!", Toast.LENGTH_SHORT).show();
                        carregarDados();
                    }
                })
                .setNegativeButton("Não", null)
                .show();
    }
}