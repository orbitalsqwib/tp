package casetrack.app.model.person;

import static casetrack.app.commons.util.AppUtil.checkArgument;
import static java.util.Objects.requireNonNull;

/**
 * Represents a Person's income in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidIncome(String)}
 */
public class Income {

    public static final String MESSAGE_CONSTRAINTS =
            "Income must be a non-negative number.";

    /**
     * Accept only non-negative whole numbers (no signs, no decimals, no commas).
     */
    public static final String VALIDATION_REGEX = "\\d+";

    public final String value;

    /**
     * Constructs an {@code Income}.
     *
     * @param income A valid non-negative integer represented as a string.
     */
    public Income(String income) {
        requireNonNull(income);
        checkArgument(isValidIncome(income), MESSAGE_CONSTRAINTS);
        this.value = income;
    }

    /**
     * Returns true if a given string is a valid income value.
     */
    public static boolean isValidIncome(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Income)) {
            return false;
        }
        Income otherIncome = (Income) other;
        return value.equals(otherIncome.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
