import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ErrorPage {

    private static String banPagePath = "blacklist.html";
    private static String urlPlaceholder = "/url";
    private static String errorMsg = "";

    public static void setBanPagePath(String banPagePath) {
        ErrorPage.banPagePath = banPagePath;
    }

    public static void setUrlPlaceholder(String urlPlaceholder) {
        ErrorPage.urlPlaceholder = urlPlaceholder;
    }

    public static void setErrorMsg(String errorMsg) {
        ErrorPage.errorMsg = errorMsg;
    }

    public static void load() throws IOException {
        errorMsg = new String(Files.readAllBytes(Paths.get(banPagePath)));
    }

    public static String getHtmlString(String url) {
        return errorMsg.replace(urlPlaceholder, url);
    }


}
