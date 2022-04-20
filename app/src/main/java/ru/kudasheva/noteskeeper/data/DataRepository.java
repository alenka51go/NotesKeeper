package ru.kudasheva.noteskeeper.data;

import java.util.List;

import ru.kudasheva.noteskeeper.ui.CommentInfoCard;
import ru.kudasheva.noteskeeper.ui.FriendInfoCard;
import ru.kudasheva.noteskeeper.ui.NoteFullCard;
import ru.kudasheva.noteskeeper.ui.NoteShortCard;

public interface DataRepository{
    boolean checkIfUserExist(String name);
    String getUserName();

    boolean signUpNewUser(String userName);
    List<FriendInfoCard> addNewFriend(FriendInfoCard infoCard);
    List<CommentInfoCard> addNewComment(CommentInfoCard info);

    NoteFullCard getNoteFullCard(String title);

    List<NoteShortCard> getListOfNoteShortCard();
    List<FriendInfoCard> getListOfFriendInfoCards();
    List<CommentInfoCard> getListOfCommentInfoCard();
}
