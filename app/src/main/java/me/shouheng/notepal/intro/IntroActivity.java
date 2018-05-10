package me.shouheng.notepal.intro;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro2;

import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.util.preferences.PreferencesUtils;

public class IntroActivity extends AppIntro2 {

    public static void launch(Context context) {
        context.startActivity(new Intent(context.getApplicationContext(), IntroActivity.class));
    }

    public static void launchIfNecessary(Context context) {
        if (PreferencesUtils.getInstance(PalmApp.getContext()).isTourActivityShowed()) {
            return;
        }
        launch(context);
    }

    @Override
    public void init(Bundle savedInstanceState) {
        addSlide(new IntroSlide1(), getApplicationContext());
        addSlide(new IntroSlide2(), getApplicationContext());
        addSlide(new IntroSlide3(), getApplicationContext());
        addSlide(new IntroSlide4(), getApplicationContext());
        addSlide(new IntroSlide5(), getApplicationContext());
        addSlide(new IntroSlide6(), getApplicationContext());
    }

    @Override
    public void onDonePressed() {
        PreferencesUtils.getInstance(this).setTourActivityShowed(true);
        finish();
    }

    @Override
    public void onBackPressed() {}
}
