package kr.ac.jbnu.se.stkim.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import kr.ac.jbnu.se.stkim.R;
import kr.ac.jbnu.se.stkim.models.User;

public class ProfileActivity extends AppCompatActivity {

    private ImageView editProfileButton;

    private TextView profileUserNickname;
    private TextView profileUserName;
    private TextView profileUserEmail;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        editProfileButton = findViewById(R.id.editProfileButton);
        editProfileButton.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, ProfileUpdateActivity.class));
            finish();
        });

        profileUserNickname = findViewById(R.id.profileUserNickname);
        profileUserName = findViewById(R.id.profileName);
        profileUserEmail = findViewById(R.id.profileEmail);

        FirebaseDatabase.getInstance()
                .getReference("Account")
                .child(mAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);

                        profileUserName.setText(user.getName());
                        profileUserEmail.setText(user.getEmail());
                        profileUserNickname.setText(user.getNickName());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }
}
