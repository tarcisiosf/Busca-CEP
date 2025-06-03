package br.com.buscacep;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "buscacep.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_HISTORICO = "historico";
    private static final String COLUMN_ID         = "id";
    private static final String COLUMN_CEP        = "cep";
    private static final String COLUMN_LOGRADOURO = "logradouro";
    private static final String COLUMN_BAIRRO     = "bairro";
    private static final String COLUMN_CIDADE     = "cidade";
    private static final String COLUMN_ESTADO     = "estado";

    private Context context;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_HISTORICO + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_CEP + " TEXT, "
                + COLUMN_LOGRADOURO + " TEXT, "
                + COLUMN_BAIRRO + " TEXT, "
                + COLUMN_CIDADE + " TEXT, "
                + COLUMN_ESTADO + " TEXT"
                + ");";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORICO);
        onCreate(db);
    }

    public boolean insertHistorico(String cep, String logradouro, String bairro,
                                   String cidade, String estado) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_CEP, cep);
        cv.put(COLUMN_LOGRADOURO, logradouro);
        cv.put(COLUMN_BAIRRO, bairro);
        cv.put(COLUMN_CIDADE, cidade);
        cv.put(COLUMN_ESTADO, estado);

        long resultado = db.insert(TABLE_HISTORICO, null, cv);
        db.close();
        return (resultado != -1);
    }

    public List<HistoricoItem> getAllHistorico() {
        List<HistoricoItem> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_HISTORICO + " ORDER BY " + COLUMN_ID + " DESC";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String cep = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CEP));
                String logradouro = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOGRADOURO));
                String bairro = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BAIRRO));
                String cidade = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CIDADE));
                String estado = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ESTADO));

                lista.add(new HistoricoItem(id, cep, logradouro, bairro, cidade, estado));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }

    public static class HistoricoItem {
        private int id;
        private String cep;
        private String logradouro;
        private String bairro;
        private String cidade;
        private String estado;

        public HistoricoItem(int id, String cep, String logradouro, String bairro,
                             String cidade, String estado) {
            this.id = id;
            this.cep = cep;
            this.logradouro = logradouro;
            this.bairro = bairro;
            this.cidade = cidade;
            this.estado = estado;
        }

        public int getId() { return id; }
        public String getCep() { return cep; }
        public String getLogradouro() { return logradouro; }
        public String getBairro() { return bairro; }
        public String getCidade() { return cidade; }
        public String getEstado() { return estado; }
    }
}
