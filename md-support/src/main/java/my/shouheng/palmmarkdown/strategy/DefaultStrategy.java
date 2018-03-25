package my.shouheng.palmmarkdown.strategy;

import android.text.TextUtils;
import android.widget.EditText;

/**
 * Created by wangshouheng on 2017/10/7.*/
public class DefaultStrategy implements MdParseStrategy {

    @Override
    public void h1(String source, int selectionStart, int selectionEnd, String selection, EditText editor) {
        String result = isSingleLine(source, selectionStart) ? "# " + selection : "\n# " + selection;
        editor.getText().replace(selectionStart, selectionEnd, result);
        editor.setSelection(selectionStart + result.length());
    }

    @Override
    public void h2(String source, int selectionStart, int selectionEnd, String selection, EditText editor) {
        String result = isSingleLine(source, selectionStart) ? "## " + selection : "\n## " + selection;
        editor.getText().replace(selectionStart, selectionEnd, result);
        editor.setSelection(selectionStart + result.length());
    }

    @Override
    public void h3(String source, int selectionStart, int selectionEnd, String selection, EditText editor) {
        String result = isSingleLine(source, selectionStart) ? "### " + selection : "\n### " + selection;
        editor.getText().replace(selectionStart, selectionEnd, result);
        editor.setSelection(selectionStart + result.length());
    }

    @Override
    public void h4(String source, int selectionStart, int selectionEnd, String selection, EditText editor) {
        String result = isSingleLine(source, selectionStart) ? "#### " + selection : "\n#### " + selection;
        editor.getText().replace(selectionStart, selectionEnd, result);
        editor.setSelection(selectionStart + result.length());
    }

    @Override
    public void h5(String source, int selectionStart, int selectionEnd, String selection, EditText editor) {
        String result = isSingleLine(source, selectionStart) ? "##### " + selection : "\n##### " + selection;
        editor.getText().replace(selectionStart, selectionEnd, result);
        editor.setSelection(selectionStart + result.length());
    }

    @Override
    public void h6(String source, int selectionStart, int selectionEnd, String selection, EditText editor) {
        String result = isSingleLine(source, selectionStart) ? "###### " + selection : "\n###### " + selection;
        editor.getText().replace(selectionStart, selectionEnd, result);
        editor.setSelection(selectionStart + result.length());
    }

    @Override
    public void quote(String source, int selectionStart, int selectionEnd, String selection, EditText editor) {
        String result = isSingleLine(source, selectionStart) ? "> " + selection : "\n>" + selection;
        editor.getText().replace(selectionStart, selectionEnd, result);
        editor.setSelection(selectionStart + result.length());
    }

    @Override
    public void bold(String source, int selectionStart, int selectionEnd, String selection, EditText editor) {
        String result = " **" + selection + "** ";
        if (TextUtils.isEmpty(selection)) {
            editor.getText().replace(selectionStart, selectionEnd, result);
            editor.setSelection(selectionStart + result.length() - 3);
            return;
        }
        editor.getText().replace(selectionStart, selectionEnd, result);
        editor.setSelection(selectionStart + result.length());
    }

    @Override
    public void italic(String source, int selectionStart, int selectionEnd, String selection, EditText editor) {
        String result = " *" + selection + "* ";
        if (TextUtils.isEmpty(selection)) {
            editor.getText().replace(selectionStart, selectionEnd, result);
            editor.setSelection(selectionStart + result.length() - 2);
            return;
        }
        editor.getText().replace(selectionStart, selectionEnd, result);
        editor.setSelection(selectionStart + result.length());
    }

    @Override
    public void codeBlock(String source, int selectionStart, int selectionEnd, String selection, EditText editor) {
        String result = isSingleLine(source, selectionStart) ? "```\n" + selection + "\n```\n" : "\n```\n" + selection + "\n```\n";
        if (TextUtils.isEmpty(selection)) {
            editor.getText().replace(selectionStart, selectionEnd, result);
            editor.setSelection(selectionStart + result.length() - 5);
            return;
        }
        editor.getText().replace(selectionStart, selectionEnd, result);
        editor.setSelection(selectionStart + result.length());
    }

    @Override
    public void strike(String source, int selectionStart, int selectionEnd, String selection, EditText editor) {
        String result = " ~~" + selection + "~~ ";
        if (TextUtils.isEmpty(selection)) {
            editor.getText().replace(selectionStart, selectionEnd, result);
            editor.setSelection(selectionStart + result.length() - 3);
            return;
        }
        editor.getText().replace(selectionStart, selectionEnd, result);
        editor.setSelection(selectionStart + result.length());
    }

    @Override
    public void horizontalLine(String source, int selectionStart, int selectionEnd, String selection, EditText editor) {
        String result = isSingleLine(source, selectionStart) ? "-------\n" : "\n-------\n";
        editor.getText().replace(selectionStart, selectionStart, result);
    }

    @Override
    public void xml(String source, int selectionStart, int selectionEnd, String selection, EditText editor) {
        String result = " `" + selection + "` ";
        if (TextUtils.isEmpty(selection)) {
            editor.getText().replace(selectionStart, selectionEnd, result);
            editor.setSelection(selectionStart + result.length() - 2);
            return;
        }
        editor.getText().replace(selectionStart, selectionEnd, result);
        editor.setSelection(selectionStart + result.length());
    }

