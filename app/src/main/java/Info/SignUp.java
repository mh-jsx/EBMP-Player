package Info;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.emot.emotionPlayer.R;
import com.emot.emotionPlayer.activities.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    EditText EditText_email;
    EditText EditText_password;
    EditText EditText_name;
    EditText EditText_confirm;
    TextView textView;
    Intent intent;
    ProgressBar progressBar;
    DatabaseReference rootNode;

    private com.google.firebase.auth.FirebaseAuth FirebaseAuth;
    String myEmail, myPassword, myConfirm, myName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        EditText_email = findViewById(R.id.editText_email);
        EditText_email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText_email.setBackground(getResources().getDrawable(R.drawable.edittext_bg_focused));
                if(!hasFocus){
                    EditText_email.setBackground(getResources().getDrawable(R.drawable.edittext_bg));
                }
            }

        });

        EditText_name = findViewById(R.id.editText_name);
        EditText_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText_name.setBackground(getResources().getDrawable(R.drawable.edittext_bg_focused));
                if(!hasFocus){
                    EditText_name.setBackground(getResources().getDrawable(R.drawable.edittext_bg));
                }
            }

        });

        EditText_confirm = findViewById(R.id.editText_Confirm);
        EditText_confirm.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText_confirm.setBackground(getResources().getDrawable(R.drawable.edittext_bg_focused));
                if(!hasFocus){
                    EditText_confirm.setBackground(getResources().getDrawable(R.drawable.edittext_bg));
                }
            }

        });

        EditText_password = findViewById(R.id.editText_Pass);
        EditText_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText_password.setBackground(getResources().getDrawable(R.drawable.edittext_bg_focused));
                if(!hasFocus){
                    EditText_password.setBackground(getResources().getDrawable(R.drawable.edittext_bg));
                }
            }

        });

        ConstraintLayout constraintLayout = findViewById(R.id.sign_contraint);
        constraintLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (EditText_email.isFocused() || EditText_confirm.isFocused() || EditText_name.isFocused() || EditText_password.isFocused()) {
                        EditText_email.clearFocus();
                        EditText_confirm.clearFocus();
                        EditText_name.clearFocus();
                        EditText_password.clearFocus();
                        Rect outRect1 = new Rect();
                        Rect outRect2 = new Rect();
                        Rect outRect3 = new Rect();
                        Rect outRect4 = new Rect();
                        EditText_email.getGlobalVisibleRect(outRect1);
                        EditText_confirm.getGlobalVisibleRect(outRect2);
                        EditText_name.getGlobalVisibleRect(outRect3);
                        EditText_password.getGlobalVisibleRect(outRect4);
                        if (!outRect1.contains((int)event.getRawX(), (int)event.getRawY())) {
                            EditText_email.clearFocus();
                            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        }
                        if (!outRect2.contains((int)event.getRawX(), (int)event.getRawY())) {
                            EditText_confirm.clearFocus();
                            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        }
                        if (!outRect3.contains((int)event.getRawX(), (int)event.getRawY())) {
                            EditText_name.clearFocus();
                            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        }
                        if (!outRect4.contains((int)event.getRawX(), (int)event.getRawY())) {
                            EditText_password.clearFocus();
                            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        }
                    }
                }
                return false;
            }
        });


        textView = findViewById(R.id.textView7);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(SignUp.this, Login.class);
                startActivity(intent);
            }
        });

        //Firebase

        FirebaseAuth = FirebaseAuth.getInstance();

        rootNode = FirebaseDatabase.getInstance().getReference("userInfo");

        //progress

        progressBar = findViewById(R.id.progressBar);


    }

    public void onClick_register(View view){

        myEmail = EditText_email.getText().toString().trim();
        myConfirm = EditText_confirm.getText().toString().trim();
        myPassword = EditText_password.getText().toString();
        myName = EditText_name.getText().toString();

        if(TextUtils.isEmpty(myEmail)){
            EditText_email.setError("Email is required!");
            EditText_email.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(myName)){
            EditText_name.setError("Name is required!");
            EditText_name.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(myConfirm)){
            EditText_confirm.setError("Password is required!");
            EditText_confirm.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(myPassword)){
            EditText_password.setError("Confirm Password is required!");
            EditText_password.requestFocus();
            return;
        }
        if(myConfirm.length() < 6){
            EditText_confirm.requestFocus();
            EditText_confirm.setError("Password can contain minimum 6 character!");
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(myEmail).matches()){
            EditText_email.setError("Please enter a valid email!");
            return;
        }

        if(myConfirm.equals(myPassword)){

            progressBar.setVisibility(View.VISIBLE);

            FirebaseAuth.createUserWithEmailAndPassword(myEmail, myConfirm)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                userInformation info = new userInformation(myName, myEmail, myConfirm);
                                FirebaseDatabase.getInstance().getReference("userInfo")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(info).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {


                                        Toast.makeText(SignUp.this, "Account created Successfully!", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                        FirebaseAuth = FirebaseAuth.getInstance();
                                        FirebaseAuth.signOut();
                                        Intent intent = new Intent(SignUp.this, Login.class);
                                        startActivity(intent);
                                        finish();

                                    }
                                });


                            } else {
                                // If sign in fails, display a message to the user.
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(SignUp.this, "Authentication failed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            return;
        }
        else{
            EditText_password.requestFocus();
            EditText_password.setError("Password didn't matched!");
        }
    }
}
