package me.shouheng.notepal.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import me.shouheng.commons.event.PageName;
import me.shouheng.commons.event.UMEvent;
import me.shouheng.commons.utils.ViewUtils;
import me.shouheng.data.entity.Attachment;
import me.shouheng.notepal.Constants;
import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.GalleryActivity;
import me.shouheng.notepal.manager.FileManager;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * The image fragment to display the image, video preview image etc.
 *
 * Created by WngShhng (shouheng2015@gmail.com) on 2017/4/9.
 */
@PageName(name = UMEvent.PAGE_IMAGE)
public class ImageFragment extends Fragment {

    public final static String ARG_ATTACHMENT = "__args_key_attachment";

    private final static String STATE_SAVE_KEY_ATTACHMENT = "__state_save_key_attachment";

    private Attachment attachment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(ARG_ATTACHMENT)){
            attachment = (Attachment) getArguments().get(ARG_ATTACHMENT);
        }
        if (savedInstanceState != null){
            attachment = savedInstanceState.getParcelable(STATE_SAVE_KEY_ATTACHMENT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (attachment != null && Constants.MIME_TYPE_VIDEO.equals(attachment.getMineType())){
            RelativeLayout layout = new RelativeLayout(getContext());
            ImageView imageView = new ImageView(getContext());
            imageView.setLayoutParams(new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            layout.addView(imageView);
            ImageView videoTip = new ImageView(getContext());
            videoTip.setImageResource(R.drawable.ic_play_circle_outline_white_24dp);
            int dp50 = ViewUtils.dp2Px(PalmApp.getContext(), 50);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(dp50, dp50);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            videoTip.setLayoutParams(params);
            layout.addView(videoTip);
            displayMedia(imageView);
            return layout;
        }

        PhotoView photoView = new PhotoView(getContext());
        if (attachment != null && "gif".endsWith(attachment.getUri().getPath())) {
            Glide.with(getActivity()).load(attachment.getUri().getPath()).into(photoView);
        } else {
            displayMedia(photoView);
        }
        photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                Activity activity = getActivity();
                if (activity instanceof GalleryActivity) {
                    ((GalleryActivity) activity).toggleSystemUI();
                }
            }

            @Override
            public void onOutsidePhotoTap() {
                Activity activity = getActivity();
                if (activity instanceof GalleryActivity) {
                    ((GalleryActivity) activity).toggleSystemUI();
                }
            }
        });
        photoView.setMaximumScale(5.0F);
        photoView.setMediumScale(3.0F);
        return photoView;
    }

    private void displayMedia(PhotoView photoView) {
        Glide.with(getContext())
                .load(FileManager.getThumbnailUri(getContext(), attachment.getUri()))
                .thumbnail(0.5f)
                .transition(withCrossFade())
                .into(photoView);
        photoView.setOnClickListener(v -> {
            if (attachment != null && Constants.MIME_TYPE_VIDEO.equals(attachment.getMineType())){
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(attachment.getUri(), FileManager.getMimeType(getContext(), attachment.getUri()));
                startActivity(intent);
            }
        });
    }

    /**
     * Note that you shouldn't set diskCacheStrategy of Glide, and you should use ImageView instead of PhotoView
     *
     * @param imageView view to show
     */
    private void displayMedia(ImageView imageView){
        Glide.with(getContext())
                .load(FileManager.getThumbnailUri(getContext(), attachment.getUri()))
                .transition(withCrossFade())
                .into(imageView);
        imageView.setOnClickListener(v -> {
            if (attachment != null && Constants.MIME_TYPE_VIDEO.equals(attachment.getMineType())){
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(attachment.getUri(), FileManager.getMimeType(getContext(), attachment.getUri()));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(STATE_SAVE_KEY_ATTACHMENT, attachment);
        super.onSaveInstanceState(outState);
    }
}
