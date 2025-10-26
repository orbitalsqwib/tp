package casetrack.app.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.function.Predicate;
import java.util.logging.Logger;

import casetrack.app.commons.core.LogsCenter;
import casetrack.app.commons.util.ToStringBuilder;
import casetrack.app.logic.Messages;
import casetrack.app.model.Model;
import casetrack.app.model.person.Person;

/**
 * Finds and lists all persons in address book whose specified field contains any of the argument keywords.
 * Keyword matching is case insensitive.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "search";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Finds all persons whose specified field contains any of "
            + "the specified keywords (case-insensitive) and displays them as a list with index numbers.\n"
            + "Parameters: FIELD KEYWORD [MORE_KEYWORDS]...\n"
            + "Supported fields: name, number, email, tag\n"
            + "Examples: " + COMMAND_WORD + " name alice charlie\n"
            + "          " + COMMAND_WORD + " number 91234567\n"
            + "          " + COMMAND_WORD + " email alice@example.com\n"
            + "          " + COMMAND_WORD + " tag friend colleague";

    private static final Logger logger = LogsCenter.getLogger(FindCommand.class);
    private final Predicate<Person> predicate;

    public FindCommand(Predicate<Person> predicate) {
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        logger.info("Executing search command with predicate: " + predicate);

        model.updateFilteredPersonList(predicate);
        int resultCount = model.getFilteredPersonList().size();

        logger.info("Search completed. Found " + resultCount + " person(s) matching the criteria");

        return new CommandResult(
                String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, resultCount),
                new DetailPanelInstruction(null));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FindCommand)) {
            return false;
        }

        FindCommand otherFindCommand = (FindCommand) other;
        return predicate.equals(otherFindCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }
}
