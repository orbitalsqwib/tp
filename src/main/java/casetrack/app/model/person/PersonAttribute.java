package main.java.casetrack.app.model.person;

/**
 * Represents an attribute for a Person.
 */
public class PersonAttribute {
    public final String name;
    public final String value;

    /**
     * Creates a new attribute object.
     * @param name The name of the attribute
     * @param value The value of the attribute
     */
    public PersonAttribute(String name, String value) {
        this.name = name;
        this.value = value;
    }
}
