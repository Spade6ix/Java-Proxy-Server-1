import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BlockedPage {

    private static String urlPlaceholder = "/url";
    private static String errorMsg = "";

    public static void load() throws IOException {
    	//Retrieves the pre-made html error page 
        errorMsg = new String(Files.readAllBytes(Paths.get("blacklist.html")));
    }

    public static String getHtmlString(String url) {
        return errorMsg.replace(urlPlaceholder, url);
    }

}
