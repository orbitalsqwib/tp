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
     * @param targetIndex The index of the target in the filtered person list
     */
    public DeleteCommand(Index targetIndex) {
        requireNonNull(targetIndex);
        this.targetIndex = targetIndex;
    }

    /**
     * Returns the patient at the index in the filtered list, or raise exception if invalid.
     */
    protected Person getPersonByIndex(Model model, Index index) throws CommandException {
        assert model != null : "Model should exist";
        assert index != null : "Index should exist";

        List<Person> lastShownList = model.getFilteredPersonList();
        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
        return lastShownList.get(index.getZeroBased());
    }
}


