package me.shouheng.notepal.intro;

import android.os.Bundle;

import me.shouheng.notepal.R;


public class IntroSlide4 extends IntroFragment {

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		background.setBackgroundResource(R.color.intro_color_4);
		title.setText(R.string.intro_4_title);
		image.setImageResource(R.mipmap.ic_launcher);
		description.setText(R.string.intro_4_description);
	}
}