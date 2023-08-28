package com.abhi.gcsfortasker

class InputMatching {
    fun matchStrings(input: String, pattern: String, useRegex: Boolean): Boolean {
        return if (useRegex) {
            input.matches(Regex(pattern))
        } else {
            when {
                pattern.isEmpty() -> true // empty pattern matches anything
                pattern == input -> true // exact match
                pattern.startsWith("!") -> !input.simpleMatching(pattern.substring(1))
                pattern.contains("/") -> {
                    // split pattern by "/" and check if at least one part matches input
                    val parts = pattern.split("/")
                    parts.any { part -> input.simpleMatching(part) }
                }

                else -> input.simpleMatching(pattern)
            }
        }
    }

    private fun String.simpleMatching(pattern: String): Boolean {
        var index = 0
        var p = pattern
        while (index < length && p.isNotEmpty()) {
            val c = get(index)
            when {
                p.startsWith("*") -> {
                    p = p.substring(1)
                    if (p.isEmpty()) return true // "*" at end matches anything
                    while (index < length) {
                        if (substring(index).simpleMatching(p)) return true // match remaining input against remaining pattern
                        index++
                    }
                    return false // no match found
                }

                p.startsWith("+") -> {
                    p = p.substring(1)
                    if (p.isEmpty()) return true // "+" at end matches anything
                    if (index == length) return false // "+" cannot match an empty string
                    while (index < length) {
                        if (substring(index).simpleMatching(p)) return true // match remaining input against remaining pattern
                        index++
                    }
                    return false // no match found
                }

                c.equals(p[0], ignoreCase = true) -> {
                    p = p.substring(1)
                    index++
                }

                else -> return false // no match found
            }
        }
        return p.isEmpty() && index == length // pattern matches if it's empty and we've consumed all input
    }
}
