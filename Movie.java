import java.io.PrintWriter;

public class Movie extends Media {
    protected String stars;

    public String getStars() {
        return stars;
    }

    public void setStars(String stars) {
        this.stars = stars;
    }

    public void printStars(PrintWriter report) {
        report.println(stars);
    }

    @Override
    public String printMedia() {
        return super.printMedia().stripTrailing()
                + "\tStars: " + stars + System.lineSeparator();
    }
}
