package me.urakalee.markdown

import me.urakalee.markdown.handler.HeaderHandler
import me.urakalee.markdown.handler.ListHandler
import me.urakalee.markdown.handler.NoneHandler

/**
 * @author Uraka.Lee
 */
enum class Mark(val pattern: Regex, val defaultMark: String, val handler: MarkHandler) {

    NONE(Regex(""), "", NoneHandler),
    H(Regex("#+"), "#", HeaderHandler),
    LI(Regex("[-*]"), "-", ListHandler);
//    LO(Regex("\\d\\."), null),
//    LA(Regex("[a-z]\\."), null);

    companion object {

        private fun fromString(s: String): Mark {
            return values().firstOrNull {
                s.matches(it.pattern)
            } ?: NONE
        }

        fun handle(inputMark: Mark, sourceMark: String): String {
            return Mark.fromString(sourceMark).handler.handleMark(inputMark, sourceMark)
        }
    }
}
