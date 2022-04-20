package ru.kudasheva.noteskeeper.data;

import java.util.List;

import ru.kudasheva.noteskeeper.CommentInfoCard;
import ru.kudasheva.noteskeeper.FriendInfoCard;
import ru.kudasheva.noteskeeper.NoteFullCard;
import ru.kudasheva.noteskeeper.NoteShortCard;

public interface DataRepository {
    void setUserName(String name);
    String getUserName();

    int addNewUser(String userName);
    int addNewFriend(String userName);
    int addNewComment(CommentInfoCard info);

    NoteFullCard getNoteFullCard(String title);

    List<NoteShortCard> getListOfNoteShortCard();
    List<FriendInfoCard> getListOfFriendInfoCards();
    List<CommentInfoCard> getListOfCommentInfoCard();
}
