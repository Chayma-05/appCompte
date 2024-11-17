package com.example.myapplication;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.api.ApiInterface;
import com.example.myapplication.beans.Compte;
import com.example.myapplication.config.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompteAdapter extends RecyclerView.Adapter<CompteAdapter.CompteViewHolder> {

    private Spinner editType;
    private EditText editSolde;
    private List<Compte> comptes;
    private final String payloadFormat;

    public CompteAdapter(List<Compte> comptes, String payloadFormat) {
        this.comptes = comptes;
        this.payloadFormat = payloadFormat;
    }

    static class CompteViewHolder extends RecyclerView.ViewHolder {
        TextView text1, text2;
        Button deleteButton, modifyButton;

        public CompteViewHolder(@NonNull View itemView) {
            super(itemView);
            text1 = itemView.findViewById(R.id.text1);
            text2 = itemView.findViewById(R.id.text2);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            modifyButton = itemView.findViewById(R.id.modifyButton);
        }
    }

    @NonNull
    @Override
    public CompteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_compte, parent, false);
        return new CompteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompteViewHolder holder, int position) {
        Compte compte = comptes.get(position);
        holder.text1.setText("Type: " + compte.getType());
        holder.text2.setText("Solde: " + compte.getSolde());

        holder.deleteButton.setOnClickListener(v -> deleteCompte(compte.getIdLong(), position, holder.itemView.getContext()));
        holder.modifyButton.setOnClickListener(v -> modifyCompte(compte, position, holder.itemView.getContext()));
    }

    @Override
    public int getItemCount() {
        return comptes.size();
    }

    public void setComptes(List<Compte> comptes) {
        this.comptes = comptes;
        notifyDataSetChanged();
    }

    private void deleteCompte(Long compteId, int position, Context context) {
        ApiInterface apiInterface = RetrofitClient.getApi(payloadFormat);
        Call<Void> call = apiInterface.deleteCompte(compteId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    comptes.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Compte supprimé avec succès!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Erreur de suppression", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Échec de la connexion", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void modifyCompte(Compte compte, int position, Context context) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_modify_compte, null);

        Spinner editType = dialogView.findViewById(R.id.editType);
        EditText editSolde = dialogView.findViewById(R.id.editSolde);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, new String[]{"COURANT", "EPARGNE"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editType.setAdapter(adapter);

        int typePosition = adapter.getPosition(compte.getType());
        editType.setSelection(typePosition);

        editSolde.setText(String.valueOf(compte.getSolde()));

        Button btnUpdateCompte = dialogView.findViewById(R.id.btnUpdateCompte);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .setTitle("Modifier Compte")
                .setNegativeButton("Annuler", null)
                .create();

        btnUpdateCompte.setOnClickListener(v -> {
            String updatedType = editType.getSelectedItem().toString();
            String updatedSoldeStr = editSolde.getText().toString().trim();

            if (updatedType.isEmpty() || updatedSoldeStr.isEmpty()) {
                Toast.makeText(context, "Tous les champs sont requis", Toast.LENGTH_SHORT).show();
                return;
            }

            double updatedSolde;
            try {
                updatedSolde = Double.parseDouble(updatedSoldeStr);
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Solde invalide", Toast.LENGTH_SHORT).show();
                return;
            }

            compte.setType(updatedType);
            compte.setSolde(updatedSolde);

            ApiInterface apiInterface = RetrofitClient.getApi(payloadFormat);
            Call<Compte> call = apiInterface.updateCompte(compte.getIdLong(), compte);
            call.enqueue(new Callback<Compte>() {
                @Override
                public void onResponse(Call<Compte> call, Response<Compte> response) {
                    if (response.isSuccessful()) {
                        comptes.set(position, compte);
                        notifyItemChanged(position);
                        Toast.makeText(context, "Compte modifié avec succès!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(context, "Erreur lors de la modification", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Compte> call, Throwable t) {
                    Toast.makeText(context, "Échec de la connexion", Toast.LENGTH_SHORT).show();
                }
            });
        });
        dialog.show();
    }

}

