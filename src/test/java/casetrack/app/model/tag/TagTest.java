package casetrack.app.model.tag;

import static casetrack.app.testutil.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TagTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Tag(null));
    }

    @Test
    public void constructor_invalidTagName_throwsIllegalArgumentException() {
        String invalidTagName = "";
        assertThrows(IllegalArgumentException.class, () -> new Tag(invalidTagName));
    }

    @Test
    public void isValidTagName() {
        // null tag name
        assertThrows(NullPointerException.class, () -> Tag.isValidTagName(null));

        // invalid tag names
        assertFalse(Tag.isValidTagName("")); // empty string
        assertFalse(Tag.isValidTagName(" ")); // spaces only
        assertFalse(Tag.isValidTagName("^")); // special characters
        assertFalse(Tag.isValidTagName("friend*")); // contains special characters

        // valid tag names
        assertTrue(Tag.isValidTagName("friend")); // alphabets only
        assertTrue(Tag.isValidTagName("friend123")); // alphanumeric
        assertTrue(Tag.isValidTagName("123")); // numbers only
        assertTrue(Tag.isValidTagName("high-risk")); // with hyphen
        assertTrue(Tag.isValidTagName("best-friend")); // with hyphen
        assertTrue(Tag.isValidTagName("a-b-c")); // multiple hyphens
        assertTrue(Tag.isValidTagName("123-456")); // numbers with hyphen
    }

}
