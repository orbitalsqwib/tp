package casetrack.app.model.person;

import static casetrack.app.commons.util.AppUtil.checkArgument;
import static java.util.Objects.requireNonNull;

/**
 * Represents a Person's phone number in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidPhone(String)}
 */
public class Phone {


    public static final String MESSAGE_CONSTRAINTS =
            "Phone numbers may be preceded by a country code of up to 3 numbers, that may be preceded with a '+' sign. "
                    + "Phone numbers should only contain numbers, and it should be between 3 and 17 digits long";
    public static final String VALIDATION_REGEX = "(\\+\\d{1,3}\\s?|\\d{1,3}\\s)?\\d{3,17}";
    public final String value;

    /**
     * Constructs a {@code Phone}.
     *
     * @param phone A valid phone number.
     */
    public Phone(String phone) {
        requireNonNull(phone);
        checkArgument(isValidPhone(phone), MESSAGE_CONSTRAINTS);
        value = phone;
    }

    /**
     * Returns true if a given string is a valid phone number.
     */
    public static boolean isValidPhone(String test) {
        if (!test.matches(VALIDATION_REGEX)) {
            return false;
        }
        // Remove spaces and + sign to count only digits
        String digitsOnly = test.replaceAll("[\\s+]", "");
        // Maximum 20 digits total (country code + phone number)
        return digitsOnly.length() <= 20;
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

        // instanceof handles nulls
        if (!(other instanceof Phone)) {
            return false;
        }

        Phone otherPhone = (Phone) other;
        return value.equals(otherPhone.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
