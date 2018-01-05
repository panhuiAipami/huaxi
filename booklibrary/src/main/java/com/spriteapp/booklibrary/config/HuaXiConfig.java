package com.spriteapp.booklibrary.config;

import android.content.Context;

import com.spriteapp.booklibrary.listener.ActivityResultListener;
import com.spriteapp.booklibrary.listener.ChannelListener;

/**
 * Created by kuangxiaoguo on 2017/7/25.
 */

public class HuaXiConfig {

    final Context context;
    final int channelId;
    final int clientId;
    final String signSecret;
    final ChannelListener channelListener;
    final ActivityResultListener resultListener;
    final int titleBackground;
    final int titleColor;
    final int rightTitleColor;
    final int bottomBackground;
    final int backImageResource;
    final int statusBarColor;
    final boolean isNightMode;
    int sex;

    private HuaXiConfig(Builder builder) {
        this.channelId = builder.channelId;
        this.clientId = builder.clientId;
        this.signSecret = builder.signSecret;
        this.context = builder.context;
        this.channelListener = builder.channelListener;
        this.titleBackground = builder.titleBackground;
        this.titleColor = builder.titleColor;
        this.rightTitleColor = builder.rightTitleColor;
        this.bottomBackground = builder.bottomBackground;
        this.backImageResource = builder.backImageResource;
        this.statusBarColor = builder.statusBarColor;
        this.isNightMode = builder.isNightMode;
        this.sex = builder.sex;
        this.resultListener = builder.resultListener;
    }

    public static class Builder {
        private Context context;
        private int channelId;
        private int clientId;
        private String signSecret;
        private ChannelListener channelListener;
        private int titleBackground;
        private int titleColor;
        private int rightTitleColor;
        private int bottomBackground;
        private int backImageResource;
        private int statusBarColor;
        private int sex;
        private boolean isNightMode;
        private ActivityResultListener resultListener;

        public Builder setChannelId(int channelId) {
            this.channelId = channelId;
            return this;
        }


        public Builder setSex(int sex) {
            this.sex = sex;
            return this;
        }

        public Builder setClientId(int clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder setSignSecret(String signSecret) {
            this.signSecret = signSecret;
            return this;
        }

        public Builder setContext(Context context) {
            this.context = context;
            return this;
        }

        public Builder setChannelListener(ChannelListener channelListener) {
            this.channelListener = channelListener;
            return this;
        }

        public Builder setTitleBackground(int titleBackground) {
            this.titleBackground = titleBackground;
            return this;
        }

        public Builder setTitleColor(int titleColor) {
            this.titleColor = titleColor;
            return this;
        }

        public Builder setRightTitleColor(int rightTitleColor) {
            this.rightTitleColor = rightTitleColor;
            return this;
        }

        public Builder setBottomBackground(int bottomBackground) {
            this.bottomBackground = bottomBackground;
            return this;
        }

        public Builder setBackImageResource(int backImageResource) {
            this.backImageResource = backImageResource;
            return this;
        }

        public Builder setStatusBarColor(int statusBarColor) {
            this.statusBarColor = statusBarColor;
            return this;
        }

        public Builder setNightMode(boolean nightMode) {
            isNightMode = nightMode;
            return this;
        }

        public Builder setResultListener(ActivityResultListener resultListener) {
            this.resultListener = resultListener;
            return this;
        }

        public HuaXiConfig build() {
            return new HuaXiConfig(this);
        }
    }


    public Context getContext() {
        return context;
    }

    public int getChannelId() {
        return channelId;
    }

    public int getSex() {
        return sex;
    }

    public int getClientId() {
        return clientId;
    }

    public String getSignSecret() {
        return signSecret;
    }

    public ChannelListener getChannelListener() {
        return channelListener;
    }

    public int getTitleBackground() {
        return titleBackground;
    }

    public int getTitleColor() {
        return titleColor;
    }

    public int getRightTitleColor() {
        return rightTitleColor;
    }

    public int getBottomBackground() {
        return bottomBackground;
    }

    public int getBackImageResource() {
        return backImageResource;
    }

    public int getStatusBarColor() {
        return statusBarColor;
    }
}
