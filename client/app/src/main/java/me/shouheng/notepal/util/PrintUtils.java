package me.shouheng.notepal.util;

import android.content.Context;
import android.print.PrintManager;
import android.webkit.WebView;

import me.shouheng.commons.utils.PalmUtils;
import me.shouheng.notepal.model.Note;

/**
 * Created by wang shouheng on 2017/12/28.*/
public class PrintUtils {

    public static void print(Context ctx, WebView webView, Note note) {
        if (PalmUtils.isKitKat()) {
            PrintManager printManager = (PrintManager) ctx.getSystemService(Context.PRINT_SERVICE);
            if (PalmUtils.isLollipop()) {
                printManager.print("Print Note", webView.createPrintDocumentAdapter(note.getTitle()), null);
                return;
            }
            printManager.print("Print Note", webView.createPrintDocumentAdapter(), null);
        }
    }
}
