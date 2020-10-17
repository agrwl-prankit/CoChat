package com.prankit.cochat.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prankit.cochat.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter {

    public static class ChatViewHolder extends RecyclerView.ViewHolder{
        TextView userName, userStatus;
        CircleImageView profileImage;
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userProfileNameView);
            userStatus = itemView.findViewById(R.id.userStatusView);
            profileImage = itemView.findViewById(R.id.userProfileImageView);
        }
    }
}
