package com.andrognito.pinlockview;

import android.graphics.drawable.Drawable;

/**
 * The customization options for the buttons in {@link PinLockView}
 * passed to the {@link PinLockAdapter} to decorate the individual views
 *
 * Created by aritraroy on 01/06/16.
 */
public class CustomizationOptionsBundle {

    private int textColor;
    private int textSize;
    private int buttonSize;
    private Drawable buttonBackgroundDrawable;
    private Drawable deleteButtonDrawable;
    private int deleteButtonSize;
    private boolean showDeleteButton;
    private int deleteButtonPressesColor;
    private Drawable fingerButtonDrawable;
    private int fingerButtonSize;
    private boolean showFingerButton;
    private int fingerButtonPressesColor;

    public CustomizationOptionsBundle() {
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public int getButtonSize() {
        return buttonSize;
    }

    public void setButtonSize(int buttonSize) {
        this.buttonSize = buttonSize;
    }

    public Drawable getButtonBackgroundDrawable() {
        return buttonBackgroundDrawable;
    }

    public void setButtonBackgroundDrawable(Drawable buttonBackgroundDrawable) {
        this.buttonBackgroundDrawable = buttonBackgroundDrawable;
    }

    public Drawable getDeleteButtonDrawable() {
        return deleteButtonDrawable;
    }

    public void setDeleteButtonDrawable(Drawable deleteButtonDrawable) {
        this.deleteButtonDrawable = deleteButtonDrawable;
    }

    public int getDeleteButtonSize() {
        return deleteButtonSize;
    }

    public void setDeleteButtonSize(int deleteButtonSize) {
        this.deleteButtonSize = deleteButtonSize;
    }

    public boolean isShowDeleteButton() {
        return showDeleteButton;
    }

    public void setShowDeleteButton(boolean showDeleteButton) {
        this.showDeleteButton = showDeleteButton;
    }

    public int getDeleteButtonPressesColor() {
        return deleteButtonPressesColor;
    }

    public void setDeleteButtonPressesColor(int deleteButtonPressesColor) {
        this.deleteButtonPressesColor = deleteButtonPressesColor;
    }

    public Drawable getFingerButtonDrawable() {
        return fingerButtonDrawable;
    }

    public void setFingerButtonDrawable(Drawable fingerButtonDrawable) {
        this.fingerButtonDrawable = fingerButtonDrawable;
    }

    public int getFingerButtonSize() {
        return fingerButtonSize;
    }

    public void setFingerButtonSize(int fingerButtonSize) {
        this.fingerButtonSize = fingerButtonSize;
    }

    public boolean isShowFingerButton() {
        return showFingerButton;
    }

    public void setShowFingerButton(boolean showFingerButton) {
        this.showFingerButton = showFingerButton;
    }

    public int getFingerButtonPressesColor() {
        return fingerButtonPressesColor;
    }

    public void setFingerButtonPressesColor(int fingerButtonPressesColor) {
        this.fingerButtonPressesColor = fingerButtonPressesColor;
    }
}
