package com.br.audiotecacopia.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.br.audiotecacopia.R;
import com.br.audiotecacopia.config.ConfiguracaoFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText edtEmail, edtSenha;
    Button btnAcessar;
    FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.editEmail);
        edtSenha = findViewById(R.id.editSenha);

        btnAcessar = findViewById(R.id.btnEntrar);

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        verificarUsuarioLogado();

        btnAcessar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = edtEmail.getText().toString();
                String senha = edtSenha.getText().toString();


                if (email.isEmpty() || senha.isEmpty()) {

                    Toast.makeText(LoginActivity.this, "Os campos email e senha são obrigatórios", Toast.LENGTH_SHORT)
                            .show();
                } else {

                    autenticacao.signInWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this,
                                        "Logado com sucesso!",
                                        Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(getBaseContext(), HomeActivity.class));
                                finish();
                            } else {

                                Toast.makeText(LoginActivity.this,
                                        "Erro ao fazer login!" + task.getException(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            }
        });
    }

    public void btnCadastro(View view) {

        startActivity(new Intent(this, CadastroActivity.class));
        finish();

    }

    public void verificarUsuarioLogado(){
        FirebaseUser usuarioLogado = autenticacao.getCurrentUser();
        if( usuarioLogado != null){

            startActivity(new Intent(getBaseContext(), HomeActivity.class));
        }
    }

}

