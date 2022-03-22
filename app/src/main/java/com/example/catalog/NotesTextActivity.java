package com.example.catalog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Base64;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;


import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class NotesTextActivity extends AppCompatActivity {
    private static final String FILENAME = "my_notes.txt";
    private String key = "";
    private static final int MIN_PWD_LENGTH = 8;

    // Encryption Parameters
    public static final int PBE_ITERATION_COUNT = 500;
    private static final String PBE_ALGORITHM = "PBEWithSHA256and256BitAES-CBC-BC";
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    byte[] initVector = "1a2b3c4d5e6f7g8h".getBytes();
    byte[] salt = "hide/password".getBytes();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_text);

        // watch password text box and dim button until password entered
        EditText pwdTextBox = findViewById(R.id.txt_pwd);
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                dimButtonsUnlessPwdEntered();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        pwdTextBox.addTextChangedListener(watcher); // makes the buttons non accessible until text and password is entered correctly
    }

    private void dimButtonsUnlessPwdEntered() {
        int pwdLength = ((EditText)findViewById(R.id.txt_pwd)).getText().length();
        Boolean pwdEntered = (pwdLength >= MIN_PWD_LENGTH);
        (findViewById(R.id.btn_write)).setEnabled(pwdEntered);
        (findViewById(R.id.btn_read)).setEnabled(pwdEntered);
    }

    public void writePrivateFile(View view) {
        writeFile();
    }

    // Return true if password has been entered; get password; clear input
    private void writeFile() {
        if(haveKey()){
            try{
                OutputStream os = this.openFileOutput(FILENAME, Context.MODE_PRIVATE);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(os);

                EditText textNotes = this.findViewById(R.id.txt_log);
                String cipherText = encrypt(textNotes.getText().toString(), key);

                outputStreamWriter.write(cipherText);
                outputStreamWriter.close();
                Toast.makeText(this.getBaseContext(), "Note encrypted to file",
                        Toast.LENGTH_SHORT).show();

                // Hide the text now that it's been saved
                clearMessageText();
            }catch (IOException e){
                Toast.makeText(this.getBaseContext(), "Unable to write note to file",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    private boolean haveKey() {
        // Read the password
        EditText pwdTextBox = findViewById(R.id.txt_pwd);
        Boolean keyWasEntered = (pwdTextBox.getText().toString().length() >= MIN_PWD_LENGTH);

        if(keyWasEntered){
            // Get the password and clear it from the text box
            key = pwdTextBox.getText().toString(); // * is dependent on write file and is processed 1st *
            pwdTextBox.setText("");
        }else{
            // Warn the user to enter a long password
            Toast.makeText(getBaseContext(), "Password must be at least" + MIN_PWD_LENGTH +
                    " character long.", Toast.LENGTH_LONG).show();
        }

        return keyWasEntered;
    }

    private String encrypt(String toString, String password) {
        byte[] encryptedText;
        String result = "";

        try {
            // Get key based on user-provided password
            PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray(),
                    salt, PBE_ITERATION_COUNT, 256);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(PBE_ALGORITHM);
            SecretKey tmp = keyFactory.generateSecret(keySpec);

            // Encrypt
            SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
            Cipher encryptionCipher = Cipher.getInstance(CIPHER_ALGORITHM);
            IvParameterSpec ivspec = new IvParameterSpec(initVector);
            encryptionCipher.init(Cipher.ENCRYPT_MODE, secret, ivspec);
            encryptedText = encryptionCipher.doFinal(toString.getBytes());

            // Encode encrypted bytes to Base64 text to save in text file
            result = Base64.encodeToString(encryptedText, Base64.DEFAULT);

        }catch (Exception e){
            e.printStackTrace();

        }

        return result;
    }

    private void clearMessageText() {
        // created the variable textNotes and both notes and password will disappear
        EditText textNotes = this.findViewById(R.id.txt_log);
        textNotes.setText("");
    }

    public void readPrivateFile(View view) {
        readFile();
    }

    // Use the user-provided password to decrypt the message from file
    private void readFile() {
        if(haveKey()){
            try{
                InputStream inputStream = this.openFileInput(FILENAME);
                if (inputStream != null){
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String receiveString;
                    StringBuilder stringBuilder = new StringBuilder();
                    while ((receiveString = bufferedReader.readLine()) !=null ){
                        stringBuilder.append(receiveString);
                    }
                    inputStream.close();
                    String encryptedNotes = stringBuilder.toString();
                    String cipherText = decrypt(encryptedNotes, key);
                    EditText textNotes = (EditText) this.findViewById(R.id.txt_log);
                    textNotes.setText(cipherText);
                }
            }catch (FileNotFoundException e){
                Toast.makeText(this.getBaseContext(),
                        "No previous note file found", Toast.LENGTH_SHORT).show();
            }catch (IOException e){
                Toast.makeText(this.getBaseContext(),
                        "Error reading from previous note file", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Password based encryption
    // Decode the provided Base64 string, then decrypt based on the provided password
    // Return decrypted cleartext string
    private String decrypt(String encryptedNotes, String password) {
        String result = "";

        try {
            // Decode Base64 text back to encrypted bytes
            byte[] toDecrypt = Base64.decode(encryptedNotes, Base64.DEFAULT);

            // Get key based on user provided password
            PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray(),
                    salt, PBE_ITERATION_COUNT, 256);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(PBE_ALGORITHM);
            SecretKey tmp = keyFactory.generateSecret(keySpec);

            // Decrypt
            SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
            Cipher decryptionCipher = Cipher.getInstance(CIPHER_ALGORITHM);
            IvParameterSpec ivspec = new IvParameterSpec(initVector);
            decryptionCipher.init(Cipher.DECRYPT_MODE, secret, ivspec);
            byte[] decryptedText = decryptionCipher.doFinal(toDecrypt);
            result = new String(decryptedText);

        }catch (Exception e){
            Toast.makeText(this.getBaseContext(),
                    "Cannot decrypt", Toast.LENGTH_SHORT).show();
            e.printStackTrace(); // shows Logcat
        }

        return result;
    }
}