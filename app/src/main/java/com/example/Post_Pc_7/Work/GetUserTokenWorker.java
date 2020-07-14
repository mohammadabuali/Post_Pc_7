package com.example.Post_Pc_7.Work;

import android.content.Context;

import com.example.Post_Pc_7.Data.TokenResponse;
import com.example.Post_Pc_7.Server.MyServerInterface;
import com.example.Post_Pc_7.Server.ServerHolder;
import com.google.gson.Gson;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import retrofit2.Response;

public class GetUserTokenWorker extends Worker {
    public GetUserTokenWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        MyServerInterface serverInterface = ServerHolder.getInstance().serverInterface;

        String username = getInputData().getString("key_username");
        try {
            Response<TokenResponse> response = serverInterface.getUserToken(username).execute();
            TokenResponse token = response.body();
            String userAsJson = new Gson().toJson(token);

            Data outputData = new Data.Builder()
                    .putString("key_output_user_token", userAsJson)
                    .build();

            return Result.success(outputData);

        } catch (IOException e) {
            e.printStackTrace();
            return Result.retry();
        }
    }
}