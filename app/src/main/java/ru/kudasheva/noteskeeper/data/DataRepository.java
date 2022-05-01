package ru.kudasheva.noteskeeper.data;

import java.util.List;

import ru.kudasheva.noteskeeper.notebrowse.CommentInfoCard;
import ru.kudasheva.noteskeeper.ui.FriendInfoCard;
import ru.kudasheva.noteskeeper.notebrowse.InfoCard;
import ru.kudasheva.noteskeeper.notebrowse.NoteFullCard;
import ru.kudasheva.noteskeeper.notescroll.NoteShortCard;

public interface DataRepository{
    boolean checkIfUserExist(String name);
    String getUsername();

    boolean signUpNewUser(String userName);
    List<FriendInfoCard> addNewFriend(FriendInfoCard infoCard);
    void addComment(CommentInfoCard info);
    void addNote(NoteData info);

    NoteFullCard getNoteFullCard(String title);

    List<NoteShortCard> getListOfNoteShortCard();
    List<FriendInfoCard> getListOfFriendInfoCards();
    List<InfoCard> getListOfCommentInfoCard();
}
