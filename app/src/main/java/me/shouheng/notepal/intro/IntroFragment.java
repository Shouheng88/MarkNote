package me.shouheng.notepal.intro;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import me.shouheng.notepal.R;


public class IntroFragment extends Fragment {

	protected View background;
	protected TextView title;
	protected ImageView image;
	protected ImageView imageSmall;
	protected TextView description;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_intro_slide, container, false);
		background = root.findViewById(R.id.intro_background);
		title = (TextView) root.findViewById(R.id.intro_title);
		image = (ImageView) root.findViewById(R.id.intro_image);
		imageSmall = (ImageView) root.findViewById(R.id.intro_image_small);
		description = (TextView) root.findViewById(R.id.intro_description);
		return root;
	}
}