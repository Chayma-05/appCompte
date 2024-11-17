package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.api.ApiInterface;
import com.example.myapplication.beans.Compte;
import com.example.myapplication.config.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private Button btnCreate;
    private EditText soldeInput;
    private RecyclerView recyclerView;
    private CompteAdapter compteAdapter;
    private Spinner payloadTypeSpinner, typeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        payloadTypeSpinner = findViewById(R.id.payloadTypeSpinner);
        typeSpinner = findViewById(R.id.typeSpinner);
        btnCreate = findViewById(R.id.btnCreate);
        soldeInput = findViewById(R.id.soldeInput);
        recyclerView = findViewById(R.id.recyclerView);

        ArrayAdapter<CharSequence> payloadAdapter = ArrayAdapter.createFromResource(this,
                R.array.payload_types, android.R.layout.simple_spinner_item);
        payloadAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        payloadTypeSpinner.setAdapter(payloadAdapter);

        String[] accountTypes = {"COURANT", "EPARGNE"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, accountTypes);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        String selectedFormat = getSelectedPayloadFormat();
        compteAdapter = new CompteAdapter(new ArrayList<>(), selectedFormat);
        recyclerView.setAdapter(compteAdapter);

        getAllComptes();

        btnCreate.setOnClickListener(v -> createCompte());
    }

    private void getAllComptes() {
        String selectedFormat = getSelectedPayloadFormat();
        Call<List<Compte>> call = RetrofitClient.getClient(selectedFormat).create(ApiInterface.class).getAllComptes();
        call.enqueue(new Callback<List<Compte>>() {
            @Override
            public void onResponse(Call<List<Compte>> call, Response<List<Compte>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    compteAdapter.setComptes(response.body());
                } else {
                    Toast.makeText(MainActivity.this, "Aucun compte trouvé.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Compte>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Compte créé avec succès !", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createCompte() {
        String soldeText = soldeInput.getText().toString();
        String accountType = typeSpinner.getSelectedItem().toString();

        if (soldeText.isEmpty()) {
            Toast.makeText(this, "Veuillez entrer le solde.", Toast.LENGTH_SHORT).show();
            return;
        }

        Compte newCompte = new Compte();
        newCompte.setSolde(Double.parseDouble(soldeText));
        newCompte.setType(accountType);

        String selectedFormat = getSelectedPayloadFormat();
        Call<Compte> call = RetrofitClient.getClient(selectedFormat).create(ApiInterface.class).createCompte(newCompte);
        call.enqueue(new Callback<Compte>() {
            @Override
            public void onResponse(Call<Compte> call, Response<Compte> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(MainActivity.this, "Compte créé avec succès !", Toast.LENGTH_SHORT).show();
                    getAllComptes();
                } else {
                    Toast.makeText(MainActivity.this, "Erreur lors de la création du compte.", Toast.LENGTH_SHORT).show();
                    getAllComptes();
                }
            }

            @Override
            public void onFailure(Call<Compte> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Compte créé avec succès !", Toast.LENGTH_SHORT).show();
                getAllComptes();
            }
        });
    }

    private String getSelectedPayloadFormat() {
        return payloadTypeSpinner.getSelectedItem().toString().equals("XML") ? "application/xml" : "application/json";
    }
}
