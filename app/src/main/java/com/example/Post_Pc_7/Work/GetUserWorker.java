package com.example.Post_Pc_7.Work;

import android.content.Context;

import com.google.gson.Gson;
import com.example.Post_Pc_7.Data.UserResponse;
import com.example.Post_Pc_7.Server.MyServerInterface;
import com.example.Post_Pc_7.Server.ServerHolder;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import retrofit2.Response;

public class GetUserWorker extends Worker {
    public GetUserWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        MyServerInterface serverInterface = ServerHolder.getInstance().serverInterface;

        String userToken = "token "+ getInputData().getString("key_token");
        try {
            Response<UserResponse> response = serverInterface.getUser(userToken).execute();
            UserResponse user = response.body();
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