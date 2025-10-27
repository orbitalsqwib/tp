package casetrack.app.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.logging.Logger;

import casetrack.app.commons.core.LogsCenter;
import casetrack.app.commons.core.index.Index;
import casetrack.app.commons.util.ToStringBuilder;
import casetrack.app.logic.commands.exceptions.CommandException;
import casetrack.app.model.Model;
import casetrack.app.model.person.Note;
import casetrack.app.model.person.Person;

/**
 * Deletes a note from a person based on its displayed index in the address book.
 */
public class DeleteNoteCommand extends DeleteCommand {

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes a note from a person identified by the index number used in the displayed person list.\n"
            + "Parameters: note <PERSON_INDEX> <NOTE_INDEX>\n"
            + "Example: " + COMMAND_WORD + " note 1 2";

    public static final String MESSAGE_DELETE_NOTE_SUCCESS = "Deleted Note from %1$s: %2$s";
    public static final String MESSAGE_INVALID_NOTE_INDEX = "The note index provided is invalid.";
    public static final String MESSAGE_NO_NOTES = "This person has no notes to delete.";

    private static final Logger logger = LogsCenter.getLogger(DeleteNoteCommand.class);
    private final Index noteIndex;

    /**
     * Creates a DeleteNoteCommand to delete a note from a person.
     *
     * @param targetIndex The index of the target in the filtered person list
     * @param noteIndex The index of the note to delete from the person's notes
     */
    public DeleteNoteCommand(Index targetIndex, Index noteIndex) {
        super(targetIndex);
        requireNonNull(noteIndex);
        this.noteIndex = noteIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Person personToUpdate = getPersonByIndex(model, targetIndex);
        List<Note> notes = personToUpdate.getNotes();

        if (notes.isEmpty()) {
            logger.warning("Attempt to delete note from person with empty notes: " + personToUpdate.getName().fullName);
            throw new CommandException(MESSAGE_NO_NOTES);
        }

        if (noteIndex.getZeroBased() >= notes.size()) {
            logger.warning("Invalid note index " + noteIndex + " for person with " + notes.size()
                    + " notes: " + personToUpdate.getName().fullName);
            throw new CommandException(MESSAGE_INVALID_NOTE_INDEX);
        }

        Note noteToDelete = notes.get(noteIndex.getZeroBased());
        Person updatedPerson = personToUpdate.removeNote(noteIndex.getZeroBased());
        model.setPerson(personToUpdate, updatedPerson);

        DetailPanelInstruction updateInstruction = new DetailPanelInstruction(updatedPerson);
        return new CommandResult(String.format(MESSAGE_DELETE_NOTE_SUCCESS,
                personToUpdate.getName().fullName, noteToDelete.value), updateInstruction);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteNoteCommand)) {
            return false;
        }

        DeleteNoteCommand otherDeleteNoteCommand = (DeleteNoteCommand) other;
        return targetIndex.equals(otherDeleteNoteCommand.targetIndex)
                && noteIndex.equals(otherDeleteNoteCommand.noteIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("noteIndex", noteIndex)
                .toString();
    }
}
