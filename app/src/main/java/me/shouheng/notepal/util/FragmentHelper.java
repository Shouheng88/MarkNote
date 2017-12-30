package me.shouheng.notepal.util;

import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by wang shouheng on 2017/12/23.*/
public class FragmentHelper {

    public static void replace(AppCompatActivity activity, Fragment fragment, @IdRes int containerId) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(containerId, fragment).commit();
    }

    public static void replace(AppCompatActivity activity, android.app.Fragment fragment, @IdRes int containerId) {
        android.app.FragmentManager fragmentManager = activity.getFragmentManager();
        android.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(containerId, fragment).commit();
    }

    public static void replaceWithCallback(AppCompatActivity activity, Fragment fragment, @IdRes int containerId) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(containerId, fragment).commit();
    }
}
