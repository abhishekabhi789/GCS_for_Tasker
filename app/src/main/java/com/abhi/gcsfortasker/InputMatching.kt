package com.abhi.gcsfortasker
/**Won't be accurate, but follows the rules.
 * @see <a href=”https://tasker.joaoapps.com/userguide/en/matching”>Tasker Pattern Matching</a> */
object InputMatching {
    fun matchStrings(input: String, pattern: String, useRegex: Boolean): Boolean {
        return if (useRegex) {
            input.matches(Regex(pattern))
        } else {
            when {
                pattern.isEmpty() -> true // empty pattern matches anything
                pattern.equals(input,true) -> true // not blank it must match the whole target
                pattern.contains("/") -> { // '/' means 'or', it divides up multiple possible matches
                    // split pattern by "/" and check if at least one part matches input
                    if (pattern.startsWith("!")) {
                        //a ! at the very start of a match means not
                        val parts = pattern.drop(1).split("/")
                        parts.all { part -> !input.simpleMatching(part) }
                    } else {
                        pattern.split("/").any { part -> input.simpleMatching(part) }
                    }
                }
                pattern.startsWith("!") -> !input.simpleMatching(pattern.substring(1))
                else -> input.simpleMatching(pattern)
            }
        }
    }

    private fun String.simpleMatching(pattern: String): Boolean {
        var rPattern = pattern
        // a * will match any number of any character.
        rPattern = rPattern.replace("*", ".*")
        // a + will match one or more of any character.
        rPattern = rPattern.replace("+", ".+")
        return matches(Regex(rPattern))
    }
}
