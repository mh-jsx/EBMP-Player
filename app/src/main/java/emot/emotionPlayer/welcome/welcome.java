package emot.emotionPlayer.welcome;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.emot.emotionPlayer.R;
import com.emot.emotionPlayer.activities.MainActivity;
import com.google.firebase.auth.FirebaseAuth;

import Info.Login;

public class welcome extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null){
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("msg", "login");
            startActivity(intent);
        }

    }


    public void onClick_login(View view){
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    public void onClick_skip(View view){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("msg", "test");
        startActivity(intent);
    }
}
