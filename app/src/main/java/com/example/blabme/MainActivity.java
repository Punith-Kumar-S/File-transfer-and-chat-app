package com.example.blabme;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {
    public Button button;
    public Button button1;
    private TextView username;
    private TextView password;
    private TextView info;
    signup newsignup=new signup();
    private String u;
    private String p;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = (EditText) findViewById(R.id.user);
        password = (EditText) findViewById(R.id.pass);
        info = findViewById(R.id.info);
        Button signin = (Button) findViewById(R.id.signinbutton);
        Button signup = (Button) findViewById(R.id.signupbutton);
        button1 = findViewById(R.id.signinbutton);
        Button exit = null;
        try {
            exit = (Button) findViewById(R.id.exit);
        } catch (ClassCastException ignored) {
        }

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openmain();
                validate(username.getText().toString(), password.getText().toString());
            }
        });


        button = findViewById(R.id.signupbutton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openactivity2();
            }
        });
        try{exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean nonRoot;
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);


            }
        });}
        catch(NullPointerException ignored){}
    }
    public void openactivity2(){
        Intent intent =new Intent(this,signup.class);
        startActivity(intent);
    }

    private void validate(String toString, String toString1) {
        try {
            String u = username.getText().toString().trim().toLowerCase();
            String p = password.getText().toString().trim().toLowerCase();
            openmain();
           /* for (int i = 0; i < newsignup.count; i++) {
                if (u.contentEquals(newsignup.user.get(i)) && (p.contentEquals(newsignup.pass.get(i)))) {
                    info.setText("Sucessfully Logined");
                    openmain();
                } else {
                    info.setText("Entered Wrong Username or Password");
                }
            }*/
        }
        catch(NullPointerException ignored){}


    }
    public void openmain() {
        Intent intent = new Intent(MainActivity.this, mainclass.class);
        startActivity(intent);

    }




}
