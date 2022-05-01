package ru.kudasheva.noteskeeper.data;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.kudasheva.noteskeeper.notebrowse.CommentInfoCard;
import ru.kudasheva.noteskeeper.ui.FriendInfoCard;
import ru.kudasheva.noteskeeper.notebrowse.InfoCard;
import ru.kudasheva.noteskeeper.notebrowse.NoteFullCard;
import ru.kudasheva.noteskeeper.notescroll.NoteShortCard;

public class StubDataRepository implements DataRepository {
    private static final String TAG = StubDataRepository.class.getSimpleName();

    private String username = "";

    @Override
    public boolean checkIfUserExist(String name) {
        username = name;
        return true;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean signUpNewUser(String userName) {
        return true;
    }

    @Override
    public List<FriendInfoCard> addNewFriend(FriendInfoCard infoCard) {
        if (stubFriendsInfoCard.contains(infoCard)) {
            return null;
        }
        stubFriendsInfoCard.add(infoCard);
        return stubFriendsInfoCard;
    }

    @Override
    public void addComment(CommentInfoCard info) {
        Log.d(TAG, info.getDate());
        Log.d(TAG, info.getName());
        Log.d(TAG, info.getText());
        stubCommentInfoCards.add(info);
    }

    @Override
    public void addNote(NoteData info) {

    }

    @Override
    public NoteFullCard getNoteFullCard(String title) {
        return stubNoteFullCard;
    }

    @Override
    public List<NoteShortCard> getListOfNoteShortCard() {
        return stubNoteShortCards;
    }

    @Override
    public List<FriendInfoCard> getListOfFriendInfoCards() {
        return stubFriendsInfoCard;
    }

    @Override
    public List<InfoCard> getListOfCommentInfoCard() {
        return stubCommentInfoCards;
    }

    private final List<FriendInfoCard> stubFriendsInfoCard = Arrays.asList(
            new FriendInfoCard("Bob"),
            new FriendInfoCard("Alice"),
            new FriendInfoCard("Ron"),
            new FriendInfoCard("Harry"),
            new FriendInfoCard("Hermione"),
            new FriendInfoCard("Draco"),
            new FriendInfoCard("Dobby"),
            new FriendInfoCard("Volodia"),
            new FriendInfoCard("Lyona")
    );

    private final List<NoteShortCard> stubNoteShortCards = Arrays.asList(
            new NoteShortCard("First note", "10.01.22"),
            new NoteShortCard("Second note", "10.01.22"),
            new NoteShortCard("Third note", "10.01.22"),
            new NoteShortCard("Fourth note", "10.01.22"),
            new NoteShortCard("Fifth note", "10.01.22"),
            new NoteShortCard("Sixth note", "10.01.22"),
            new NoteShortCard("Seventh note", "10.01.22"),
            new NoteShortCard("Eighth note", "10.01.22"),
            new NoteShortCard("Ninth note", "10.01.22"),
            new NoteShortCard("Tenth note", "10.01.22"),
            new NoteShortCard("Eleventh note", "10.01.22"),
            new NoteShortCard("Twelfth note", "10.01.22")
    );

    private final List<InfoCard> stubCommentInfoCards = new ArrayList<>(Arrays.asList(
            new CommentInfoCard("Bob", "10.01.11", "Wow!"),
            new CommentInfoCard("Alice", "10.01.11", "Cool!"),
            new CommentInfoCard("Ron", "11.01.11", "Aunt Petunia often said that Dudley looked like a baby angel - Harry often said that Dudley looked like a pig in a wig."),
            new CommentInfoCard("Harry", "12.01.11", "booble!"),
            new CommentInfoCard("Hermione", "13.01.11", "levIosa not leviosA!"),
            new CommentInfoCard("Draco", "10.03.11", "Mudblood"),
            new CommentInfoCard("Dobby", "11.01.11", "The owner gave Dobby a sock!"),
            new CommentInfoCard("Volodia", "14.01.11", "Avada!")
    ));

    NoteFullCard stubNoteFullCard = new NoteFullCard("Harry Potter and the Philosopher's Stone is the first novel in the Harry Potter series written by J. K. Rowling. The book was first published " +
            "on 26 June 1997[1] by Bloomsbury in London and was later made into a film of the same name.\n" +
            "The book was released in the United States under the name Harry Potter and the Sorcerer's Stone because the publishers were concerned that most American readers would not be familiar enough with the term \"Philosopher's Stone\". " +
            "However, this decision led to criticism by the British public who felt it shouldn't be changed due to the fact it was an English book.",
            "Alena Kudasheva",
            "12.12.12");
}
