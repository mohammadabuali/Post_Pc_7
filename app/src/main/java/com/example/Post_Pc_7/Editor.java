package com.example.Post_Pc_7;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.Post_Pc_7.Data.User;
import com.example.Post_Pc_7.Data.UserResponse;
import com.example.Post_Pc_7.Work.SetUserWorker;
import com.google.gson.Gson;

import java.util.UUID;

public class Editor extends AppCompatActivity {
    private static final String URL = "https://hujipostpc2019.pythonanywhere.com/";
    private static String IMG_CRAB = "/images/crab.png";
    private static String IMG_ALIEN = "/images/alien.png";
    private static String IMG_OCTOPUS = "/images/octopus.png";
    private static String IMG_UNICORN = "/images/unicorn.png";
    private static String IMG_FROG = "/images/frog.png";
    private static String IMG_ROBOT = "/images/robot.png";
    private final static String TOKEN = "key_token";
    private TextView welcomeText;
    private ImageView userImage;
    private ImageView userImage2;
    private Button edit;
    private EditText prettyName;
    private EditText userName;
    private String currImg;
    private String savedToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        userImage = findViewById(R.id.user_image);
        userImage2 = findViewById(R.id.user_image2);
        welcomeText = findViewById(R.id.welcome);
        prettyName = findViewById(R.id.pretty_name);
        userName = findViewById(R.id.username);
        edit = findViewById(R.id.edit_btn);
        Intent creator = getIntent();
        currImg = creator.getStringExtra("userImg");
        savedToken = creator.getStringExtra("getToken");
        toggleImageLayout();
        edit.setOnClickListener(v -> {
            if(prettyName.getText().toString().isEmpty()){
                Toast.makeText(getApplicationContext(), "Please enter a pretty name", Toast.LENGTH_SHORT).show();
            }
            else {
                Intent intentBack = new Intent();
                updateUser(prettyName.getText().toString());
                updateImageUrl(currImg);
                setResult(RESULT_OK, intentBack);
                finish();
            }
        });

    }

    private void updateUser(String prettyName) {
        UUID workTagUniqueId = UUID.randomUUID();
        OneTimeWorkRequest updateUserWork = new OneTimeWorkRequest.Builder(SetUserWorker.class)
                .setConstraints(new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .setInputData(new Data.Builder()
                        .putString("key_token", savedToken)
                        .putString("key_image_url", null)
                        .putString("key_pretty_name", prettyName).build())
                .addTag(workTagUniqueId.toString())
                .build();

        WorkManager.getInstance(getApplicationContext()).enqueue(updateUserWork);

        WorkManager.getInstance(getApplicationContext()).getWorkInfosByTagLiveData(workTagUniqueId.toString()).observe(this, workInfos -> {
            if (workInfos == null || workInfos.isEmpty())
                return;

            WorkInfo info = workInfos.get(0);
            if (info.getState() == WorkInfo.State.FAILED) {
                Toast.makeText(getApplicationContext(), "Failed to update user", Toast.LENGTH_SHORT).show();
                return;
            }
            String userResponseAsJson = info.getOutputData().getString("key_output_user");
            if (userResponseAsJson == null || userResponseAsJson.equals("")) {
                return;
            }
            Log.d("ex7Tag", "got user: " + userResponseAsJson);

            UserResponse userResponse = new Gson().fromJson(userResponseAsJson, UserResponse.class);
            if (userResponse == null || userResponse.data == null) {
                return;
            }
            User user = userResponse.data;

            Log.d("ex7Tag", "got user: " + user);
            toggleImageLayout();

        });

    }

    private void toggleImageLayout() {
        Glide.with(this).load(URL + IMG_ALIEN).into((ImageView) findViewById(R.id.image_alien2));
        Glide.with(this).load(URL + IMG_CRAB).into((ImageView) findViewById(R.id.image_crab2));
        Glide.with(this).load(URL + IMG_FROG).into((ImageView) findViewById(R.id.image_frog2));
        Glide.with(this).load(URL + IMG_OCTOPUS).into((ImageView) findViewById(R.id.image_octopus2));
        Glide.with(this).load(URL + IMG_ROBOT).into((ImageView) findViewById(R.id.image_robot2));
        Glide.with(this).load(URL + IMG_UNICORN).into((ImageView) findViewById(R.id.image_unicorn2));
        Glide.with(this).load(currImg).into((ImageView) findViewById(R.id.user_image2));

        findViewById(R.id.image_alien2).setOnClickListener(view -> {
            Glide.with(this).load(URL + IMG_ALIEN).into((ImageView) findViewById(R.id.user_image2));
            currImg = IMG_ALIEN;
        });
        findViewById(R.id.image_crab2).setOnClickListener(view -> {
            Glide.with(this).load(URL + IMG_CRAB).into((ImageView) findViewById(R.id.user_image2));
            currImg = IMG_CRAB;
        });
        findViewById(R.id.image_frog2).setOnClickListener(view -> {
            Glide.with(this).load(URL + IMG_FROG).into((ImageView) findViewById(R.id.user_image2));
            currImg = IMG_FROG;

        });
        findViewById(R.id.image_unicorn2).setOnClickListener(view -> {
            Glide.with(this).load(URL + IMG_UNICORN).into((ImageView) findViewById(R.id.user_image2));
            currImg = IMG_UNICORN;
        });

        findViewById(R.id.image_robot2).setOnClickListener(view -> {

            Glide.with(this).load(URL + IMG_ROBOT).into((ImageView) findViewById(R.id.user_image2));
            currImg = IMG_ROBOT;

        });
        findViewById(R.id.image_octopus2).setOnClickListener(view -> {

            Glide.with(this).load(URL + IMG_OCTOPUS).into((ImageView) findViewById(R.id.user_image2));
            currImg = IMG_OCTOPUS;
        });
    }
    private void updateImageUrl(String url) {
        UUID workTagUniqueId = UUID.randomUUID();
        final OneTimeWorkRequest setUserImageUrl = new OneTimeWorkRequest.Builder(SetUserWorker.class)
                .setConstraints(new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .setInputData(new Data.Builder().putString("key_token", savedToken)
                        .putString("key_image_url", url)
                        .build())
                .addTag(workTagUniqueId.toString())
                .build();

        WorkManager.getInstance().enqueue(setUserImageUrl);

        WorkManager.getInstance().getWorkInfosByTagLiveData(workTagUniqueId.toString()).observe(this, workInfos -> {

            if (workInfos == null || workInfos.isEmpty())
                return;
            WorkInfo info = workInfos.get(0);
            if (info.getState() == WorkInfo.State.FAILED) {
                Toast.makeText(getApplicationContext(), "Failed updating user", Toast.LENGTH_SHORT).show();
                return;
            }
            String userResponseAsJson = info.getOutputData().getString("key_output_user");
            if (userResponseAsJson == null || userResponseAsJson.equals("")) {
                return;
            }
            Log.d("ex7Tag", "got user: " + userResponseAsJson);

            UserResponse userResponse = new Gson().fromJson(userResponseAsJson, UserResponse.class);
            if (userResponse == null || userResponse.data == null) {
                return;
            }
            User user = userResponse.data;

            Log.d("ex7Tag", "got user: " + user);
            toggleImageLayout();

        });
    }
}

