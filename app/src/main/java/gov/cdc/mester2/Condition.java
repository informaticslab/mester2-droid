package gov.cdc.mester2;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by jason on 2/13/17.
 */

public class Condition extends RealmObject {
    @PrimaryKey
    private String id;

    private String name;
    private Condition parentCondition;
    private RealmList<Condition> childConditions;
    private boolean hasChildren = false;
    private boolean hasNotes = false;
    private Rating chcInitiation;
    private Rating popInitiation;
    private Rating dmpaInitiation;
    private Rating implantsInitiation;
    private Rating lngiudInitiation;
    private Rating cuiudInitiation;
    private Rating chcContinuation;
    private Rating popContinuation;
    private Rating dmpaContinuation;
    private Rating implantsContinuation;
    private Rating lngiudContinuation;
    private Rating cuiudContinuation;

    private RealmList<Note> notes;

    public Condition(){}

    public Condition(String name, RealmList<Condition> childConditions) {
        this.name = name;
        this.id = name;
        this.childConditions = childConditions;
    }

    public Condition(String name, Rating chcRating, Rating popRating, Rating dmpaRating, Rating implantsRating, Rating lngiudRating, Rating cuiudRating, RealmList<Note> notes, Condition parentCondition) {
        this.name = name;
        this.chcContinuation = chcRating;
        this.chcInitiation = chcRating;
        this.popContinuation = popRating;
        this.popInitiation = popRating;
        this.dmpaContinuation = dmpaRating;
        this.dmpaInitiation = dmpaRating;
        this.implantsContinuation = implantsRating;
        this.implantsInitiation = implantsRating;
        this.lngiudContinuation = lngiudRating;
        this.lngiudInitiation = lngiudRating;
        this.cuiudContinuation = cuiudRating;
        this.cuiudInitiation = cuiudRating;

        if (notes != null) {
            this.hasNotes = true;
            this.notes = notes;
        }

        this.parentCondition = parentCondition;

        this.id = "" +parentCondition.id + this.name;

    }

    public Condition(String name, Rating chcInitiation, Rating popInitiation, Rating dmpaInitiation, Rating implantsInitiation, Rating lngiudInitiation, Rating cuiudInitiation, Rating chcContinuation, Rating popContinuation, Rating dmpaContinuation, Rating implantsContinuation, Rating cuiudContinuation, Rating lngiudContinuation, RealmList<Note> notes, Condition parentCondition) {
        this.name = name;

        this.chcInitiation = chcInitiation;
        this.popInitiation = popInitiation;
        this.dmpaInitiation = dmpaInitiation;
        this.implantsInitiation = implantsInitiation;
        this.lngiudInitiation = lngiudInitiation;
        this.cuiudInitiation = cuiudInitiation;
        this.chcContinuation = chcContinuation;
        this.popContinuation = popContinuation;
        this.dmpaContinuation = dmpaContinuation;
        this.implantsContinuation = implantsContinuation;
        this.cuiudContinuation = cuiudContinuation;
        this.lngiudContinuation = lngiudContinuation;

        if (notes != null) {
            this.hasNotes = true;
            this.notes = notes;
        }

        this.parentCondition = parentCondition;

        this.id = "" +parentCondition.id + this.name;
    }

    public boolean hasChildren() {
        return hasChildren;
    }

    public boolean hasNotes() {
        return hasNotes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Condition getParentCondition() {
        return parentCondition;
    }

    public void setParentCondition(Condition parentCondition) {
        this.parentCondition = parentCondition;
    }

    public RealmList<Condition> getChildConditions() {
        return childConditions;
    }

    public void setChildConditions(RealmList<Condition> childConditions) {
        this.childConditions = childConditions;
    }

    public boolean isHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    public boolean isHasNotes() {
        return hasNotes;
    }

    public void setHasNotes(boolean hasNotes) {
        this.hasNotes = hasNotes;
    }

    public Rating getChcInitiation() {
        return chcInitiation;
    }

    public void setChcInitiation(Rating chcInitiation) {
        this.chcInitiation = chcInitiation;
    }

    public Rating getPopInitiation() {
        return popInitiation;
    }

    public void setPopInitiation(Rating popInitiation) {
        this.popInitiation = popInitiation;
    }

    public Rating getDmpaInitiation() {
        return dmpaInitiation;
    }

    public void setDmpaInitiation(Rating dmpaInitiation) {
        this.dmpaInitiation = dmpaInitiation;
    }

    public Rating getImplantsInitiation() {
        return implantsInitiation;
    }

    public void setImplantsInitiation(Rating implantsInitiation) {
        this.implantsInitiation = implantsInitiation;
    }

    public Rating getLngiudInitiation() {
        return lngiudInitiation;
    }

    public void setLngiudInitiation(Rating lngiudInitiation) {
        this.lngiudInitiation = lngiudInitiation;
    }

    public Rating getCuiudInitiation() {
        return cuiudInitiation;
    }

    public void setCuiudInitiation(Rating cuiudInitiation) {
        this.cuiudInitiation = cuiudInitiation;
    }

    public Rating getChcContinuation() {
        return chcContinuation;
    }

    public void setChcContinuation(Rating chcContinuation) {
        this.chcContinuation = chcContinuation;
    }

    public Rating getPopContinuation() {
        return popContinuation;
    }

    public void setPopContinuation(Rating popContinuation) {
        this.popContinuation = popContinuation;
    }

    public Rating getDmpaContinuation() {
        return dmpaContinuation;
    }

    public void setDmpaContinuation(Rating dmpaContinuation) {
        this.dmpaContinuation = dmpaContinuation;
    }

    public Rating getImplantsContinuation() {
        return implantsContinuation;
    }

    public void setImplantsContinuation(Rating implantsContinuation) {
        this.implantsContinuation = implantsContinuation;
    }

    public Rating getLngiudContinuation() {
        return lngiudContinuation;
    }

    public void setLngiudContinuation(Rating lngiudContinuation) {
        this.lngiudContinuation = lngiudContinuation;
    }

    public Rating getCuiudContinuation() {
        return cuiudContinuation;
    }

    public void setCuiudContinuation(Rating cuiudContinuation) {
        this.cuiudContinuation = cuiudContinuation;
    }

    public RealmList<Note> getNotes() {
        return notes;
    }

    public void setNotes(RealmList<Note> notes) {
        this.notes = notes;
    }
}
