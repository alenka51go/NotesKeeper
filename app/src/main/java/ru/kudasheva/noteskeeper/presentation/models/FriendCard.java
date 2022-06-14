package ru.kudasheva.noteskeeper.presentation.models;

import java.util.Objects;

public class FriendCard {
    private final String name;

    public FriendCard(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendCard that = (FriendCard) o;
        return Objects.equals(name, that.name);
    }
}
