package me.urakalee.markdown

/**
 * @author Uraka.Lee
 */
interface MarkHandler {

    /**
     * @return targetMark on input [Mark.H]
     */
    fun handleHeader(sourceMark: String): String

    /**
     * @return targetMark
     */
    fun handleMark(inputMark: Mark, sourceMark: String): String {
        return when (inputMark) {
            Mark.H -> handleHeader(sourceMark)
            else -> sourceMark
        }
    }
}