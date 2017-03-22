package gov.cdc.mester2;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Rating extends RealmObject {
    private String rating;
    private RealmList<Note> notes;

    public Rating() {
    }

    public Rating(String rating) {

        this.rating = rating;
    }

    public Rating(String rating, RealmList<Note> notes) {
        this.rating = rating;
        this.notes = notes;
    }

    public String getRating() {
        return rating;
    }

    public RealmList<Note> getNotes() {
        return notes;
    }
}
