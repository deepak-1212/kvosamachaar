package com.example.ngonotification;

import android.content.Context;
import android.content.Intent;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NotificationAdaptor extends RecyclerView.Adapter<NotificationAdaptor.NotificationHolder> {
    private Context context;
    private ArrayList<NotificationModel> notificationModels;

    public NotificationAdaptor(Context context, ArrayList<NotificationModel> notificationModels){
        this.context = context;
        this.notificationModels = notificationModels;
    }

    @NonNull
    @Override
    public NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_notification, parent, false);

        return new NotificationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationHolder holder, int position) {
        NotificationModel notificationModel = notificationModels.get(position);
        holder.textView.setText(notificationModel.getTitle());
        holder.textView2.setText(notificationModel.getBody());
        holder.textView3.setText(notificationModel.getUrl());
        //holder.textView3.setMovementMethod(LinkMovementMethod.getInstance());
        holder.textView3.setOnClickListener(v -> {
            NotificationModel notificationModel1 = notificationModels.get(position);

            Log.i("TAG", "onBindViewHolder: " + position);

            Intent intent = new Intent(context, WebViewActivity.class);
            intent.putExtra("url", notificationModel1.getUrlSupport());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.getApplicationContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return notificationModels.size();
    }

    public class NotificationHolder extends RecyclerView.ViewHolder {

        private TextView textView, textView2, textView3;

        public NotificationHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.textView);
            textView2 = itemView.findViewById(R.id.textView2);
            textView3 = itemView.findViewById(R.id.textView3);
        }
    }
}
