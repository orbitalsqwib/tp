package casetrack.app.logic.commands;

import static casetrack.app.logic.commands.CommandTestUtil.assertCommandSuccess;
import static casetrack.app.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import casetrack.app.model.AddressBook;
import casetrack.app.model.Model;
import casetrack.app.model.ModelManager;
import casetrack.app.model.UserPrefs;

public class ClearCommandTest {

    @Test
    public void execute_emptyAddressBook_success() {
        Model model = new ModelManager();
        Model expectedModel = new ModelManager();
        CommandResult expectedCommandResult = new CommandResult(ClearCommand.MESSAGE_SUCCESS,
                new DetailPanelInstruction(null));

        assertCommandSuccess(new ClearCommand(), model, expectedCommandResult, expectedModel);
    }

    @Test
    public void execute_nonEmptyAddressBook_success() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel.setAddressBook(new AddressBook());
        CommandResult expectedCommandResult = new CommandResult(ClearCommand.MESSAGE_SUCCESS,
                new DetailPanelInstruction(null));

        assertCommandSuccess(new ClearCommand(), model, expectedCommandResult, expectedModel);
    }

}
