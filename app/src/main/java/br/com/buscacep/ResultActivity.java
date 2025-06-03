package br.com.buscacep;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
public class ResultActivity extends AppCompatActivity {
    private TextView tvLogradouroValor, tvBairroValor, tvCidadeValor, tvEstadoValor;
    private Button btnSalvarHistorico, btnVerHistorico;
    private String cep, logradouro, bairro, cidade, estado;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        tvLogradouroValor = findViewById(R.id.tv_logradouro_valor);
        tvBairroValor     = findViewById(R.id.tv_bairro_valor);
        tvCidadeValor     = findViewById(R.id.tv_cidade_valor);
        tvEstadoValor     = findViewById(R.id.tv_estado_valor);
        btnSalvarHistorico = findViewById(R.id.btn_salvar_historico);
        btnVerHistorico    = findViewById(R.id.btn_ver_historico);

        Intent intent = getIntent();
        cep        = intent.getStringExtra("cep");
        logradouro = intent.getStringExtra("logradouro");
        bairro     = intent.getStringExtra("bairro");
        cidade     = intent.getStringExtra("cidade");
        estado     = intent.getStringExtra("estado");

        tvLogradouroValor.setText(logradouro);
        tvBairroValor.setText(bairro);
        tvCidadeValor.setText(cidade);
        tvEstadoValor.setText(estado);

        dbHelper = new DBHelper(this);

        btnSalvarHistorico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean sucesso = dbHelper.insertHistorico(cep, logradouro, bairro, cidade, estado);
                if (sucesso){
                    Toast.makeText(ResultActivity.this,
                            "Busca salva no histórico", Toast.LENGTH_SHORT).show();
                } else{
                    new AlertDialog.Builder(ResultActivity.this)
                            .setTitle("ERRO")
                            .setMessage("Falha ao salvar histórico")
                            .setPositiveButton("OK", null)
                            .show();
                }
            }
        });

        btnVerHistorico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ResultActivity.this, HistoryActivity.class);
                startActivity(i);
            }
        });
    }
}
