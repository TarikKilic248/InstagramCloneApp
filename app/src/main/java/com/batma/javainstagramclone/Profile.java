package com.batma.javainstagramclone;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.batma.javainstagramclone.databinding.ActivityMainBinding;
import com.batma.javainstagramclone.databinding.ActivityProfileBinding;
import com.batma.javainstagramclone.databinding.ActivityUploadBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.ktx.Firebase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.storage.internal.StorageReferenceUri;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Profile extends AppCompatActivity {
    private ActivityProfileBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    Uri imageData;
    //Intent ile galleriye gittiğimiz ve oradan veri alacağımız launcer
    ActivityResultLauncher<Intent> activityResultLauncher;
    //izin isteme launcher'ı
    ActivityResultLauncher<String> permissionLauncher;
    //galeryden cektigimiz fotoraf bu bitmapde saklanıyor
    Bitmap selectedBitmap;
    FirebaseUser currentUser;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        registerLauncher();
        displayUserEmailAndPhoto();

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        auth = FirebaseAuth.getInstance();
    }

    public void displayUserEmailAndPhoto() {
        // FirebaseAuth ile mevcut kullanıcıyı al
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        // Eğer kullanıcı mevcutsa, e-posta adresini al
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();

            // E-posta adresini bir TextView'a atama
            TextView textViewUserEmail = findViewById(R.id.textViewUserEmail);
            textViewUserEmail.setText(userEmail);
        }
        if (currentUser != null) {
            // Kullanıcının UID'sini al
            String userId = currentUser.getUid();
            // Kullanıcının verilerini "Users" koleksiyonundan al
            FirebaseFirestore.getInstance().collection("Users").document(userId)
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                                System.out.println(error.getLocalizedMessage());
                                return;
                            }

                            if (value != null && value.exists()) {
                                // Users belgesinden resimUri alanını al
                                String resimUri = value.getString("resimUri");

                                if (resimUri != null && !resimUri.isEmpty()) {
                                    System.out.println("Buraya girdi3");

                                    // Picasso kütüphanesini kullanarak resmi ImageView'a yükle
                                    ImageView imageView2 = findViewById(R.id.imageView2);
                                    Picasso.get().load(resimUri).into(imageView2);
                                } else {
                                    // Eğer resimUri null veya boşsa, kullanıcının profil fotoğrafı yok demektir
                                    System.out.println("Kullanıcının profil fotoğrafı yüklemesi başarısız.");
                                }
                            }
                        }
                    });
        }
    }


    public void goToFeed(View view) {
        Intent intent = new Intent(Profile.this, FeedActivity.class);
        startActivity(intent);
        finish();
    }

    public void saveImage(View view) {

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Kullanıcının UID'sini al
            String userId = user.getUid();
            UUID uuid = UUID.randomUUID();
            String usersImage = "users/" + uuid + ".jpg";
            storageReference.child(usersImage).putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    StorageReference newReference = firebaseStorage.getReference(usersImage);
                    newReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            System.out.println("burayagirdi0");
                            String userEmail = currentUser.getEmail();
                            String downloadUrl = uri.toString();
                            HashMap<String, Object> postData = new HashMap<>();
                            postData.put("resimUri", downloadUrl);

                            System.out.println("burayagirdi01");
                            // "Users" koleksiyonu altında kullanıcının UID'si ile belge ekleyin

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            DocumentReference belgeReferansi = db.collection("Users").document(userId);
                            belgeReferansi
                                    .update(postData)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(Profile.this, "Byrayagirdi22222", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });
                }
            });
        }
    }

    public void selectImage(View view) {
        //eger izin yoksa
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                //izin vericegiz
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_MEDIA_IMAGES)) {
                    Snackbar.make(view, "gallery için izin gerekli", Snackbar.LENGTH_INDEFINITE).setAction("izin ver", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //izin isteme
                            permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES);
                        }
                    }).show();
                } else {
                    //izin isteme
                    permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES);
                }
            } else {
                //izin verilmis gallery'e git
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intentToGallery);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //izin vericegiz
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Snackbar.make(view, "gallery için izin gerekli", Snackbar.LENGTH_INDEFINITE).setAction("izin ver", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //izin isteme
                            permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE);
                        }
                    }).show();
                } else {
                    //izin isteme
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
            } else {
                //izin verilmis gallery'e git
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intentToGallery);
            }
        }
    }

    private void registerLauncher() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {
                if (o.getResultCode() == Activity.RESULT_OK) {
                    Intent intentFromResult = o.getData();
                    if (intentFromResult != null) {
                        imageData = intentFromResult.getData();
                        binding.imageView2.setImageURI(imageData);
                        /*try {
                            if(Build.VERSION.SDK_INT >= 28){
                                ImageDecoder.Source source = ImageDecoder.createSource(UploadActivity.this.getContentResolver(), imageData);
                                selectedBitmap = ImageDecoder.decodeBitmap(source);
                            }else{
                                selectedBitmap = MediaStore.Images.Media.getBitmap(UploadActivity.this.getContentResolver(),imageData);
                                binding.imageView.setImageBitmap(selectedBitmap);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }*/
                    }
                }
            }
        });

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean o) {
                //izin verildi mi?
                if (o) {
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);

                } else {
                    Toast.makeText(Profile.this, "İzin gerekli!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}