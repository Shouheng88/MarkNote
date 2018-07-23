package me.urakalee.markdown

/**
 * @author Uraka.Lee
 */
interface MarkHandler {

    /**
     * @return targetMark on input [Mark.H]
     */
    fun handleHeader(source: String, sourceMark: Mark): String {
        return Mark.H.defaultMark
    }

    /**
     * @return targetMark on input [Mark.LI]
     */
    fun handleList(source: String, sourceMark: Mark): String {
        return Mark.LI.defaultMark
    }

    /**
     * @return targetMark
     */
    fun handleMark(inputMark: Mark, source: String, sourceMark: Mark): String {
        return when (inputMark) {
            Mark.H -> handleHeader(source, sourceMark)
            Mark.LI -> handleList(source, sourceMark)
            else -> source
        }
    }
}