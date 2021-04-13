package com.br.audiotecacopia.model;

import com.br.audiotecacopia.config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

public class Usuario {
    private String idUsuario;
    private String nome;
    private String idade;
    private String genero;
    private String sexo;
    private String urlImagem;

    public Usuario() {
    }


    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getIdade() {
        return idade;
    }

    public void setIdade(String idade) {
        this.idade = idade;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }

    public void salvar(){
        DatabaseReference fireabaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference usuarioRef = fireabaseRef.child("usuarios")
                .child(getIdUsuario());

        usuarioRef.setValue(this);
    }
}
