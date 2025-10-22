package casetrack.app.logic.commands;

import casetrack.app.model.person.Person;

/**
 * Represents an instruction for the detail panel on how to display information.
 */
public class DetailPanelInstruction {

    /** The person information to display in the detail panel. If null, display nothing. */
    private final Person displayPerson;

    /**
     * Creates a DetailPanelInstruction with the specified person information to display.
     *
     * @param displayPerson The person information to display in the detail panel. If null, the detail panel should display nothing.
     */
    public DetailPanelInstruction(Person displayPerson) {
        this.displayPerson = displayPerson;
    }

    /**
     * Gets the person information to display in the detail panel.
     *
     * @return The person information to display, or null if nothing should be displayed.
     */
    public Person getDisplayPerson() {
        return displayPerson;
    }

    /**
     * Checks if the detail panel should clear its display.
     *
     * @return true if the person information to display is null, false otherwise.
     */
    public boolean shouldClearDisplay() {
        return displayPerson == null;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof DetailPanelInstruction)) {
            return false;
        }

        DetailPanelInstruction otherInstruction = (DetailPanelInstruction) other;
        return (displayPerson == null && otherInstruction.displayPerson == null)
                || (displayPerson != null && displayPerson.equals(otherInstruction.displayPerson));
    }

    @Override
    public int hashCode() {
        return displayPerson == null ? 0 : displayPerson.hashCode();
    }
}
