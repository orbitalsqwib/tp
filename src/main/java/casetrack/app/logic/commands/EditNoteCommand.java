package casetrack.app.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import casetrack.app.commons.core.index.Index;
import casetrack.app.commons.util.ToStringBuilder;
import casetrack.app.logic.Messages;
import casetrack.app.logic.commands.exceptions.CommandException;
import casetrack.app.model.Model;
import casetrack.app.model.person.Note;
import casetrack.app.model.person.Person;

/**
 * Edits a note of a person identified using its displayed index from the address book.
 */
public class EditNoteCommand extends EditCommand {

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Edits a note of a person identified by the index number used in the displayed person list.\n"
            + "Parameters: note <PERSON_INDEX> <NOTE_INDEX> t/NEW_TEXT\n"
            + "Example: " + COMMAND_WORD + " note 1 2 t/Updated note content";

    public static final String MESSAGE_EDIT_NOTE_SUCCESS = "Edited Note for %1$s: %2$s";
    public static final String MESSAGE_INVALID_NOTE_INDEX = "The note index provided is invalid.";
    public static final String MESSAGE_NO_NOTES = "This person has no notes to edit.";

    private final Index personIndex;
    private final Index noteIndex;
    private final Note newNote;

    /**
     * Creates an EditNoteCommand to edit a note of a person.
     *
     * @param personIndex The index of the person in the filtered person list
     * @param noteIndex The index of the note to edit from the person's notes
     * @param newNote The new note to replace the old note
     */
    public EditNoteCommand(Index personIndex, Index noteIndex, Note newNote) {
        this.personIndex = personIndex;
        this.noteIndex = noteIndex;
        this.newNote = newNote;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (personIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToUpdate = lastShownList.get(personIndex.getZeroBased());
        List<Note> notes = personToUpdate.getNotes();

        if (notes.isEmpty()) {
            throw new CommandException(MESSAGE_NO_NOTES);
        }

        if (noteIndex.getZeroBased() >= notes.size()) {
            throw new CommandException(MESSAGE_INVALID_NOTE_INDEX);
        }

        Person updatedPerson = personToUpdate.editNote(noteIndex.getZeroBased(), newNote);
        model.setPerson(personToUpdate, updatedPerson);
        return new CommandResult(String.format(MESSAGE_EDIT_NOTE_SUCCESS,
                personToUpdate.getName().fullName, newNote.value), updatedPerson, false, false);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditNoteCommand)) {
            return false;
        }

        EditNoteCommand otherEditNoteCommand = (EditNoteCommand) other;
        return personIndex.equals(otherEditNoteCommand.personIndex)
                && noteIndex.equals(otherEditNoteCommand.noteIndex)
                && newNote.equals(otherEditNoteCommand.newNote);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("personIndex", personIndex)
                .add("noteIndex", noteIndex)
                .add("newNote", newNote)
                .toString();
    }
}

