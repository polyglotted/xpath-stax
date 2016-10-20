package org.polyglotted.xpathstax.bind;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
@XmlRootElement(name = "book")
public class Book {

    @XmlEnum
    public enum Genre {
        @XmlEnumValue("Computer Science")
        Computer, Fantasy, Romance, Horror,
        @XmlEnumValue("Science Fiction")
        SciFi
    }

    @XmlEnum
    public enum Type {
        Hardcover, Softback, Bounded
    }

    @XmlAttribute
    private String id;

    @XmlElement(name = "author")
    private String authorName;

    @XmlElement
    private String title;

    @XmlElement
    private double price;

    @XmlElement
    private Genre genre;

    @XmlElement(name = "type")
    private Set<Type> types;

    @XmlElement(name = "comment")
    private ArrayList<String> comments;

    @XmlElement(name = "description")
    private Desc description;

    @XmlElement(name = "revision")
    private List<Revision> revisions;

    public String getAuthor() {
        return authorName;
    }

    public void setAuthor(String author) {
        this.authorName = author;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Revision> getRevisions() {
        return revisions;
    }

    public void setRevisions(List<Revision> revisions) {
        this.revisions = revisions;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public void setTypes(Set<Type> types) {
        this.types = types;
    }

    public Set<Type> getTypes() {
        return types;
    }

    public void setDescription(Desc description) {
        this.description = description;
    }

    public Desc getDescription() {
        return description;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public ArrayList<String> getComments() {
        return comments;
    }

    public void setComments(ArrayList<String> comments) {
        this.comments = comments;
    }
}
