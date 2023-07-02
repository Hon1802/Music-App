package hcmute.edu.vn.mp3app.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import hcmute.edu.vn.mp3app.Global;
import hcmute.edu.vn.mp3app.R;
import hcmute.edu.vn.mp3app.UserDAO;
import hcmute.edu.vn.mp3app.model.User;

public class ChangePasswordActivity extends AppCompatActivity {
    private EditText et_oldPassword, et_newPassword, et_reNewPassword;
    private Button bt_changePassword;
    private User user;
    private UserDAO userDAO;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        et_oldPassword = findViewById(R.id.et_oldPassword);
        et_newPassword = findViewById(R.id.et_newPassword);
        et_reNewPassword = findViewById(R.id.et_reNewPassword);
        bt_changePassword = findViewById(R.id.bt_changePassword);
        userDAO = new UserDAO(this);
        user = userDAO.getUserByID(Global.GlobalUserID);

        bt_changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword(et_newPassword.getText().toString().trim());
            }
        });

    }
    private void changePassword(String newPassword){
        // Check null field
        if(verif()){
            if(user!=null && user.getPassword().trim().equals(et_oldPassword.getText().toString().trim()))
            {
                if(newPassword.length() >=8){
                    if(newPassword.trim().equals(et_reNewPassword.getText().toString().trim())){
                        Toast.makeText(this, "Password changed!", Toast.LENGTH_SHORT).show();
                        user.setPassword(newPassword.trim());
                        userDAO.updateUser(user);
                        finish();
                    }
                    else{
                        Toast.makeText(this, "New password does not match!", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(this, "Password length must be >=8", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(this, "Wrong old password!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean verif()
    {
        if(et_oldPassword.getText().toString().equals("")
                || et_newPassword.getText().toString().equals("")
                || et_reNewPassword.getText().toString().equals(""))
        {
            return false;
        }
        else
            return true;
    }

}