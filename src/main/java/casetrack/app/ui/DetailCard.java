package casetrack.app.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import casetrack.app.model.person.PersonAttribute;

/**
 * An UI component that displays information of a {@code Person}.
 */
public class DetailCard extends UiPart<Region> {

    private static final String FXML = "DetailListCard.fxml";

    public final PersonAttribute attribute;

    @FXML
    private Label title;
    @FXML
    private Label value;

    /**
     * Creates a {@code PersonCode} with the given {@code Person} and index to display.
     */
    public DetailCard(PersonAttribute attribute, int displayedIndex) {
        super(FXML);
        this.attribute = attribute;
        title.setText(attribute.name);
        value.setText(attribute.value);
    }
}
