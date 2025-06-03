package br.com.buscacep;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;


public class HistoryActivity extends AppCompatActivity {

    private ListView lvHistorico;
    private DBHelper dbHelper;
    private List<DBHelper.HistoricoItem> listaHistorico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        lvHistorico = findViewById(R.id.lv_historico);
        dbHelper = new DBHelper(this);

        listaHistorico = dbHelper.getAllHistorico();

        if (listaHistorico.isEmpty()) {
            Toast.makeText(this, "Nenhum registro no histórico.", Toast.LENGTH_SHORT).show();
            return;
        }


        String[] itensParaListView = new String[listaHistorico.size()];
        for (int i = 0; i < listaHistorico.size(); i++) {
            DBHelper.HistoricoItem item = listaHistorico.get(i);
            String texto = item.getCep() + " – "
                    + item.getLogradouro() + ", "
                    + item.getCidade() + "/"
                    + item.getEstado();
            itensParaListView[i] = texto;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                itensParaListView
        );
        lvHistorico.setAdapter(adapter);

        lvHistorico.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DBHelper.HistoricoItem selecionado = listaHistorico.get(position);

                Intent i = new Intent(HistoryActivity.this, ResultActivity.class);
                i.putExtra("cep", selecionado.getCep());
                i.putExtra("logradouro", selecionado.getLogradouro());
                i.putExtra("bairro", selecionado.getBairro());
                i.putExtra("cidade", selecionado.getCidade());
                i.putExtra("estado", selecionado.getEstado());
                startActivity(i);
            }
        });
    }
}
