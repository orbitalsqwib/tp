package casetrack.app.logic.parser;

import static casetrack.app.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import casetrack.app.commons.core.index.Index;
import casetrack.app.logic.commands.DeletePatientCommand;
import casetrack.app.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new DeletePatientCommand object
 */
public class DeletePatientCommandParser implements Parser<DeletePatientCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the DeletePatientCommand
     * and returns a DeletePatientCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public DeletePatientCommand parse(String args) throws ParseException {
        final Index index = parseIndexArg(args);
        return new DeletePatientCommand(index);
    }

    /**
     * Normalizes and parses the raw arguments for delete.
     * Only supports the form {@code patient <index>} and returns the parsed index.
     */
    private Index parseIndexArg(String args) throws ParseException {
        final String trimmedArgs = args.trim();
        if (trimmedArgs.length() < 1) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeletePatientCommand.MESSAGE_USAGE));
        }

        if (!trimmedArgs.startsWith("patient")) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeletePatientCommand.MESSAGE_USAGE));
        }

        final String candidate = trimmedArgs.substring("patient".length()).trim();

        return ParserUtil.parseIndex(candidate);

    }

}
