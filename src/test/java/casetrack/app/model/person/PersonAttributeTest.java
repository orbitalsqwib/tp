package casetrack.app.model.person;

import static casetrack.app.testutil.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class PersonAttributeTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new PersonAttribute(null, null));
        assertThrows(NullPointerException.class, () -> new PersonAttribute("test", null));
        assertThrows(NullPointerException.class, () -> new PersonAttribute(null, "test"));
    }

    @Test
    public void constructor_success() {
        PersonAttribute attribute = new PersonAttribute("name", "value");
        assertEquals(attribute.name, "name");
        assertEquals(attribute.value, "value");
    }

}
