package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.image.ImageDetailsList;
import seedu.address.model.person.Address;
import seedu.address.model.person.DeadlineList;
import seedu.address.model.person.Email;
import seedu.address.model.person.Favourite;
import seedu.address.model.person.HighImportance;
import seedu.address.model.person.Name;
import seedu.address.model.person.Notes;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

/**
 * Unassigns a tag to a contact in the address book.
 */
public class UnassignTagCommand extends Command implements DetailedViewExecutable {

    public static final String COMMAND_WORD = "unassign";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Unassigns a tag with a given tag name "
            + "(case-insensitive) to a contact identified by a given index number used in the displayed "
            + "contacts list.\n"
            + "Parameters: INDEX (must be a positive integer), TAGNAME"
            + "Example: " + COMMAND_WORD + " 1 friends";

    public static final String MESSAGE_SUCCESS = "Tag successfully unassigned: %1$s";
    public static final String MESSAGE_NOT_TAGGED = "This tag was not assigned to this person previously";
    public static final String MESSAGE_UNKNOWN_TAG = "Tag '%1$s' has not been created yet.";

    private final String tagName;
    private final Index targetIndex;

    /**
     * Creates an UnassignTagCommand to un-assign a {@code Tag} from a {@code Person}.
     * @param targetIndex the index of the contact specified.
     * @param tagName the name of the Tag.
     */
    public UnassignTagCommand(Index targetIndex, String tagName) {
        this.tagName = tagName;
        this.targetIndex = targetIndex;
    }

    /**
     * Creates an UnassignTagCommand to un-assign a {@code Tag} from the {@code Person}
     * in detailed view.
     * @param tagName the name of the Tag.
     */
    public UnassignTagCommand(String tagName) {
        this.tagName = tagName;
        this.targetIndex = null;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Tag newTag = new Tag(tagName);
        boolean tagHasBeenCreated = model.hasTag(newTag);

        if (!tagHasBeenCreated) {
            throw new CommandException(String.format(MESSAGE_UNKNOWN_TAG, tagName));
        }

        List<Person> lastShownList = model.getSortedPersonList();
        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(targetIndex.getZeroBased());

        if (!canRemoveTag(personToEdit, newTag)) {
            throw new CommandException(MESSAGE_NOT_TAGGED);
        }

        Person editedPerson = removeTagFromNewPerson(personToEdit, newTag);
        model.setPerson(personToEdit, editedPerson);
        return new CommandResult(String.format(MESSAGE_SUCCESS, editedPerson));
    }

    @Override
    public CommandResult executeInDetailedView(Model model) throws CommandException {
        requireNonNull(model);
        Tag newTag = new Tag(tagName);
        boolean tagHasBeenCreated = model.hasTag(newTag);

        if (!tagHasBeenCreated) {
            throw new CommandException(String.format(MESSAGE_UNKNOWN_TAG, tagName));
        }

        Person personToEdit = model.getDetailedContactViewPerson();

        if (!canRemoveTag(personToEdit, newTag)) {
            throw new CommandException(MESSAGE_NOT_TAGGED);
        }

        Person editedPerson = removeTagFromNewPerson(personToEdit, newTag);
        model.setPerson(personToEdit, editedPerson);
        model.setDetailedContactView(editedPerson);
        return new CommandResult(String.format(MESSAGE_SUCCESS, editedPerson),
                CommandResult.SpecialCommandResult.DETAILED_VIEW);
    }

    /**
     * Removes a specific {@code Tag} from the {@code Set<Tag>} of a {@code Person}.
     * @param personToEdit the person to remove the tag from.
     * @param newTag the tag to remove.
     * @return the person with the removed tag.
     */
    private static Person removeTagFromNewPerson(Person personToEdit, Tag newTag) {
        requireNonNull(personToEdit);
        requireNonNull(newTag);

        Name name = personToEdit.getName();
        Phone phone = personToEdit.getPhone();
        Email email = personToEdit.getEmail();
        Address address = personToEdit.getAddress();
        DeadlineList deadlines = personToEdit.getDeadlines();
        Notes notes = personToEdit.getNotes();
        Set<Tag> tags = personToEdit.getTags();
        Favourite favourite = personToEdit.getFavouriteStatus();
        HighImportance highImportance = personToEdit.getHighImportanceStatus();
        ImageDetailsList images = personToEdit.getImageDetailsList();

        Set<Tag> newTags = new HashSet<>(tags);
        newTags.remove(newTag);

        return new Person(name, phone, email, address, deadlines, notes, newTags, favourite, highImportance, images);
    }

    /**
     * Checks if a specific {@code Tag} can be removed from the {@code Set<Tag>} of a {@code Person}.
     * @param personToEdit the person to remove the tag from.
     * @param newTag the tag to remove.
     * @return boolean value of whether the tag can be removed.
     */
    private static boolean canRemoveTag(Person personToEdit, Tag newTag) {
        requireNonNull(personToEdit);
        requireNonNull(newTag);
        Set<Tag> tags = personToEdit.getTags();
        Set<Tag> newTags = new HashSet<>(tags);
        return newTags.contains(newTag);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof UnassignTagCommand)) {
            return false;
        }

        UnassignTagCommand e = (UnassignTagCommand) other;
        return Objects.equals(targetIndex, e.targetIndex)
                && this.tagName.equals(e.tagName);
    }
}
