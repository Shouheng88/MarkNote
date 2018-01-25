package me.shouheng.notepal.intro;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import me.shouheng.notepal.R;
import me.shouheng.notepal.config.Constants;


public class IntroSlide6 extends IntroFragment {

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		background.setBackgroundResource(R.color.intro_color_1);
		title.setText(R.string.intro_6_title);
		image.setVisibility(View.GONE);
		imageSmall.setImageResource(R.drawable.ic_google_plus);
		imageSmall.setVisibility(View.VISIBLE);
		imageSmall.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(Constants.GOOGLE_PLUS_URL));
            startActivity(intent);
        });
		description.setText(R.string.intro_6_description);
	}
}