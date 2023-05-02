package com.cjz.wasif.lifesaverrider;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class contact_customer extends AppCompatActivity {

    private Button send;
    private EditText customer_email;
    private EditText customer_message;
    private String custmer_messages;
    private String customer_emaill;
    private DatabaseReference mydatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_customer);

        customer_email=(EditText)findViewById(R.id.email_customer);
        customer_message=(EditText)findViewById(R.id.message_customer);
        send=(Button)findViewById(R.id.send_customer);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(customer_email.getText().toString())){

                    Toast.makeText(contact_customer.this, "Please Entered first email !!", Toast.LENGTH_SHORT).show();
                }
               else if (TextUtils.isEmpty(customer_message.getText().toString())){

                    Toast.makeText(contact_customer.this, "Please Entered first Message !!", Toast.LENGTH_SHORT).show();
                }
                else {

                    if(isEmailValid(customer_email.getText().toString())==true){

                  customer_emaill=customer_email.getText().toString();
                  custmer_messages=customer_message.getText().toString();

                  mydatabase= FirebaseDatabase.getInstance().getReference("Customer Message");
                  mydatabase.child("Email").setValue(customer_emaill);
                    mydatabase.child("Message").setValue(custmer_messages);

                    Toast.makeText(contact_customer.this, "Message send Successfully!!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(contact_customer.this,customer_contact_extented.class));
                }
                else {
                        Toast.makeText(contact_customer.this, "Email is not valid!", Toast.LENGTH_SHORT).show();
                    }
                }






            }
        });






    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


}
