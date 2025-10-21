package casetrack.app.logic.commands;

import static casetrack.app.logic.commands.CommandTestUtil.assertCommandFailure;
import static casetrack.app.logic.commands.CommandTestUtil.assertCommandSuccess;
import static casetrack.app.logic.commands.CommandTestUtil.showPersonAtIndex;
import static casetrack.app.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static casetrack.app.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import casetrack.app.commons.core.index.Index;
import casetrack.app.logic.Messages;
import casetrack.app.model.Model;
import casetrack.app.model.ModelManager;
import casetrack.app.model.UserPrefs;
import casetrack.app.model.person.Note;
import casetrack.app.model.person.Person;
import casetrack.app.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for EditNoteCommand.
 */
public class EditNoteCommandTest {

    private Model model = new ModelManager();

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Person personWithNotes = new PersonBuilder()
                .withName("Alice Pauline")
                .withPhone("94351253")
                .withEmail("alice@example.com")
                .withAddress("123, Jurong West Ave 6, #01-111")
                .withTags("friends")
                .build();

        // add notes to the person
        Person personWithTwoNotes = personWithNotes
                .addNote(new Note("First note"))
                .addNote(new Note("Second note"));

        model.addPerson(personWithTwoNotes);

        Note newNote = new Note("Updated first note");
        EditNoteCommand editNoteCommand = new EditNoteCommand(INDEX_FIRST_PERSON, INDEX_FIRST_PERSON, newNote);

        String expectedMessage = String.format(EditNoteCommand.MESSAGE_EDIT_NOTE_SUCCESS,
                personWithTwoNotes.getName().fullName, newNote.value);

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        Person expectedPerson = personWithTwoNotes.editNote(0, newNote);
        expectedModel.setPerson(personWithTwoNotes, expectedPerson);

