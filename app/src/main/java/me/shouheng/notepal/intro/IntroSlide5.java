package me.shouheng.notepal.intro;

import android.os.Bundle;

import me.shouheng.notepal.R;


public class IntroSlide5 extends IntroFragment {

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		background.setBackgroundResource(R.color.intro_color_5);
		title.setText(R.string.intro_5_title);
		image.setImageResource(R.drawable.slide4);
		description.setText(R.string.intro_5_description);
	}
}