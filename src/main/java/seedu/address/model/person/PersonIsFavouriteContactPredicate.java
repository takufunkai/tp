package seedu.address.model.person;

import java.util.function.Predicate;


/**
 * Tests that a {@code Person} is a {@code Favourite} contact.
 */
public class PersonIsFavouriteContactPredicate implements Predicate<Person> {
    @Override
    public boolean test(Person person) {
        return person.getFavouriteStatus().isFavourite();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof NameContainsKeywordsPredicate);
    }
}
