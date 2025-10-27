package casetrack.app.logic.parser;

import static casetrack.app.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static casetrack.app.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static casetrack.app.logic.parser.CliSyntax.PREFIX_EMAIL;
import static casetrack.app.logic.parser.CliSyntax.PREFIX_MEDICAL_INFO;
import static casetrack.app.logic.parser.CliSyntax.PREFIX_NAME;
import static casetrack.app.logic.parser.CliSyntax.PREFIX_NOTE_TEXT;
import static casetrack.app.logic.parser.CliSyntax.PREFIX_PHONE;
import static casetrack.app.logic.parser.CliSyntax.PREFIX_TAG;
import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import casetrack.app.commons.core.index.Index;
import casetrack.app.logic.commands.Command;
import casetrack.app.logic.commands.EditCommand;
import casetrack.app.logic.commands.EditCommand.EditPersonDescriptor;
import casetrack.app.logic.commands.EditNoteCommand;
import casetrack.app.logic.parser.exceptions.ParseException;
import casetrack.app.model.person.Note;
import casetrack.app.model.tag.Tag;

/**
 * Parses input arguments and creates either an EditCommand or EditNoteCommand object
 * based on the arguments provided.
 */
public class EditCommandParser implements Parser<Command> {

    public static final String MESSAGE_INVALID_EDIT_FORMAT =
        "Expected 'edit note <PERSON_INDEX> <NOTE_INDEX> t/<TEXT>' or 'edit <INDEX> [fields...]'";

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
            return parseEditNoteCommand(trimmedArgs);
        } else {
            return parseEditPersonCommand(trimmedArgs);
        }
    }

    /**
     * Parses arguments for editing a note and returns an EditNoteCommand.
     */
    private EditNoteCommand parseEditNoteCommand(String args) throws ParseException {
        // Remove "note " prefix
        String remainingArgs = args.substring(ParserUtil.NOTE_STRING.length() + 1).trim();

        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(" " + remainingArgs, PREFIX_NOTE_TEXT);

        // Preamble should contain two indices: PERSON_INDEX and NOTE_INDEX
        String preamble = argMultimap.getPreamble().trim();
        String[] parts = preamble.split("\\s+");

        if (parts.length != 2) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    EditNoteCommand.MESSAGE_USAGE));
        }

        if (!argMultimap.getValue(PREFIX_NOTE_TEXT).isPresent()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    EditNoteCommand.MESSAGE_USAGE));
        }

        Index personIndex;
        Index noteIndex;
        Note newNote;

        try {
            personIndex = ParserUtil.parseIndex(parts[0]);
            noteIndex = ParserUtil.parseIndex(parts[1]);
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    EditNoteCommand.MESSAGE_USAGE), pe);
        }

        newNote = ParserUtil.parseNote(argMultimap.getValue(PREFIX_NOTE_TEXT).get());

        return new EditNoteCommand(personIndex, noteIndex, newNote);
    }

    /**
     * Parses arguments for editing a person and returns an EditCommand.
     */
    private EditCommand parseEditPersonCommand(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS,
                        PREFIX_MEDICAL_INFO, PREFIX_TAG);

        Index index;

        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE), pe);
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS,
                PREFIX_MEDICAL_INFO);

        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();

        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            editPersonDescriptor.setName(ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get()));
        }
        if (argMultimap.getValue(PREFIX_PHONE).isPresent()) {
            editPersonDescriptor.setPhone(ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE).get()));
        }
        if (argMultimap.getValue(PREFIX_EMAIL).isPresent()) {
            editPersonDescriptor.setEmail(ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL).get()));
        }
        if (argMultimap.getValue(PREFIX_ADDRESS).isPresent()) {
            editPersonDescriptor.setAddress(ParserUtil.parseAddress(argMultimap.getValue(PREFIX_ADDRESS).get()));
        }
        if (argMultimap.getValue(PREFIX_MEDICAL_INFO).isPresent()) {
            editPersonDescriptor.setMedicalInfo(ParserUtil.parseMedicalInfo(
                    argMultimap.getValue(PREFIX_MEDICAL_INFO).get()));
        }
        parseTagsForEdit(argMultimap.getAllValues(PREFIX_TAG)).ifPresent(editPersonDescriptor::setTags);

        if (!editPersonDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditCommand.MESSAGE_NOT_EDITED);
        }

        return new EditCommand(index, editPersonDescriptor);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>} if {@code tags} is non-empty.
     * If {@code tags} contain only one element which is an empty string, it will be parsed into a
     * {@code Set<Tag>} containing zero tags.
     */
    private Optional<Set<Tag>> parseTagsForEdit(Collection<String> tags) throws ParseException {
        assert tags != null;

        if (tags.isEmpty()) {
            return Optional.empty();
        }
        Collection<String> tagSet = tags.size() == 1 && tags.contains("") ? Collections.emptySet() : tags;
        return Optional.of(ParserUtil.parseTags(tagSet));
    }

}
