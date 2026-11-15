import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Media {

    protected char type;
    protected String title;
    protected String name;
    protected int rating;
    protected String genre;
    protected int length;
    protected int yearReleased;
    public static int[] mediaCounts = { 0, 0, 0 };
    private List<String> extraFields;

    // default constructor
    public Media() {
        this.type = ' ';
        this.title = " ";
        this.name = " ";
        this.rating = -1;
        this.genre = " ";
        this.length = -1;
        this.yearReleased = -1;
        this.extraFields = new ArrayList<>();
    }

    // overloaded constructor
    public Media(char type, String title, String name, int rating, String genre,
            int length, int yearReleased) {
        this(type, title, name, rating, genre, length, yearReleased,
                Collections.emptyList());
    }

    public Media(char type, String title, String name, int rating, String genre,
            int length, int yearReleased, List<String> extraFields) {
        this.type = type;
        this.title = title;
        this.name = name;
        this.rating = rating;
        this.genre = genre;
        this.length = length;
        this.yearReleased = yearReleased;
        this.extraFields = new ArrayList<>(extraFields);
    }

    // getters (accessors)
    public char getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getName() {
        return name;
    }

    public int getRating() {
        return rating;
    }

    public String getGenre() {
        return genre;
    }

    public int getLength() {
        return length;
    }

    public int getYearReleased() {
        return yearReleased;
    }

    public List<String> getExtraFields() {
        return new ArrayList<>(extraFields);
    }

    public void setExtraFields(List<String> extraFields) {
        this.extraFields = new ArrayList<>(extraFields);
    }

    // setters (mutators)

    public String printMedia() {
        String out = String.format("%-37s%10d%10d     %-23s%7s\n", title, yearReleased, rating, genre, type);
        return out;
    }

    public void setType(char type) {
        this.type = type;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setYearReleased(int yearReleased) {
        this.yearReleased = yearReleased;
    }
}