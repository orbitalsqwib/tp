package casetrack.app.logic.parser;

import static casetrack.app.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static casetrack.app.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static casetrack.app.logic.commands.CommandTestUtil.ADDRESS_DESC_BOB;
import static casetrack.app.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static casetrack.app.logic.commands.CommandTestUtil.EMAIL_DESC_BOB;
import static casetrack.app.logic.commands.CommandTestUtil.INVALID_ADDRESS_DESC;
import static casetrack.app.logic.commands.CommandTestUtil.INVALID_EMAIL_DESC;
import static casetrack.app.logic.commands.CommandTestUtil.INVALID_NAME_DESC;
import static casetrack.app.logic.commands.CommandTestUtil.INVALID_PHONE_DESC;
import static casetrack.app.logic.commands.CommandTestUtil.INVALID_TAG_DESC;
import static casetrack.app.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static casetrack.app.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static casetrack.app.logic.commands.CommandTestUtil.PHONE_DESC_BOB;
import static casetrack.app.logic.commands.CommandTestUtil.TAG_DESC_FRIEND;
import static casetrack.app.logic.commands.CommandTestUtil.TAG_DESC_HUSBAND;
import static casetrack.app.logic.commands.CommandTestUtil.VALID_ADDRESS_AMY;
import static casetrack.app.logic.commands.CommandTestUtil.VALID_EMAIL_AMY;
import static casetrack.app.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static casetrack.app.logic.commands.CommandTestUtil.VALID_PHONE_AMY;
import static casetrack.app.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static casetrack.app.logic.commands.CommandTestUtil.VALID_TAG_FRIEND;
import static casetrack.app.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static casetrack.app.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static casetrack.app.logic.parser.CliSyntax.PREFIX_EMAIL;
import static casetrack.app.logic.parser.CliSyntax.PREFIX_PHONE;
import static casetrack.app.logic.parser.CliSyntax.PREFIX_TAG;
import static casetrack.app.logic.parser.CommandParserTestUtil.assertParseFailure;
import static casetrack.app.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static casetrack.app.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static casetrack.app.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static casetrack.app.testutil.TypicalIndexes.INDEX_THIRD_PERSON;

import org.junit.jupiter.api.Test;

import casetrack.app.commons.core.index.Index;
import casetrack.app.logic.Messages;
import casetrack.app.logic.commands.EditCommand;
import casetrack.app.logic.commands.EditCommand.EditPersonDescriptor;
import casetrack.app.logic.commands.EditNoteCommand;
import casetrack.app.model.person.Address;
import casetrack.app.model.person.Email;
import casetrack.app.model.person.Name;
import casetrack.app.model.person.Note;
import casetrack.app.model.person.Phone;
import casetrack.app.model.tag.Tag;
import casetrack.app.testutil.EditPersonDescriptorBuilder;

public class EditCommandParserTest {

