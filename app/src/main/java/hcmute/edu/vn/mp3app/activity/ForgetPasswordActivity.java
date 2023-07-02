package hcmute.edu.vn.mp3app.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import hcmute.edu.vn.mp3app.R;
import hcmute.edu.vn.mp3app.SendMail;
import hcmute.edu.vn.mp3app.UserDAO;
import hcmute.edu.vn.mp3app.model.User;

public class ForgetPasswordActivity extends AppCompatActivity {
    private EditText et_emailForget, et_newPasswordForget, et_reNewPasswordForget, et_OTP;
    private Button bt_resetPassword, bt_sendOTP;
    private User user;
    private UserDAO userDAO;
    private SendMail sendMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        et_emailForget = findViewById(R.id.et_emailForget);
        et_newPasswordForget = findViewById(R.id.et_newPasswordForget);
        et_reNewPasswordForget = findViewById(R.id.et_reNewPasswordForget);
        bt_resetPassword = findViewById(R.id.bt_resetPassword);
        bt_sendOTP = findViewById(R.id.bt_sendOTP);
        et_OTP = findViewById(R.id.et_OTP);

        userDAO = new UserDAO(this);

        bt_sendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOTP(et_emailForget.getText().toString().trim());
            }
        });

        bt_resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword(et_newPasswordForget.getText().toString().trim());
            }
        });

    }
    private void sendOTP(String email){
        if(email.equals("")){
            Toast.makeText(this, "Please input email!", Toast.LENGTH_SHORT).show();
        }
        else{
            if(!userDAO.isEmailExists(email)){
                Toast.makeText(this, "Invalid email!", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "Sending email to "+email.trim(), Toast.LENGTH_SHORT).show();
                sendMail = new SendMail();
                sendMail.execute(email.trim());
            }
        }
    }
    private void resetPassword(String newPassword){
        if(newPassword.trim().equals("") && et_reNewPasswordForget.getText().toString().trim().equals("")){
            Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
        }
        else{
            if(newPassword.trim().equals(et_reNewPasswordForget.getText().toString().trim())){
                if(newPassword.length() >=8){
                    if(et_OTP.getText().toString().trim().equals(String.valueOf(SendMail.otp))){
                        user = userDAO.getUserByEmail(et_emailForget.getText().toString().trim());
                        user.setPassword(newPassword.trim());
                        userDAO.updateUser(user);
                        Toast.makeText(this, "Password reset!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else{
                        Toast.makeText(this, "Invalid OTP!", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(this, "Password length must be >=8", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(this, "Password does not match!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}