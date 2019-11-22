import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Main {
    public static void main(String[] args) throws IOException, InvalidFormatException {
        File folder = new File("textFiles");
        File[] listOfFiles = folder.listFiles();
        Scanner reader = new Scanner(System.in);  // Reading from System.in
        System.out.println("Enter a search term: ");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String searchTerm = reader.nextLine();
        String directoryName = searchTerm + timestamp;
        File destinationDir = new File("searchResults/" + directoryName);
        Files.createDirectory(destinationDir.toPath());
        copyFilesNewDirectory(listOfFiles, destinationDir, searchTerm, folder);
    }

    private static void copyFilesNewDirectory(File[] listOfFiles, File destinationDir, String searchTerm, File folder) throws IOException, InvalidFormatException {
        File destFile = new File(destinationDir + "/" + UUID.randomUUID().toString().replace("-", ""));
        Path path = Paths.get(destFile.getPath());
        Scanner reader2 = new Scanner(System.in);
        System.out.println("Do you wish to get sentences or paragraphs as answers? Press type 'n' for paragraph and 'y' for the sentence");
        String answer = reader2.nextLine();
        for (File file : listOfFiles) {
            if (!file.isDirectory()) {
                List<String> readAllLines = checkExtension(file);
                if (readAllLines == null) {
                    System.out.println("Invalid file input, please remove the non doc, docx or text file from the textFiles folder");
                } else {
                    if (answer.equals("y")) {
                        readAllLines = makeSentencesFromParagraphs(readAllLines, searchTerm);
                        writeToFile(path, readAllLines, searchTerm);
                    } else if (answer.equals("n")) {
                        writeToFile(path, readAllLines, searchTerm);
                    } else {
                        System.out.println("Your answer does match the options provide a new one");
                        copyFilesNewDirectory(listOfFiles, destinationDir, searchTerm, folder);
                    }
                }
            } else {
                folder = new File(file.getPath());
                listOfFiles = folder.listFiles();
                copyFilesNewDirectory(listOfFiles, destinationDir, searchTerm, folder);
            }
        }
    }

    public static List<String> makeSentencesFromParagraphs(List<String> readAllLines, String searchTerm) {
        CharSequence[] cs = readAllLines.toArray(new CharSequence[readAllLines.size()]);
        List<String> split = new ArrayList<>();
        for (CharSequence c : cs) {
            List<String> splitTemp = Pattern.compile("\\.")
                    .splitAsStream(c.toString())
                    .filter(d -> d.toLowerCase().contains(searchTerm.toLowerCase()))
                    .collect(Collectors.toList());
            for (String text : splitTemp) {
                split.add(text);
            }
        }
        return split;
    }

    public static void writeToFile(Path path, List<String> readAllLines, String searchTerm) throws IOException {
        FileWriter fw = new FileWriter(path.toString(), true);
        List<String> filterResult = readAllLines
                .stream()
                .filter(c -> c.toLowerCase().contains(searchTerm.toLowerCase()))
                .collect(Collectors.toList());
        for (String lines : filterResult) {
            fw.write(lines + "\n" + "\n");
        }
        fw.close();
    }

    static List<String> checkExtension(File file) throws IOException {
        List<String> readAllLines = new ArrayList<>();
        if (getExtensionByStringHandling(file.getName()).get().equals("txt")) {
            readAllLines = Files.readAllLines(file.toPath());
        } else if (getExtensionByStringHandling(file.getName()).get().equals("doc") || getExtensionByStringHandling(file.getName()).get().equals("docx")) {
            Stream<String> lines = Files.lines(Paths.get(file.getPath()));
            String content = lines.collect(Collectors.joining(System.lineSeparator()));
            readAllLines.add(content);
            //||getExtensionByStringHandling(file.getName()).get().equals("docx")
        } else {
            System.out.println("Invalid file in the folder. Please remove it and restart the program");
            return null;
        }
        return readAllLines;
    }


    public static Optional<String> getExtensionByStringHandling(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }
}

