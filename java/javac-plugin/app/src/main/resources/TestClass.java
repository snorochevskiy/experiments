import com.github.snorochevskiy.javac.plugin.wrongType;

public class TestClass {
    public static void main(String[] args) {
        wrongType s = "Hello";
        System.out.println(s);
    }
}