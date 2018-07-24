package me.urakalee.markdown

/**
 * @author Uraka.Lee
 */
interface MarkHandler {

    /**
     * @return targetMark on input [Mark.H]
     */
    fun handleHeader(inputMark: Mark, source: String, sourceMark: Mark): String {
        return defaultMark(inputMark)
    }

    /**
     * @return targetMark on input [Mark.LI]
     */
    fun handleList(inputMark: Mark, source: String, sourceMark: Mark): String {
        return defaultMark(inputMark)
    }

    /**
     * @return targetMark on input [Mark.TD]
     */
    fun handleTodo(inputMark: Mark, source: String, sourceMark: Mark): String {
        return defaultMark(inputMark)
    }

    /**
     * @return targetMark on input [Mark.QT]
     */
    fun handleQuote(inputMark: Mark, source: String, sourceMark: Mark): String {
        return defaultMark(inputMark)
    }

    private fun defaultMark(mark: Mark): String {
        return mark.defaultMark
    }

    /**
     * @return targetMark
     */
    fun handleMark(inputMark: Mark, source: String, sourceMark: Mark): String {
        return when (inputMark) {
            Mark.H -> handleHeader(inputMark, source, sourceMark)
            Mark.LI -> handleList(inputMark, source, sourceMark)
            Mark.TD -> handleTodo(inputMark, source, sourceMark)
            Mark.QT -> handleQuote(inputMark, source, sourceMark)
            else -> source
        }
    }
}