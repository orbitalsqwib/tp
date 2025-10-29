package casetrack.app.logic.commands;

import static casetrack.app.testutil.TypicalPersons.getTypicalAddressBook;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import casetrack.app.model.Model;
import casetrack.app.model.ModelManager;
import casetrack.app.model.UserPrefs;
import casetrack.app.model.person.Person;
import casetrack.app.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code DetailPanelInstruction}.
 */
public class DetailPanelInstructionTest {

    @Test
    public void constructor_withPerson_success() {
        Person person = new PersonBuilder().withName("Mary").build();
        DetailPanelInstruction instruction = new DetailPanelInstruction(person);

        assertEquals(person, instruction.getDisplayPerson());
        assertFalse(instruction.shouldClearDisplay());
    }

    @Test
    public void constructor_withNull_success() {
        DetailPanelInstruction instruction = new DetailPanelInstruction(null);

        assertEquals(null, instruction.getDisplayPerson());
        assertTrue(instruction.shouldClearDisplay());
    }

    @Test
    public void getDisplayPerson_withPerson_returnsPerson() {
        Person person = new PersonBuilder().withName("John").build();
        DetailPanelInstruction instruction = new DetailPanelInstruction(person);

        assertEquals(person, instruction.getDisplayPerson());
    }

    @Test
    public void getDisplayPerson_withNull_returnsNull() {
        DetailPanelInstruction instruction = new DetailPanelInstruction(null);

        assertEquals(null, instruction.getDisplayPerson());
    }

    @Test
    public void shouldClearDisplay_withPerson_returnsFalse() {
        Person person = new PersonBuilder().withName("Bobby").build();
        DetailPanelInstruction instruction = new DetailPanelInstruction(person);

        assertFalse(instruction.shouldClearDisplay());
    }

    @Test
    public void shouldClearDisplay_withNull_returnsTrue() {
        DetailPanelInstruction instruction = new DetailPanelInstruction(null);

        assertTrue(instruction.shouldClearDisplay());
    }

    @Test
    public void equals() {
        Person person1 = new PersonBuilder().withName("Davidson").build();
        Person person2 = new PersonBuilder().withName("Jerry").build();
        DetailPanelInstruction instruction1 = new DetailPanelInstruction(person1);
        DetailPanelInstruction instruction2 = new DetailPanelInstruction(person2);
        DetailPanelInstruction clearInstruction = new DetailPanelInstruction(null);

        // same object -> returns true
        assertTrue(instruction1.equals(instruction1));

        // same person -> returns true
        DetailPanelInstruction instruction1Copy = new DetailPanelInstruction(person1);
        assertTrue(instruction1.equals(instruction1Copy));

        // different person -> returns false
        assertFalse(instruction1.equals(instruction2));

        // one has person, other has null -> returns false
        assertFalse(instruction1.equals(clearInstruction));
        assertFalse(clearInstruction.equals(instruction1));

        // both null -> returns true
        DetailPanelInstruction clearInstruction2 = new DetailPanelInstruction(null);
        assertTrue(clearInstruction.equals(clearInstruction2));

        // different types -> returns false
        assertFalse(instruction1.equals("not a DetailPanelInstruction"));
        assertFalse(instruction1.equals(123));
        assertFalse(instruction1.equals(null));
    }

    @Test
    public void hashCodeMethod() {
        Person person1 = new PersonBuilder().withName("Sam").build();
        Person person2 = new PersonBuilder().withName("Gam").build();
        DetailPanelInstruction instruction1 = new DetailPanelInstruction(person1);
        DetailPanelInstruction instruction2 = new DetailPanelInstruction(person2);
        DetailPanelInstruction clearInstruction = new DetailPanelInstruction(null);

        // same person -> returns same hashcode
        DetailPanelInstruction instruction1Copy = new DetailPanelInstruction(person1);
        assertEquals(instruction1.hashCode(), instruction1Copy.hashCode());

        // different person -> returns different hashcode
        assertNotEquals(instruction1.hashCode(), instruction2.hashCode());

        // one has person, other has null -> returns different hashcode
        assertNotEquals(instruction1.hashCode(), clearInstruction.hashCode());

        // both null -> returns same hashcode
        DetailPanelInstruction clearInstruction2 = new DetailPanelInstruction(null);
        assertEquals(clearInstruction.hashCode(), clearInstruction2.hashCode());

        // null instruction -> returns zero
        assertEquals(0, clearInstruction.hashCode());
    }

    @Test
    public void execute_commandResult_withPerson() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Person person = model.getFilteredPersonList().get(0);

        DetailPanelInstruction instruction = new DetailPanelInstruction(person);
        CommandResult commandResult = new CommandResult("Test message", instruction);

        assertEquals(instruction, commandResult.getDetailPanelInstruction());
        assertEquals(person, commandResult.getDetailPanelInstruction().getDisplayPerson());
        assertFalse(commandResult.getDetailPanelInstruction().shouldClearDisplay());
    }

    @Test
    public void execute_commandResult_withNull() {
        DetailPanelInstruction clearInstruction = new DetailPanelInstruction(null);
        CommandResult commandResult = new CommandResult("Message", clearInstruction);

        assertEquals(clearInstruction, commandResult.getDetailPanelInstruction());
        assertEquals(null, commandResult.getDetailPanelInstruction().getDisplayPerson());
        assertTrue(commandResult.getDetailPanelInstruction().shouldClearDisplay());
    }

    @Test
    public void execute_commandResult_equality() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Person person = model.getFilteredPersonList().get(0);

        DetailPanelInstruction instruction1 = new DetailPanelInstruction(person);
        DetailPanelInstruction instruction2 = new DetailPanelInstruction(person);
        CommandResult result1 = new CommandResult("Test", instruction1);
        CommandResult result2 = new CommandResult("Test", instruction2);

        // verify that are equal
        assertTrue(result1.equals(result2));
        assertEquals(result1.hashCode(), result2.hashCode());
    }

}
