package casetrack.app.logic.commands;

import static casetrack.app.logic.commands.CommandTestUtil.assertCommandFailure;
import static casetrack.app.logic.commands.CommandTestUtil.assertCommandSuccess;
import static casetrack.app.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import casetrack.app.logic.Messages;
import casetrack.app.model.Model;
import casetrack.app.model.ModelManager;
import casetrack.app.model.UserPrefs;
import casetrack.app.model.person.Person;
import casetrack.app.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code AddCommand}.
 */
public class AddCommandIntegrationTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_newPerson_success() {
        Person validPerson = new PersonBuilder().build();

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(validPerson);

        assertCommandSuccess(new AddCommand(validPerson), model,
                String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(validPerson)),
                expectedModel);
    }

    @Test
    public void execute_duplicatePerson_throwsCommandException() {
        Person personInList = model.getAddressBook().getPersonList().get(0);
        assertCommandFailure(new AddCommand(personInList), model,
                AddCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_duplicateNameAndPhone_throwsCommandException() {
        Person personInList = model.getAddressBook().getPersonList().get(0);
        // Create a person with the same name and phone but different email
        Person personWithSameNameAndPhone = new PersonBuilder(personInList)
                .withEmail("different@example.com")
                .build();
        assertCommandFailure(new AddCommand(personWithSameNameAndPhone), model,
                AddCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_duplicateNameAndPhoneDifferentCase_throwsCommandException() {
        Person personInList = model.getAddressBook().getPersonList().get(0);
        // Create a person with the same phone and name in different case
        Person personWithSameNameDifferentCase = new PersonBuilder(personInList)
                .withName(personInList.getName().toString().toLowerCase())
                .withEmail("different@example.com")
                .build();
        assertCommandFailure(new AddCommand(personWithSameNameDifferentCase), model,
                AddCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_duplicateNameAndPhoneExtraWhitespace_throwsCommandException() {
        Person personInList = model.getAddressBook().getPersonList().get(0);
        // Create a person with the same phone and name with extra whitespace
        Person personWithExtraWhitespace = new PersonBuilder(personInList)
                .withName(personInList.getName().toString().replaceAll(" ", "  "))
                .withEmail("different@example.com")
                .build();
        assertCommandFailure(new AddCommand(personWithExtraWhitespace), model,
                AddCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_sameNameDifferentPhone_success() {
        Person personInList = model.getAddressBook().getPersonList().get(0);
        // Create a person with the same name but different phone (should be allowed)
        Person personWithSameName = new PersonBuilder()
                .withName(personInList.getName().toString())
                .withPhone("99999999")
                .withEmail("different@example.com")
                .build();

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(personWithSameName);

        assertCommandSuccess(new AddCommand(personWithSameName), model,
                String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(personWithSameName)),
                expectedModel);
    }

    @Test
    public void execute_samePhoneDifferentName_success() {
        Person personInList = model.getAddressBook().getPersonList().get(0);
        // Create a person with the same phone but different name (should be allowed)
        Person personWithSamePhone = new PersonBuilder()
                .withName("Different Name")
                .withPhone(personInList.getPhone().toString())
                .withEmail("different@example.com")
                .build();

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(personWithSamePhone);

        assertCommandSuccess(new AddCommand(personWithSamePhone), model,
                String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(personWithSamePhone)),
                expectedModel);
    }

}
