package casetrack.app.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import casetrack.app.model.person.Person;
import casetrack.app.testutil.PersonBuilder;

public class CommandResultTest {
    @Test
    public void equals() {
        CommandResult commandResult = new CommandResult("feedback");

        // same values -> returns true
        assertTrue(commandResult.equals(new CommandResult("feedback")));
        assertTrue(commandResult.equals(new CommandResult("feedback", null, false, false)));

        // same object -> returns true
        assertTrue(commandResult.equals(commandResult));

        // null -> returns false
        assertFalse(commandResult.equals(null));

        // different types -> returns false
        assertFalse(commandResult.equals(0.5f));

        // different feedbackToUser value -> returns false
        assertFalse(commandResult.equals(new CommandResult("different")));

        // different showHelp value -> returns false
        assertFalse(commandResult.equals(new CommandResult("feedback", null, true, false)));

        CommandResult resultWithDetailTarget = new CommandResult("feedback",
                new PersonBuilder().build(), false, false);

        // different detailTarget value -> returns false
        assertFalse(resultWithDetailTarget.equals(new CommandResult("feedback",
                new PersonBuilder().withName("diff").build(), false, false)));

        // different detailTarget value (1 null) -> returns false
        assertFalse(commandResult.equals(resultWithDetailTarget));
        assertFalse(resultWithDetailTarget.equals(commandResult));

        // same detailTarget value -> returns true
        assertTrue(resultWithDetailTarget.equals(new CommandResult("feedback",
                new PersonBuilder().build(), false, false)));

        // both detailTarget = null -> returns true
        assertTrue(commandResult.equals(new CommandResult("feedback")));

        // different exit value -> returns false
        assertFalse(commandResult.equals(new CommandResult("feedback", null, false, true)));
    }

    @Test
    public void equals_detailPanelInstruction() {
        Person person1 = new PersonBuilder().withName("Mary").build();
        Person person2 = new PersonBuilder().withName("Bob").build();

        DetailPanelInstruction instruction1 = new DetailPanelInstruction(person1);
        DetailPanelInstruction instruction2 = new DetailPanelInstruction(person2);
        DetailPanelInstruction clearInstruction = new DetailPanelInstruction(null);

        // test all possible detailPanelInstruction equality
        CommandResult result1 = new CommandResult("test", instruction1);
        CommandResult result2 = new CommandResult("test", instruction1);
        CommandResult result3 = new CommandResult("test", instruction2);
        CommandResult result4 = new CommandResult("test", clearInstruction);
        CommandResult result5 = new CommandResult("test");

        // same instruction -> true
        assertTrue(result1.equals(result2));

        // different instruction with same person -> true
        DetailPanelInstruction instruction1Copy = new DetailPanelInstruction(person1);
        CommandResult result1Copy = new CommandResult("test", instruction1Copy);
        assertTrue(result1.equals(result1Copy));

        // different instruction objects with different person -> false
        assertFalse(result1.equals(result3));

        // one has instruction, other has clear instruction -> false
        assertFalse(result1.equals(result4));
        assertFalse(result4.equals(result1));

        // one has instruction, other has null -> false
        assertFalse(result1.equals(result5));
        assertFalse(result5.equals(result1));

        // both have null -> true
        assertTrue(result5.equals(new CommandResult("test")));

        // Both have clear instruction -> true
        assertTrue(result4.equals(new CommandResult("test", clearInstruction)));
    }

    @Test
    public void hashcode() {
        CommandResult commandResult = new CommandResult("feedback");

        // same values -> returns same hashcode
        assertEquals(commandResult.hashCode(), new CommandResult("feedback").hashCode());

        // different feedbackToUser value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("different").hashCode());

        // different showHelp value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("feedback", null, true, false).hashCode());

        // different exit value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("feedback", null, false, true).hashCode());
    }

    @Test
    public void hashcode_withDetailPanelInstruction() {
        Person person1 = new PersonBuilder().withName("Mary").build();
        Person person2 = new PersonBuilder().withName("Bob").build();

        DetailPanelInstruction instruction1 = new DetailPanelInstruction(person1);
        DetailPanelInstruction instruction2 = new DetailPanelInstruction(person2);
        DetailPanelInstruction clearInstruction = new DetailPanelInstruction(null);

        CommandResult resultWithInstruction = new CommandResult("feedback", instruction1);
        CommandResult resultWithClearInstruction = new CommandResult("feedback", clearInstruction);
        CommandResult resultWithNullInstruction = new CommandResult("feedback");

        // same detailPanelInstruction -> returns same hashcode
        assertEquals(resultWithInstruction.hashCode(), new CommandResult("feedback", instruction1).hashCode());

        // different detailPanelInstruction -> returns different hashcode
        assertNotEquals(resultWithInstruction.hashCode(), new CommandResult("feedback", instruction2).hashCode());

        // one has instruction, other has null -> returns different hashcode
        assertNotEquals(resultWithInstruction.hashCode(), resultWithNullInstruction.hashCode());

        // clear instruction vs null instruction -> returns same hashcode (both represent "clear panel")
        assertEquals(resultWithClearInstruction.hashCode(), resultWithNullInstruction.hashCode());

        // clear instruction vs clear instruction -> returns same hashcode
        assertEquals(resultWithClearInstruction.hashCode(), new CommandResult("feedback", clearInstruction).hashCode());
    }

    @Test
    public void toStringMethod() {
        CommandResult commandResult = new CommandResult("feedback");
        String expected = CommandResult.class.getCanonicalName() + "{feedbackToUser="
                + commandResult.getFeedbackToUser() + ", detailTarget=" + commandResult.getDetailTarget()
                + ", detailPanelInstruction=" + commandResult.getDetailPanelInstruction()
                + ", showHelp=" + commandResult.isShowHelp() + ", exit=" + commandResult.isExit() + "}";
        assertEquals(expected, commandResult.toString());
    }
}
