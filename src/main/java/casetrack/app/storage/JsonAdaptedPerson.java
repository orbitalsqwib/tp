package casetrack.app.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import casetrack.app.commons.exceptions.IllegalValueException;
import casetrack.app.model.person.Address;
import casetrack.app.model.person.Email;
import casetrack.app.model.person.Income;
import casetrack.app.model.person.MedicalInfo;
import casetrack.app.model.person.Name;
import casetrack.app.model.person.Note;
import casetrack.app.model.person.Person;
import casetrack.app.model.person.Phone;
import casetrack.app.model.tag.Tag;

/**
 * Jackson-friendly version of {@link Person}.
 */
class JsonAdaptedPerson {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Person's %s field is missing!";

    private final String name;
    private final String phone;
    private final String email;
    private final String address;
    private final String income;
    private final String medicalInfo;
    private final List<JsonAdaptedTag> tags = new ArrayList<>();
    private final List<String> notes = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedPerson} with the given person details.
     */
    @JsonCreator
    public JsonAdaptedPerson(@JsonProperty("name") String name,
            @JsonProperty("phone") String phone, @JsonProperty("email") String email,
            @JsonProperty("address") String address, @JsonProperty("income") String income,
            @JsonProperty("medicalInfo") String medicalInfo,
            @JsonProperty("tags") List<JsonAdaptedTag> tags,
            @JsonProperty("notes") List<String> notes) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.income = income;
        this.medicalInfo = medicalInfo;
        if (tags != null) {
            this.tags.addAll(tags);
        }
        if (notes != null) {
            this.notes.addAll(notes);
        }
    }

    /**
     * Converts a given {@code Person} into this class for Jackson use.
     */
    public JsonAdaptedPerson(Person source) {
        name = source.getName().fullName;
        phone = source.getPhone().value;
        email = source.getEmail().value;
        address = source.getAddress().value;
        // Store income as raw numeric string to keep JSON parseable by Income.isValidIncome
        income = source.getIncome().toPlainString();
        medicalInfo = source.getMedicalInfo().toString();
        tags.addAll(source.getTags().stream()
                .map(JsonAdaptedTag::new)
                .collect(Collectors.toList()));
        notes.addAll(source.getNotes().stream()
                .map(note -> note.value)
                .collect(Collectors.toList()));
    }

    /**
     * Converts this Jackson-friendly adapted person object into the model's {@code Person} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person.
     */
    public Person toModelType() throws IllegalValueException {
        List<Tag> personTags = tags.stream()
                .map(tag -> {
                    try {
                        return tag.toModelType();
                    } catch (IllegalValueException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

        List<Note> personNotes = notes.stream()
                .map(note -> validateAndCreateNote(note))
                .collect(Collectors.toList());

        Name modelName = validateAndCreate(name, Name::isValidName, Name::new,
                Name.class.getSimpleName(), Name.MESSAGE_CONSTRAINTS);
        Phone modelPhone = validateAndCreate(phone, Phone::isValidPhone, Phone::new,
                Phone.class.getSimpleName(), Phone.MESSAGE_CONSTRAINTS);
        Email modelEmail = validateAndCreate(email, Email::isValidEmail, Email::new,
                Email.class.getSimpleName(), Email.MESSAGE_CONSTRAINTS);
        Address modelAddress = validateAndCreate(address, Address::isValidAddress, Address::new,
                Address.class.getSimpleName(), Address.MESSAGE_CONSTRAINTS);
        Income modelIncome = validateAndCreate(income, Income::isValidIncome, Income::new,
                Income.class.getSimpleName(), Income.MESSAGE_CONSTRAINTS);
        MedicalInfo modelMedicalInfo = validateAndCreate(medicalInfo != null ? medicalInfo : "-",
                MedicalInfo::isValidMedicalInfo, MedicalInfo::new,
                MedicalInfo.class.getSimpleName(), MedicalInfo.MESSAGE_CONSTRAINTS);

        return new Person(modelName, modelPhone, modelEmail, modelAddress, modelIncome,
                modelMedicalInfo, new HashSet<>(personTags), personNotes);
    }

    private Note validateAndCreateNote(String noteValue) {
        if (noteValue == null) {
            throw new RuntimeException(new IllegalValueException(
                    String.format(MISSING_FIELD_MESSAGE_FORMAT, Note.class.getSimpleName())));
        }
        if (!Note.isValidNote(noteValue)) {
            throw new RuntimeException(new IllegalValueException(Note.MESSAGE_CONSTRAINTS));
        }
        return new Note(noteValue);
    }

    private <T> T validateAndCreate(String value, java.util.function.Predicate<String> validator,
            java.util.function.Function<String, T> constructor, String typeName, String errorMessage)
            throws IllegalValueException {
        if (value == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, typeName));
        }
        if (!validator.test(value)) {
            throw new IllegalValueException(errorMessage);
        }
        return constructor.apply(value);
    }

}
