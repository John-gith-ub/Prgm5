import java.io.PrintWriter;

public class Book extends Media {
    protected int weeksNYT;

    public int getWeeksNYT() {
        return weeksNYT;
    }

    public void setWeeksNYT(int weeksNYT) {
        this.weeksNYT = weeksNYT;
    }

    public void printWeeksNYT(PrintWriter report) {
        report.println("Weeks on NYT Best Seller List: " + weeksNYT);
    }

    @Override
    public String printMedia() {
        return super.printMedia().stripTrailing()
                + "\tWeeks on NYT list: " + weeksNYT + System.lineSeparator();
    }

}
