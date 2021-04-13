package com.br.audiotecacopia.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.br.audiotecacopia.R;
import com.br.audiotecacopia.config.ConfiguracaoFirebase;
import com.br.audiotecacopia.config.UsuarioFirebase;
import com.br.audiotecacopia.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class EditarPerfilActivity extends AppCompatActivity {

    private EditText editNome, editIdade, editGenero;
    private RadioButton rbMasc, rbFem;
    private ImageView imgPerfil;

    private static final int SELECAO_GALERIA = 2000;

    private StorageReference storageReference;
    private String idUsuarioLogado;
    private String urlImagemSelecionada = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Editar Perfil");
        setSupportActionBar(toolbar);

        storageReference = ConfiguracaoFirebase.getReferenceStorage();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inicializaComponentes();

        imgPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                if(i.resolveActivity(getPackageManager()) != null){

                    startActivityForResult(i, SELECAO_GALERIA);
                }
            }
        });


    }

    private void inicializaComponentes() {

        editNome = findViewById(R.id.editNome);
        editIdade = findViewById(R.id.editIdade);
        editGenero = findViewById(R.id.editGenero);
        rbMasc = findViewById(R.id.rbMasc);
        rbFem = findViewById(R.id.rbFem);

        imgPerfil = findViewById(R.id.imgPerfil);

    }

    public void salvarDados(View view) {

        String nome = editNome.getText().toString();
        String idade = editIdade.getText().toString();
        String genero = editGenero.getText().toString();
        String sexo = "";

        if(rbMasc.isChecked()){
            sexo = "Masculino";
        }else{
            sexo = "Feminino";
        }

        if(!nome.isEmpty() || !idade.isEmpty() || !genero.isEmpty() || !sexo.isEmpty()){

            Usuario usuario = new Usuario();
            usuario.setIdUsuario(idUsuarioLogado);
            usuario.setNome(nome);
            usuario.setIdade(idade);
            usuario.setGenero(genero);
            usuario.setSexo(sexo);
            usuario.setUrlImagem(urlImagemSelecionada);
            usuario.salvar();
            finish();

        }else{
            exibirMensagem("Todos os campos são obrigatórios!");
        }

    }

    private void exibirMensagem(String texto){
        Toast.makeText(this, texto, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            Bitmap imagem = null;
            try{
                switch (requestCode){
                    case SELECAO_GALERIA:
                        Uri localImagem = data.getData();
                        imagem = MediaStore.Images
                                .Media
                                .getBitmap(
                                        getContentResolver(),
                                        localImagem
                                );
                        break;
                }

                //verifica se a imagem foi escolhida e já faz o upload
                if(imagem != null){
                    imgPerfil.setImageBitmap(imagem);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    //configurando storage
                    final StorageReference imagemRef = storageReference
                            .child("imagens")
                            .child("usuarios")
                            .child(idUsuarioLogado + "jpeg");

                    //tarefa de upload
                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);

                    //em caso de falha no upload
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(EditarPerfilActivity.this,
                                    "Erro ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    Uri url = task.getResult();
                                }
                            });
                            Toast.makeText(EditarPerfilActivity.this,
                                    "Sucesso ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}