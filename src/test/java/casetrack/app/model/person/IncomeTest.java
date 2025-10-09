package casetrack.app.model.person;

import static casetrack.app.testutil.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class IncomeTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Income(null));
    }

    @Test
    public void constructor_invalidIncome_throwsIllegalArgumentException() {
        String invalidIncome = "";
        assertThrows(IllegalArgumentException.class, () -> new Income(invalidIncome));
    }

    @Test
    public void isValidIncome() {
        // null income
        assertThrows(NullPointerException.class, () -> Income.isValidIncome(null));

        // invalid income
        assertFalse(Income.isValidIncome("")); // empty string
        assertFalse(Income.isValidIncome(" ")); // spaces only
        assertFalse(Income.isValidIncome("-1")); // negative number sign
        assertFalse(Income.isValidIncome("+1")); // plus sign not allowed
        assertFalse(Income.isValidIncome("1.0")); // decimals not allowed
        assertFalse(Income.isValidIncome("1,000")); // commas not allowed
        assertFalse(Income.isValidIncome("$1000")); // currency symbol not allowed
        assertFalse(Income.isValidIncome("abc")); // non-numeric

        // valid income
        assertTrue(Income.isValidIncome("0")); // zero allowed
        assertTrue(Income.isValidIncome("5")); // small integer
        assertTrue(Income.isValidIncome("1234567890")); // long integer
    }

    @Test
    public void equals() {
        Income income = new Income("1000");

        // same values -> returns true
        assertTrue(income.equals(new Income("1000")));

        // same object -> returns true
        assertTrue(income.equals(income));

        // null -> returns false
        assertFalse(income.equals(null));

        // different types -> returns false
        assertFalse(income.equals(5.0f));

        // different values -> returns false
        assertFalse(income.equals(new Income("999")));
    }
}
