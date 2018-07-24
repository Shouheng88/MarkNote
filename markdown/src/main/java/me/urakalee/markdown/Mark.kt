package me.urakalee.markdown

import me.urakalee.markdown.handler.*

/**
 * @author Uraka.Lee
 */
enum class Mark(val pattern: Regex, val defaultMark: String, val handler: MarkHandler) {

    NONE(Regex(""), "", NoneHandler),
    H(Regex("#+"), "#", HeaderHandler),
    LI(Regex("[-*]"), "-", ListHandler),
    LO(Regex("\\d\\."), "1.", ListHandler),
    LA(Regex("[a-z]\\."), "a.", ListHandler),
    TD(Regex("- \\[[x ]]", RegexOption.IGNORE_CASE), "- [ ]", TodoHandler),
    QT(Regex(">"), ">", QuoteHandler);

    companion object {

        private fun fromString(s: String): Mark {
            return values().firstOrNull {
                s.matches(it.pattern)
            } ?: NONE
        }

        fun handle(inputMark: Mark, source: String): String {
            return Mark.fromString(source).let {
                it.handler.handleMark(inputMark, source, it)
            }
        }
    }
}
