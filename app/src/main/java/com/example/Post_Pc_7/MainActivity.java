package com.example.Post_Pc_7;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Post_Pc_7.Data.TokenResponse;
import com.example.Post_Pc_7.Data.User;
import com.example.Post_Pc_7.Data.UserResponse;
import com.example.Post_Pc_7.Work.GetUserTokenWorker;
import com.example.Post_Pc_7.Work.GetUserWorker;
import com.google.gson.Gson;
import com.bumptech.glide.Glide;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final String URL = "https://hujipostpc2019.pythonanywhere.com/";
    private static String WELCOME_MSG = "Welcome\n %s!";
    private final static String TOKEN = "key_token";
    private String imgUrl;
    private TextView welcomeText;
    private Button edit_account;
    private ImageView userImage;
    private String savedToken;
    private Button connect;
    private EditText usernameInput;
    private String userLogin;
    private SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameInput = findViewById(R.id.username);
        connect = findViewById(R.id.connect);
        edit_account = findViewById(R.id.edit_profile);
        edit_account.setVisibility(View.INVISIBLE);
        welcomeText = findViewById(R.id.welcome);
        welcomeText.setVisibility(View.INVISIBLE);
        userImage = findViewById(R.id.user_image);
        userImage.setVisibility(View.INVISIBLE);

        requestPermission();
        sp = getSharedPreferences("app", Context.MODE_PRIVATE);

        savedToken = sp.getString(TOKEN, null);


        connect.setOnClickListener(v -> {
            userLogin = usernameInput.getText().toString();
            if (userLogin.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please enter a valid username", Toast.LENGTH_SHORT).show();
            } else {
                getUserToken(usernameInput.getText().toString());
            }
        });
        edit_account.setOnClickListener(v -> {
            Intent intent = new Intent(this, Editor.class);
            intent.putExtra("getToken", savedToken);
            intent.putExtra("userImg", imgUrl);
            startActivityForResult(intent, 12);
        });
    }

    private void getUserToken(String username) {
        UUID workTagUniqueId = UUID.randomUUID();
        OneTimeWorkRequest checkConnectivityWork = new OneTimeWorkRequest.Builder(GetUserTokenWorker.class)
                .setConstraints(new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .setInputData(new Data.Builder().putString("key_username", username).build())
                .addTag(workTagUniqueId.toString())
                .build();

        WorkManager.getInstance(getApplicationContext()).enqueue(checkConnectivityWork);

        WorkManager.getInstance(getApplicationContext()).getWorkInfosByTagLiveData(workTagUniqueId.toString()).observe(this, workInfos -> {
            if (workInfos == null || workInfos.isEmpty())
                return;

            WorkInfo info = workInfos.get(0);
            String tokenAsJson = info.getOutputData().getString("key_output_user_token");
            if (tokenAsJson == null || tokenAsJson.equals("")) {
                return;
            }

            TokenResponse token = new Gson().fromJson(tokenAsJson, TokenResponse.class);
            if (token.data == null || token.data.equals("")) {
                return;
            }
            savedToken = token.data;
            sp.edit().putString(TOKEN, savedToken).apply();
            getUserInfo();
        });
    }

    private void getUserInfo() {
        usernameInput.setVisibility(View.INVISIBLE);
        connect.setVisibility(View.INVISIBLE);
        getUser();
    }

    private void getUser() {
        UUID workTagUniqueId = UUID.randomUUID();
        OneTimeWorkRequest getUserWork = new OneTimeWorkRequest.Builder(GetUserWorker.class)
                .setConstraints(new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .setInputData(new Data.Builder().putString("key_token", savedToken).build())
                .addTag(workTagUniqueId.toString())
                .build();

        WorkManager.getInstance(getApplicationContext()).enqueue(getUserWork);

        WorkManager.getInstance(getApplicationContext()).getWorkInfosByTagLiveData(workTagUniqueId.toString()).observe(this, workInfos -> {
            if (workInfos == null || workInfos.isEmpty())
                return;

            WorkInfo info = workInfos.get(0);
            String userResponseAsJson = info.getOutputData().getString("key_output_user");
            if (userResponseAsJson == null || userResponseAsJson.equals("")) {
                return;
            }

            UserResponse userResponse = new Gson().fromJson(userResponseAsJson, UserResponse.class);
            if (userResponse == null || userResponse.data == null) {
                return;
            }
            User user = userResponse.data;
            showUserInfo(user);
        });
    }


    private void showUserInfo(User user) {
        String name = user.pretty_name == null || user.pretty_name.equals("") ?
                user.username : user.pretty_name;
        if (name != null)
            welcomeText.setText(String.format(WELCOME_MSG, name));
        else
            welcomeText.setText("");
        welcomeText.setVisibility(View.VISIBLE);
        edit_account.setVisibility(View.VISIBLE);
        imgUrl = URL + user.image_url;
        Glide.with(this).load(URL + user.image_url).into(userImage);
        userImage.setVisibility(View.VISIBLE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 12) {
            if (resultCode == RESULT_OK) {
                getUserToken(userLogin);
            }
        }
    }

    private void requestPermission() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 1);
        }
    }

}
