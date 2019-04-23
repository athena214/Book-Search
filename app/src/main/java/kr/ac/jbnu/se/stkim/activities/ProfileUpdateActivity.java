package kr.ac.jbnu.se.stkim.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import kr.ac.jbnu.se.stkim.R;
import kr.ac.jbnu.se.stkim.models.User;

public class ProfileUpdateActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView textViewNickname;
    private Button updateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_update);

        textViewNickname = findViewById(R.id.editTextNickname);

        updateButton = findViewById(R.id.updateButton);
        updateButton.setOnClickListener(v -> {
            User model = new User();
            model.setNickName(textViewNickname.getText().toString());

            FirebaseDatabase.getInstance()
                    .getReference("Account")
                    .child(mAuth.getUid())
                    .setValue(model);
            startActivity(new Intent(this, ProfileActivity.class));
        });
    }

    public void updatePassword(String password) {
        // [START update_password]
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String newPassword = password;

        user.updatePassword(newPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("gggggggggggg", "User password updated.");
                    }
                });
        // [END update_password]
    }
}
