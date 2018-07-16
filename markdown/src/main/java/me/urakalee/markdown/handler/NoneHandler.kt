package me.urakalee.markdown.handler

import me.urakalee.markdown.MarkHandler

/**
 * empty 在文字顶头时会出现, 需要多加一个空格
 *
 * @author Uraka.Lee
 */
object NoneHandler : MarkHandler {

    override fun handleHeader(sourceMark: String): String {
        return if (sourceMark.isEmpty()) "# " else "# $sourceMark"
    }
}