package casetrack.app.model.person;

import static casetrack.app.testutil.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class NameTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Name(null));
    }

    @Test
    public void constructor_invalidName_throwsIllegalArgumentException() {
        String invalidName = "";
        assertThrows(IllegalArgumentException.class, () -> new Name(invalidName));
    }

    @Test
    public void isValidName() {
        // null name
        assertThrows(NullPointerException.class, () -> Name.isValidName(null));

        // invalid name
        assertFalse(Name.isValidName("")); // empty string
        assertFalse(Name.isValidName(" ")); // spaces only
        assertFalse(Name.isValidName("^")); // only non-alphanumeric characters
        assertFalse(Name.isValidName("peter*")); // contains non-alphanumeric characters
        assertFalse(Name.isValidName("s/o peter*")); // starts with s/o
        assertFalse(Name.isValidName("john s/ o peter*")); // s/o is not continous

        // valid name
        assertTrue(Name.isValidName("peter jack")); // alphabets only
        assertTrue(Name.isValidName("12345")); // numbers only
        assertTrue(Name.isValidName("peter the 2nd")); // alphanumeric characters
        assertTrue(Name.isValidName("Capital Tan")); // with capital letters
        assertTrue(Name.isValidName("David Roger Jackson Ray Jr 2nd")); // long names
        assertTrue(Name.isValidName("Ravichandran S/O Tharumalinga")); // indian names
        assertTrue(Name.isValidName("Dr. Lim")); // names with periods
        assertTrue(Name.isValidName("John Jr.")); // names ending with period
        assertTrue(Name.isValidName("Hubert Blaine Wolfeschlegelsteinhausenbergerd Sr.")); // long name with period
        assertTrue(Name.isValidName("o'Connor")); // names with apostrophes
        assertTrue(Name.isValidName("d'Angelo")); // names with apostrophes
        assertTrue(Name.isValidName("Mary O'Brien")); // names with apostrophes in middle
        assertTrue(Name.isValidName("Mary-Jane")); // names with hyphens
        assertTrue(Name.isValidName("Jean-Claude")); // names with hyphens
        assertTrue(Name.isValidName("Anne-Marie Watson")); // names with hyphens and spaces
    }

    @Test
    public void equals() {
        Name name = new Name("Valid Name");

        // same values -> returns true
        assertTrue(name.equals(new Name("Valid Name")));

        // same object -> returns true
        assertTrue(name.equals(name));

        // null -> returns false
        assertFalse(name.equals(null));

        // different types -> returns false
        assertFalse(name.equals(5.0f));

        // different values -> returns false
        assertFalse(name.equals(new Name("Other Valid Name")));

        // same values but different case -> returns true (case-insensitive)
        assertTrue(name.equals(new Name("valid name")));
        assertTrue(name.equals(new Name("VALID NAME")));
        assertTrue(name.equals(new Name("VaLiD nAmE")));

        // same values but different whitespace -> returns true (whitespace normalized)
        assertTrue(name.equals(new Name("Valid  Name"))); // double space
        assertTrue(name.equals(new Name("Valid   Name"))); // triple space
        assertTrue(name.equals(new Name("Valid    Name"))); // multiple spaces
    }

    @Test
    public void equals_multipleWords() {
        Name name = new Name("Alice Bob Charlie");

        // same name with extra spaces between words -> returns true
        assertTrue(name.equals(new Name("Alice  Bob  Charlie")));
        assertTrue(name.equals(new Name("Alice   Bob   Charlie")));
        assertTrue(name.equals(new Name("Alice Bob  Charlie")));
    }
}
