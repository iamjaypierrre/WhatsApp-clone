package com.example.whatsappclone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.whatsappclone.R;
import com.example.whatsappclone.config.ConfiguracaoFirebase;
import com.example.whatsappclone.helper.Base64Custom;
import com.example.whatsappclone.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class CadastroActivity extends AppCompatActivity {

    private TextInputEditText campoNome, campoEmail, campoSenha;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        campoNome = findViewById(R.id.editNome);
        campoEmail = findViewById(R.id.editLoginEmail);
        campoSenha = findViewById(R.id.editLoginSenha);

    }

    public void cadastrarUsuario(Usuario usuario){

        autenticacao = ConfiguracaoFirebase.getFirebaseAuthentication();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if ( task.isSuccessful() ){

                    try{

                    String identificadorUsuario = Base64Custom.codificarBase64( usuario.getEmail() );
                    usuario.setUid(identificadorUsuario);
                    usuario.salvar();

                }catch (Exception e){
                    e.printStackTrace();
                }



                    Toast.makeText(CadastroActivity.this,
                            "Sucesso ao cadastrar usuario!",
                            Toast.LENGTH_SHORT).show();
                    finish();



                }else{

                    String excecao = "";
                    try{
                        throw task.getException();

                    } catch ( FirebaseAuthWeakPasswordException e ){
                        excecao = "Digite uma senha mais forte!";
                    } catch ( FirebaseAuthInvalidCredentialsException e ){
                        excecao = "Por favor, digite um e-mail válido!";
                    }catch ( FirebaseAuthUserCollisionException e ){
                        excecao = "Esta conta ja foi cadastrada!";
                    }
                    catch (Exception e) {
                        excecao = "Erro ao cadastrar usuario: "  + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(CadastroActivity.this,
                            excecao,
                            Toast.LENGTH_SHORT).show();

                }

            }
        });

    }

    public void validarCadastrarUsuario(View view){

        //Recuperar textos dos campos
        String textoNome = campoNome.getText().toString();
        String textoEmail = campoEmail.getText().toString();
        String textoSenha = campoSenha.getText().toString();

        if( !textoNome.isEmpty() ){//verifica o nome
            if( !textoEmail.isEmpty() ){//verifica o e-mail
                if (!textoSenha.isEmpty() ){

                    Usuario usuario = new Usuario();
                    usuario.setNome( textoNome );
                    usuario.setEmail( textoEmail );
                    usuario.setSenha( textoSenha );

                    cadastrarUsuario( usuario );

                }else {
                    Toast.makeText(CadastroActivity.this,
                            "Escolha sua senha!",
                            Toast.LENGTH_SHORT).show();
                }

            }else {
                Toast.makeText(CadastroActivity.this,
                        "Preencha seu E-mail",
                        Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(CadastroActivity.this,
                    "Preencha o nome!",
                    Toast.LENGTH_SHORT).show();
        }
    }
}