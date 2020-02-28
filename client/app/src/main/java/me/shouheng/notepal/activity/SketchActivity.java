package me.shouheng.notepal.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.balysv.materialmenu.MaterialMenuDrawable;
import com.facebook.stetho.common.LogUtil;
import com.larswerkman.holocolorpicker.ColorPicker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import me.shouheng.commons.activity.CommonActivity;
import me.shouheng.commons.event.PageName;
import me.shouheng.commons.utils.LogUtils;
import me.shouheng.commons.utils.ToastUtils;
import me.shouheng.commons.utils.ViewUtils;
import me.shouheng.commons.widget.sketch.OnDrawChangedListener;
import me.shouheng.commons.widget.sketch.SketchView;
import me.shouheng.notepal.R;
import me.shouheng.notepal.databinding.ActivitySketchBinding;

import static me.shouheng.commons.event.UMEvent.*;

/**
 * The activity used to sketch
 *
 * refactored at 2018-11-28, 23:12,
 * by WngShhng (shouheng2015@gmail.com)
 */
@PageName(name = PAGE_SKETCH)
public class SketchActivity extends CommonActivity<ActivitySketchBinding>
        implements OnDrawChangedListener, View.OnClickListener {

    /**
     * Use the key to put the {@link Uri} of a bitmap to the intent, and the draw action
     * will then be based on the bitmap.
     */
    public final static String EXTRA_KEY_BASE_BITMAP = "__extra_key_based_bitmap";

    /**
     * The key used to put the output file path.
     */
    public final static String EXTRA_KEY_OUTPUT_FILE_PATH = "__extra_key_output_file_path";

    private View popupLayout, popupEraserLayout;
    private ImageView strokeImageView, eraserImageView;
    private ColorPicker mColorPicker;
    private MaterialMenuDrawable materialMenu;
    private Dialog eraserDialog, brushDialog;

    private int seekBarStrokeProgress;
    private int seekBarEraserProgress;
    private int oldColor, size;

    private boolean isContentChanged, onceSaved;

    private String outputFilePath;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_sketch;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        handleIntent();
        configToolbar();
        configViews();
        configDialogs();
    }

    private void handleIntent() {
        Intent intent = getIntent();
        Uri baseUri = intent.getParcelableExtra(EXTRA_KEY_BASE_BITMAP);
        if (baseUri != null) {
            Bitmap bmp;
            try {
                bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(baseUri));
                getBinding().sketchView.setBackgroundBitmap(this, bmp);
            } catch (FileNotFoundException e) {
                LogUtils.e("Error replacing sketch bitmap background.", e);
            }
        }
        if (!intent.hasExtra(EXTRA_KEY_OUTPUT_FILE_PATH)) {
            LogUtil.e("The bitmap won't be saved if you don't specify the output file path.");
            throw new IllegalStateException("The bitmap won't be saved if you don't specify the output file path.");
        }
        outputFilePath = intent.getStringExtra(EXTRA_KEY_OUTPUT_FILE_PATH);
    }

    private void configToolbar() {
        setSupportActionBar(getBinding().toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("");
        }
        materialMenu = new MaterialMenuDrawable(this,
                isDarkTheme() ? Color.WHITE : Color.BLACK, MaterialMenuDrawable.Stroke.THIN);
        materialMenu.setIconState(MaterialMenuDrawable.IconState.ARROW);
        getBinding().toolbar.setNavigationIcon(materialMenu);
    }

    private void configViews() {
        getBinding().ivBrush.setOnClickListener(this);
        getBinding().ivUndo.setOnClickListener(this);
        getBinding().ivRedo.setOnClickListener(this);
        getBinding().ivEraser.setOnClickListener(this);
        getBinding().ivClear.setOnClickListener(this);

        getBinding().sketchView.setOnDrawChangedListener(this);

        ViewUtils.setAlpha(getBinding().ivEraser, 0.4f);
    }

    private void configDialogs() {
        // Stroke Dialog
        popupLayout = getLayoutInflater().inflate(R.layout.popup_sketch_stroke, null);
        strokeImageView = popupLayout.findViewById(R.id.stroke_circle);
        mColorPicker = popupLayout.findViewById(R.id.stroke_color_picker);
        mColorPicker.addSVBar(popupLayout.findViewById(R.id.svbar));
        mColorPicker.addOpacityBar(popupLayout.findViewById(R.id.opacitybar));
        mColorPicker.setOnColorChangedListener(color -> getBinding().sketchView.setStrokeColor(color));
        mColorPicker.setColor(getBinding().sketchView.getStrokeColor());
        mColorPicker.setOldCenterColor(getBinding().sketchView.getStrokeColor());

        // Brush Dialog
        brushDialog = new AlertDialog.Builder(this)
                .setView(popupLayout)
                .setOnDismissListener(dialog -> {
                    if (mColorPicker.getColor() != oldColor){
                        mColorPicker.setOldCenterColor(oldColor);
                    }
                })
                .create();

        // Eraser Dialog
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

        setSeekBarProgress(SketchView.DEFAULT_STROKE_SIZE, SketchView.STROKE);
        setSeekBarProgress(SketchView.DEFAULT_ERASER_SIZE, SketchView.ERASER);
    }

    private void setSeekBarProgress(int progress, int eraserOrStroke) {
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

        if (isErasing) {
            eraserDialog.show();
        } else {
            brushDialog.show();
        }

        SeekBar mSeekBar = (SeekBar) (isErasing ? popupEraserLayout.findViewById(R.id.stroke_seekbar)
                : popupLayout.findViewById(R.id.stroke_seekbar));
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setSeekBarProgress(progress, eraserOrStroke);
            }
        });
        int progress = isErasing ? seekBarEraserProgress : seekBarStrokeProgress;
        mSeekBar.setProgress(progress);
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
    public void onClick(View v) {
        switch (v.getId()) {
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
            case R.id.iv_clear:
                new MaterialDialog.Builder(this)
                        .title(R.string.text_tips)
                        .content(R.string.sketch_clear_tips)
                        .positiveText(R.string.text_confirm)
                        .negativeText(R.string.text_cancel)
                        .onPositive((materialDialog, dialogAction) -> clearAll())
                        .show();
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

    private void doSaveBitmap() {
        isContentChanged = false;
        onceSaved = true;
        materialMenu.animateIconState(MaterialMenuDrawable.IconState.ARROW);
        Bitmap bitmap = getBinding().sketchView.getBitmap();
        if (bitmap != null) {
            try {
                File bitmapFile = new File(outputFilePath);
                FileOutputStream out = new FileOutputStream(bitmapFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.close();
                if (!bitmapFile.exists()) {
                    ToastUtils.makeToast(R.string.text_file_not_exist);
                }
            } catch (Exception e) {
                LogUtils.e("Error writing sketch image data", e);
                finish();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (getBinding().sketchView.getPaths().size() == 0) {
                    finish();
                } else {
                    if (isContentChanged) {
                        doSaveBitmap();
                        return true;
                    } else {
                        if (onceSaved) {
                            setResult(RESULT_OK);
                            finish();
                        }
                    }
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (getBinding().sketchView.getPaths().size() == 0) {
            super.onBackPressed();
        }
        if (isContentChanged) {
            new MaterialDialog.Builder(this)
                    .title(R.string.text_tips)
                    .content(R.string.text_save_or_discard)
                    .positiveText(R.string.text_save)
                    .negativeText(R.string.text_give_up)
                    .onPositive((materialDialog, dialogAction) -> {
                        doSaveBitmap();
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
}
