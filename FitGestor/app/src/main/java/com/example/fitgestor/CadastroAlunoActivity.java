package com.example.fitgestor;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CadastroAlunoActivity extends AppCompatActivity {

    private EditText etNome, etCpf, etTelefone;
    private Spinner spPlanos, spDiaVencimento;
    private TextView tvValorCalculado;
    private Button btnSalvar, btnVoltar;

    private DatabaseHelper dbHelper;

    private final String[] nomesPlanos = {"Mensal", "Trimestral", "Semestral", "Anual"};
    private final double[] valoresPlanos = {120.0, 320.0, 600.0, 1100.0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_aluno);

        dbHelper = DatabaseHelper.getInstance(this);

        etNome = findViewById(R.id.etNome);
        etCpf = findViewById(R.id.etCpf);
        etTelefone = findViewById(R.id.etTelefone);
        spPlanos = findViewById(R.id.spPlanos);
        spDiaVencimento = findViewById(R.id.spDiaVencimento);
        tvValorCalculado = findViewById(R.id.tvValorCalculado);
        btnSalvar = findViewById(R.id.btnSalvar);
        btnVoltar = findViewById(R.id.btnVoltar);

        configurarSpinners();

        btnVoltar.setOnClickListener(v -> finish());
        btnSalvar.setOnClickListener(v -> salvarMatricula());
    }

    private void configurarSpinners() {
        // Spinner de Planos (RF03)
        List<String> listaPlanosLabels = new ArrayList<>();
        for (int i = 0; i < nomesPlanos.length; i++) {
            listaPlanosLabels.add(nomesPlanos[i] + " (" + String.format(Locale.getDefault(), "R$ %.2f", valoresPlanos[i]) + ")");
        }

        ArrayAdapter<String> adapterPlanos = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, listaPlanosLabels);
        spPlanos.setAdapter(adapterPlanos);

        spPlanos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                double valor = valoresPlanos[position];
                tvValorCalculado.setText(String.format(Locale.getDefault(), "R$ %.2f", valor));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Spinner de Dias de Vencimento
        List<String> dias = new ArrayList<>();
        for (int i = 1; i <= 31; i++) {
            dias.add("Dia " + i);
        }
        ArrayAdapter<String> adapterDias = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, dias);
        spDiaVencimento.setAdapter(adapterDias);
        spDiaVencimento.setSelection(9); // Padrão dia 10
    }

    private void salvarMatricula() {
        String nome = etNome.getText().toString().trim();
        String cpf = etCpf.getText().toString().trim();
        String telefone = etTelefone.getText().toString().trim();

        if (nome.isEmpty()) {
            etNome.setError("Informe o nome completo");
            etNome.requestFocus();
            return;
        }

        if (cpf.isEmpty() || cpf.length() < 11) {
            etCpf.setError("Informe um CPF válido com 11 dígitos");
            etCpf.requestFocus();
            return;
        }

        if (telefone.isEmpty()) {
            etTelefone.setError("Informe o telefone de contato");
            etTelefone.requestFocus();
            return;
        }

        // Validação da Regra de Negócio RN001: Unicidade de CPF ativo
        if (dbHelper.existeCpfAtivo(cpf, 0)) {
            etCpf.setError("CPF já cadastrado");
            etCpf.requestFocus();
            Toast.makeText(this, "⚠️ [RN001] Já existe um aluno ativo cadastrado com este CPF!", Toast.LENGTH_LONG).show();
            return;
        }

        int planoPos = spPlanos.getSelectedItemPosition();
        String planoSelecionado = nomesPlanos[planoPos];
        double valorSelecionado = valoresPlanos[planoPos];
        int diaVencimento = spDiaVencimento.getSelectedItemPosition() + 1;

        Aluno novoAluno = new Aluno(nome, cpf, telefone, planoSelecionado, valorSelecionado, diaVencimento, false);
        novoAluno.setFormaPagamento("PIX (Matrícula)");
        novoAluno.setUltimoPagamento("Matrícula Quitada");

        long resultado = dbHelper.inserirAluno(novoAluno);
        if (resultado > 0) {
            Toast.makeText(this, "✅ Aluno " + nome + " matriculado com sucesso!", Toast.LENGTH_SHORT).show();
            finish();
        } else if (resultado == -2) {
            Toast.makeText(this, "⚠️ [RN001] Não é permitido duplicar CPF ativo no sistema!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Erro ao gravar dados no banco local.", Toast.LENGTH_SHORT).show();
        }
    }
}