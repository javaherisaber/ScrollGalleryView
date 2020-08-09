package com.veinhorn.scrollgalleryview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentManager;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;
import androidx.viewpager.widget.ViewPager;

import com.veinhorn.scrollgalleryview.builder.GalleryBuilder;
import com.veinhorn.scrollgalleryview.builder.GalleryBuilderImpl;
import com.veinhorn.scrollgalleryview.loader.MediaLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScrollGalleryView extends LinearLayout {
    private FragmentManager fragmentManager;
    private Context context;
    private Point displayProps;
    private ScreenSlidePagerAdapter pagerAdapter;
    private List<MediaInfo> mListOfMedia;
    private int selectedIndex = 0;

    // Options
    private int thumbnailSize; // width and height in pixels
    private boolean zoomEnabled;
    private boolean isControlsHidden;
    private boolean hideControlsOnClick;
    private Integer hideControlsAfterDelay;

    // Views
    private LinearLayout thumbnailsContainer;
    private HorizontalScrollView horizontalScrollView;
    private ViewPager viewPager;
    public TextView positionView;
    public ImageView closeView;

    // Transitions
    private Transition thumbnailsTransition;
    private boolean useDefaultThumbnailsTransition;

    public ScrollGalleryView(Context context) {
        super(context);
        init(context, null);
    }

    public ScrollGalleryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ScrollGalleryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ScrollGalleryView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ScrollGalleryView);
        // Attrs
        int closeIconColor = array.getColor(R.styleable.ScrollGalleryView_closeIconColor, Color.WHITE);
        int countTextColor = array.getColor(R.styleable.ScrollGalleryView_countTextColor, Color.WHITE);
        int countTextSize = array.getDimensionPixelSize(R.styleable.ScrollGalleryView_countTextSize, -1);
        array.recycle();

        this.context = context;
        mListOfMedia = new ArrayList<>();

        setOrientation(VERTICAL);
        displayProps = getDisplaySize();
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.scroll_gallery_view, this, true);

        closeView = (ImageView) findViewById(R.id.imageView_close);
        closeView.setColorFilter(closeIconColor);

        positionView = (TextView) findViewById(R.id.textView_position);
        positionView.setTextColor(countTextColor);
        if (countTextSize != -1) {
            positionView.setTextSize(pixelToDp(context, countTextSize));
        }

        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.thumbnails_scroll_view);
        thumbnailsContainer = (LinearLayout) findViewById(R.id.thumbnails_container);
        thumbnailsContainer.setPadding(displayProps.x / 2, 0, displayProps.x / 2, 0);
    }

    private int pixelToDp(Context context, int pixel) {
        return (int) (pixel / context.getResources().getDisplayMetrics().density);
    }

    // Listeners
    private final ViewPager.SimpleOnPageChangeListener viewPagerChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            updateSelectedThumbnail(false);
            selectedIndex = position;
            updateSelectedThumbnail(true);
            showControls();
            updatePositionView();
            scroll(thumbnailsContainer.getChildAt(selectedIndex));
        }
    };

    private void updatePositionView() {
        positionView.setText((selectedIndex + 1) + "/" + mListOfMedia.size());
    }

    private void updateSelectedThumbnail(boolean isActive) {
        View view = thumbnailsContainer.getChildAt(selectedIndex);
        View activeView = view.findViewById(R.id.view_thumbnail_active);
        if (isActive) {
            activeView.setVisibility(View.VISIBLE);
        } else  {
            activeView.setVisibility(View.INVISIBLE);
        }
    }

    private final OnClickListener thumbnailOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            updateSelectedThumbnail(false);
            selectedIndex = position;
            updateSelectedThumbnail(true);
            scroll(v);
            viewPager.setCurrentItem(position, true);
        }
    };

    private OnImageClickListener onImageClickListener;
    private OnImageLongClickListener onImageLongClickListener;

    /**
     * We should create OnImageClickListener to wrap our provided OnImageClickListener,
     * otherwise it will be null when we pass it to PagerAdapter. Also it used to wrap
     * code which is responsible for showing/hiding thumbnails when user click on image
     */
    private OnImageClickListener innerOnImageClickListener = new OnImageClickListener() {
        @Override
        public void onClick(int position) {
            if (hideControlsOnClick) {
                if (isControlsHidden) {
                    showControls();
                    isControlsHidden = false;
                } else {
                    hideControls();
                    isControlsHidden = true;
                }
            }
            if (onImageClickListener != null) onImageClickListener.onClick(position);
        }
    };

    private OnImageLongClickListener innerOnImageLongClickListener = new OnImageLongClickListener() {
        @Override
        public void onClick(int position) {
            if (onImageLongClickListener != null) onImageLongClickListener.onClick(position);
        }
    };

    public interface OnImageClickListener {
        void onClick(int position);
    }

    public interface OnImageLongClickListener {
        void onClick(int position);
    }

    public ScrollGalleryView setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        initializeViewPager();

        if (hideControlsAfterDelay != null) hideThumbnailsAfterDelay(hideControlsAfterDelay);

        return this;
    }

    public void build() {
        horizontalScrollView.post(new Runnable() {
            @Override
            public void run() {
                scroll(thumbnailsContainer.getChildAt(selectedIndex));
            }
        });
    }

    /**
     * @return inner ViewPager
     */
    public ViewPager getViewPager() {
        return viewPager;
    }

    /**
     * Set up OnImageClickListener for your gallery images
     * You should set OnImageClickListener only before setFragmentManager call!
     *
     * @param onImageClickListener which is called when you click on image
     * @return ScrollGalleryView
     */
    public ScrollGalleryView addOnImageClickListener(OnImageClickListener onImageClickListener) {
        this.onImageClickListener = onImageClickListener;
        return this;
    }

    public ScrollGalleryView addOnImageLongClickListener(OnImageLongClickListener onImageLongClickListener) {
        this.onImageLongClickListener = onImageLongClickListener;
        return this;
    }

    /**
     * Set up OnPageChangeListener for internal ViewPager
     *
     * @param listener which is used by internal ViewPager
     */
    public void addOnPageChangeListener(final ViewPager.OnPageChangeListener listener) {
        viewPager.clearOnPageChangeListeners();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                listener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                scroll(thumbnailsContainer.getChildAt(position));
                listener.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                listener.onPageScrollStateChanged(state);
            }
        });
    }

    public ScrollGalleryView addMedia(MediaInfo mediaInfo) {
        if (mediaInfo == null) {
            throw new NullPointerException("Infos may not be null!");
        }

        return addMedia(Collections.singletonList(mediaInfo));
    }

    public ScrollGalleryView setSelectedIndex(int index) {
        this.selectedIndex = index;
        return this;
    }

    public ScrollGalleryView addMedia(List<MediaInfo> infos) {
        if (infos == null) {
            throw new NullPointerException("Infos may not be null!");
        }

        for (int index = 0; index < infos.size(); index++) {
            MediaInfo info = infos.get(index);
            mListOfMedia.add(info);

            final ImageView thumbnail = addThumbnail(getDefaultThumbnail(), selectedIndex == mListOfMedia.size() - 1);
            info.getLoader().loadThumbnail(getContext(), thumbnail, new MediaLoader.SuccessCallback() {
                @Override
                public void onSuccess() {
                    thumbnail.setScaleType(ImageView.ScaleType.FIT_CENTER);
                }
            });

            pagerAdapter.notifyDataSetChanged();
        }
        if (selectedIndex < mListOfMedia.size()) {
            viewPager.setCurrentItem(selectedIndex, true);
        }
        updatePositionView();

        return this;
    }

    private ImageView addThumbnail(Bitmap image, boolean isSelected) {
        Bitmap thumbnail = createThumbnail(image);
        Pair<View, ImageView> thumbnailView = createThumbnailView(thumbnail, isSelected);
        thumbnailsContainer.addView(thumbnailView.first);
        if (thumbnailsContainer.getChildCount() > selectedIndex) {
            scroll(thumbnailsContainer.getChildAt(selectedIndex));
        }
        return thumbnailView.second;
    }

    private Pair<View, ImageView> createThumbnailView(Bitmap thumbnail, boolean isSelected) {
        View view = inflate(context, R.layout.thumbnail_view, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView_thumbnail);
        View active = (View) view.findViewById(R.id.view_thumbnail_active);
        imageView.setImageBitmap(thumbnail);
        imageView.setOnClickListener(thumbnailOnClickListener);
        imageView.setTag(mListOfMedia.size() - 1);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        if (isSelected) {
            active.setVisibility(View.VISIBLE);
        } else {
            active.setVisibility(View.GONE);
        }
        return new Pair<>(view, imageView);
    }

    /**
     * Set the current item displayed in the view pager.
     *
     * @param i a zero-based index
     */
    public ScrollGalleryView setCurrentItem(int i) {
        viewPager.setCurrentItem(i, false);
        return this;
    }

    public int getCurrentItem() {
        return viewPager.getCurrentItem();
    }

    public ScrollGalleryView setThumbnailSize(int thumbnailSize) {
        this.thumbnailSize = thumbnailSize;
        return this;
    }

    public ScrollGalleryView setZoom(boolean zoomEnabled) {
        this.zoomEnabled = zoomEnabled;
        return this;
    }

    /**
     * If you enabled this option, hideThumbnailsOnClick() method will not work
     *
     * @param isThumbnailsHidden hides thumbnails container
     * @return ScrollGalleryView
     */
    public ScrollGalleryView withHiddenThumbnails(boolean isThumbnailsHidden) {
        if (this.isControlsHidden && !isThumbnailsHidden) {
            showControls();
        } else if (!this.isControlsHidden && isThumbnailsHidden) {
            hideControls();
        }
        this.isControlsHidden = isThumbnailsHidden;

        return this;
    }

    /**
     * Keep in mind that this method do not work with enabled isThumbnailsHidden option
     *
     * @param hideThumbnailsOnClick hides thumbnails container on image click
     * @return ScrollGalleryView
     */
    public ScrollGalleryView hideThumbnailsOnClick(boolean hideThumbnailsOnClick) {
        if (!isControlsHidden) {
            this.hideControlsOnClick = hideThumbnailsOnClick;
            if (hideThumbnailsOnClick) this.useDefaultThumbnailsTransition = true;
        }
        return this;
    }

    /**
     * Keep in mind that this method do not work with enabled isThumbnailsHidden option
     *
     * @param hideThumbnailsOnClick hides thumbnails container on image click
     * @param thumbnailsTransition  null is used to disable transation
     * @return ScrollGalleryView
     */
    public ScrollGalleryView hideThumbnailsOnClick(boolean hideThumbnailsOnClick, Transition thumbnailsTransition) {
        if (!isControlsHidden) {
            this.hideControlsOnClick = hideThumbnailsOnClick;
            this.thumbnailsTransition = thumbnailsTransition;
        }
        return this;
    }

    /**
     * Automatically hide thumbnails container after specified delay
     *
     * @param hideThumbnailsAfterDelay delay in ms
     * @return ScrollGalleryView
     */
    public ScrollGalleryView hideThumbnailsAfter(int hideThumbnailsAfterDelay) {
        if (!isControlsHidden) {
            this.hideControlsAfterDelay = hideThumbnailsAfterDelay;
        }
        return this;
    }

    public void showControls() {
        closeView.setVisibility(View.VISIBLE);
        positionView.setVisibility(View.VISIBLE);
        setThumbnailsTransition();
        horizontalScrollView.setVisibility(VISIBLE);
        // Hide thumbnails container when hideThumbnailsAfterDelay option is enabled
        if (hideControlsAfterDelay != null) hideThumbnailsAfterDelay(hideControlsAfterDelay);
    }

    public void hideControls() {
        closeView.setVisibility(View.GONE);
        positionView.setVisibility(View.GONE);
        setThumbnailsTransition();
        horizontalScrollView.setVisibility(GONE);
    }

    /**
     * Remove all images from gallery
     */
    public void clearGallery() {
        // remove all media infos
        mListOfMedia.clear();

        // create new adapter
        pagerAdapter = new ScreenSlidePagerAdapter(fragmentManager, mListOfMedia, zoomEnabled, innerOnImageClickListener,
                innerOnImageLongClickListener,
                getResources().getString(R.string.first_image_transition_name));

        viewPager.setAdapter(pagerAdapter);
        // remove thumbnails
        thumbnailsContainer.removeAllViews();
    }

    /**
     * Remove a media from the gallery
     *
     * @param position media's position to remove
     */
    public void removeMedia(int position) {
        if (position >= mListOfMedia.size() || position < 0) {
            return;
        }
        pagerAdapter.removeItem(position);
        removeThumbnail(position);
    }

    public static GalleryBuilder from(ScrollGalleryView galleryView) {
        return new GalleryBuilderImpl(galleryView);
    }

    private void hideThumbnailsAfterDelay(int delay) {
        horizontalScrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideControls();
                isControlsHidden = !isControlsHidden;
            }
        }, delay);
    }

    // Make choice between default and provided by user transition
    private void setThumbnailsTransition() {
        if (thumbnailsTransition == null && useDefaultThumbnailsTransition) {
            TransitionManager.beginDelayedTransition(horizontalScrollView);
        } else if (thumbnailsTransition != null) {
            TransitionManager.beginDelayedTransition(horizontalScrollView, thumbnailsTransition);
        }
    }

    private Bitmap getDefaultThumbnail() {
        return ((BitmapDrawable) getContext().getResources().getDrawable(R.drawable.placeholder_image)).getBitmap();
    }

    private Point getDisplaySize() {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        return point;
    }

    private void removeThumbnail(int position) {
        View thumbnail = thumbnailsContainer.getChildAt(position);
        if (thumbnail == null) {
            return;
        }
        thumbnailsContainer.removeView(thumbnail);
    }

    private Bitmap createThumbnail(Bitmap image) {
        return ThumbnailUtils.extractThumbnail(image, thumbnailSize, thumbnailSize);
    }

    private void initializeViewPager() {
        viewPager = (HackyViewPager) findViewById(R.id.viewPager);

        pagerAdapter = new ScreenSlidePagerAdapter(
                fragmentManager,
                mListOfMedia,
                zoomEnabled,
                innerOnImageClickListener,
                innerOnImageLongClickListener,
                getResources().getString(R.string.first_image_transition_name));

        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerChangeListener);
    }

    private void scroll(View thumbnail) {
        int thumbnailCoords[] = new int[2];
        thumbnail.getLocationOnScreen(thumbnailCoords);

        int thumbnailCenterX = thumbnailCoords[0] + thumbnailSize / 2;
        int thumbnailDelta = displayProps.x / 2 - thumbnailCenterX;

        horizontalScrollView.smoothScrollBy(-thumbnailDelta, 0);
    }

    private int calculateInSampleSize(int imgWidth, int imgHeight, int maxWidth, int maxHeight) {
        int inSampleSize = 1;
        while (imgWidth / inSampleSize > maxWidth || imgHeight / inSampleSize > maxHeight) {
            inSampleSize *= 2;
        }
        return inSampleSize;
    }
}
