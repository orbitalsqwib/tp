package casetrack.app.model.person;

import java.util.List;
import java.util.function.Predicate;

import casetrack.app.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person}'s {@code Tag}s match any of the keywords given.
 */
public class TagContainsKeywordsPredicate implements Predicate<Person> {
    private final List<String> keywords;

    public TagContainsKeywordsPredicate(List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public boolean test(Person person) {
        return keywords.stream()
                .anyMatch(keyword -> person.getTags().stream()
                        .anyMatch(tag -> tag.tagName.toLowerCase().contains(keyword.toLowerCase())));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof TagContainsKeywordsPredicate)) {
            return false;
        }

        TagContainsKeywordsPredicate otherTagContainsKeywordsPredicate =
                (TagContainsKeywordsPredicate) other;
        return keywords.equals(otherTagContainsKeywordsPredicate.keywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }
}
