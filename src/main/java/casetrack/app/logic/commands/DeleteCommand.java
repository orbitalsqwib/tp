package casetrack.app.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import casetrack.app.commons.core.index.Index;
import casetrack.app.logic.Messages;
import casetrack.app.logic.commands.exceptions.CommandException;
import casetrack.app.model.Model;
import casetrack.app.model.person.Person;

/**
 * Abstract class for delete note and patient command.
 */
public abstract class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    protected final Index targetIndex;

    /**
     * Creates a DeleteCommand with the target index.
     *
     * @param targetIndex The index of the target to be deleted.
     */
    public DeleteCommand(Index targetIndex) {
        requireNonNull(targetIndex);
        this.targetIndex = targetIndex;
    }

    /**
     * Returns the {@link Person} at the specified index in the filtered list.
     *
     * @param model The model with the filtered person list
     * @param index The index of the person in the filtered list
     * @return {@link Person} at the specified index
     * @throws CommandException if the index is out of bounds for the filtered list
     */
    protected Person getPersonByIndex(Model model, Index index) throws CommandException {
        List<Person> lastShownList = model.getFilteredPersonList();
        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
        return lastShownList.get(index.getZeroBased());
    }
}


