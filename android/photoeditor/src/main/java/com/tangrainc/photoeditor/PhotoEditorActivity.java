package com.tangrainc.photoeditor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentStatePagerAdapter;
//import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

//import com.tangrainc.photoeditor.widget.SlidingUpPanelLayout;
import com.ahmedadeltito.photoeditorsdk.BrushDrawingView;
import com.ahmedadeltito.photoeditorsdk.OnPhotoEditorSDKListener;
import com.ahmedadeltito.photoeditorsdk.PhotoEditorSDK;
import com.ahmedadeltito.photoeditorsdk.ViewType;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageActivity;
//import com.viewpagerindicator.PageIndicator;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
//import java.util.List;

public class PhotoEditorActivity extends AppCompatActivity implements View.OnClickListener, OnPhotoEditorSDKListener {

    private final String TAG = "PhotoEditorActivity";

    private ImageView photoEditImageView;
    private RelativeLayout parentImageRelativeLayout;
    private RecyclerView drawingViewColorPickerRecyclerView;
    private TextView doneDrawingTextView;
//    private SlidingUpPanelLayout mLayout;
//    private Typeface emojiFont;
    private View topShadow;
    private RelativeLayout topShadowRelativeLayout;
    private View bottomShadow;
    private RelativeLayout bottomShadowRelativeLayout;
    private ArrayList<Integer> colorPickerColors;
    private int colorCodeTextView = -1;
    private PhotoEditorSDK photoEditorSDK;
    private String selectedImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_editor);

        Bundle extras = getIntent().getExtras();

        selectedImagePath = extras.getString("selectedImagePath");

        Typeface iconFont = Typeface.createFromAsset(getAssets(), "photo-editor-icons.ttf");
//        emojiFont = Typeface.createFromAsset(getAssets(), "emojione-android.ttf");

        BrushDrawingView brushDrawingView = findViewById(R.id.drawing_view);
        drawingViewColorPickerRecyclerView = findViewById(R.id.drawing_view_color_picker_recycler_view);
        parentImageRelativeLayout = findViewById(R.id.parent_image_rl);
        TextView closeTextView = findViewById(R.id.close_tv);
        TextView cropTextView = findViewById(R.id.crop_tv);
        TextView addTextView = findViewById(R.id.add_text_tv);
        TextView addPencil = findViewById(R.id.add_pencil_tv);
        RelativeLayout deleteRelativeLayout = findViewById(R.id.delete_rl);
        TextView deleteTextView = findViewById(R.id.delete_tv);
//        TextView addImageEmojiTextView = findViewById(R.id.add_image_emoji_tv);
        TextView saveTextView = findViewById(R.id.save_tv);
//        undoTextView = findViewById(R.id.undo_tv);
//        undoTextTextView = findViewById(R.id.undo_text_tv);
        doneDrawingTextView = findViewById(R.id.done_drawing_tv);
//        eraseDrawingTextView = findViewById(R.id.erase_drawing_tv);
        TextView clearAllTextView = findViewById(R.id.clear_all_tv);
        TextView goToNextTextView = findViewById(R.id.go_to_next_screen_tv);
        photoEditImageView = findViewById(R.id.photo_edit_iv);
//        mLayout = findViewById(R.id.sliding_layout);
        topShadow = findViewById(R.id.top_shadow);
        topShadowRelativeLayout = findViewById(R.id.top_parent_rl);
        bottomShadow = findViewById(R.id.bottom_shadow);
        bottomShadowRelativeLayout = findViewById(R.id.bottom_parent_rl);

//        ViewPager pager = findViewById(R.id.image_emoji_view_pager);
//        PageIndicator indicator = findViewById(R.id.image_emoji_indicator);

        setImage(selectedImagePath);

        // Set icon font
        closeTextView.setTypeface(iconFont);
        cropTextView.setTypeface(iconFont);
        addTextView.setTypeface(iconFont);
        addPencil.setTypeface(iconFont);
//        addImageEmojiTextView.setTypeface(newFont);
        saveTextView.setTypeface(iconFont);
