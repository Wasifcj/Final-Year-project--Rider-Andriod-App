package com.cjz.wasif.lifesaverrider;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cjz.wasif.lifesaverrider.Model.Customer;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileCustomer extends AppCompatActivity {

    private Button back,Confirm;
    private EditText Customer_Name_edit;
    private TextView  Customer_contact_edit;
    private FirebaseAuth auth;
    private DatabaseReference mCustomerDataBase;
    private String user_id;
    private String customer_name;
    private String customer_contact;
    private CircleImageView Customer_profile;
    private Uri resultUri;
    private String Customer_profile_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_customer);




        back=(Button)findViewById(R.id.back_id);
        Confirm=(Button)findViewById(R.id.confirm_id);

        Customer_contact_edit=(TextView) findViewById(R.id.customer_contact_id);
        Customer_Name_edit=(EditText)findViewById(R.id.customer_name_id);
//       profile image of driver
        Customer_profile=(CircleImageView)findViewById(R.id.profile_image);
        Customer_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,1);

            }
        });






        auth=FirebaseAuth.getInstance();
        user_id=FirebaseAuth.getInstance().getCurrentUser().getUid();
        mCustomerDataBase= FirebaseDatabase.getInstance().getReference().child("Customers").child(user_id);

        GetCustomerInfo();


        Confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UpdateCustomerData();

                finish();
                return;

            }
        });


        Customer_contact_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(EditProfileCustomer.this,editNumberRegistration.class);
                intent.putExtra("driver_id",user_id);
                startActivity(intent);
            }
        });


    }

    private void UpdateCustomerData() {

        customer_name=Customer_Name_edit.getText().toString();
        //customer_contact=Customer_contact_edit.getText().toString();

        Map CustomerInfo=new HashMap();
        CustomerInfo.put("name",customer_name);
       // CustomerInfo.put("phone",customer_contact);

        mCustomerDataBase.updateChildren(CustomerInfo);
        //store image on database
        if(resultUri!=null){

            final StorageReference filepath= FirebaseStorage.getInstance().getReference().child("profile_images").child(user_id);
            Bitmap bitmap=null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(),resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //smaller size of Image
            ByteArrayOutputStream baos= new ByteArrayOutputStream();

            bitmap.compress(Bitmap.CompressFormat.JPEG,20,baos);

            byte [] data= baos.toByteArray();

            UploadTask uploadTask=filepath.putBytes(data);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Map newImage = new HashMap();
                            newImage.put("profileImageUrl", uri.toString());
                            mCustomerDataBase.updateChildren(newImage);

                            finish();
                            return;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            finish();
                            return;
                        }
                    });
                }
            });
        }
        finish();


    }
    private void GetCustomerInfo() {

        mCustomerDataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()&& dataSnapshot.getChildrenCount()>0){
                    Map<String,Object> map=(Map<String, Object>)dataSnapshot.getValue();
                    if(map.get("name")!=null) {
                        customer_name = map.get("name").toString();
                        Customer_Name_edit.setText(customer_name);
                    }
                    if(map.get("phone")!=null) {
                        customer_contact = map.get("phone").toString();
                       // Customer_contact_edit.setText(customer_contact);
                        Customer_contact_edit.setText(customer_contact);
                    }

                    if(map.get("profileImageUrl")!=null) {
                        Customer_profile_image = map.get("profileImageUrl").toString();
                        Glide.with(getApplicationContext()).load(Customer_profile_image).into(Customer_profile);

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 && resultCode== Activity.RESULT_OK );
        {
            if(data!=null){
                final Uri imageUri= data.getData();
                resultUri=imageUri;
                Customer_profile.setImageURI(resultUri);
            }
            else {
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
            }

        }

    }
}

