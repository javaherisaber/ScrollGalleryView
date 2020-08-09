package com.veinhorn.scrollgalleryview.loader.picasso;

import com.veinhorn.scrollgalleryview.MediaInfo;
import java.util.List;

public class DSL {
    private static PicassoMediaHelper mediaHelper = new PicassoMediaHelper();

    public static MediaInfo image(String url) {
        return mediaHelper.image(url);
    }

    public static List<MediaInfo> images(List<String> urls) {
        return mediaHelper.images(urls);
    }

    public static List<MediaInfo> images(String... urls) {
        return mediaHelper.images(urls);
    }

    public static MediaInfo video(String url, int placeholderViewId) {
        return mediaHelper.video(url, placeholderViewId);
    }
}
