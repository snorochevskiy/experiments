import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class Compiler {

    public static void main(String args[]) {
        String testFile = Compiler.class.getClassLoader().getResource("TestClass.java").getFile();

        JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
        int rc = javac.run(null, null, null, new String[]{testFile});
    }
}
