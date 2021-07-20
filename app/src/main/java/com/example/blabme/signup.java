package com.example.blabme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import android.content.Intent;

public class signup extends AppCompatActivity {
    public static ArrayList<String> user;
    public static ArrayList<String>  pass;
    private TextView username;
    private TextView password;
    private TextView confirmpassword;
    private Button sign;
    static public int count=0;
    private TextView info2;
    private Button back;
    private Button back2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        user = new ArrayList<>();
        pass = new ArrayList<>();

        username=(EditText)findViewById(R.id.user);
        password=(EditText)findViewById(R.id.pass);
        confirmpassword=(EditText)findViewById(R.id.confirmpass);
        info2=(TextView) findViewById(R.id.info2);
        sign = (Button)findViewById(R.id.signup);
        final String x=username.getText().toString().trim().toLowerCase();
        final String y=password.getText().toString().trim().toLowerCase();
        back =(Button)findViewById(R.id.backsignin);
        back2=(Button)findViewById(R.id.back);
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(x.contentEquals(y))
                {
                    user.add(x);
                    pass.add(y);
                    count++;
                    info2.setText("User ADDED Sucessfully!");
                }
                else{
                    info2.setText("Enter the same Password!");
                }

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(signup.this,MainActivity.class));
            }
        });
        back2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent (signup.this,MainActivity.class));
            }
        });


    }
}





