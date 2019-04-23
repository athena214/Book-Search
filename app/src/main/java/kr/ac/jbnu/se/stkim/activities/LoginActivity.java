package kr.ac.jbnu.se.stkim.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import kr.ac.jbnu.se.stkim.R;
import kr.ac.jbnu.se.stkim.models.User;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 10;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private EditText emailText;
    private EditText passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.googleSignInButton).setOnClickListener(v -> {
            findViewById(R.id.login_progress).setVisibility(View.VISIBLE);
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });

        //로그인
        emailText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);
        Button signInButton = findViewById(R.id.emailSignInButton);
        signInButton.setOnClickListener(v -> {
            findViewById(R.id.login_progress).setVisibility(View.VISIBLE);
            loginUser(emailText.getText().toString(), passwordText.getText().toString());
        });


        // 회원가입
        TextView registerText = findViewById(R.id.registerText);
        registerText.setOnClickListener(v -> {
            findViewById(R.id.login_progress).setVisibility(View.GONE);
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();
                        findViewById(R.id.login_progress).setVisibility(View.GONE);
                        finish();
                        startActivity(new Intent(this, MainActivity.class));
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        findViewById(R.id.login_progress).setVisibility(View.GONE);
                    }
                });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                findViewById(R.id.login_progress).setVisibility(View.GONE);
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                findViewById(R.id.login_progress).setVisibility(View.GONE);
                Log.w("DDDDDDD  ", "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        User model = new User();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information

                        model.setEmail(mAuth.getCurrentUser().getEmail());
                        model.setName(mAuth.getCurrentUser().getDisplayName());
                        model.setNickName(mAuth.getCurrentUser().getDisplayName());

                        FirebaseDatabase.getInstance()
                                .getReference("Account")
                                .child(mAuth.getUid())
                                .setValue(model);
                        findViewById(R.id.login_progress).setVisibility(View.GONE);
                        finish();
                        startActivity(new Intent(this, MainActivity.class));

                    } else {
                        findViewById(R.id.login_progress).setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this, "구글 로그인 실패", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}