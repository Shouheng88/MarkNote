package me.shouheng.notepal.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import me.shouheng.commons.utils.ViewUtils;
import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.GalleryActivity;
import me.shouheng.notepal.config.Constants;
import me.shouheng.data.entity.Attachment;
import me.shouheng.notepal.util.FileHelper;
import me.shouheng.notepal.widget.tools.RotateTransformation;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by wangshouheng on 2017/4/9.*/
public class ImageFragment extends Fragment {

    public final static String ARG_ATTACHMENT = "__args_key_attachment";

    private Attachment attachment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(ARG_ATTACHMENT)){
            attachment = (Attachment) getArguments().get(ARG_ATTACHMENT);
        }
        if (savedInstanceState != null){
            attachment = savedInstanceState.getParcelable("attachment");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (attachment != null && Constants.MIME_TYPE_VIDEO.equals(attachment.getMineType())){
            RelativeLayout layout = new RelativeLayout(getContext());
            ImageView imageView = new ImageView(getContext());
            imageView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            layout.addView(imageView);
            ImageView videoTip = new ImageView(getContext());
            videoTip.setImageResource(R.drawable.ic_play_circle_outline_white_24dp);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewUtils.dp2Px(getContext(), 50), ViewUtils.dp2Px(getContext(), 50));
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
                ((GalleryActivity) getActivity()).toggleSystemUI();
            }

            @Override
            public void onOutsidePhotoTap() {
                ((GalleryActivity) getActivity()).toggleSystemUI();
            }
        });
        photoView.setMaximumScale(5.0F);
        photoView.setMediumScale(3.0F);
        return photoView;
    }

    private void displayMedia(PhotoView photoView) {
        Glide.with(getContext())
                .load(FileHelper.getThumbnailUri(getContext(), attachment.getUri()))
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .thumbnail(0.5f)
                .transform(new RotateTransformation(getContext(), 0, false))
                .animate(R.anim.fade_in_support)
                .into(photoView);
        photoView.setOnClickListener(v -> {
            if (attachment != null && Constants.MIME_TYPE_VIDEO.equals(attachment.getMineType())){
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(attachment.getUri(), FileHelper.getMimeType(getContext(), attachment.getUri()));
                startActivity(intent);
            }
        });
    }

    /**
     * Note that you shouldn't set diskCacheStrategy of Glide, and you should use ImageView instead of PhotoView
     * @param imageView view to show*/
    private void displayMedia(ImageView imageView){
        Glide.with(getContext())
                .load(FileHelper.getThumbnailUri(getContext(), attachment.getUri()))
                .asBitmap()
                .animate(R.anim.fade_in_support)
                .into(imageView);
        imageView.setOnClickListener(v -> {
            if (attachment != null && Constants.MIME_TYPE_VIDEO.equals(attachment.getMineType())){
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(attachment.getUri(), FileHelper.getMimeType(getContext(), attachment.getUri()));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("attachment", attachment);
        super.onSaveInstanceState(outState);
    }
}
