package com.veinhorn.example;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.veinhorn.scrollgalleryview.MediaInfo;
import com.veinhorn.scrollgalleryview.ScrollGalleryView;
import ogbe.ozioma.com.glideimageloader.GlideImageLoader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ScrollGalleryView scrollGalleryView = findViewById(R.id.scroll_gallery_view);
        scrollGalleryView.setFragmentManager(getSupportFragmentManager())
                .setThumbnailSize(150)
                .setZoom(true)
                .withHiddenThumbnails(false)
                .hideThumbnailsOnClick(true)
                .setSelectedIndex(2)
                .addMedia(MediaInfo.mediaLoader(new GlideImageLoader("https://i.picsum.photos/id/249/960/1080.jpg?hmac=M1eYv2hPaxVrc1jgoI4o9r5YJi_Xm9kMEbaxFfQn-yU")))
                .addMedia(MediaInfo.mediaLoader(new GlideImageLoader("https://i.picsum.photos/id/803/960/1080.jpg?hmac=HFNjRwjV8mL2POgQh45zzTc1MkBU55vgxbpwJeg7utk")))
                .addMedia(MediaInfo.mediaLoader(new GlideImageLoader("https://i.picsum.photos/id/976/960/1080.jpg?hmac=Osd25GIpQNNUZe7y__Leq_FL83JmeoiOzVdn2CrE7DM")))
                .addMedia(MediaInfo.mediaLoader(new GlideImageLoader("https://i.picsum.photos/id/593/960/1080.jpg?hmac=vFbBrBQQUKOhqDcf_1fMvKs6KzlF21qqS04X2tzbumo")))
                .addMedia(MediaInfo.mediaLoader(new GlideImageLoader("https://i.picsum.photos/id/932/960/1080.jpg?hmac=DTJwQ2y6OSfcuFI6mY8rpmtWjliCxqzP1-NsVC-nICM")))
                .build();

        scrollGalleryView.closeView.setOnClickListener(view -> finish());
    }
}
