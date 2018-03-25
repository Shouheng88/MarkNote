package my.shouheng.palmmarkdown;

import android.content.ClipboardManager;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;

import java.util.Stack;

import my.shouheng.palmmarkdown.strategy.DefaultStrategy;
import my.shouheng.palmmarkdown.strategy.MdParseStrategy;
import my.shouheng.palmmarkdown.tools.MarkdownEffect;

/**
 * Created by wangshouheng on 2017/6/29.*/
public class MarkdownEditor extends android.support.v7.widget.AppCompatEditText {

    /* 操作序号(一次编辑可能对应多个操作，如替换文字，就是删除+插入) */
    private int index;

    /* 撤销栈 */
    private Stack<Action> history = new Stack<>();

    /* 恢复栈 */
    private Stack<Action> historyBack = new Stack<>();

    private boolean formatPasteEnable = true;

    private boolean flag = false;

    private MdParseStrategy mdParseStrategy = new DefaultStrategy();

    public MarkdownEditor(Context context) {
        super(context);
        init();
    }

    public MarkdownEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MarkdownEditor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        this.addTextChangedListener(new InputTextWatcher());
        this.addTextChangedListener(new ActionWatcher());
    }

    protected void onEditableChanged(Editable s) {}

    protected void onTextChanged(Editable s) {}

    public final void addEffect(MarkdownEffect markdownEffect) {
        String source = this.getText().toString();
        int selectionStart = getSelectionStart();
        int selectionEnd = getSelectionEnd();
        String selection = source.substring(selectionStart, selectionEnd);
        switch (markdownEffect){
            case H1:
                mdParseStrategy.h1(source, selectionStart, selectionEnd, selection, this);
                break;
            case H2:
                mdParseStrategy.h2(source, selectionStart, selectionEnd, selection, this);
                break;
            case H3:
                mdParseStrategy.h3(source, selectionStart, selectionEnd, selection, this);
                break;
            case H4:
                mdParseStrategy.h4(source, selectionStart, selectionEnd, selection, this);
                break;
            case H5:
                mdParseStrategy.h5(source, selectionStart, selectionEnd, selection, this);
                break;
            case H6:
                mdParseStrategy.h6(source, selectionStart, selectionEnd, selection, this);
                break;
            case QUOTE:
                mdParseStrategy.quote(source, selectionStart, selectionEnd, selection, this);
                break;
            case BOLD:
                mdParseStrategy.bold(source, selectionStart, selectionEnd, selection, this);
                break;
            case ITALIC:
                mdParseStrategy.italic(source, selectionStart, selectionEnd, selection, this);
                break;
            case CODE_BLOCK:
                mdParseStrategy.codeBlock(source, selectionStart, selectionEnd, selection, this);
                break;
            case STRIKE:
                mdParseStrategy.strike(source, selectionStart, selectionEnd, selection, this);
                break;
            case H_LINE:
                mdParseStrategy.horizontalLine(source, selectionStart, selectionEnd, selection, this);
                break;
            case XML:
                mdParseStrategy.xml(source, selectionStart, selectionEnd, selection, this);
                break;
            case LINK:
                mdParseStrategy.h1(source, selectionStart, selectionEnd, selection, this);
                break;
            case TABLE:
                mdParseStrategy.h1(source, selectionStart, selectionEnd, selection, this);
                break;
            case NORMAL_LIST:
                mdParseStrategy.normalList(source, selectionStart, selectionEnd, this);
                break;
            case NUMBER_LIST:
                mdParseStrategy.numberList(source, selectionStart, selectionEnd, this);
                break;
            case SUB:
                mdParseStrategy.sub(source, selectionStart, selectionEnd, selection, this);
                break;
            case SUP:
                mdParseStrategy.sup(source, selectionStart, selectionEnd, selection, this);
                break;
        }
    }

    public final void addTableEffect(int cols, int rows) {
        String source = this.getText().toString();
        int selectionStart = getSelectionStart();
        int selectionEnd = getSelectionEnd();
        mdParseStrategy.table(source, selectionStart, selectionEnd, rows, cols, this);
    }

    public final void addLinkEffect(MarkdownEffect markdownEffect, String title, String link) {
        String source = this.getText().toString();
        int selectionStart = getSelectionStart();
        int selectionEnd = getSelectionEnd();
        switch (markdownEffect){
            case LINK:
                mdParseStrategy.link(source, selectionStart, selectionEnd, title, link, this);
                break;
            case IMAGE:
                mdParseStrategy.image(source, selectionStart, selectionEnd, title, link, this);
                break;
        }
    }

    public final void clearHistory() {
        history.clear();
        historyBack.clear();
    }

    public final void undo() {
        if (history.empty()) return;
        // 锁定操作
        flag = true;
        Action action = history.pop();
        historyBack.push(action);
        if (action.isAdd) {
            // 撤销添加
            getText().delete(action.startCursor, action.startCursor + action.actionTarget.length());
            this.setSelection(action.startCursor, action.startCursor);
        } else {
            // 插销删除
            getText().insert(action.startCursor, action.actionTarget);
            if (action.endCursor == action.startCursor) {
                this.setSelection(action.startCursor + action.actionTarget.length());
            } else {
                this.setSelection(action.startCursor, action.endCursor);
            }
        }
        // 释放操作
        flag = false;
        // 判断是否是下一个动作是否和本动作是同一个操作，直到不同为止
        if (!history.empty() && history.peek().index == action.index) {
            undo();
        }
    }

    public final void redo() {
        if (historyBack.empty()) return;
        flag = true;
        Action action = historyBack.pop();
        history.push(action);
        if (action.isAdd) {
            // 恢复添加
            getText().insert(action.startCursor, action.actionTarget);
            this.setSelection(action.startCursor, action.endCursor);
        } else {
            // 恢复删除
            getText().delete(action.startCursor, action.startCursor + action.actionTarget.length());
            this.setSelection(action.startCursor, action.startCursor);
        }
        flag = false;
        // 判断是否是下一个动作是否和本动作是同一个操作
        if (!historyBack.empty() && historyBack.peek().index == action.index) {
            redo();
        }
    }

    public void setFormatPasteEnable(boolean enable) {
        formatPasteEnable = enable;
    }

    public final void setDefaultText(CharSequence text) {
        clearHistory();
        flag = true;
        getText().replace(0, getText().length(), text);
        flag = false;
    }

    public void setMdParseStrategy(MdParseStrategy mdParseStrategy) {
        this.mdParseStrategy = mdParseStrategy;
    }

    private class InputTextWatcher implements TextWatcher {

        /**
         * @param s     the s
         * @param start the start 起始光标
         * @param count the count 选择数量
         * @param after the after 替换增加的文字数 */
        @Override
        public final void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (flag) return;
            int end = start + count;
            if (end > start && end <= s.length()) {
                CharSequence charSequence = s.subSequence(start, end);
                if (charSequence.length() > 0) {
                    onSubText(s, charSequence, start);
                }
            }
        }

        /**
         * @param start  the start 起始光标
         * @param before the before 选择数量
         * @param count  the count 添加的数量 */
        @Override
        public final void onTextChanged(CharSequence s, int start, int before, int count) {
            if (flag) return;
            int end = start + count;
            if (end > start) {
                CharSequence charSequence = s.subSequence(start, end);
                if (charSequence.length() > 0) {
                    onAddText(s, charSequence, start);
                }
            }
        }

        @Override
        public final void afterTextChanged(Editable s) {
            if (flag) return;
        }
    }

    private class ActionWatcher implements TextWatcher {

        /**
         * @param s     the s
         * @param start the start 起始光标
         * @param count the endCursor 选择数量
         * @param after the after 替换增加的文字数 */
        @Override
        public final void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (flag) return;
            int end = start + count;
            if (end > start && end <= s.length()) {
                CharSequence charSequence = s.subSequence(start, end);
                // 删除了文字
                if (charSequence.length() > 0) {
                    Action action = new Action(charSequence, start, false);
                    if (count > 1) {
                        // 如果一次超过一个字符，说名用户选择了，然后替换或者删除操作
                        action.setSelectCount(count);
                    } else if (count == 1 && count == after) {
                        // 一个字符替换
                        action.setSelectCount(count);
                    }
                    // 还有一种情况:选择一个字符,然后删除(暂时没有考虑这种情况)
                    history.push(action);
                    historyBack.clear();
                    action.setIndex(++index);
                }
            }
        }

        /**
         * On text changed.
         *
         * @param s      the s
         * @param start  the start 起始光标
         * @param before the before 选择数量
         * @param count  the endCursor 添加的数量 */
        @Override
        public final void onTextChanged(CharSequence s, int start, int before, int count) {
            if (flag) return;
            int end = start + count;
            if (end > start) {
                CharSequence charSequence = s.subSequence(start, end);
                // 添加文字
                if (charSequence.length() > 0) {
                    Action action = new Action(charSequence, start, true);
                    history.push(action);
                    historyBack.clear();
                    if (before > 0) {
                        // 文字替换（先删除再增加），删除和增加是同一个操作，所以不需要增加序号
                        action.setIndex(index);
                    } else {
                        action.setIndex(++index);
                    }
                }
            }
        }

        @Override
        public final void afterTextChanged(Editable s) {
            if (flag) return;
            if (s != getText()) {
                onEditableChanged(s);
            }
            MarkdownEditor.this.onTextChanged(s);
        }

    }

    private void onAddText(CharSequence source, CharSequence charSequence, int start) {
        flag = true;
        if ("\n".equals(charSequence.toString())) {
            performAddEnter(getText(), source, start);
        }
        flag = false;
    }

    private void onSubText(CharSequence source, CharSequence charSequence, int start) {
        flag = true;
        // 操作代码
        flag = false;
    }

    private void performAddEnter(Editable editable, CharSequence source, int start) {
        // 获取回车之前的字符
        String tempStr = source.subSequence(0, start).toString();
        // 查找最后一个回车
        int lastEnter = tempStr.lastIndexOf(10);
        if (lastEnter > 0) {
            // 最后一个回车到输入回车之间的字符
            tempStr = tempStr.substring(lastEnter + 1, start);
        }

        String mString = tempStr.trim();
        String startSpace = getStartChar(tempStr, ' ');

        if (mString.startsWith("* ") && mString.length() > 2) {// * 开头
            editable.insert(start + 1, startSpace + "* ");
        } else if (mString.startsWith("1. ") && mString.length() > 3) {// 1. 开头
            editable.insert(start + 1, startSpace + "1. ");
        } else if (mString.length() > 1) {
            editable.insert(start + 1, startSpace);
        }
    }

    private String getStartChar(String target, char startChar) {
        StringBuilder sb = new StringBuilder();
        char[] chars = target.toCharArray();
        for (char aChar : chars) {
            if (aChar == startChar) {
                sb.append(startChar);
            } else {
                break;
            }
        }
        return sb.toString();
    }

    private class Action {

        /**
         * 改变字符 */
        CharSequence actionTarget;

        /**
         * 光标位置 */
        int startCursor, endCursor;

        /**
         * 标志增加操作 */
        boolean isAdd;

        /**
         * 操作序号 */
        int index;

        Action(CharSequence actionTag, int startCursor, boolean add) {
            this.actionTarget = actionTag;
            this.startCursor = startCursor;
            this.endCursor = startCursor;
            this.isAdd = add;
        }

        void setSelectCount(int count) {
            this.endCursor = endCursor + count;
        }

        void setIndex(int index) {
            this.index = index;
        }
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        switch (id) {
            case android.R.id.paste:
                if (!formatPasteEnable) break;
                ClipboardManager clip = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                String content;
                if (clip != null && !TextUtils.isEmpty(content = clip.getText().toString()))
                    clip.setText(content.replace("\t", "    "));
                break;
        }
        return super.onTextContextMenuItem(id);
    }
}
