package my.shouheng.palmmarkdown.strategy

import android.widget.EditText
import me.urakalee.markdown.Indent
import me.urakalee.markdown.Mark
import me.urakalee.ranger.extension.selectedLine

/**
 * @author Uraka.Lee
 */
class DayOneStrategy : DefaultStrategy() {

    // XXX: \n 和 \r\n 对于 selectionStart 的影响相同吗?
    override fun h1(source: String, selectionStart: Int, selectionEnd: Int, selection: String?, editor: EditText?) {
        val (targetLine, start, end) = source.selectedLine(selectionStart, selectionEnd)
        // parse 出前面格式(header, list, task, quote)之外的文字
        val (mark, indent, content) = detectMark(targetLine)
        val newMark = Mark.handle(Mark.H, mark)
        editor?.text?.replace(start, start + mark.length, newMark)
        editor?.setSelection(end + newMark.length - mark.length) // 简单处理, 光标放在行尾
    }

    override fun normalList(source: String, selectionStart: Int, selectionEnd: Int, editor: EditText?) {
        val (targetLine, start, end) = source.selectedLine(selectionStart, selectionEnd)
        // parse 出前面格式(header, list, task, quote)之外的文字
        val (mark, indent, content) = detectMark(targetLine)
        val newMark = Mark.handle(Mark.LI, mark)
        editor?.text?.replace(start, start + mark.length, newMark)
        editor?.setSelection(end + newMark.length - mark.length) // 简单处理, 光标放在行尾
    }

    /**
     * @return mark, indent, content
     */
    private fun detectMark(line: String): Triple<String, Indent, String> {
        // TODO: 处理 indent
        val firstBlank = line.indexOf(' ')
        if (firstBlank == -1) {
            return Triple("", Indent(null), line)
        } else {
            val mark = line.substring(0 until firstBlank)
            val content = line.substring(firstBlank + 1)
            return Triple(mark, Indent(null), content)
        }
    }
}