//        undoTextView.setTypeface(iconFont);
        clearAllTextView.setTypeface(iconFont);
        goToNextTextView.setTypeface(iconFont);
        deleteTextView.setTypeface(iconFont);

        // Hide disabled controls
        if (!extras.getBoolean("isCropIn")) {
            cropTextView.setVisibility(View.GONE);
        }
        if (!extras.getBoolean("isDrawIn")) {
            addPencil.setVisibility(View.GONE);
        }
        if (!extras.getBoolean("isTextIn")) {
            addTextView.setVisibility(View.GONE);
        }
        if (!extras.getBoolean("isSaveIn")) {
            saveTextView.setVisibility(View.GONE);
        }
        if (!extras.getBoolean("isClearIn")) {
            clearAllTextView.setVisibility(View.GONE);
        }

//        final List<Fragment> fragmentsList = new ArrayList<>();
        // fragmentsList.add(new ImageFragment());
        // fragmentsList.add(new EmojiFragment());

//        PreviewSlidePagerAdapter adapter = new PreviewSlidePagerAdapter(getSupportFragmentManager(), fragmentsList);
//        pager.setAdapter(adapter);
//        pager.setOffscreenPageLimit(5);
//        indicator.setViewPager(pager);

        photoEditorSDK = new PhotoEditorSDK.PhotoEditorSDKBuilder(PhotoEditorActivity.this)
                .parentView(parentImageRelativeLayout) // add parent image view
                .childView(photoEditImageView) // add the desired image view
                .deleteView(deleteRelativeLayout) // add the deleted view that will appear during the movement of the views
                .brushDrawingView(brushDrawingView) // add the brush drawing view that is responsible for drawing on the image view
                .buildPhotoEditorSDK(); // build photo editor sdk
        photoEditorSDK.setOnPhotoEditorSDKListener(this);

//        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                if (position == 0)
//                    mLayout.setScrollableView(((ImageFragment) fragmentsList.get(position)).imageRecyclerView);
//                else if (position == 1)
//                    mLayout.setScrollableView(((EmojiFragment) fragmentsList.get(position)).emojiRecyclerView);
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });

        closeTextView.setOnClickListener(this);
//        addImageEmojiTextView.setOnClickListener(this);
        cropTextView.setOnClickListener(this);
        addTextView.setOnClickListener(this);
        addPencil.setOnClickListener(this);
        saveTextView.setOnClickListener(this);
//        undoTextView.setOnClickListener(this);
//        undoTextTextView.setOnClickListener(this);
        doneDrawingTextView.setOnClickListener(this);
//        eraseDrawingTextView.setOnClickListener(this);
        clearAllTextView.setOnClickListener(this);
        goToNextTextView.setOnClickListener(this);

        colorPickerColors = new ArrayList<>();
        colorPickerColors.add(getResources().getColor(R.color.black));
        colorPickerColors.add(getResources().getColor(R.color.blue_color_picker));
        colorPickerColors.add(getResources().getColor(R.color.brown_color_picker));
        colorPickerColors.add(getResources().getColor(R.color.green_color_picker));
        colorPickerColors.add(getResources().getColor(R.color.orange_color_picker));
        colorPickerColors.add(getResources().getColor(R.color.red_color_picker));
        colorPickerColors.add(getResources().getColor(R.color.red_orange_color_picker));
        colorPickerColors.add(getResources().getColor(R.color.sky_blue_color_picker));
        colorPickerColors.add(getResources().getColor(R.color.violet_color_picker));
        colorPickerColors.add(getResources().getColor(R.color.white));
        colorPickerColors.add(getResources().getColor(R.color.yellow_color_picker));
        colorPickerColors.add(getResources().getColor(R.color.yellow_green_color_picker));

//        new CountDownTimer(500, 100) {
//
//            public void onTick(long millisUntilFinished) {
//            }
//
//            public void onFinish() {
//                mLayout.setScrollableView(((ImageFragment) fragmentsList.get(0)).imageRecyclerView);
//            }
//
//        }.start();
    }

    private void setImage(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);

        photoEditImageView.setImageBitmap(bitmap);
    }
    private boolean stringIsNotEmpty(String string) {
        return string != null && !string.equals("null") && !string.trim().equals("");
    }

//    public void addEmoji(String emojiName) {
//        photoEditorSDK.addEmoji(emojiName, emojiFont);
//        if (mLayout != null)
//            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
//    }
//
//    public void addImage(Bitmap image) {
//        photoEditorSDK.addImage(image);
//        if (mLayout != null)
//            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
//    }

    private void addText(String text, int colorCodeTextView) {
        photoEditorSDK.addText(text, colorCodeTextView);
    }

    private void clearAllViews() {
        photoEditorSDK.clearAllViews();
    }

