package com.abhi.gcsfortasker

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test


class PatternMatchingTest {
    //Testng examples from [https://tasker.joaoapps.com/userguide/en/matching.html]
    @Test
    fun testExamples() {
        //help matches help but not helper.
        assertTrue(InputMatching().matchStrings("help", "help", false))
        assertFalse(InputMatching().matchStrings("helper", "help", false))
        //help* matches helper
        assertTrue(InputMatching().matchStrings("helper", "help*", false))
        //*the* matches the (anywhere)
        assertTrue(InputMatching().matchStrings("the other way and the first way", "*the*", false))
        //123+ matches 123 and minimally one more character
        assertTrue(InputMatching().matchStrings("12345", "123+", false))
        //+ matches anything with at least one character (non-empty)
        assertTrue(InputMatching().matchStrings("12345", "+", false))
        //the*way matches the other way and the first way, amongst others
        assertTrue(InputMatching().matchStrings("the other way and the first way", "the*way", false))
        //Help/*shell matches Help or anything ending with shell, case-sensitively
        assertTrue(InputMatching().matchStrings("Help or anything ending with shell", "Help/*shell", false))
        assertTrue(InputMatching().matchStrings("anything ending with shell", "Help/*shell", false))
        assertFalse(InputMatching().matchStrings("Help or anything", "Help/*shell", false))
    }

    @Test
    fun testMatchingRules(){
        // if a pattern is left blank, it will match against anything
        assertTrue(InputMatching().matchStrings("anything ending with shell", "", false))
        // if it is not blank it must match the whole target text
        assertTrue(InputMatching().matchStrings("anything ending with shell", "anything ending with shell", false))
        // `/` means 'or', it divides up multiple possible matches
        assertTrue(InputMatching().matchStrings("anything ending with shell", "Help/*shell", false))
        // a * will match any number of any character. It's not possible to specifically match a * character.
        //assertTrue(InputMatching().matchStrings("abcdefghijklmnopqsrtuvwxyz", "*z*", false))
        // a + will match one or more of any character. It's not possible to specifically match a + character.
        assertFalse(InputMatching().matchStrings("abcdefghijklmnopqsrtuvwxyz", "*z+", false))
        // Beware: the condition '%var matches +' will be true if %var has not been assigned a value, because Tasker does not replace variables which do not have a value.
        assertTrue(InputMatching().matchStrings("%var", "*", false))
        // matching is case-insensitive (magic will match with MagiC) unless the pattern contains an upper-case letter e.g. Magic* will not match against magically, but it will match against Magic Roundabout
        assertTrue(InputMatching().matchStrings("MagiC", "magic", false))
        //assertFalse(InputMatching().matchStrings("magically", "Magic*", false))
        assertTrue(InputMatching().matchStrings("Magic Roundabout", "Magic*", false))
        //  a ! at the very start of a match means not e.g. !*Magic*/*Yellow* matches anything not containing the words Magic or Yellow
        assertFalse(InputMatching().matchStrings("anything not containing the words Magic or Yellow", " !*Magic*/*Yellow*", false))
        assertFalse(InputMatching().matchStrings("the quick brown fox", " !*Magic*/*Yellow*", false))

    }
    @Test
    fun testRegexMatch() {
        assertTrue(InputMatching().matchStrings("help", "hel.", true))
        assertFalse(InputMatching().matchStrings("help", "he.", true))
        assertTrue(InputMatching().matchStrings("https://tasker.joaoapps.com/userguide/en/matching.html", "^http\\S+\\.html", true)) //text starts with http and ends in .html and should not contain white space.

    }
}
