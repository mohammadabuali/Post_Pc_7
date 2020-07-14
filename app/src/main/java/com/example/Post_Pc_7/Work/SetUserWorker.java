package com.example.Post_Pc_7.Work;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.Post_Pc_7.Data.UserResponse;
import com.example.Post_Pc_7.Server.MyServerInterface;
import com.example.Post_Pc_7.Server.ServerHolder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

import retrofit2.Response;

public class SetUserWorker extends Worker {

    public SetUserWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        MyServerInterface serverInterface = ServerHolder.getInstance().serverInterface;

        String pretty_name = getInputData().getString("key_pretty_name");
        String image_url = getInputData().getString("key_image_url");
        String token = getInputData().getString("key_token");
        try {
            token = "token " + token;
            JsonObject jObj = new JsonObject();
            if (pretty_name != null) {
                jObj.addProperty("pretty_name", pretty_name);
            } else if (image_url != null) {
                jObj.addProperty("image_url", image_url);
            }

            Response<UserResponse> response = serverInterface.updateUser(token, jObj).execute();
            UserResponse user = response.body();
            if (user == null) {
                return Result.failure();
            }
            String userAsJson = new Gson().toJson(user);

            Data outputData = new Data.Builder()
                    .putString("key_output_user", userAsJson)
                    .build();

            return Result.success(outputData);

        } catch (IOException e) {
            e.printStackTrace();
            return Result.retry();
        }
    }
}