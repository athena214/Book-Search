package kr.ac.jbnu.se.stkim.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import kr.ac.jbnu.se.stkim.R;
import kr.ac.jbnu.se.stkim.models.User;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private EditText editTextName;
    private EditText editTextNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();


        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        editTextName = findViewById(R.id.editTextName);
        editTextNickname = findViewById(R.id.editTextNickname);


        Button signUpButton = findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(v -> createUser(editTextEmail.getText().toString(), editTextPassword.getText().toString(), editTextConfirmPassword.getText().toString()));

    }

    private void createUser(String email, String password, String confirmPassword) {
        User model = new User();
        if (password.equals(confirmPassword)) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("SSSSSSSSSSSSSS", "createUserWithEmail:success");
                            Toast.makeText(RegisterActivity.this, "회원가입 성공",
                                    Toast.LENGTH_LONG).show();
                            model.setEmail(email);
                            model.setName(editTextName.getText().toString());
                            model.setNickName(editTextNickname.getText().toString());

                            FirebaseDatabase.getInstance()
                                    .getReference("Account")
                                    .child(mAuth.getUid())
                                    .setValue(model);

                            startActivity(new Intent(this, LoginActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("FFFFFFFFFFFFFF", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "회원가입 실패",
                                    Toast.LENGTH_LONG).show();
//                        updateUI(null);
                        }

                        // ...
                    });
        } else {
            Toast.makeText(RegisterActivity.this, "비밀번호가 일치하지 않습니다.",
                    Toast.LENGTH_LONG).show();
        }
    }
}
