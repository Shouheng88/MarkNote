package me.shouheng.commons;

/**
 * Created on 2018/11/28.
 */
public interface BaseConstants {
    String MIME_TYPE_IMAGE = "image/jpeg";
    String MIME_TYPE_AUDIO = "audio/amr";
    String MIME_TYPE_VIDEO = "video/mp4";
    String MIME_TYPE_SKETCH = "image/png";
    String MIME_TYPE_FILES = "file/*";
    String MIME_TYPE_HTML = "text/html";
    String MIME_TYPE_IMAGE_EXTENSION = ".jpeg";
    String MIME_TYPE_SKETCH_EXTENSION = ".png";
    String MIME_TYPE_CONTACT_EXTENSION = ".vcf";

    /**
     * The max length used to get the title form the web page, used in
     * {@link me.shouheng.commons.fragment.WebviewFragment} to get the title from the page.
     * If the title is longer than this value, the longer part will be replaced with '...'.
     */
    int MAX_WEB_PAGE_TITLE_LENGTH = 15;
}
