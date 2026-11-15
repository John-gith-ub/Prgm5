import java.util.ArrayList;
import java.io.File;
import java.util.Scanner;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public abstract class Main {

    public static void main(String args[]) {
        /// Cde that calls medialist reader and mediacommand
        System.out.println("Welcome to Media Library");
        Scanner response = new Scanner(System.in);
        System.out.println("Please input file name: ");
        String filename = response.nextLine();
        try (PrintWriter errors = new PrintWriter("Errors.txt")) {
            medialistreader(filename, errors);
            mediaCommands("mediaCommands.txt", "Report.txt", errors);
        } catch (FileNotFoundException e) {
            System.err.println("Could not create Errors.txt: " + e.getMessage());
        }

    }

    /// Initializes mediaist array list
    private static final List<Media> mediaList = new ArrayList<>();

    public static void medialistreader(String filename, PrintWriter errors) {
        try (BufferedReader mediareader = new BufferedReader(new FileReader(filename))) {
            String line = " ";
            while ((line = mediareader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                if (line.equalsIgnoreCase("Q")) {
                    break;
                }
                /// Checks length and contents of lines
                String[] fields = line.split(",", -1);
                if (fields[0].trim().equals("Q")) {
                    break;
                }
                if (fields.length < 7 || fields[0].trim().isEmpty()) {
                    logRecordError(errors, line, "Previous record has an invalid number of fields.");
                    continue;
                }
                try {
                    /// Collects all remaining columns as strings; there may be none or many.
                    List<String> extraFields = new ArrayList<>();
                    boolean invalidExtraField = false;
                    for (int i = 7; i < fields.length; i++) {
                        extraFields.add(fields[i].trim());
                        /// Trims every value found after year, and checks to ensure there is
                        /// an existing string, further checks if existing string can be converted into
                        /// an integer
                        if (fields[i].trim().isEmpty()) {
                            errors.println("Blank extra field " + (fields[i].trim()) + " in " + filename
                                    + "; line: " + line);
                            invalidExtraField = true;
                        } else if (fields[i].trim().matches("[+-]?\\d+")) {
                            // This value looks like an integer.
                            try {
                                /// Ensures that the integer at the end of entries tarting witb S are either 0
                                /// or 1
                                int parsedvalue = Integer.parseInt(fields[i].trim());
                                if (fields[0].trim().charAt(0) == 'S' && ((parsedvalue != 0) && (parsedvalue != 1))) {
                                    errors.println("Invalid Top40 value: " + parsedvalue + " in " + filename
                                            + "; line: " + line);
                                    invalidExtraField = true;
                                }
                                /// Ensures entries starting with B have a numeric value abvove 0
                                if (fields[0].trim().charAt(0) == 'B' && (parsedvalue < 0)) {
                                    errors.println("Invalid WEEKNYT value: " + parsedvalue + " in " + filename
                                            + "; line: " + line);
                                    invalidExtraField = true;
                                }
                            } catch (NumberFormatException e) {
                                continue;
                            }
                        }
                    }
                    if (invalidExtraField) {
                        logRecordError(errors, line, "Previous record has an invalid extra field.");
                        continue;
                    }

                    char mediaType = fields[0].trim().charAt(0);
                    int rating = Integer.parseInt(fields[3].trim());
                    int length = Integer.parseInt(fields[5].trim());
                    int yearReleased = Integer.parseInt(fields[6].trim());

                    /// Checks that int values in the lines are above 0
                    if (rating <= 0 || length <= 0 || yearReleased <= 0) {
                        logRecordError(errors, line, "Previous record has a non-positive numeric value.");
                        continue;
                    }
                    /// Error messsage if rating above 10
                    if (rating > 10) {
                        logRecordError(errors, line, "Previous record has an error in the rating.");
                        continue;
                    }
                    /// error message of reease year is outside 1920-2024
                    if (yearReleased < 1920 || yearReleased > 2024) {
                        logRecordError(errors, line, "Previous record has an error in year.");
                        continue;
                    }
                    /// Error message if the type is anyhting other than m,b or s
                    if (mediaType != 'M' && mediaType != 'S' && mediaType != 'B') {
                        logRecordError(errors, line, "Previous record has an invalid media type.");
                        continue;
                    }
                    if ((mediaType == 'S' || mediaType == 'B') && extraFields.size() != 1) {
                        logRecordError(errors, line, "Previous record has an invalid number of extra fields.");
                        continue;
                    }
                    // Creates the media variable
                    Media media;
                    /// Creates movie variable referncing the string stored in extraFields
                    /// and assigns it to media
                    if (mediaType == 'M') {
                        Movie movie = new Movie();
                        movie.setStars(String.join(", ", extraFields));
                        media = movie;
                        /// Creates song variable rferncing the boolean value stores in
                        /// extraFields and assigns it to media
                    } else if (mediaType == 'S') {
                        Song song = new Song();
                        song.setTop40(Integer.parseInt(extraFields.get(0)) == 1);
                        media = song;
                        /// Creates book variable referencing weeksNYT
                        /// and assigns its value to media
                    } else if (mediaType == 'B') {
                        Book book = new Book();
                        book.setWeeksNYT(Integer.parseInt(extraFields.get(0)));
                        media = book;
                    } else {
                        errors.println("Invalid media type: " + mediaType);
                        continue;
                    }
                    /// initializes values for the media constructor
                    media.setType(mediaType);
                    media.setTitle(fields[1].trim());
                    media.setName(fields[2].trim());
                    media.setRating(rating);
                    media.setGenre(fields[4].trim());
                    media.setLength(length);
                    media.setYearReleased(yearReleased);
                    media.setExtraFields(extraFields);

                    /// adds line to medialist
                    mediaList.add(media);
                } catch (NumberFormatException e) {
                    logRecordError(errors, line,
                            "Previous record has an java.lang.NumberFormatException: "
                                    + e.getMessage() + " error");
                }
            }
        } catch (IOException e) {
            errors.println("Could not read media file " + filename + ": " + e.getMessage());
        }

    }

    public static void mediaCommands(String filename, String reportfile, PrintWriter errors) {
        try (BufferedReader mediacommandreader = new BufferedReader(new FileReader(filename));
                PrintWriter report = new PrintWriter(reportfile)) {
            String line = " ";
            while ((line = mediacommandreader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                String[] commandfield = line.split(",", -1);

                /// Checks the legnth of input line from medicommands
                if (commandfield.length == 1) {
                    String command = commandfield[0].trim();
                    if (command.equals("L") || command.equals("F") || command.equals("K")) {
                        errors.println("Command " + command + " does not contain a string.");
                        continue;
                    }
                    /// Print header for media list and media list contents
                    if (command.equals("A")) {
                        printListHeader(report, command);
                        for (Media media : mediaList) {
                            report.print(media.printMedia());
                        }
                    }
                    /// prints for movie list and movies
                    if (command.equals("M")) {
                        printListHeader(report, command);
                        for (Media media : mediaList) {
                            if (media.getType() == 'M') {
                                report.print(media.printMedia());
                            }
                        }
                    }
                    /// prints song list and songs
                    if (command.equals("S")) {
                        printListHeader(report, command);
                        for (Media media : mediaList) {
                            if (media.getType() == 'S') {
                                report.print(media.printMedia());
                            }
                        }
                    }
                    /// prints book list and books
                    if (command.equals("B")) {
                        printListHeader(report, command);
                        for (Media media : mediaList) {
                            if (media.getType() == 'B') {
                                report.print(media.printMedia());
                            }
                        }
                    }
                    /// pritns total count of media content
                    /// and book, movie, song counts
                    if (command.equals("T")) {
                        int moviecount = 0;
                        int songcount = 0;
                        int bookcount = 0;
                        int totalcount = 0;
                        for (Media media : mediaList) {
                            if (media.getType() == 'M') {
                                moviecount++;
                                totalcount++;
                            }
                            if (media.getType() == 'B') {
                                bookcount++;
                                totalcount++;
                            }
                            if (media.getType() == 'S') {
                                songcount++;
                                totalcount++;
                            }
                        }
                        report.println("Your Media Library");
                        report.println("Movies: " + moviecount + "\nBooks: "
                                + bookcount + "\nSongs: " + songcount + "\nTotal: " + totalcount);
                    }
                    /// prints ned of list once "q" is reached
                    /// breaks
                    if (command.equals("Q")) {
                        report.print("\nEnd of input list");
                        break;
                    }
                }
                /// checks length of line from media commands
                if (commandfield.length == 2) {
                    String command = commandfield[0].trim();
                    String value = commandfield[1].trim();

                    if (command.equals("L") || command.equals("F") || command.equals("K")) {
                        if (value.isEmpty()) {
                            errors.println("Command " + command + " does not contain a string.");
                            continue;
                        }

                        boolean found = false;
                        /// handles printing all the stars in the specified movie
                        if (command.equals("L")) {
                            for (Media media : mediaList) {
                                if (media instanceof Movie
                                        && media.getTitle().equalsIgnoreCase(value)) {
                                    if (!found) {
                                        report.println("The Stars of the Movie " + value + " are:");
                                    }
                                    ((Movie) media).printStars(report);
                                    found = true;
                                }
                            }
                            if (!found) {
                                errors.println(value + " is not found in your list");
                            }
                            /// Code heandling the printign of all movies associated with a specific star
                        } else if (command.equals("F")) {
                            for (Media media : mediaList) {
                                if (media instanceof Movie) {
                                    Movie movie = (Movie) media;
                                    String stars = movie.getStars();
                                    if (stars != null) {
                                        for (String star : stars.split(",")) {
                                            if (star.trim().equalsIgnoreCase(value)) {
                                                if (!found) {
                                                    report.println("Movies starring: " + commandfield[1]);
                                                }
                                                report.print(movie.printMedia());
                                                found = true;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                            if (!found) {
                                errors.println(value + " is not in any movies in your list");
                            } /// Section of code printing any media conatining the keyword entered afte a K,
                        } else if (command.equals("K")) {
                            for (Media media : mediaList) {
                                if (media.getName().equalsIgnoreCase(value)) {
                                    if (!found) {
                                        report.println("Media with Keyword: " + commandfield[1]);
                                        report.println("Title\tYear\tRating\tGenre\tType");

                                    }
                                    report.print(media.printMedia());
                                    found = true;
                                }
                            }
                            if (!found) {
                                errors.println(value + " is not in any media in your list");
                            }
                        }
                        continue;
                    }

                    try {
                        /// checks if rating is outside 1-10
                        int ratingThreshold = Integer.parseInt(value);
                        if (ratingThreshold > 10 || ratingThreshold < 1) {
                            errors.println("Error in rating provided: " + ratingThreshold);
                            continue;
                        }
                        /// value to print values accoring to type
                        /// will print all media types, unless M, S or B instead of A
                        /// found remebers if a match was found
                        /// if !found is true, header and its contents print
                        /// If !found is false, only the contents print
                        boolean found = false;
                        for (Media media : mediaList) {
                            boolean typeMatches = command.equals("A")
                                    || (command.equals("M") && media.getType() == 'M')
                                    || (command.equals("S") && media.getType() == 'S')
                                    || (command.equals("B") && media.getType() == 'B');
                            if (typeMatches && media.getRating() >= ratingThreshold) {
                                if (!found) {
                                    printListHeader(report, command);
                                }
                                report.print(media.printMedia());
                                found = true;
                            }
                        }
                        /// pprints when the type or rating isnt valid
                        if (!found) {
                            errors.println("No media matched command: " + line);
                        }
                        /// activvates if the second elemnt cannot be converted into integer,
                        /// moves further on into the code
                    } catch (NumberFormatException notANumber) {
                        boolean found = false;
                        /// Works similarly as above
                        for (Media media : mediaList) {
                            boolean typeMatches = command.equals("A")
                                    || (command.equals("M") && media.getType() == 'M')
                                    || (command.equals("S") && media.getType() == 'S')
                                    || (command.equals("B") && media.getType() == 'B');
                            if (typeMatches && media.getGenre().equalsIgnoreCase(value)) {
                                if (!found) {
                                    printListHeader(report, command);
                                }
                                report.print(media.printMedia());
                                found = true;
                            }
                        }
                        if (!found) {
                            errors.println("No media matched command: " + line);
                        }
                    }

                }
                /// checks length of the line
                if (commandfield.length > 2) {
                    if (commandfield[0].trim().equals("N")) {
                        if (commandfield.length < 8) {
                            errors.println("Invalid new media command: " + line);
                            continue;
                        }

                        String title = commandfield[2].trim();
                        boolean hasError = false;
                        for (String field : commandfield) {
                            if (field.trim().equalsIgnoreCase("ERROR")) {
                                hasError = true;
                                break;
                            }
                        }
                        if (hasError) {
                            errors.println(title + " contains an error in a numeric field");
                            continue;
                        }
                        /// checks int values on the lines tjat may be added to mediaList
                        /// from mediaCommands
                        try {
                            int rating = Integer.parseInt(commandfield[4].trim());
                            int length = Integer.parseInt(commandfield[6].trim());
                            int yearReleased = Integer.parseInt(commandfield[7].trim());

                            if (rating < 1 || rating > 10) {
                                errors.println(title + " contains an error in rating: " + rating);
                                continue;
                            }
                            if (length <= 0) {
                                errors.println(title + " contains an invalid length: " + length);
                                continue;
                            }
                            if (yearReleased < 1920 || yearReleased > 2024) {
                                errors.println(title + " contains an error in the year: " + yearReleased);
                                continue;
                            }
                            /// calls method to add line to mediaList
                            List<String> extraFields = new ArrayList<>();
                            for (int i = 8; i < commandfield.length; i++) {
                                extraFields.add(commandfield[i].trim());
                            }
                            newentry(commandfield[1].trim().charAt(0), title,
                                    commandfield[3].trim(), rating, commandfield[5].trim(),
                                    length, yearReleased, extraFields);
                            report.println(title + " was added to your list");
                        } catch (NumberFormatException e) {
                            errors.println(title + " contains an error in a numeric field");
                        }
                    }
                }
            }
        } catch (IOException e) {
            errors.println("Could not process command/report file: " + e.getMessage());
        }
    }

    private static void logRecordError(PrintWriter errors, String line, String reason) {
        errors.println("ERROR: " + line);
        errors.println(reason);
    }

    /// method that prints out header
    private static void printListHeader(PrintWriter report, String command) {
        if (command.equals("M")) {
            report.println("\nYour Movie List\n");
            report.println("Title\tYear\tRating\tGenre\tType\tStars\n");
        } else if (command.equals("S")) {
            report.println("\nYour Song List\n");
            report.println("Title\tYear\tRating\tGenre\tType\tTop 40\n");
        } else if (command.equals("B")) {
            report.println("\nYour Book List\n");
            report.println("Title\tYear\tRating\tGenre\tType\tWeeks on NYT List\n");
        } else {
            report.println("\nYour Media List\n");
            report.println("Title\tYear\tRating\tGenre\tType\n");
        }
    }

    /// methid that adds line from media commands to mediaList
    public static void newentry(char type, String title, String name, int rating, String genre,
            int length, int yearReleased, List<String> extraFields) {
        if (type == 'M') {
            Movie movie = new Movie();
            movie.setType(type);
            movie.setTitle(title);
            movie.setName(name);
            movie.setRating(rating);
            movie.setGenre(genre);
            movie.setLength(length);
            movie.setYearReleased(yearReleased);
            movie.setStars(String.join(", ", extraFields));
            movie.setExtraFields(extraFields);
            mediaList.add(movie);
        } else {
            mediaList.add(new Media(type, title, name, rating,
                    genre, length, yearReleased, extraFields));
        }
    }

}