    private static final String TAG_EMPTY = " " + PREFIX_TAG;

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE);

    private EditCommandParser parser = new EditCommandParser();

    @Test
    public void parse_missingParts_failure() {
        // no index specified
        assertParseFailure(parser, VALID_NAME_AMY, MESSAGE_INVALID_FORMAT);

        // no field specified
        assertParseFailure(parser, "1", EditCommand.MESSAGE_NOT_EDITED);

        // no index and no field specified
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidPreamble_failure() {
        // negative index
        assertParseFailure(parser, "-5" + NAME_DESC_AMY, MESSAGE_INVALID_FORMAT);

        // zero index
        assertParseFailure(parser, "0" + NAME_DESC_AMY, MESSAGE_INVALID_FORMAT);

        // invalid arguments being parsed as preamble
        assertParseFailure(parser, "1 some random string", MESSAGE_INVALID_FORMAT);

        // invalid prefix being parsed as preamble
        assertParseFailure(parser, "1 i/ string", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidValue_failure() {
        assertParseFailure(parser, "1" + INVALID_NAME_DESC, Name.MESSAGE_CONSTRAINTS); // invalid name
        assertParseFailure(parser, "1" + INVALID_PHONE_DESC, Phone.MESSAGE_CONSTRAINTS); // invalid phone
        assertParseFailure(parser, "1" + INVALID_EMAIL_DESC, Email.MESSAGE_CONSTRAINTS); // invalid email
        assertParseFailure(parser, "1" + INVALID_ADDRESS_DESC, Address.MESSAGE_CONSTRAINTS); // invalid address
        assertParseFailure(parser, "1" + INVALID_TAG_DESC, Tag.MESSAGE_CONSTRAINTS); // invalid tag

        // invalid phone followed by valid email
        assertParseFailure(parser, "1" + INVALID_PHONE_DESC + EMAIL_DESC_AMY, Phone.MESSAGE_CONSTRAINTS);

        // while parsing {@code PREFIX_TAG} alone will reset the tags of the {@code Person} being edited,
        // parsing it together with a valid tag results in error
        assertParseFailure(parser, "1" + TAG_DESC_FRIEND + TAG_DESC_HUSBAND + TAG_EMPTY, Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1" + TAG_DESC_FRIEND + TAG_EMPTY + TAG_DESC_HUSBAND, Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1" + TAG_EMPTY + TAG_DESC_FRIEND + TAG_DESC_HUSBAND, Tag.MESSAGE_CONSTRAINTS);

        // multiple invalid values, but only the first invalid value is captured
        assertParseFailure(parser, "1" + INVALID_NAME_DESC + INVALID_EMAIL_DESC + VALID_ADDRESS_AMY + VALID_PHONE_AMY,
                Name.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_allFieldsSpecified_success() {
        Index targetIndex = INDEX_SECOND_PERSON;
        String userInput = targetIndex.getOneBased() + PHONE_DESC_BOB + TAG_DESC_HUSBAND
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY + NAME_DESC_AMY + TAG_DESC_FRIEND;

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_AMY)
                .withPhone(VALID_PHONE_BOB).withEmail(VALID_EMAIL_AMY).withAddress(VALID_ADDRESS_AMY)
                .withTags(VALID_TAG_HUSBAND, VALID_TAG_FRIEND).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_someFieldsSpecified_success() {
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = targetIndex.getOneBased() + PHONE_DESC_BOB + EMAIL_DESC_AMY;

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withPhone(VALID_PHONE_BOB)
                .withEmail(VALID_EMAIL_AMY).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_oneFieldSpecified_success() {
        // name
        Index targetIndex = INDEX_THIRD_PERSON;
        String userInput = targetIndex.getOneBased() + NAME_DESC_AMY;
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_AMY).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // phone
        userInput = targetIndex.getOneBased() + PHONE_DESC_AMY;
        descriptor = new EditPersonDescriptorBuilder().withPhone(VALID_PHONE_AMY).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // email
        userInput = targetIndex.getOneBased() + EMAIL_DESC_AMY;
        descriptor = new EditPersonDescriptorBuilder().withEmail(VALID_EMAIL_AMY).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // address
        userInput = targetIndex.getOneBased() + ADDRESS_DESC_AMY;
        descriptor = new EditPersonDescriptorBuilder().withAddress(VALID_ADDRESS_AMY).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // tags
        userInput = targetIndex.getOneBased() + TAG_DESC_FRIEND;
        descriptor = new EditPersonDescriptorBuilder().withTags(VALID_TAG_FRIEND).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_multipleRepeatedFields_failure() {
        // More extensive testing of duplicate parameter detections is done in
        // AddCommandParserTest#parse_repeatedNonTagValue_failure()

        // valid followed by invalid
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = targetIndex.getOneBased() + INVALID_PHONE_DESC + PHONE_DESC_BOB;

        assertParseFailure(parser, userInput, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // invalid followed by valid
        userInput = targetIndex.getOneBased() + PHONE_DESC_BOB + INVALID_PHONE_DESC;

        assertParseFailure(parser, userInput, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // mulltiple valid fields repeated
        userInput = targetIndex.getOneBased() + PHONE_DESC_AMY + ADDRESS_DESC_AMY + EMAIL_DESC_AMY
                + TAG_DESC_FRIEND + PHONE_DESC_AMY + ADDRESS_DESC_AMY + EMAIL_DESC_AMY + TAG_DESC_FRIEND
                + PHONE_DESC_BOB + ADDRESS_DESC_BOB + EMAIL_DESC_BOB + TAG_DESC_HUSBAND;

        assertParseFailure(parser, userInput,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS));

        // multiple invalid values
        userInput = targetIndex.getOneBased() + INVALID_PHONE_DESC + INVALID_ADDRESS_DESC + INVALID_EMAIL_DESC
                + INVALID_PHONE_DESC + INVALID_ADDRESS_DESC + INVALID_EMAIL_DESC;

        assertParseFailure(parser, userInput,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS));
    }

    @Test
    public void parse_resetTags_success() {
        Index targetIndex = INDEX_THIRD_PERSON;
        String userInput = targetIndex.getOneBased() + TAG_EMPTY;

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withTags().build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    // ==================== Edit Note Tests ====================

    @Test
    public void parse_validNoteArgs_returnsEditNoteCommand() {
        Note newNote = new Note("Updated note content");
        EditNoteCommand expectedCommand = new EditNoteCommand(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON, newNote);
        assertParseSuccess(parser, "note 1 2 t/Updated note content", expectedCommand);
    }

    @Test
    public void parse_noteWithInvalidFormat_throwsParseException() {
        // only one index
        assertParseFailure(parser, "note 1 t/Updated note",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditNoteCommand.MESSAGE_USAGE));

        // no indices
        assertParseFailure(parser, "note t/Updated note",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditNoteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_noteWithMissingNoteText_throwsParseException() {
        // missing t/ prefix
        assertParseFailure(parser, "note 1 2",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditNoteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_noteWithEmptyNoteText_throwsParseException() {
        // empty note text
        assertParseFailure(parser, "note 1 2 t/", Note.MESSAGE_CONSTRAINTS);

        // whitespace only note text
        assertParseFailure(parser, "note 1 2 t/   ", Note.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_noteWithInvalidIndices_throwsParseException() {
        // invalid person index (not a number)
        assertParseFailure(parser, "note abc 2 t/Updated note",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditNoteCommand.MESSAGE_USAGE));

        // invalid note index (not a number)
        assertParseFailure(parser, "note 1 xyz t/Updated note",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditNoteCommand.MESSAGE_USAGE));

        // negative person index
        assertParseFailure(parser, "note -1 2 t/Updated note",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditNoteCommand.MESSAGE_USAGE));

        // zero person index
        assertParseFailure(parser, "note 0 2 t/Updated note",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditNoteCommand.MESSAGE_USAGE));

        // negative note index
        assertParseFailure(parser, "note 1 -2 t/Updated note",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditNoteCommand.MESSAGE_USAGE));

        // zero note index
        assertParseFailure(parser, "note 1 0 t/Updated note",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditNoteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_noteWithTooManyIndices_throwsParseException() {
        // three indices instead of two
        assertParseFailure(parser, "note 1 2 3 t/Updated note",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditNoteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_noteWithValidLongText_success() {
        String longNoteText = "This is a very long note that contains a lot of information "
                + "about the patient's condition and treatment plan.";
        Note newNote = new Note(longNoteText);
        EditNoteCommand expectedCommand = new EditNoteCommand(INDEX_FIRST_PERSON, INDEX_FIRST_PERSON, newNote);
        assertParseSuccess(parser, "note 1 1 t/" + longNoteText, expectedCommand);
    }

    @Test
    public void parse_noteWithSpecialCharacters_success() {
        String noteWithSpecialChars = "Patient mentioned: 50% improvement, $100 payment, & follow-up needed!";
        Note newNote = new Note(noteWithSpecialChars);
        EditNoteCommand expectedCommand = new EditNoteCommand(INDEX_SECOND_PERSON, INDEX_FIRST_PERSON, newNote);
        assertParseSuccess(parser, "note 2 1 t/" + noteWithSpecialChars, expectedCommand);
    }
}
