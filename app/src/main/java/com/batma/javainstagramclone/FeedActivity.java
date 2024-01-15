package com.batma.javainstagramclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.batma.javainstagramclone.Adapter.PostAdapter;
import com.batma.javainstagramclone.Model.Post;
import com.batma.javainstagramclone.databinding.ActivityFeedBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class FeedActivity extends AppCompatActivity {
    //FeedActiviyde Firebase den verileri çekeceğiz

    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    //Recycler viewda verilerimizi saklamak icin arraylist
    ArrayList<Post> postArrayList;
    private ActivityFeedBinding binding;
    PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFeedBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        //Baslangicta bos arrayla basladigimizi belirtmek icin
        postArrayList = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        getData();

        //recyclerview'un alt alta gosterecegimizi soyluyoruz
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        postAdapter = new PostAdapter(postArrayList,this);
        binding.recyclerView.setAdapter(postAdapter);

    }

    private void getData(){
        firebaseFirestore.collection("Posts").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Toast.makeText(FeedActivity.this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
                if(value != null){
                    //bu bize liste verdigi için for loop
                    for(DocumentSnapshot snapshot : value.getDocuments()){
                        Map<String, Object> data = snapshot.getData();

                        //gelen verileri object olarak tanımladıgımız icin casting yapıyorum
                        String id = (String) data.get("postId");
                        String userEmail = (String) data.get("useremail");
                        String comment = (String) data.get("comment");
                        String downloadUrl = (String) data.get("downloadUrl");
                        String publisher = (String)  data.get("publisher");

                        Log.d("Post",publisher);
                        //olusturdugumuz Post clasını kullaniyoruz
                        Post post = new Post(id,userEmail, comment, downloadUrl,publisher);
                        postArrayList.add(post);
                    }

                    //recyclerview'a yeni verinin gelecegini soylamesi icin haber veriyoruz
                    postAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    public void goToProfile(View view){
        Intent intent = new Intent(FeedActivity.this, Profile.class);
        startActivity(intent);
        finish();
    }

    public void goToUploadPhoto(View view){
        Intent intent = new Intent(FeedActivity.this, UploadActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.add_post){
            Intent intentToUpload = new Intent(FeedActivity.this, UploadActivity.class);
            startActivity(intentToUpload);
        } else if (item.getItemId() == R.id.sign_out) {
            //sigh out
            auth.signOut();
            Intent intentToMain = new Intent(FeedActivity.this, MainActivity.class);
            startActivity(intentToMain);
        }
        return super.onOptionsItemSelected(item);
    }
}