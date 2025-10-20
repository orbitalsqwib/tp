package casetrack.app.logic.commands;

import java.util.List;

import casetrack.app.commons.core.index.Index;
import casetrack.app.logic.Messages;
import casetrack.app.logic.commands.exceptions.CommandException;
import casetrack.app.model.Model;
import casetrack.app.model.person.Person;

/**
 * Base class for all delete-related commands.
 */
public abstract class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    /**
     * Returns the person at the given index in the filtered list, or throws if out of bounds.
     */
    protected Person getPersonByIndex(Model model, Index index) throws CommandException {
        List<Person> lastShownList = model.getFilteredPersonList();
        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
        return lastShownList.get(index.getZeroBased());
    }
}


