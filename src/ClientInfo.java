import java.io.PrintWriter;

public class ClientInfo {
    String name;
    PrintWriter writer;

    ClientInfo(String name, PrintWriter writer) {
        this.name = name;
        this.writer = writer;
    }
}
