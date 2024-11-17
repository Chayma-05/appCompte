package com.example.myapplication.api;

import com.example.myapplication.beans.Compte;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.DELETE;
import retrofit2.http.Path;

public interface ApiInterface {
    @GET("banque/comptes")
    Call<List<Compte>> getAllComptes();

    @GET("banque/comptes/{id}")
    Call<Compte> getCompteById(@Path("id") Long id);

    @POST("banque/comptes")
    Call<Compte> createCompte(@Body Compte compte);

    @PUT("banque/comptes/{id}")
    Call<Compte> updateCompte(@Path("id") Long id, @Body Compte compte);

    @DELETE("banque/comptes/{id}")
    Call<Void> deleteCompte(@Path("id") Long id);

}
