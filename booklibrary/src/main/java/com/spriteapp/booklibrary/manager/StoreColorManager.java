package com.spriteapp.booklibrary.manager;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;

/**
 * Created by kuangxiaoguo on 2017/8/14.
 */

public class StoreColorManager {

    private int containerBackground;
    private int payTextColor;
    private ColorStateList rechargeTextColor;
    private int rechargeTextSelector;
    private int divideViewBackground;
    private int divideLineColor;
    private int verticalMarkColor;
    private int bookTitleColor;
    private int bookAuthorColor;
    private int headerBackground;
    private int allBookColor;
    //原生书城书籍边线颜色
    private int bookLineBackground;
    private int allBookImageResource;

    private StoreColorManager(Builder builder) {
        this.containerBackground = builder.containerBackground;
        this.payTextColor = builder.payTextColor;
        this.rechargeTextColor = builder.rechargeTextColor;
        this.rechargeTextSelector = builder.rechargeTextSelector;
        this.divideViewBackground = builder.divideViewBackground;
        this.divideLineColor = builder.divideLineColor;
        this.verticalMarkColor = builder.verticalMarkColor;
        this.bookTitleColor = builder.bookTitleColor;
        this.bookAuthorColor = builder.bookAuthorColor;
        this.headerBackground = builder.headerBackground;
        this.allBookColor = builder.allBookColor;
        this.bookLineBackground = builder.bookLineBackground;
        this.allBookImageResource = builder.allBookImageResource;
    }

    public static class Builder {
        private int containerBackground;
        private int payTextColor;
        private ColorStateList rechargeTextColor;
        private int rechargeTextSelector;
        private int divideViewBackground;
        private int divideLineColor;
        private int verticalMarkColor;
        private int bookTitleColor;
        private int bookAuthorColor;
        private int headerBackground;
        private int allBookColor;
        private int bookLineBackground;
        private int allBookImageResource;

        public Builder setContainerBackground(int containerBackground) {
            this.containerBackground = containerBackground;
            return this;
        }

        public Builder setPayTextColor(int payTextColor) {
            this.payTextColor = payTextColor;
            return this;
        }

        public Builder setRechargeTextColor(ColorStateList rechargeTextColor) {
            this.rechargeTextColor = rechargeTextColor;
            return this;
        }

        public Builder setRechargeTextSelector(int rechargeTextSelector) {
            this.rechargeTextSelector = rechargeTextSelector;
            return this;
        }

        public Builder setDivideViewBackground(int divideViewBackground) {
            this.divideViewBackground = divideViewBackground;
            return this;
        }

        public Builder setDivideLineColor(int divideLineColor) {
            this.divideLineColor = divideLineColor;
            return this;
        }

        public Builder setVerticalMarkColor(int verticalMarkColor) {
            this.verticalMarkColor = verticalMarkColor;
            return this;
        }

        public Builder setBookTitleColor(int bookTitleColor) {
            this.bookTitleColor = bookTitleColor;
            return this;
        }

        public Builder setBookAuthorColor(int bookAuthorColor) {
            this.bookAuthorColor = bookAuthorColor;
            return this;
        }

        public Builder setHeaderBackground(int headerBackground) {
            this.headerBackground = headerBackground;
            return this;
        }

        public Builder setAllBookColor(int allBookColor) {
            this.allBookColor = allBookColor;
            return this;
        }

        public Builder setBookLineBackground(int bookLineBackground) {
            this.bookLineBackground = bookLineBackground;
            return this;
        }

        public Builder setAllBookImageResource(int allBookImageResource) {
            this.allBookImageResource = allBookImageResource;
            return this;
        }

        public StoreColorManager build() {
            return new StoreColorManager(this);
        }
    }

    public int getContainerBackground() {
        return containerBackground;
    }

    public int getPayTextColor() {
        return payTextColor;
    }

    public ColorStateList getRechargeTextColor() {
        return rechargeTextColor;
    }

    public int getRechargeTextSelector() {
        return rechargeTextSelector;
    }

    public int getDivideViewBackground() {
        return divideViewBackground;
    }

    public int getDivideLineColor() {
        return divideLineColor;
    }

    public int getVerticalMarkColor() {
        return verticalMarkColor;
    }

    public int getBookTitleColor() {
        return bookTitleColor;
    }

    public int getBookAuthorColor() {
        return bookAuthorColor;
    }

    public int getHeaderBackground() {
        return headerBackground;
    }

    public int getAllBookColor() {
        return allBookColor;
    }

    public int getBookLineBackground() {
        return bookLineBackground;
    }

    public int getAllBookImageResource() {
        return allBookImageResource;
    }
}
