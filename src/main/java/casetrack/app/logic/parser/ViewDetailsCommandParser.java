package casetrack.app.logic.parser;

import static casetrack.app.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import casetrack.app.commons.core.index.Index;
import casetrack.app.logic.commands.Command;
import casetrack.app.logic.commands.ViewDetailsCommand;
import casetrack.app.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates either a DeleteNoteCommand or DeletePatientCommand object
 * based on the arguments provided.
 */
public class ViewDetailsCommandParser implements Parser<Command> {

    public static final String MESSAGE_INVALID_DELETE_FORMAT =
        "Expected 'view <PERSON_INDEX>'";

    /**
     * Parses the given {@code String} of arguments and returns either a DeleteNoteCommand
     * or DeletePatientCommand object for execution.
     * @throws ParseException if the user input does not follow the expected format
     */
    public Command parse(String args) throws ParseException {
        try {
            Index index = ParserUtil.parseIndex(args);
            return new ViewDetailsCommand(index);
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewDetailsCommand.MESSAGE_USAGE), pe);
        }
    }
}