    @Override
    public void link(String source, int selectionStart, int selectionEnd, String title, String link, EditText editor){
        String result = title == null ?
                (link == null ? "[]()" : "[](" + link + ")") :
                (link == null ? "[" + title + "]()" : "[" + title + "](" + link + ")");
        editor.getText().insert(selectionStart, result);
        editor.setSelection(selectionStart + result.length());
    }

    @Override
    public void table(String source, int selectionStart, int selectionEnd, int rows, int cols, EditText editor) {
        StringBuilder sb = new StringBuilder();
        int i;

        if (!isTwoSingleLines(source, selectionStart)) {
            sb.append(isSingleLine(source, selectionStart) ? "\n" : "\n\n");
        }

        sb.append("|");
        for (i = 0; i < cols; i++) sb.append(" HEADER |");

        sb.append("\n|");
        for (i = 0; i < cols; i++) sb.append(":----------:|");

        sb.append("\n");
        for (int i2 = 0; i2 < rows; i2++) {
            sb.append("|");
            for (i = 0; i < cols; i++) {
                sb.append("            |");
            }
            sb.append("\n");
        }

        String result = sb.toString();
        editor.getText().insert(selectionStart, result);
        editor.setSelection(selectionStart + result.length());
    }

    @Override
    public void normalList(String source, int selectionStart, int selectionEnd, EditText editor) {
        insertList("* ", source, selectionStart, selectionEnd, editor);
    }

    @Override
    public void numberList(String source, int selectionStart, int selectionEnd, EditText editor) {
        insertList("1. ", source, selectionStart, selectionEnd, editor);
    }

    @Override
    public void image(String source, int selectionStart, int selectionEnd, String title, String imgUri, EditText editor) {
        imgUri = TextUtils.isEmpty(imgUri) ? "" : imgUri;

        String result = isSingleLine(source, selectionStart) ? "![" + title + "](" + imgUri + ")"
                : "\n![" + title + "](" + imgUri + ")";

        editor.getText().insert(selectionStart, result);
        editor.setSelection(TextUtils.isEmpty(imgUri) ? result.length() + selectionStart - 1
                : result.length() + selectionStart);
    }

    @Override
    public void mark(String source, int selectionStart, int selectionEnd, EditText editor) {}

    @Override
    public void checkbox(String source, int selectionStart, int selectionEnd, String name, boolean isChecked, EditText editor) {}

    @Override
    public void mathJax(String source, int selectionStart, int selectionEnd, String exp, boolean inline, EditText editor) {}

    @Override
    public void sub(String source, int selectionStart, int selectionEnd, String selection, EditText editor) {
        String result = "~" + selection + "~";
        if (TextUtils.isEmpty(selection)) {
            editor.getText().replace(selectionStart, selectionEnd, result);
            editor.setSelection(selectionStart + result.length() - 1);
            return;
        }
        editor.getText().replace(selectionStart, selectionEnd, result);
        editor.setSelection(selectionStart + result.length());
    }

    @Override
    public void sup(String source, int selectionStart, int selectionEnd, String selection, EditText editor) {
        String result = "^" + selection + "^";
        if (TextUtils.isEmpty(selection)) {
            editor.getText().replace(selectionStart, selectionEnd, result);
            editor.setSelection(selectionStart + result.length() - 1);
            return;
        }
        editor.getText().replace(selectionStart, selectionEnd, result);
        editor.setSelection(selectionStart + result.length());
    }

    @Override
    public void footNote(String source, int selectionStart, int selectionEnd, EditText editor) {}

    private void insertList(String listType, String source, int selectionStart, int selectionEnd, EditText editor){
        String substring = source.substring(0, selectionStart);
        int line = substring.lastIndexOf(10);

        selectionStart = line == -1 ? 0 : line + 1;

        substring = source.substring(selectionStart, selectionEnd);

        String[] split = substring.split("\n");
        StringBuilder sb = new StringBuilder();

        if (split.length > 0) {
            for (String s : split) {
                if (s.length() == 0 && sb.length() != 0) {
                    sb.append("\n");
                    continue;
                }
                if (!s.trim().startsWith(listType)) {
                    if (sb.length() > 0) sb.append("\n");
                    sb.append(listType).append(s);
                } else {
                    if (sb.length() > 0) sb.append("\n");
                    sb.append(s);
                }
            }
        }

        if (sb.length() == 0) sb.append(listType);
        editor.getText().replace(selectionStart, selectionEnd, sb.toString());
        editor.setSelection(sb.length() + selectionStart);
    }

    private boolean isSingleLine(String source, int selectionStart) {
        if (source.isEmpty()) return true;
        source = source.substring(0, selectionStart);
        return source.length() == 0 || source.charAt(source.length() - 1) == 10;
    }

    private boolean isTwoSingleLines(String source, int selectionStart) {
        source = source.substring(0, selectionStart);
        return source.length() >= 2
                && source.charAt(source.length() - 1) == 10
                && source.charAt(source.length() - 2) == 10;
    }
}
