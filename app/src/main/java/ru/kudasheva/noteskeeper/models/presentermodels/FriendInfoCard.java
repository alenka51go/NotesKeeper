package ru.kudasheva.noteskeeper.models.presentermodels;

import java.util.Objects;

public class FriendInfoCard {
    private final String name;

    public FriendInfoCard(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendInfoCard that = (FriendInfoCard) o;
        return Objects.equals(name, that.name);
    }
}
