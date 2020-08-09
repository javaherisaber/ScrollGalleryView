# ScrollGalleryView
[![](https://jitpack.io/v/javaherisaber/ScrollGalleryView.svg)](https://jitpack.io/#javaherisaber/ScrollGalleryView)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-ScrollGalleryView-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/2472)

ScrollGalleryView is a flexible library which helps you to create awesome media galleries in your Android application. It's easily integrated with the most popular image loading libraries such as Picasso, Glide and Fresco.

![ScrollGalleryView](http://i.imgur.com/xrBt4Xx.gif)

## Key features

- Easy way to select images in gallery (thumbnails)
- Zooming
- Simple API
- Video

## Installing

Add [JitPack](https://jitpack.io) repository to your root `build.gradle`:

```gradle
allprojects {
    repositories {
        // ...
        maven { url 'https://jitpack.io' }
    }
}
```

Add dependency to your module `build.gradle`:

```gradle
implementation "com.github.javaherisaber:ScrollGalleryView:$versions.scrollGalleryView" // all libs (including media loaders)
implementation "com.github.javaherisaber.ScrollGalleryView:library:$versions.scrollingGalleryView" // core library only
```

### MediaLoaders

There are several MediaLoaders implementations for most popular caching libraries: Picasso, Glide, Fresco.

#### Picasso

```gradle
implementation "com.github.javaherisaber.ScrollGalleryView:picasso-loader:$versions.scrollingGalleryView"
```

#### Glide

```gradle
implementation "com.github.javaherisaber.ScrollGalleryView:glide-loader:$versions.scrollingGalleryView"
```

#### Fresco

```gradle
implementation "com.github.javaherisaber.ScrollGalleryView:fresco-loader:$versions.scrollingGalleryView"
```

## Usage

Add *ScrollGalleryView* to your layout:

```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    <com.veinhorn.scrollgalleryview.ScrollGalleryView
        android:id="@+id/scroll_gallery_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="#000"/>
</LinearLayout>
```

Initialize *ScrollGalleryView* in your activity:

```java
import static com.veinhorn.scrollgalleryview.loader.picasso.DSL.*; // simplifies adding media

public class MainActivity extends FragmentActivity {
    private ScrollGalleryView galleryView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ScrollGalleryView galleryView = findViewById(R.id.scroll_gallery_view);
        galleryView.setFragmentManager(getSupportFragmentManager())
                .setThumbnailSize(150)
                .setZoom(true)
                .withHiddenThumbnails(false)
                .hideThumbnailsOnClick(true)
                .setSelectedIndex(2)
                .addMedia(image("https://i.picsum.photos/id/249/960/1080.jpg?hmac=M1eYv2hPaxVrc1jgoI4o9r5YJi_Xm9kMEbaxFfQn-yU"))
                .addMedia(image("https://i.picsum.photos/id/803/960/1080.jpg?hmac=HFNjRwjV8mL2POgQh45zzTc1MkBU55vgxbpwJeg7utk"))
                .addMedia(image("https://i.picsum.photos/id/976/960/1080.jpg?hmac=Osd25GIpQNNUZe7y__Leq_FL83JmeoiOzVdn2CrE7DM"))
                .addMedia(image("https://i.picsum.photos/id/593/960/1080.jpg?hmac=vFbBrBQQUKOhqDcf_1fMvKs6KzlF21qqS04X2tzbumo"))
                .addMedia(image("https://i.picsum.photos/id/932/960/1080.jpg?hmac=DTJwQ2y6OSfcuFI6mY8rpmtWjliCxqzP1-NsVC-nICM"))
                .build();
    }
}
```

If you use *ScrollGalleryView* version prior `1.2.0` or need more info about gallery initialization you can find it [here](docs/init-gallery.md).

### Adding media

ScrollGalleryView supports different types of media such as images and videos. You can create image gallery, video gallery, or mix them in any way. To abstract from concreate way of image loading ScrollGalleryView uses [MediaLoader](library/src/main/java/com/veinhorn/scrollgalleryview/loader/MediaLoader.java) so it makes possible to use different image loading libraries depending on your needs (Picasso, Glide, Fresco).

#### **Picasso** loader

#### Default image loader

Library is also contains default image loader but it's not optimized for performance.

> Note: it's highly recommended to use custom image loader against default

### Configuration

You can specify a bunch of additional settings during gallery initialization.

|Option|Method|Description|
|------|------|-----------|
| Thumbnail size | `.setThumbnailSize(200)` | You can configure thumbnails size in |
| Zoom | `.setZoom(true)` | Enable zoom |
| Hide thumbnails | `.withHiddenThumbnails(false)` | Hide scroll view container with thumbnails on the bottom of screen |
| Hide thumbnails on click | `.hideThumbnailsOnClick(true)` | Hide scroll view container with thumbnails when you click on main image area |

### Adding listeners

*ScrollGalleryView* supports adding listeners for events like: image click, long image click, changed page. More details you can find in [separate doc](docs/event-listeners.md).

## Sample application

The sample application published on Google Play.

[![Get it on Google Play](http://www.android.com/images/brand/get_it_on_play_logo_small.png)](https://play.google.com/store/apps/details?id=com.veinhorn.scrollgalleryview)

## License

    MIT License
    
    Copyright (c) 2019 Boris Korogvich
    
    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:
    
    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.
    
    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
