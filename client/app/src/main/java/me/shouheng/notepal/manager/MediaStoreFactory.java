package me.shouheng.notepal.manager;

import android.net.Uri;
import android.provider.MediaStore;

public class MediaStoreFactory {

    private static MediaStoreFactory instance;

    public static MediaStoreFactory getInstance(){
        if (instance == null) {
            synchronized (MediaStoreFactory.class){
                if (instance == null) {
                    instance = new MediaStoreFactory();
                }
            }
        }
        return instance;
    }

    public Uri createURI(String type){
        switch (type) {
            case "image":
                return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            case "video":
                return  MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            case "audio":
                return  MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }
        return null;
    }
}
