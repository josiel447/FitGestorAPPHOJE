package com.example.fitgestor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "fitgestor.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_ALUNOS = "alunos";
    public static final String COL_ID = "id";
    public static final String COL_NOME = "nome";
    public static final String COL_CPF = "cpf";
    public static final String COL_TELEFONE = "telefone";
    public static final String COL_PLANO = "plano";
    public static final String COL_VALOR = "valor";
    public static final String COL_DIA_VENCIMENTO = "dia_vencimento";
    public static final String COL_PENDENTE = "pendente"; // 1 = Inadimplente, 0 = Regular
    public static final String COL_ATIVO = "ativo";       // 1 = Ativo, 0 = Inativo (RN004)
    public static final String COL_DATA_INICIO = "data_inicio";
    public static final String COL_ULTIMO_PAGAMENTO = "ultimo_pagamento";
    public static final String COL_FORMA_PAGAMENTO = "forma_pagamento";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_ALUNOS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NOME + " TEXT NOT NULL, " +
                COL_CPF + " TEXT NOT NULL, " +
                COL_TELEFONE + " TEXT, " +
                COL_PLANO + " TEXT, " +
                COL_VALOR + " REAL, " +
                COL_DIA_VENCIMENTO + " INTEGER, " +
                COL_PENDENTE + " INTEGER DEFAULT 0, " +
                COL_ATIVO + " INTEGER DEFAULT 1, " +
                COL_DATA_INICIO + " TEXT, " +
                COL_ULTIMO_PAGAMENTO + " TEXT, " +
                COL_FORMA_PAGAMENTO + " TEXT" +
                ")";
        db.execSQL(createTable);

        // Inserir dados padrão para demonstração inicial
        inserirDadosIniciais(db);
    }

    private void inserirDadosIniciais(SQLiteDatabase db) {
        inserirAlunoInicial(db, "Carlos Souza", "11122233344", "61999991111", "Mensal", 120.0, 10, 1, 1, "10/08/2024", "Pendente", "PIX");
        inserirAlunoInicial(db, "Ana Beatriz", "22233344455", "61999992222", "Trimestral", 320.0, 15, 0, 1, "15/07/2024", "15/08/2024 (PIX)", "PIX");
        inserirAlunoInicial(db, "Marcos Lima", "33344455566", "61999993333", "Anual", 1100.0, 5, 0, 1, "05/01/2024", "05/08/2024 (Cartão)", "Cartão de Crédito");
    }

    private void inserirAlunoInicial(SQLiteDatabase db, String nome, String cpf, String tel, String plano, double valor, int dia, int pendente, int ativo, String dataIni, String ultPag, String formaPag) {
        ContentValues cv = new ContentValues();
        cv.put(COL_NOME, nome);
        cv.put(COL_CPF, cpf);
        cv.put(COL_TELEFONE, tel);
        cv.put(COL_PLANO, plano);
        cv.put(COL_VALOR, valor);
        cv.put(COL_DIA_VENCIMENTO, dia);
        cv.put(COL_PENDENTE, pendente);
        cv.put(COL_ATIVO, ativo);
        cv.put(COL_DATA_INICIO, dataIni);
        cv.put(COL_ULTIMO_PAGAMENTO, ultPag);
        cv.put(COL_FORMA_PAGAMENTO, formaPag);
        db.insert(TABLE_ALUNOS, null, cv);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALUNOS);
        onCreate(db);
    }

    // RN001: Verifica unicidade de CPF entre alunos ATIVOS
    public boolean existeCpfAtivo(String cpf, int idIgnorar) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_ALUNOS + " WHERE " + COL_CPF + " = ? AND " + COL_ATIVO + " = 1";
        String[] args;
        if (idIgnorar > 0) {
            query += " AND " + COL_ID + " != ?";
            args = new String[]{cpf, String.valueOf(idIgnorar)};
        } else {
            args = new String[]{cpf};
        }
        Cursor cursor = db.rawQuery(query, args);
        boolean existe = false;
        if (cursor.moveToFirst()) {
            existe = cursor.getInt(0) > 0;
        }
        cursor.close();
        return existe;
    }

    // RF02: Cadastrar aluno
    public long inserirAluno(Aluno aluno) {
        if (existeCpfAtivo(aluno.getCpf(), 0)) {
            return -2; // Erro específico de CPF duplicado (RN001)
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_NOME, aluno.getNome());
        cv.put(COL_CPF, aluno.getCpf());
        cv.put(COL_TELEFONE, aluno.getTelefone());
        cv.put(COL_PLANO, aluno.getPlano());
        cv.put(COL_VALOR, aluno.getValor());
        cv.put(COL_DIA_VENCIMENTO, aluno.getDiaVencimento());
        cv.put(COL_PENDENTE, aluno.isPendente() ? 1 : 0);
        cv.put(COL_ATIVO, aluno.isAtivo() ? 1 : 0);
        cv.put(COL_DATA_INICIO, aluno.getDataInicio());
        cv.put(COL_ULTIMO_PAGAMENTO, aluno.getUltimoPagamento());
        cv.put(COL_FORMA_PAGAMENTO, aluno.getFormaPagamento());

        return db.insert(TABLE_ALUNOS, null, cv);
    }

    // RF04 & RN003: Baixar pagamento e renovar ciclo
    public boolean baixarPagamento(int alunoId, String formaPagamento) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        String dataAtual = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        cv.put(COL_PENDENTE, 0); // Torna regular
        cv.put(COL_FORMA_PAGAMENTO, formaPagamento);
        cv.put(COL_ULTIMO_PAGAMENTO, dataAtual + " (" + formaPagamento + ")");

        int rows = db.update(TABLE_ALUNOS, cv, COL_ID + " = ?", new String[]{String.valueOf(alunoId)});
        return rows > 0;
    }

    // RN004: Inativar/Arquivar ou reativar aluno sem exclusão definitiva
    public boolean alternarStatusAtivo(int alunoId, boolean novoStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_ATIVO, novoStatus ? 1 : 0);
        int rows = db.update(TABLE_ALUNOS, cv, COL_ID + " = ?", new String[]{String.valueOf(alunoId)});
        return rows > 0;
    }

    // RF02: Consulta com filtros e busca por nome ou CPF
    public List<Aluno> listarAlunos(String busca, String filtro) {
        List<Aluno> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        StringBuilder query = new StringBuilder("SELECT * FROM " + TABLE_ALUNOS + " WHERE 1=1");
        List<String> argsList = new ArrayList<>();

        if (filtro != null) {
            switch (filtro.toUpperCase()) {
                case "REGULARES":
                    query.append(" AND ").append(COL_PENDENTE).append(" = 0 AND ").append(COL_ATIVO).append(" = 1");
                    break;
                case "INADIMPLENTES":
                    query.append(" AND ").append(COL_PENDENTE).append(" = 1 AND ").append(COL_ATIVO).append(" = 1");
                    break;
                case "INATIVOS":
                    query.append(" AND ").append(COL_ATIVO).append(" = 0");
                    break;
                default:
                    // "TODOS" ativos primeiro
                    query.append(" AND ").append(COL_ATIVO).append(" = 1");
                    break;
            }
        }

        if (busca != null && !busca.trim().isEmpty()) {
            query.append(" AND (").append(COL_NOME).append(" LIKE ? OR ").append(COL_CPF).append(" LIKE ?)");
            argsList.add("%" + busca.trim() + "%");
            argsList.add("%" + busca.trim() + "%");
        }

        query.append(" ORDER BY ").append(COL_NOME).append(" ASC");

        Cursor c = db.rawQuery(query.toString(), argsList.toArray(new String[0]));
        if (c.moveToFirst()) {
            do {
                Aluno aluno = new Aluno(
                        c.getInt(c.getColumnIndexOrThrow(COL_ID)),
                        c.getString(c.getColumnIndexOrThrow(COL_NOME)),
                        c.getString(c.getColumnIndexOrThrow(COL_CPF)),
                        c.getString(c.getColumnIndexOrThrow(COL_TELEFONE)),
                        c.getString(c.getColumnIndexOrThrow(COL_PLANO)),
                        c.getDouble(c.getColumnIndexOrThrow(COL_VALOR)),
                        c.getInt(c.getColumnIndexOrThrow(COL_DIA_VENCIMENTO)),
                        c.getInt(c.getColumnIndexOrThrow(COL_PENDENTE)) == 1,
                        c.getInt(c.getColumnIndexOrThrow(COL_ATIVO)) == 1,
                        c.getString(c.getColumnIndexOrThrow(COL_DATA_INICIO)),
                        c.getString(c.getColumnIndexOrThrow(COL_ULTIMO_PAGAMENTO)),
                        c.getString(c.getColumnIndexOrThrow(COL_FORMA_PAGAMENTO))
                );
                lista.add(aluno);
            } while (c.moveToNext());
        }
        c.close();
        return lista;
    }

    public int contarRegulares() {
        return contarAlunosPorCondicao(COL_PENDENTE + " = 0 AND " + COL_ATIVO + " = 1");
    }

    public int contarInadimplentes() {
        return contarAlunosPorCondicao(COL_PENDENTE + " = 1 AND " + COL_ATIVO + " = 1");
    }

    public int contarTotalAtivos() {
        return contarAlunosPorCondicao(COL_ATIVO + " = 1");
    }

    private int contarAlunosPorCondicao(String whereClause) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_ALUNOS + " WHERE " + whereClause, null);
        int total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }
        cursor.close();
        return total;
    }
}
