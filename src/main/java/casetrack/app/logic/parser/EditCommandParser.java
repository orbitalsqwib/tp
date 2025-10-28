package casetrack.app.logic.parser;

import static casetrack.app.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static java.util.Objects.requireNonNull;

import casetrack.app.logic.commands.Command;
import casetrack.app.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates either an EditCommand or EditNoteCommand object
 * based on the arguments provided.
 */
public class EditCommandParser implements Parser<Command> {

    public static final String MESSAGE_INVALID_EDIT_FORMAT =
        "Expected 'edit note <PATIENT_INDEX> <NOTE_INDEX> t/<TEXT>' or 'edit patient <INDEX> [fields...]'";

    /**
     * Parses the given {@code String} of arguments and returns either an EditCommand
     * or EditNoteCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public Command parse(String args) throws ParseException {
        requireNonNull(args);
        final String trimmedArgs = args.trim();

        // Check if this is an "edit note" command
        if (trimmedArgs.startsWith(ParserUtil.NOTE_STRING + " ")) {
            // Remove "note " prefix and delegate to EditNoteCommandParser
            String remainingArgs = trimmedArgs.substring(ParserUtil.NOTE_STRING.length() + 1).trim();
            return new EditNoteCommandParser().parse(remainingArgs);
        } else if (trimmedArgs.startsWith(ParserUtil.PATIENT_STRING + " ")) {
            // Remove "patient " prefix and delegate to EditPatientCommandParser
            String remainingArgs = trimmedArgs.substring(ParserUtil.PATIENT_STRING.length() + 1).trim();
            return new EditPatientCommandParser().parse(remainingArgs);
        } else {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_INVALID_EDIT_FORMAT));
        }
    }
}
