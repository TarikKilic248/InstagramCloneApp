package com.batma.javainstagramclone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.batma.javainstagramclone.CommentsActivity;
import com.batma.javainstagramclone.Model.Post;
import com.batma.javainstagramclone.Model.Users;
import com.batma.javainstagramclone.R;
import com.batma.javainstagramclone.databinding.RecyclerRowBinding;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {
    private ArrayList<Post> postArrayList;
    public Context context;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    public PostAdapter(ArrayList<Post> postArrayList, Context context) {
        this.postArrayList = postArrayList;
        this.context = context;
        this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }


    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowBinding recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new PostHolder(recyclerRowBinding);
    }

    @Override
    public int getItemCount() {
        return postArrayList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {
        firebaseFirestore = FirebaseFirestore.getInstance();
        /*
        holder.recyclerRowBinding.recyclerViewUserEmailText.setText(postArrayList.get(position).email);
*/
        holder.recyclerRowBinding.recyclerViewCommentText.setText(postArrayList.get(position).comment);

        Picasso.get().load(postArrayList.get(position).downloadUrl).into(holder.recyclerRowBinding.recyclerViewImageView);

        Post post = postArrayList.get(position);



        publisherInfo(holder.image_profile,holder.username,post.getPublisher());



        isLiked(post.getPostId(),holder.like);
        getLikeCount(post.getPostId(),holder.likes);
        getCommentCount(post.getPostId(),holder.commentCount);

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.like.getTag().equals("like")) {
                    Map<String, Object> likeData = new HashMap<>();
                    likeData.put("liked", true);
                    firebaseFirestore.collection("Likes").document(post.postId).collection("liked").document(firebaseUser.getUid())
                            .set(likeData)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(context, "Beğenildi", Toast.LENGTH_SHORT).show();
                                    //Log.d("Deneme", "DocumentSnapshot successfully written!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText  (context, "Beğenilmedi", Toast.LENGTH_SHORT).show();
                                }
                            });
                }else{
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    db.collection("Likes").document(post.postId).collection("liked").document(firebaseUser.getUid())
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(context, "Beğeni Silindi", Toast.LENGTH_SHORT).show();
                                }
                            });
                }

            }

        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommentsActivity.class);
                intent.putExtra("postid",post.getPostId());
                intent.putExtra("publisherid",post.getEmail());
                context.startActivity(intent);
            }
        });


    }
    class PostHolder extends RecyclerView.ViewHolder{

        RecyclerRowBinding recyclerRowBinding;
        public ImageView like,image_profile;
        public ImageView comment;
        public TextView username,likes,commentCount;

        public PostHolder(RecyclerRowBinding recyclerRowBinding) {
            super(recyclerRowBinding.getRoot());
            this.recyclerRowBinding=recyclerRowBinding;
            commentCount = recyclerRowBinding.getRoot().findViewById(R.id.commentCount);
            likes = recyclerRowBinding.getRoot().findViewById(R.id.likes);
            like = recyclerRowBinding.getRoot().findViewById(R.id.like);
            comment= recyclerRowBinding.getRoot().findViewById(R.id.comment);
            image_profile = recyclerRowBinding.getRoot().findViewById(R.id.image_profile);
            username=recyclerRowBinding.getRoot().findViewById(R.id.recyclerViewUserEmailText);
        }
    }
    //isLiked metodu çağrılarak kullanıcının gönderiyi beğenip beğenmediği kontrol edilir. Bu duruma göre like butonunun görüntüsü ve etiketi değiştirilir.
    //Beğenildi mi kontrolü ?
    private void isLiked(String postid,ImageView imageView){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser1 = FirebaseAuth.getInstance().getCurrentUser();

        DocumentReference docRef = db.collection("Likes").document(postid).collection("liked").document(firebaseUser1.getUid());

        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    imageView.setImageResource(R.drawable.ic_liked);
                    imageView.setTag("liked");
                } else {
                    imageView.setImageResource(R.drawable.ic_like);
                    imageView.setTag("like");
                }
            }
        });
    }
    private void getLikeCount(String postid, TextView textView) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference likesCollection = db.collection("Likes").document(postid).collection("liked");

        likesCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG", "Listen failed.", e);
                    return;
                }

                if (snapshot != null) {
                    int likeCount = snapshot.size();
                    textView.setText(String.valueOf(likeCount));
                }
            }
        });
    }
    private void getCommentCount(String postid, TextView textView) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference likesCollection = db.collection("Comments").document(postid).collection("Comments");

        likesCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG", "Listen failed.", e);
                    return;
                }

                if (snapshot != null) {
                    int likeCount = snapshot.size();
                    textView.setText(String.valueOf(likeCount));
                }
            }
        });
    }




    //Fotoyu paylaşan kişi bilgileri düzenleme
    private void publisherInfo(final ImageView image_profile, final TextView username, final String userid){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("Users").document(userid);

        Log.d("Publisherinfo","Burasi1");
        Log.d("Publisherinfo",userid);

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Log.d("Publisherinfo","Burasi1");
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Users kullanici = document.toObject(Users.class);
                        System.out.println(kullanici.getResimUri());
                        System.out.println(kullanici.getEmail());

                        if (kullanici.getResimUri().equals("")){
                            image_profile.setBackgroundResource(R.drawable.profilphoto);
                        }else{
                            Log.d("Resim",kullanici.getResimUri());
                            Glide.with(context).load(kullanici.getResimUri()).into(image_profile);
                        }
                        username.setText(kullanici.getEmail());
                    }
                }
            }
        });

    }


}
