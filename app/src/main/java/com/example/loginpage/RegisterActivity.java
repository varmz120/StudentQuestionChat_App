package com.example.loginpage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loginpage.utility.Database;
import com.example.loginpage.utility.LoadingDialogFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    private Spinner Role;
    private Button Register;
    private Button Back;
    private String Username;
    private String Password;
    private String confirmPassword;
    private String selectedRole = "";
    private ArrayAdapter<CharSequence> adapter;
    private Database mDatabase;

    public LoadingDialogFragment loadingDialogFragment = new LoadingDialogFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Role = (Spinner) findViewById(R.id.spinnerRole);
        Register = (Button) findViewById(R.id.register);
        Back = (Button) findViewById(R.id.back);
        mDatabase = Database.getInstance();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        adapter= ArrayAdapter.createFromResource(this, R.array.role, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        Role.setAdapter(adapter);
        Role.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRole = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Username = ((EditText) findViewById(R.id.newusername)).getText().toString();
                Password = ((EditText) findViewById(R.id.newpassword)).getText().toString();
                confirmPassword = ((EditText) findViewById(R.id.confirmpassword)).getText().toString();
                if (TextUtils.isEmpty(Username)){
                    Toast.makeText(getApplicationContext(),"Username is empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (selectedRole.equals("")){
                    Toast.makeText(RegisterActivity.this,"Registration failed, please select a role",Toast.LENGTH_LONG).show();
                } else if (Password.equalsIgnoreCase(confirmPassword) && !Password.equalsIgnoreCase("")) {
                    mAuth.createUserWithEmailAndPassword(Username, Password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Get the user ID
                                        String userId = mAuth.getCurrentUser().getUid();
                                        mDatabase.storeDetails(userId,Username,selectedRole);

                                        // Show success message
                                        Toast.makeText(RegisterActivity.this, "Successful Registration. Press back to return to home page", Toast.LENGTH_LONG).show();
                                    } else {
                                        // Show error message
                                        Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        Log.d("TAG",task.getException().getMessage());
                                    }
                                }
                            });

                } else
                {
                    Toast.makeText(RegisterActivity.this,"Password fields are empty or do not match", Toast.LENGTH_LONG).show();
                }
                ((EditText) findViewById(R.id.newusername)).setText("");
                ((EditText) findViewById(R.id.newpassword)).setText("");
                ((EditText) findViewById(R.id.confirmpassword)).setText("");
            }
        });
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!loadingDialogFragment.isAdded()) {
                    loadingDialogFragment.show(getSupportFragmentManager(), "loader");
                }
                Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

    }}

