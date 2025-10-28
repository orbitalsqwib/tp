package casetrack.app.logic.parser;

import static casetrack.app.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import casetrack.app.commons.core.index.Index;
import casetrack.app.logic.commands.Command;
import casetrack.app.logic.commands.DeleteNoteCommand;
import casetrack.app.logic.commands.DeletePatientCommand;
import casetrack.app.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates either a DeleteNoteCommand or DeletePatientCommand object
 * based on the arguments provided.
 */
public class DeleteCommandParser implements Parser<Command> {

    public static final String MESSAGE_INVALID_DELETE_FORMAT =
        "Expected 'delete note <PATIENT_INDEX> <NOTE_INDEX>' or 'delete patient <PATIENT_INDEX>'";

    /**
     * Parses the given {@code String} of arguments and returns either a DeleteNoteCommand
     * or DeletePatientCommand object.
     * @throws ParseException if the user input does not follow the expected format
     */
    public Command parse(String args) throws ParseException {
        final String trimmedArgs = args.trim();

        if (trimmedArgs.startsWith(ParserUtil.NOTE_STRING + " ")) {
            return parseDeleteNoteCommand(trimmedArgs);
        } else if (trimmedArgs.startsWith(ParserUtil.PATIENT_STRING + " ")) {
            return parseDeletePatientCommand(trimmedArgs);
        } else {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_INVALID_DELETE_FORMAT));
        }
    }

    /**
     * Parses arguments for delete note command.
     * @param trimmedArgs the trimmed arguments
     * @return DeleteNoteCommand object
     * @throws ParseException if the arguments are invalid
     */
    private DeleteNoteCommand parseDeleteNoteCommand(String trimmedArgs) throws ParseException {
        String remainingArgs = removeCommandPrefix(trimmedArgs, ParserUtil.NOTE_STRING);
        String[] parts = remainingArgs.split("\\s+");

        if (parts.length != 2) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    DeleteNoteCommand.MESSAGE_USAGE));
        }

        Index personIndex = ParserUtil.parseIndex(parts[0]);
        Index noteIndex = ParserUtil.parseIndex(parts[1]);
        return new DeleteNoteCommand(personIndex, noteIndex);
    }

    /**
     * Parses arguments for delete patient command.
     * @param trimmedArgs the trimmed arguments
     * @return DeletePatientCommand object
     * @throws ParseException if the arguments are invalid
     */
    private DeletePatientCommand parseDeletePatientCommand(String trimmedArgs) throws ParseException {
        String remainingArgs = removeCommandPrefix(trimmedArgs, ParserUtil.PATIENT_STRING);
        String[] parts = remainingArgs.split("\\s+");

        if (parts.length != 1) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    DeletePatientCommand.MESSAGE_USAGE));
        }

        Index index = ParserUtil.parseIndex(parts[0]);
        return new DeletePatientCommand(index);
    }

    /**
     * Removes the command prefix.
     * @param trimmedArgs the trimmed arguments
     * @param commandPrefix the command prefix to remove
     * @return the remaining arguments after removing the prefix
     */
    private String removeCommandPrefix(String trimmedArgs, String commandPrefix) {
        return trimmedArgs.substring(commandPrefix.length() + 1).trim();
    }
}
