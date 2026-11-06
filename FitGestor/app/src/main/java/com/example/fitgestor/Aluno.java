package com.example.fitgestor;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Aluno implements Serializable {
    private int id;
    private String nome;
    private String cpf;
    private String telefone;
    private String plano;
    private double valor;
    private int diaVencimento;
    private boolean pendente;
    private boolean ativo;
    private String dataInicio;
    private String ultimoPagamento;
    private String formaPagamento;

    public Aluno() {
        this.ativo = true;
    }

    public Aluno(String nome, String cpf, String telefone, String plano, double valor, int diaVencimento, boolean pendente) {
        this.nome = nome;
        this.cpf = cpf;
        this.telefone = telefone;
        this.plano = plano;
        this.valor = valor;
        this.diaVencimento = diaVencimento;
        this.pendente = pendente;
        this.ativo = true;
        this.dataInicio = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        this.formaPagamento = "Não informada";
        this.ultimoPagamento = "Nenhum";
    }

    public Aluno(int id, String nome, String cpf, String telefone, String plano, double valor, int diaVencimento, boolean pendente, boolean ativo, String dataInicio, String ultimoPagamento, String formaPagamento) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.telefone = telefone;
        this.plano = plano;
        this.valor = valor;
        this.diaVencimento = diaVencimento;
        this.pendente = pendente;
        this.ativo = ativo;
        this.dataInicio = dataInicio;
        this.ultimoPagamento = ultimoPagamento;
        this.formaPagamento = formaPagamento;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getPlano() { return plano; }
    public void setPlano(String plano) { this.plano = plano; }

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    public int getDiaVencimento() { return diaVencimento; }
    public void setDiaVencimento(int diaVencimento) { this.diaVencimento = diaVencimento; }

    public boolean isPendente() { return pendente; }
    public void setPendente(boolean pendente) { this.pendente = pendente; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public String getDataInicio() { return dataInicio; }
    public void setDataInicio(String dataInicio) { this.dataInicio = dataInicio; }

    public String getUltimoPagamento() { return ultimoPagamento; }
    public void setUltimoPagamento(String ultimoPagamento) { this.ultimoPagamento = ultimoPagamento; }

    public String getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(String formaPagamento) { this.formaPagamento = formaPagamento; }

    public String getValorFormatado() {
        return String.format(Locale.getDefault(), "R$ %.2f", valor);
    }

    public String getCpfFormatado() {
        if (cpf != null && cpf.length() == 11) {
            return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." + cpf.substring(6, 9) + "-" + cpf.substring(9, 11);
        }
        return cpf;
    }

    public String gerarComprovantePagamento() {
        String dataAtual = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
        return "🏋️ *FITGESTOR MOBILE - RECIBO DE PAGAMENTO* 🏋️\n" +
               "-----------------------------------------------\n" +
               "👤 *Aluno:* " + nome + "\n" +
               "📄 *CPF:* " + getCpfFormatado() + "\n" +
               "📞 *Telefone:* " + telefone + "\n" +
               "📋 *Plano:* " + plano + " (" + getValorFormatado() + ")\n" +
               "📅 *Dia de Vencimento:* Todo dia " + diaVencimento + "\n" +
               "💳 *Forma de Pagamento:* " + (formaPagamento != null ? formaPagamento : "PIX") + "\n" +
               "✅ *Status da Mensalidade:* REGULAR / QUITADO\n" +
               "⏱️ *Data da Baixa:* " + dataAtual + "\n" +
               "-----------------------------------------------\n" +
               "Gestor: Josiel dos Santos (202511319)\n" +
               "FitGestor Mobile - Taguatinga-DF";
    }
}