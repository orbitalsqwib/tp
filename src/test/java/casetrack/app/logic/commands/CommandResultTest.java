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
    public void equals_withDetailPanelInstruction() {
        Person person1 = new PersonBuilder().withName("Mary").build();
        Person person2 = new PersonBuilder().withName("Bob").build();
        
        DetailPanelInstruction instruction1 = new DetailPanelInstruction(person1);
        DetailPanelInstruction instruction2 = new DetailPanelInstruction(person2);
        DetailPanelInstruction clearInstruction = new DetailPanelInstruction(null);
        
        CommandResult resultWithInstruction = new CommandResult("feedback", instruction1);
        CommandResult resultWithClearInstruction = new CommandResult("feedback", clearInstruction);
        CommandResult resultWithNullInstruction = new CommandResult("feedback");

        // same detailPanelInstruction -> returns true
        assertTrue(resultWithInstruction.equals(new CommandResult("feedback", instruction1)));
        
        // different detailPanelInstruction -> returns false
        assertFalse(resultWithInstruction.equals(new CommandResult("feedback", instruction2)));
        
        // one has instruction, other has null -> returns false
        assertFalse(resultWithInstruction.equals(resultWithNullInstruction));
        assertFalse(resultWithNullInstruction.equals(resultWithInstruction));
        
        // both have null detailPanelInstruction -> returns true
        assertTrue(resultWithNullInstruction.equals(new CommandResult("feedback")));
        
        // clear instruction vs null instruction -> returns false (different types)
        assertFalse(resultWithClearInstruction.equals(resultWithNullInstruction));
        assertFalse(resultWithNullInstruction.equals(resultWithClearInstruction));
        
        // clear instruction vs clear instruction -> returns true
        assertTrue(resultWithClearInstruction.equals(new CommandResult("feedback", clearInstruction)));
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
        
        // clear instruction vs null instruction -> returns different hashcode (different types)
        assertNotEquals(resultWithClearInstruction.hashCode(), resultWithNullInstruction.hashCode());
        
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
