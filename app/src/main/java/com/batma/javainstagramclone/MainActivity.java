package com.batma.javainstagramclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.batma.javainstagramclone.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //View binding kullaniyoruz
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        //mAuth ile bütün giriş ve çıkış işlemlerini yapabiliriz
        mAuth = FirebaseAuth.getInstance();
        // Firestore'u başlat
        firebaseFirestore = FirebaseFirestore.getInstance();


    }

    @Override
    protected void onStart() {
        //eger daha onceden giris yapmissa birdaha giris yapmasın diye
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //eğer bir kullanıcı varsa
        if(currentUser != null){
            Intent intent = new Intent(MainActivity.this, FeedActivity.class);
            startActivity(intent);
        }
    }

    public void SignInClickted(View view){
        String email = binding.emailText.getText().toString();
        String password = binding.passwordText.getText().toString();
        if(email.equals("") || password.equals("")){
            Toast.makeText(this, "Enter email and password", Toast.LENGTH_LONG).show();
        }else{
            mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {

                    Intent intent = new Intent(MainActivity.this, FeedActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        // kimlik doğrulama işlemi başarısız olduğunda
                        Toast.makeText(MainActivity.this, "Kullanıcı adı veya Şifre yanlış", Toast.LENGTH_LONG).show();
                    } else {
                        // Diğer hata durumları
                        Toast.makeText(MainActivity.this, "Giriş yapılamadı: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    public void SignUpClickted(View view){
        String email = binding.emailText.getText().toString();
        String password = binding.passwordText.getText().toString();
        if(email.equals("") || password.equals("")){
            Toast.makeText(this, "Enter email and password", Toast.LENGTH_LONG).show();
        } else {
            mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    // createUserWithEmailAndPassword işlemi başarılı, Firestore'a kullanıcı bilgilerini ekleyin
                    saveUserDataToFirestore(email, password, "");

                    // FeedActivity'ye yönlendir
                    Intent intent = new Intent(MainActivity.this, FeedActivity.class);
                    startActivity(intent);

                    // Aktiviteden çıkacağımız için boşuna hafızada yer kaplamasın
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // createUserWithEmailAndPassword işlemi başarısız oldu
                    Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void saveUserDataToFirestore(String userEmail, String userPassword, String resimUri) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // Kullanıcının UID'sini al
            String userId = user.getUid();

            // Kullanıcının bilgilerini bir HashMap'e ekle
            HashMap<String, Object> userData = new HashMap<>();
            userData.put("id",userId);
            userData.put("email", userEmail);
            userData.put("password", userPassword);
            userData.put("resimUri", resimUri);

            // "Users" koleksiyonu altında kullanıcının UID'si ile belge ekleyin
            FirebaseFirestore.getInstance().collection("Users").document(userId)
                    .set(userData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Veri ekleme işlemi başarılı
                            Toast.makeText(MainActivity.this, "Veri Firestore'a eklendi.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Veri ekleme işlemi başarısız
                            Toast.makeText(MainActivity.this, "Veri eklenemedi: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

}