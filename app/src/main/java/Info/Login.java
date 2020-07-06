package Info;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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


public class Login extends AppCompatActivity {

    EditText EditText1;
    EditText EditText2;
    TextView textView;
    String myEmail, myPassword;
    private FirebaseAuth FirebaseAuth;
    ProgressBar progressBar;

    @SuppressLint("ClickableViewAccessibility")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //EditText Email
        EditText1 = findViewById(R.id.editText_email);
        EditText1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText1.setBackground(getResources().getDrawable(R.drawable.edittext_bg_focused));
                if(!hasFocus){
                    EditText1.setBackground(getResources().getDrawable(R.drawable.edittext_bg));
                }
            }

        });

        EditText2 = findViewById(R.id.editText_Confirm);
        EditText2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText2.setBackground(getResources().getDrawable(R.drawable.edittext_bg_focused));
                if(!hasFocus){
                    EditText2.setBackground(getResources().getDrawable(R.drawable.edittext_bg));
                }
            }
        });

        ConstraintLayout constraintLayout = findViewById(R.id.constraint);
        constraintLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (EditText1.isFocused() || EditText2.isFocused()) {
                        EditText1.clearFocus();
                        EditText2.clearFocus();
                        Rect outRect1 = new Rect();
                        Rect outRect2 = new Rect();
                        EditText1.getGlobalVisibleRect(outRect1);
                        EditText2.getGlobalVisibleRect(outRect2);
                        if (!outRect1.contains((int)event.getRawX(), (int)event.getRawY())) {
                            EditText1.clearFocus();
                            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        }
                        if (!outRect2.contains((int)event.getRawX(), (int)event.getRawY())) {
                            EditText2.clearFocus();
                            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        }
                    }
                }
                return false;
            }
        });

        textView = findViewById(R.id.textView1);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(Login.this, forget.class);
                //startActivity(intent);
            }
        });

        /////////Firebase/////////
        FirebaseAuth = FirebaseAuth.getInstance();

        if(FirebaseAuth.getCurrentUser() != null){
            Intent intent = new Intent(Login.this, MainActivity.class);
            intent.putExtra("msg", "login");
            startActivity(intent);
            finish();
        }

        //////////////////////////

        progressBar = findViewById(R.id.progressBar2);

    }

    public void onClick_login(View view){


        myEmail = EditText1.getText().toString();
        myPassword = EditText2.getText().toString();

        if(TextUtils.isEmpty(myEmail)){
            EditText1.setError("Email is required!");
            EditText1.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(myPassword)){
            EditText2.setError("Password is required!");
            EditText2.requestFocus();
            return;
        }

        else{
            progressBar.setVisibility(View.VISIBLE);
            FirebaseAuth.signInWithEmailAndPassword(myEmail, myPassword)
                    .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                progressBar.setVisibility(View.GONE);
                                Intent intent = new Intent(Login.this, MainActivity.class);
                                intent.putExtra("msg", "login");
                                startActivity(intent);
                                finish();
                            } else {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(Login.this, "Email or Password incorrect!", Toast.LENGTH_LONG).show();
                            }

                            //
                        }
                    });
        }

    }

    public void onClick_btn_account(View view){
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
    }
}
