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
        final String indexGroup = extractIndexArg(args);
        try {
            Index index = ParserUtil.parseIndex(indexGroup);
            return new DeletePatientCommand(index);
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeletePatientCommand.MESSAGE_USAGE), pe);
        }
    }

    /**
     * Normalizes and validates the raw arguments for delete, supporting either
     * {@code <index>} or {@code patient <index>} forms.
     * Returns the string to parse as an index.
     */
    private String extractIndexArg(String args) throws ParseException {
        final String trimmedArgs = (args == null ? "" : args).trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeletePatientCommand.MESSAGE_USAGE));
        }

        if (!trimmedArgs.startsWith("patient")) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeletePatientCommand.MESSAGE_USAGE));
        }

        final String candidate = trimmedArgs.substring("patient".length()).trim();

        if (candidate.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeletePatientCommand.MESSAGE_USAGE));
        }
        return candidate;
    }

}
