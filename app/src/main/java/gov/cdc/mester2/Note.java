package gov.cdc.mester2;

import io.realm.RealmObject;

/**
 * Created by jason on 2/13/17.
 */

public class Note extends RealmObject{
    private String name, text;

    public Note() {
    }

    public Note(String name, String text) {
        this.name = name;
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }
}
