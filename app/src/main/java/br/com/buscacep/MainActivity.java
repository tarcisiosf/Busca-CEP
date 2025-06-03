package br.com.buscacep;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private EditText etCep;
    private Button btnBuscar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        etCep = findViewById(R.id.et_cep);
        btnBuscar = findViewById(R.id.btn_buscar);

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cepDigitado = etCep.getText().toString().trim();
                if (TextUtils.isEmpty(cepDigitado)) {
                    etCep.setError("Informe o CEP");
                    etCep.requestFocus();
                    return;
                }
                if (cepDigitado.length() != 8) {
                    etCep.setError("O CEP deve ter 8 dígitos");
                    etCep.requestFocus();
                    return;
                }

                new BuscaCep().execute(cepDigitado);
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private class BuscaCep extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String cep = params[0];
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL("https://viacep.com.br/ws/" + cep + "/json/");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String linha;
                while ((linha = reader.readLine()) != null) {
                    sb.append(linha);
                }
                return sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (Exception ignored) {
                    }
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }
        @Override
        protected void onPostExecute (String resultadoJson){
            super.onPostExecute(resultadoJson);

            if (resultadoJson == null){
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("ERRO")
                        .setMessage("Não foi possível acessar a API. Verifique a conexão")
                        .setPositiveButton("OK", null)
                        .show();
                return;
            }

            try {
                JSONObject jsonObject = new JSONObject(resultadoJson);
                if (jsonObject.has("erro")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("CEP inválido")
                            .setMessage("O CEP informado não foi encontrado.")
                            .setPositiveButton("OK", null)
                            .show();
                    return;
                }

                String logradouro = jsonObject.optString("logradouro", "");
                String bairro = jsonObject.optString("bairro","");
                String localidade = jsonObject.optString("localidade", "");
                String uf = jsonObject.optString("uf","");

                Intent i = new Intent(MainActivity.this, ResultActivity.class);
                i.putExtra("cep", etCep.getText().toString().trim());
                i.putExtra("logradouro", logradouro);
                i.putExtra("bairro", bairro);
                i.putExtra("cidade", localidade);
                i.putExtra("estado", uf);
                startActivity(i);
            } catch (Exception e){
                e.printStackTrace();
                Toast.makeText(MainActivity.this,
                        "Erro ao processar dados.", Toast.LENGTH_LONG).show();
            }
        }
    }
}