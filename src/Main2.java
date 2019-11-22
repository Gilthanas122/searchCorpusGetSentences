import java.util.Optional;

public class Main2 {
    public static void main(String[] args) {
        System.out.println(getExtensionByStringHandling("text.txt"));
    }

    public static Optional<String> getExtensionByStringHandling(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }
}
