package hcmute.edu.vn.mp3app;

import android.os.AsyncTask;
import android.util.Log;

import java.util.Properties;
import java.util.Random;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMail extends AsyncTask<String, Void, Void> {

    private static final String TAG = "SendMailTask";
    private static final String EMAIL_USERNAME = "bum.le.2k2@gmail.com";
    private static final String EMAIL_PASSWORD = "unnextvxrvlrzdvx";
    private static final String EMAIL_HOST = "smtp.gmail.com";
    private static final int EMAIL_PORT = 587;
    public static int otp;

    @Override
    public Void doInBackground(String... params) {
        String receiverMail = params[0];

        try {
            // Set up mail server properties
            Properties props = new Properties();
            props.put("mail.smtp.host", EMAIL_HOST);
            props.put("mail.smtp.port", String.valueOf(EMAIL_PORT));
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            // Create a session with authentication
            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                    return new javax.mail.PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
                }
            });

            // Create a new message
            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(EMAIL_USERNAME));
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(receiverMail));
            // Set Subject: header field
            mimeMessage.setSubject("Forget Password", "UTF-8");

            // Now set the actual message
            Random random = new Random();
            otp = random.nextInt(900000) + 100000; // Generates a random number between 100000 and 999999

            // Set the OTP in the message text
            String otpText = "Your OTP code to get password is: " + otp;
            mimeMessage.setText(otpText);

            // Send the message
            Transport.send(mimeMessage);
            Log.i(TAG, "Email sent successfully");
        } catch (MessagingException e) {
            Log.e(TAG, "Error sending email", e);
        }

        return null;
    }
}
