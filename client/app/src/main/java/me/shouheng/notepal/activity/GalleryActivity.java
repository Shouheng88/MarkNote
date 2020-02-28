package me.shouheng.notepal.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import me.shouheng.commons.event.PageName;
import me.shouheng.commons.theme.SystemUiVisibilityUtil;
import me.shouheng.commons.theme.ThemeUtils;
import me.shouheng.commons.utils.LogUtils;
import me.shouheng.commons.utils.ToastUtils;
import me.shouheng.commons.utils.ViewUtils;
import me.shouheng.commons.widget.DepthPageTransformer;
import me.shouheng.commons.widget.HackyViewPager;
import me.shouheng.data.entity.Attachment;
import me.shouheng.notepal.R;
import me.shouheng.notepal.adapter.AttachmentPagerAdapter;
import me.shouheng.notepal.manager.FileManager;
import ooo.oxo.library.widget.PullBackLayout;

import static me.shouheng.commons.event.UMEvent.*;

@PageName(name = PAGE_GALLERY)
public class GalleryActivity extends AppCompatActivity implements PullBackLayout.Callback {

    public final static String EXTRA_GALLERY_IMAGES = "__extra_gallery_images";
    public final static String EXTRA_GALLERY_TITLE = "__extra_gallery_title";
    public final static String EXTRA_GALLERY_CLICKED_IMAGE = "__extra_gallery_clicked_image";

    private ColorDrawable mBackground;
    private HackyViewPager mViewPager;
    private Toolbar toolbar;

    private ArrayList<Attachment> attachments;
    private int clickedImage;
    private String title = "";
    private boolean fullScreenMode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        handleIntent(savedInstanceState);

        configToolbar();

        configViews();
    }

    private void handleIntent(Bundle savedInstanceState) {
        attachments = new ArrayList<>();
        clickedImage = 0;
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            attachments = intent.getParcelableArrayListExtra(EXTRA_GALLERY_IMAGES);
            title = intent.getStringExtra(EXTRA_GALLERY_TITLE);
            clickedImage = intent.getIntExtra(EXTRA_GALLERY_CLICKED_IMAGE, 0);
        } else {
            attachments = savedInstanceState.getParcelableArrayList(EXTRA_GALLERY_IMAGES);
            title = savedInstanceState.getString(EXTRA_GALLERY_TITLE);
            clickedImage = savedInstanceState.getInt(EXTRA_GALLERY_CLICKED_IMAGE, 0);
        }
        LogUtils.d(attachments);
        LogUtils.d(clickedImage);
    }

    private void configToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_toolbar_shade));
            actionBar.setTitle(title);
            actionBar.setSubtitle(clickedImage + 1 + "/" + attachments.size());
        }
    }

    private void configViews() {
        PullBackLayout pullBackLayout = findViewById(R.id.pull_back);
        pullBackLayout.setCallback(this);

        setupSystemUI();

        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(visibility -> {
            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                showSystemUI();
            } else {
                hideSystemUI();
            }
        });

        mViewPager = findViewById(R.id.view_pager);
        AttachmentPagerAdapter adapter = new AttachmentPagerAdapter(getSupportFragmentManager(), attachments);
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(clickedImage);
        mViewPager.setPageTransformer(true, new DepthPageTransformer());
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                toolbar.setSubtitle((position + 1) + "/" + attachments.size());
                invalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });

        Display aa = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        if (aa.getRotation() == Surface.ROTATION_90) {
            Configuration configuration = new Configuration();
            configuration.orientation = Configuration.ORIENTATION_LANDSCAPE;
            onConfigurationChanged(configuration);
        }

        mBackground = new ColorDrawable(Color.BLACK);
        ViewUtils.getRootView(this).setBackgroundDrawable(mBackground);
    }

    public void toggleSystemUI() {
        if (fullScreenMode){
            showSystemUI();
        } else {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        runOnUiThread(() -> {
            toolbar.animate().translationY(- toolbar.getHeight()).setInterpolator(
                    new AccelerateInterpolator()).setDuration(200).start();
            getWindow().getDecorView().setSystemUiVisibility(
                    SystemUiVisibilityUtil.getSystemVisibility());
            fullScreenMode = true;
        });
    }

    private void setupSystemUI() {
        toolbar.animate().translationY(ViewUtils.getStatusBarHeight(getResources())).setInterpolator(
                new DecelerateInterpolator()).setDuration(0).start();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    private void showSystemUI() {
        runOnUiThread(() -> {
            toolbar.animate().translationY(ViewUtils.getStatusBarHeight(getResources())).setInterpolator(
                    new DecelerateInterpolator()).setDuration(240).start();
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            fullScreenMode = false;
        });
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Glide.get(getApplicationContext()).clearMemory();
        Glide.get(getApplicationContext()).trimMemory(TRIM_MEMORY_COMPLETE);
        System.gc();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        ThemeUtils.themeMenu(menu, true);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gallery, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.action_share: {
                Attachment attachment = attachments.get(mViewPager.getCurrentItem());
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType(FileManager.getMimeType(this, attachment.getUri()));
                intent.putExtra(Intent.EXTRA_STREAM, attachment.getUri());
                startActivity(intent);
                break;
            }
            case R.id.action_open: {
                try {
                    Attachment attachment = attachments.get(mViewPager.getCurrentItem());
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setDataAndType(attachment.getUri(), FileManager.getMimeType(this, attachment.getUri()));
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    ToastUtils.makeToast(R.string.text_failed_to_resolve_intent);
                }
                break;
            }
            case R.id.action_info:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void hideOrShowStatusBar() {
        if (fullScreenMode) {
            SystemUiVisibilityUtil.enter(this);
        } else {
            SystemUiVisibilityUtil.exit(this);
        }
        fullScreenMode = !fullScreenMode;
    }

    @Override
    public void onPullStart() {
        fullScreenMode = true;
        hideOrShowStatusBar();
    }

    @Override
    public void onPull(float v) {
        v = Math.min(1f, v * 3f);
        int alpha = (int) (0xff * (1f - v));
        mBackground.setAlpha(alpha);
    }

    @Override
    public void onPullCancel() {}

    @Override
    public void onPullComplete() {
        supportFinishAfterTransition();
    }

    @Override
    public void supportFinishAfterTransition() {
        super.supportFinishAfterTransition();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(EXTRA_GALLERY_IMAGES, attachments);
        outState.putString(EXTRA_GALLERY_TITLE, title);
        outState.putInt(EXTRA_GALLERY_CLICKED_IMAGE, clickedImage);
    }
}
