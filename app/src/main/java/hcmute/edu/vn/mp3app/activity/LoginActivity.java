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
import android.widget.ImageView;
import android.widget.Toast;

import hcmute.edu.vn.mp3app.Global;
import hcmute.edu.vn.mp3app.R;
import hcmute.edu.vn.mp3app.database.DB;

public class LoginActivity extends AppCompatActivity {
    private EditText et_username, et_password, et_forgetPassword, et_register;
    private ImageView bt_login;
    private SQLiteDatabase db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        et_forgetPassword = findViewById(R.id.et_forgetPassword);
        et_register = findViewById(R.id.et_register);
        bt_login = findViewById(R.id.bt_login);
        DB my_db = new DB(this);
        db = my_db.getReadableDatabase();

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(et_username.getText().toString(), et_password.getText().toString());
            }
        });

        et_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        et_forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });
    }
    @SuppressLint("Range")
    private void login(String username, String password){
        boolean loginSuccessful = false;
        Cursor cursor = db.rawQuery("SELECT username, password FROM user ",null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast())
        {
            String uname=cursor.getString(cursor.getColumnIndex("username")).trim();
            String pass=cursor.getString(cursor.getColumnIndex("password")).trim();
            if(username.trim().equals(uname.trim()) &&
                    password.trim().equals(pass.trim()))
            {
                Cursor cursor2=db.rawQuery("SELECT * FROM user WHERE username='"+username.trim()+"' AND password='"+password.trim()+"'",null);
                cursor2.moveToFirst();
                while(!cursor2.isAfterLast())
                {
                    int userID=cursor2.getInt(cursor2.getColumnIndex("id"));
                    Global.setGlobalUserID(userID);
                    cursor2.moveToNext();
                }
                cursor2.close();
                loginSuccessful = true;
                break;
            }
            cursor.moveToNext();
        }
        cursor.close();

        if (loginSuccessful) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(LoginActivity.this, "Login failed! Wrong username or password!", Toast.LENGTH_SHORT).show();
        }
    }
}