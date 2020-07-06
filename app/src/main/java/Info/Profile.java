package Info;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.emot.emotionPlayer.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Profile extends AppCompatActivity {

    String name, email, password;
    EditText Email, Password, Name;
    String EditText_name, EditText_email, EditText_password;
    TextView Uid;
    Button Cancel, Confirm, Update;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    FirebaseUser user;
    ProgressBar progressBar;
    String UID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);



        Cancel = findViewById(R.id.button_Cancel);
        Confirm = findViewById(R.id.button_Confirm);
        Update = findViewById(R.id.button_Update);


        progressBar = findViewById(R.id.progressBar3);
        progressBar.setVisibility(View.VISIBLE);

        user = FirebaseAuth.getInstance().getCurrentUser();
        UID = user.getUid();
        firebaseAuth = FirebaseAuth.getInstance();

        Name = findViewById(R.id.EditTextName);
        Email = findViewById(R.id.EditTextEmail);
        Password = findViewById(R.id.EditTextPassword);
        Uid = findViewById(R.id.textViewUID);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("userInfo");
        if(firebaseAuth.getCurrentUser() != null){


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                email = dataSnapshot.child(UID).child("email").getValue().toString();
                name = dataSnapshot.child(UID).child("fullName").getValue().toString();
                password = dataSnapshot.child(UID).child("password").getValue().toString();

                Uid.setText("UID: " + UID);
                Name.setText(name);
                Email.setText(email);
                Password.setText(password);
                Uid.setVisibility(View.VISIBLE);
                Name.setVisibility(View.VISIBLE);
                Email.setVisibility(View.VISIBLE);
                Password.setVisibility(View.VISIBLE);

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Profile.this, "DB error or internet not connected", Toast.LENGTH_LONG).show();
            }
        });
        }
    }

    public void update_Profile(View view){
        Name.setEnabled(true);
        Email.setEnabled(true);
        Password.setEnabled(true);
        Cancel.setEnabled(true);
        Confirm.setEnabled(true);
        Update.setEnabled(false);
    }
    public void cancel_Profile(View view){
        Name.setEnabled(false);
        Email.setEnabled(false);
        Password.setEnabled(false);
        Cancel.setEnabled(false);
        Confirm.setEnabled(false);
        Update.setEnabled(true);
    }
    public void confirm_Profile(View view){
        EditText_email = Email.getText().toString();
        EditText_name = Name.getText().toString();
        EditText_password = Password.getText().toString();
        if(EditText_name.equals(name) && EditText_email.equals(email) && EditText_password.equals(password)){
            Toast.makeText(this, "Change atleast one of the info", Toast.LENGTH_SHORT).show();
        }
        else{
            HashMap hashMap = new HashMap();
            hashMap.put("fullName", name);
            hashMap.put("email", email);
            hashMap.put("password", password);
            databaseReference.child(UID).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    Confirm.setEnabled(false);
                    Cancel.setEnabled(false);
                    Update.setEnabled(true);
                    Toast.makeText(Profile.this, "Updated Successfully", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void refresh_Profile(View view){
        Toast.makeText(this, "refreshed", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, Profile.class));
    }
}
