package me.shouheng.notepal.activity;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.balysv.materialmenu.MaterialMenuDrawable;
import com.larswerkman.holocolorpicker.ColorPicker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import me.shouheng.commons.activity.CommonActivity;
import me.shouheng.commons.utils.LogUtils;
import me.shouheng.notepal.R;
import me.shouheng.notepal.databinding.ActivitySketchBinding;
import me.shouheng.commons.utils.ToastUtils;
import me.shouheng.commons.utils.ViewUtils;
import me.shouheng.notepal.widget.SketchView;
import me.shouheng.notepal.widget.tools.OnDrawChangedListener;


public class SketchActivity extends CommonActivity<ActivitySketchBinding> implements
        OnDrawChangedListener,
        View.OnClickListener {

    public final static String BASED_BITMAP = "base";

    private View popupLayout, popupEraserLayout;
    private ImageView strokeImageView, eraserImageView;
    private ColorPicker mColorPicker;
    private MaterialMenuDrawable materialMenu;
    private Dialog eraserDialog, brushDialog;

    private int seekBarStrokeProgress;
    private int seekBarEraserProgress;
    private int oldColor, size;

    private boolean isContentChanged, onceSaved;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_sketch;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        configToolbar();

        configBackground();

        configViews();

        configDialogs();
    }

    private void configToolbar() {
        setSupportActionBar(getBinding().toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("");
        }
        materialMenu = new MaterialMenuDrawable(this, primaryColor(), MaterialMenuDrawable.Stroke.THIN);
        materialMenu.setIconState(MaterialMenuDrawable.IconState.ARROW);
        getBinding().toolbar.setNavigationIcon(materialMenu);
        setStatusBarColor(getResources().getColor(R.color.dark_theme_foreground));
    }

    private void configBackground() {
        Uri baseUri = getIntent().getParcelableExtra(BASED_BITMAP);
        if (baseUri != null) {
            Bitmap bmp;
            try {
                bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(baseUri));
                getBinding().sketchView.setBackgroundBitmap(this, bmp);
            } catch (FileNotFoundException e) {
                LogUtils.e("Error replacing sketch bitmap background", e);
            }
        }
    }

    private void configViews() {
        getBinding().ivBrush.setOnClickListener(this);
        getBinding().ivUndo.setOnClickListener(this);
        getBinding().ivRedo.setOnClickListener(this);
        ViewUtils.setAlpha(getBinding().ivEraser, 0.4f);
        getBinding().ivEraser.setOnClickListener(this);
        getBinding().sketchView.setOnDrawChangedListener(this);
    }

    private void configDialogs() {
        // stroke dialog
        popupLayout = getLayoutInflater().inflate(R.layout.popup_sketch_stroke, null);
        strokeImageView = popupLayout.findViewById(R.id.stroke_circle);
        mColorPicker = popupLayout.findViewById(R.id.stroke_color_picker);
        mColorPicker.addSVBar(popupLayout.findViewById(R.id.svbar));
        mColorPicker.addOpacityBar(popupLayout.findViewById(R.id.opacitybar));
        mColorPicker.setOnColorChangedListener(color -> getBinding().sketchView.setStrokeColor(color));
        mColorPicker.setColor(getBinding().sketchView.getStrokeColor());
        mColorPicker.setOldCenterColor(getBinding().sketchView.getStrokeColor());

        // brush dialog
        brushDialog = new AlertDialog.Builder(this)
                .setView(popupLayout)
                .setOnDismissListener(dialog -> {
                    if (mColorPicker.getColor() != oldColor){
                        mColorPicker.setOldCenterColor(oldColor);
                    }
                })
                .create();

        // eraser dialog
        popupEraserLayout = getLayoutInflater().inflate(R.layout.popup_sketch_eraser, null);
        eraserImageView = popupEraserLayout.findViewById(R.id.stroke_circle);
        eraserDialog = new AlertDialog.Builder(this)
                .setView(popupEraserLayout)
                .setOnDismissListener(dialog -> {
                    if (mColorPicker.getColor() != oldColor){
                        mColorPicker.setOldCenterColor(oldColor);
                    }
                })
                .create();

        final Drawable circleDrawable = getResources().getDrawable(R.drawable.circle);
        size = circleDrawable.getIntrinsicWidth();
        size = circleDrawable.getIntrinsicWidth();

        setSeekbarProgress(SketchView.DEFAULT_STROKE_SIZE, SketchView.STROKE);
        setSeekbarProgress(SketchView.DEFAULT_ERASER_SIZE, SketchView.ERASER);
    }

    protected void setSeekbarProgress(int progress, int eraserOrStroke) {
        int calcProgress = progress > 1 ? progress : 1;

        int newSize = Math.round((size / 100f) * calcProgress);
        int offset = (size - newSize) / 2;

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(newSize, newSize);
        lp.setMargins(offset, offset, offset, offset);
        if (eraserOrStroke == SketchView.STROKE) {
            strokeImageView.setLayoutParams(lp);
            seekBarStrokeProgress = progress;
        } else {
            eraserImageView.setLayoutParams(lp);
            seekBarEraserProgress = progress;
        }

        getBinding().sketchView.setSize(newSize, eraserOrStroke);
    }

    private void showPopup(final int eraserOrStroke) {
        boolean isErasing = eraserOrStroke == SketchView.ERASER;

        oldColor = mColorPicker.getColor();

        if (isErasing){
            eraserDialog.show();
        } else {
            brushDialog.show();
        }

        SeekBar mSeekBar = (SeekBar) (isErasing ? popupEraserLayout.findViewById(R.id.stroke_seekbar) : popupLayout.findViewById(R.id.stroke_seekbar));
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setSeekbarProgress(progress, eraserOrStroke);
            }
        });
        int progress = isErasing ? seekBarEraserProgress : seekBarStrokeProgress;
        mSeekBar.setProgress(progress);
    }

    public void save() {
        isContentChanged = false;
        onceSaved = true;
        materialMenu.animateIconState(MaterialMenuDrawable.IconState.ARROW);
        Bitmap bitmap = getBinding().sketchView.getBitmap();
        if (bitmap != null) {
            try {
                String path = getIntent().getStringExtra(MediaStore.EXTRA_OUTPUT);
                File bitmapFile = new File(path);
                FileOutputStream out = new FileOutputStream(bitmapFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.close();
                if (!bitmapFile.exists()) {
                    ToastUtils.makeToast(R.string.file_not_exist);
                }
            } catch (Exception e) {
                LogUtils.e("Error writing sketch image data", e);
                finish();
            }
        }
    }

    public void onBack() {
        if (getBinding().sketchView.getPaths().size() == 0){
            super.onBackPressed();
        }
        if (isContentChanged) {
            new MaterialDialog.Builder(this)
                    .title(R.string.text_tips)
                    .content(R.string.text_save_or_discard)
                    .positiveText(R.string.text_save)
                    .negativeText(R.string.text_give_up)
                    .onPositive((materialDialog, dialogAction) -> {
                        save();
                        setResult(RESULT_OK);
                        finish();
                    })
                    .onNegative((materialDialog, dialogAction) -> finish())
                    .show();
        } else {
            if (onceSaved) {
                setResult(RESULT_OK);
            }
        }
    }

    @Override
    public void onDrawChanged() {
        if (getBinding().sketchView.getPaths().size() > 0) {
            ViewUtils.setAlpha(getBinding().ivUndo, 1f);
            if (!isContentChanged) {
                setContentChanged();
            }
        } else {
            ViewUtils.setAlpha(getBinding().ivUndo, 0.4f);
            isContentChanged = false;
            materialMenu.animateIconState(MaterialMenuDrawable.IconState.ARROW);
        }
        if (getBinding().sketchView.getUndoneCount() > 0) {
            ViewUtils.setAlpha(getBinding().ivRedo, 1f);
        } else {
            ViewUtils.setAlpha(getBinding().ivRedo, 0.4f);
        }
    }

    @Override
    public void onBackPressed() {
        onBack();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_brush:
                if (getBinding().sketchView.getMode() == SketchView.STROKE) {
                    showPopup(SketchView.STROKE);
                } else {
                    getBinding().sketchView.setMode(SketchView.STROKE);
                    ViewUtils.setAlpha(getBinding().ivEraser, 0.4f);
                    ViewUtils.setAlpha(getBinding().ivBrush, 1f);
                }
                break;
            case R.id.iv_undo:
                getBinding().sketchView.undo();
                break;
            case R.id.iv_redo:
                getBinding().sketchView.redo();
                break;
            case R.id.iv_eraser:
                if (getBinding().sketchView.getMode() == SketchView.ERASER) {
                    showPopup(SketchView.ERASER);
                } else {
                    getBinding().sketchView.setMode(SketchView.ERASER);
                    ViewUtils.setAlpha(getBinding().ivBrush, 0.4f);
                    ViewUtils.setAlpha(getBinding().ivEraser, 1f);
                }
                break;
        }
    }

    private void setContentChanged() {
        if (!isContentChanged) {
            isContentChanged = true;
            materialMenu.animateIconState(MaterialMenuDrawable.IconState.CHECK);
        }
    }

    private void clearAll() {
        isContentChanged = false;
        getBinding().sketchView.erase();
        materialMenu.animateIconState(MaterialMenuDrawable.IconState.ARROW);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if (getBinding().sketchView.getPaths().size() == 0){
                    finish();
                } else {
                    if (isContentChanged) {
                        save();
                        return true;
                    } else {
                        if (onceSaved) {
                            setResult(RESULT_OK);
                            finish();
                        }
                    }
                }
                break;
            case R.id.action_clear:
                new MaterialDialog.Builder(this)
                        .title(R.string.text_tips)
                        .content(R.string.confirm_to_clear_bitmap)
                        .positiveText(R.string.text_confirm)
                        .negativeText(R.string.text_cancel)
                        .onPositive((materialDialog, dialogAction) -> clearAll())
                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sketch_editor, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
