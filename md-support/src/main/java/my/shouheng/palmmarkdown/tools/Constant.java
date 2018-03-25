package my.shouheng.palmmarkdown.tools;

/**
 * Created by shouh on 2018/3/25. */
public class Constant {

    public final static String SHOW_PHOTO_JS = "function () {\n" +
            "    var imgs = document.getElementsByTagName(\"img\");\n" +
            "    var list = new Array();\n" +
            "    for(var i = 0; i < imgs.length; i++){\n" +
            "        list[i] = imgs[i].src;\n" +
            "    }\n" +
            "    for(var i = 0; i < imgs.length; i++){\n" +
            "        imgs[i].onclick = function() {\n" +
            "            jsCallback.showPhotosInGallery(this.src, list);\n" +
            "        }\n" +
            "    }\n" +
            "}";
}