//    private void undoViews() {
//        photoEditorSDK.viewUndo();
//    }

//    private void eraseDrawing() {
//        photoEditorSDK.brushEraser();
//    }

    private void openAddTextPopupWindow(String text, int colorCode) {
        colorCodeTextView = colorCode;
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View addTextPopupWindowRootView = inflater.inflate(R.layout.add_text_popup_window, null);
        final EditText addTextEditText = addTextPopupWindowRootView.findViewById(R.id.add_text_edit_text);
        TextView addTextDoneTextView = addTextPopupWindowRootView.findViewById(R.id.add_text_done_tv);
        RecyclerView addTextColorPickerRecyclerView = addTextPopupWindowRootView.findViewById(R.id.add_text_color_picker_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(PhotoEditorActivity.this, LinearLayoutManager.HORIZONTAL, false);
        addTextColorPickerRecyclerView.setLayoutManager(layoutManager);
        addTextColorPickerRecyclerView.setHasFixedSize(true);
        ColorPickerAdapter colorPickerAdapter = new ColorPickerAdapter(PhotoEditorActivity.this, colorPickerColors);
        colorPickerAdapter.setOnColorPickerClickListener(new ColorPickerAdapter.OnColorPickerClickListener() {
            @Override
            public void onColorPickerClickListener(int colorCode) {
                addTextEditText.setTextColor(colorCode);
                colorCodeTextView = colorCode;
            }
        });
        addTextColorPickerRecyclerView.setAdapter(colorPickerAdapter);
        if (stringIsNotEmpty(text)) {
            addTextEditText.setText(text);
            addTextEditText.setTextColor(colorCode == -1 ? getResources().getColor(R.color.white) : colorCode);
        }
        final PopupWindow pop = new PopupWindow(PhotoEditorActivity.this);
        pop.setContentView(addTextPopupWindowRootView);
        pop.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        pop.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        pop.setFocusable(true);
        pop.setBackgroundDrawable(null);
        pop.showAtLocation(addTextPopupWindowRootView, Gravity.TOP, 0, 0);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        addTextDoneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addText(addTextEditText.getText().toString(), colorCodeTextView);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                pop.dismiss();
            }
        });
    }

    private void updateView(int visibility) {
        topShadow.setVisibility(visibility);
        topShadowRelativeLayout.setVisibility(visibility);
        bottomShadow.setVisibility(visibility);
        bottomShadowRelativeLayout.setVisibility(visibility);
    }

    private void updateBrushDrawingView(boolean brushDrawingMode) {
        photoEditorSDK.setBrushDrawingMode(brushDrawingMode);
        if (brushDrawingMode) {
            updateView(View.GONE);
            drawingViewColorPickerRecyclerView.setVisibility(View.VISIBLE);
            doneDrawingTextView.setVisibility(View.VISIBLE);
//            eraseDrawingTextView.setVisibility(View.VISIBLE);
            LinearLayoutManager layoutManager = new LinearLayoutManager(PhotoEditorActivity.this, LinearLayoutManager.HORIZONTAL, false);
            drawingViewColorPickerRecyclerView.setLayoutManager(layoutManager);
            drawingViewColorPickerRecyclerView.setHasFixedSize(true);
            ColorPickerAdapter colorPickerAdapter = new ColorPickerAdapter(PhotoEditorActivity.this, colorPickerColors);
            colorPickerAdapter.setOnColorPickerClickListener(new ColorPickerAdapter.OnColorPickerClickListener() {
                @Override
                public void onColorPickerClickListener(int colorCode) {
                    photoEditorSDK.setBrushColor(colorCode);
                }
            });
            drawingViewColorPickerRecyclerView.setAdapter(colorPickerAdapter);
        } else {
            updateView(View.VISIBLE);
            drawingViewColorPickerRecyclerView.setVisibility(View.GONE);
            doneDrawingTextView.setVisibility(View.GONE);
//            eraseDrawingTextView.setVisibility(View.GONE);
        }
    }

    private void returnBackWithSavedImage() {
        updateView(View.GONE);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        parentImageRelativeLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.d(TAG, "onGlobalLayout");

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageName = "IMG_" + timeStamp + ".jpg";
                Intent returnIntent = new Intent();
                returnIntent.putExtra("imagePath", photoEditorSDK.saveImage("PhotoEditorSDK", imageName));
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
        parentImageRelativeLayout.setLayoutParams(layoutParams);
    }

    private void beginCropping() {
        CropImage.activity(Uri.fromFile(new File(selectedImagePath))).start(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.close_tv) {
            onBackPressed();
//        } else if (v.getId() == R.id.add_image_emoji_tv) {
//            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        } else if (v.getId() == R.id.add_text_tv) {
            openAddTextPopupWindow("", -1);
        } else if (v.getId() == R.id.add_pencil_tv) {
            updateBrushDrawingView(true);
        } else if (v.getId() == R.id.done_drawing_tv) {
            updateBrushDrawingView(false);
        } else if (v.getId() == R.id.save_tv) {
            returnBackWithSavedImage();
        } else if (v.getId() == R.id.clear_all_tv) {
            clearAllViews();
//        } else if (v.getId() == R.id.undo_text_tv || v.getId() == R.id.undo_tv) {
//            undoViews();
//        } else if (v.getId() == R.id.erase_drawing_tv) {
//            eraseDrawing();
        } else if (v.getId() == R.id.go_to_next_screen_tv) {
            returnBackWithSavedImage();
        } else if (v.getId() == R.id.crop_tv) {
            beginCropping();
        }
    }

    @Override
    public void onEditTextChangeListener(String text, int colorCode) {
        openAddTextPopupWindow(text, colorCode);
    }

    @Override
    public void onAddViewListener(ViewType viewType, int numberOfAddedViews) {
//        if (numberOfAddedViews > 0) {
//            undoTextView.setVisibility(View.VISIBLE);
//            undoTextTextView.setVisibility(View.VISIBLE);
//        }
//        switch (viewType) {
//            case BRUSH_DRAWING:
//                Log.i("BRUSH_DRAWING", "onAddViewListener");
//                break;
//            case EMOJI:
//                Log.i("EMOJI", "onAddViewListener");
//                break;
//            case IMAGE:
//                Log.i("IMAGE", "onAddViewListener");
//                break;
//            case TEXT:
//                Log.i("TEXT", "onAddViewListener");
//                break;
//        }
    }

    @Override
    public void onRemoveViewListener(int numberOfAddedViews) {
//        Log.i(TAG, "onRemoveViewListener");
//        if (numberOfAddedViews == 0) {
//            undoTextView.setVisibility(View.GONE);
//            undoTextTextView.setVisibility(View.GONE);
//        }
    }

    @Override
    public void onStartViewChangeListener(ViewType viewType) {
        switch (viewType) {
            case BRUSH_DRAWING:
                Log.i("BRUSH_DRAWING", "onStartViewChangeListener");
                break;
            case EMOJI:
                Log.i("EMOJI", "onStartViewChangeListener");
                break;
            case IMAGE:
                Log.i("IMAGE", "onStartViewChangeListener");
                break;
            case TEXT:
                Log.i("TEXT", "onStartViewChangeListener");
                break;
        }
    }

    @Override
    public void onStopViewChangeListener(ViewType viewType) {
        switch (viewType) {
            case BRUSH_DRAWING:
                Log.i("BRUSH_DRAWING", "onStopViewChangeListener");
                break;
            case EMOJI:
                Log.i("EMOJI", "onStopViewChangeListener");
                break;
            case IMAGE:
                Log.i("IMAGE", "onStopViewChangeListener");
                break;
            case TEXT:
                Log.i("TEXT", "onStopViewChangeListener");
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                setImage(resultUri.getPath());
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.d(TAG, error.getMessage());
            }
        }
    }

//    private class PreviewSlidePagerAdapter extends FragmentStatePagerAdapter {
//        private List<Fragment> mFragments;
//
//        PreviewSlidePagerAdapter(FragmentManager fm, List<Fragment> fragments) {
//            super(fm);
//            mFragments = fragments;
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            if (mFragments == null) {
//                return (null);
//            }
//            return mFragments.get(position);
//        }
//
//        @Override
//        public int getCount() {
//            return 2;
//        }
//    }
}
