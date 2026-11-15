public class Song extends Media {
    protected boolean top40;

    public boolean isTop40() {
        return top40;
    }

    public void setTop40(boolean top40) {
        this.top40 = top40;
    }

    public String getArtist() {
        return top40 ? "Top 40 Artist" : "Non-Top 40 Artist";
    }

    @Override
    public String printMedia() {
        return super.printMedia().stripTrailing()
                + "\tTop 40: " + top40 + System.lineSeparator();
    }

}