        CommandResult expectedResult = new CommandResult(expectedMessage, expectedPerson, false, false);
        assertCommandSuccess(editNoteCommand, model, expectedResult, expectedModel);
    }

    @Test
    public void execute_invalidPersonIndexUnfilteredList_failure() {
        Person personWithNotes = new PersonBuilder()
                .withName("Alice Pauline")
                .withPhone("94351253")
                .withEmail("alice@example.com")
                .withAddress("123, Jurong East, #01-111")
                .withTags("friends")
                .build();

        Person personWithOneNote = personWithNotes.addNote(new Note("Test note"));
        model.addPerson(personWithOneNote);

        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        Note newNote = new Note("Updated note");
        EditNoteCommand editNoteCommand = new EditNoteCommand(outOfBoundIndex, INDEX_FIRST_PERSON, newNote);

        assertCommandFailure(editNoteCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_invalidNoteIndex_failure() {
        Person personWithNotes = new PersonBuilder()
                .withName("Alice Pauline")
                .withPhone("94351253")
                .withEmail("alice@example.com")
                .withAddress("123, Jurong East, #08-111")
                .withTags("friends")
                .build();

        Person personWithOneNote = personWithNotes.addNote(new Note("Test note"));
        model.addPerson(personWithOneNote);

        Index outOfBoundNoteIndex = Index.fromOneBased(personWithOneNote.getNotes().size() + 1);
        Note newNote = new Note("Updated note");
        EditNoteCommand editNoteCommand = new EditNoteCommand(INDEX_FIRST_PERSON, outOfBoundNoteIndex, newNote);

        assertCommandFailure(editNoteCommand, model, EditNoteCommand.MESSAGE_INVALID_NOTE_INDEX);
    }

    @Test
    public void execute_personWithNoNotes_failure() {
        Person personWithoutNotes = new PersonBuilder()
                .withName("Alice Pauline")
                .withPhone("94351253")
                .withEmail("alice@example.com")
                .withAddress("123, Jurong East, #01-111")
                .withTags("friends")
                .build();

        model.addPerson(personWithoutNotes);

        Note newNote = new Note("Updated note");
        EditNoteCommand editNoteCommand = new EditNoteCommand(INDEX_FIRST_PERSON, INDEX_FIRST_PERSON, newNote);

        assertCommandFailure(editNoteCommand, model, EditNoteCommand.MESSAGE_NO_NOTES);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        // Create a person with notes
        Person personWithNotes = new PersonBuilder()
                .withName("Alice Pauline")
                .withPhone("94351253")
                .withEmail("alice@example.com")
                .withAddress("123, Jurong East, #01-111")
                .withTags("friends")
                .build();

        Person personWithTwoNotes = personWithNotes
                .addNote(new Note("First note"))
                .addNote(new Note("Second note"));

        model.addPerson(personWithTwoNotes);

        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Note newNote = new Note("Updated first note");
        EditNoteCommand editNoteCommand = new EditNoteCommand(INDEX_FIRST_PERSON, INDEX_FIRST_PERSON, newNote);

        String expectedMessage = String.format(EditNoteCommand.MESSAGE_EDIT_NOTE_SUCCESS,
                personWithTwoNotes.getName().fullName, newNote.value);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        Person expectedPerson = personWithTwoNotes.editNote(0, newNote);
        expectedModel.setPerson(personWithTwoNotes, expectedPerson);
        showPersonAtIndex(expectedModel, INDEX_FIRST_PERSON);

        CommandResult expectedResult = new CommandResult(expectedMessage, expectedPerson, false, false);
        assertCommandSuccess(editNoteCommand, model, expectedResult, expectedModel);
    }

    @Test
    public void execute_invalidPersonIndexFilteredList_failure() {
        Person personWithNotes = new PersonBuilder()
                .withName("Alice Pauline")
                .withPhone("94351253")
                .withEmail("alice@example.com")
                .withAddress("123, Jurong East, #01-111")
                .withTags("friends")
                .build();

        Person personWithOneNote = personWithNotes.addNote(new Note("Test note"));
        model.addPerson(personWithOneNote);

        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);

        Note newNote = new Note("Updated note");
        EditNoteCommand editNoteCommand = new EditNoteCommand(outOfBoundIndex, INDEX_FIRST_PERSON, newNote);

        assertCommandFailure(editNoteCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_editSecondNote_success() {
        Person personWithNotes = new PersonBuilder()
                .withName("Bob Builder")
                .withPhone("98765432")
                .withEmail("bob@example.com")
                .withAddress("456, Construction St, #02-222")
                .withTags("colleague")
                .build();

        Person personWithTwoNotes = personWithNotes
                .addNote(new Note("First note"))
                .addNote(new Note("Second note"));

        model.addPerson(personWithTwoNotes);

        Note newNote = new Note("Updated second note");
        EditNoteCommand editNoteCommand = new EditNoteCommand(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON, newNote);

        String expectedMessage = String.format(EditNoteCommand.MESSAGE_EDIT_NOTE_SUCCESS,
                personWithTwoNotes.getName().fullName, newNote.value);

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        Person expectedPerson = personWithTwoNotes.editNote(1, newNote);
        expectedModel.setPerson(personWithTwoNotes, expectedPerson);

        CommandResult expectedResult = new CommandResult(expectedMessage, expectedPerson, false, false);
        assertCommandSuccess(editNoteCommand, model, expectedResult, expectedModel);
    }

    @Test
    public void equals() {
        Note firstNote = new Note("First updated note");
        Note secondNote = new Note("Second updated note");
        EditNoteCommand editFirstNoteCommand = new EditNoteCommand(INDEX_FIRST_PERSON, INDEX_FIRST_PERSON, firstNote);
        EditNoteCommand editSecondNoteCommand = new EditNoteCommand(INDEX_SECOND_PERSON, INDEX_FIRST_PERSON,
                firstNote);
        EditNoteCommand editFirstNoteSecondIndexCommand = new EditNoteCommand(INDEX_FIRST_PERSON,
                INDEX_SECOND_PERSON, firstNote);
        EditNoteCommand editFirstNoteDifferentTextCommand = new EditNoteCommand(INDEX_FIRST_PERSON,
                INDEX_FIRST_PERSON, secondNote);

        // same object - returns true
        assertTrue(editFirstNoteCommand.equals(editFirstNoteCommand));

        // same values - returns true
        EditNoteCommand editFirstNoteCommandCopy = new EditNoteCommand(INDEX_FIRST_PERSON, INDEX_FIRST_PERSON,
                firstNote);
        assertTrue(editFirstNoteCommand.equals(editFirstNoteCommandCopy));

        // different types - returns false
        assertFalse(editFirstNoteCommand.equals(1));

        // null - returns false
        assertFalse(editFirstNoteCommand.equals(null));

        // different person index - returns false
        assertFalse(editFirstNoteCommand.equals(editSecondNoteCommand));

        // different note index - returns false
        assertFalse(editFirstNoteCommand.equals(editFirstNoteSecondIndexCommand));

        // different note text - returns false
        assertFalse(editFirstNoteCommand.equals(editFirstNoteDifferentTextCommand));
    }

    @Test
    public void toStringMethod() {
        Index targetPersonIndex = Index.fromOneBased(1);
        Index targetNoteIndex = Index.fromOneBased(1);
        Note newNote = new Note("Updated note");
        EditNoteCommand editNoteCommand = new EditNoteCommand(targetPersonIndex, targetNoteIndex, newNote);
        String expected = EditNoteCommand.class.getCanonicalName() + "{personIndex=" + targetPersonIndex
                + ", noteIndex=" + targetNoteIndex + ", newNote=" + newNote + "}";
        assertEquals(expected, editNoteCommand.toString());
    }
}

