package hcmute.edu.vn.mp3app.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import hcmute.edu.vn.mp3app.R;
import hcmute.edu.vn.mp3app.UserDAO;
import hcmute.edu.vn.mp3app.database.DB;

public class RegisterActivity extends AppCompatActivity {
    private EditText et_usernameRegister, et_passwordRegister, et_re_password, et_email;
    private Button bt_register;
    SQLiteDatabase db;
    private UserDAO userDAO;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_usernameRegister = findViewById(R.id.et_usernameRegister);
        et_passwordRegister = findViewById(R.id.et_passwordRegister);
        et_re_password = findViewById(R.id.et_re_password);
        et_email = findViewById(R.id.et_email);
        bt_register = findViewById(R.id.bt_register);
        DB my_db = new DB(this);
        db = my_db.getReadableDatabase();
        userDAO = new UserDAO(this);
        userDAO.open();

        bt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register(et_usernameRegister.getText().toString(), et_passwordRegister.getText().toString(), et_email.getText().toString());
            }
        });

    }
    private void register(String username, String password, String email){
        // Check null
        if(verif()){
            // Check exist
            if(userDAO.isUsernameExists(username)){
                Toast.makeText(RegisterActivity.this,"Username existed!",Toast.LENGTH_SHORT).show();
            } else if (userDAO.isEmailExists(email)) {
                Toast.makeText(RegisterActivity.this,"Email existed!",Toast.LENGTH_SHORT).show();
            }
            else{
                if(password.length() >= 8)
                {
                    if(password.trim().equals(et_re_password.getText().toString().trim())){
                        userDAO.createUser(username.trim(),password.trim(),
                                username.trim(),email.trim(),"https://firebasestorage.googleapis.com/v0/b/tunebox-d7865.appspot.com/o/images%2Favatar.png?alt=media&token=722dfe7b-0cd9-4d08-9e32-a7193f3cff10");
                        Toast.makeText(RegisterActivity.this,"Register successfully!",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finishAffinity();
                    }
                    else
                        Toast.makeText(RegisterActivity.this,"Password does not match!",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(this, "Password length must be >=8", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else
        {
            Toast.makeText(RegisterActivity.this,"Please fill all the fields!",Toast.LENGTH_SHORT).show();
        }
    }

    // Check null value
    private boolean verif()
    {
        if(et_usernameRegister.getText().toString().equals("")
                || et_passwordRegister.getText().toString().equals("")
                || et_re_password.getText().toString().equals("")
                || et_email.getText().toString().equals(""))
        {
            return false;
        }
        else
            return true;
    }

}