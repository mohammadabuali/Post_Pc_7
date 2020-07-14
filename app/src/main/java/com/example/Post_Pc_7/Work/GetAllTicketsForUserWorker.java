package com.example.Post_Pc_7.Work;

import android.content.Context;


import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class GetAllTicketsForUserWorker extends Worker {
    public GetAllTicketsForUserWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        return Result.retry();
    }
}
