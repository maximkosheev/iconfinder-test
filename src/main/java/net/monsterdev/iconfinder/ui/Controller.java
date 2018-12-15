package net.monsterdev.iconfinder.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.*;

public class Controller extends AbstractUIController implements WindowController {
    private final String AuthURL = "http://iconsearcher.com/refresh.php";
    private final String SearchURL = "https://api.iconfinder.com/v3/icons/search";
    private final String CLIENT_ID = "braAYSfQvqQOqu45jEJyTg6qLiLTr2r4jf6HbpcS1clVoHQEefOq4YRxW1CdTOtq";
    private final String CLIENT_SECRET = "zZ0eQgiz8bhSc0NUmFA9aqe51Rq84K7ylZlxnLqfrqxv02nRzRZgBecDmPqMTHo6";

    private OkHttpClient client = null;

    @FXML
    private StackPane rootPane;
    @FXML
    private WebView mainView;
    @FXML
    private TextField edtSearch;

    public class MyCookieJar implements CookieJar {

        private List<Cookie> cookies;

        @Override
        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
            this.cookies =  cookies;
        }

        @Override
        public List<Cookie> loadForRequest(HttpUrl url) {
            if (cookies != null)
                return cookies;
            return new ArrayList<Cookie>();

        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.cookieJar(new MyCookieJar());
        client = builder.build();
    }

    @Override
    public Stage getStage() {
        return (Stage)rootPane.getScene().getWindow();
    }


    // Возвращает access_token
    private String auth() throws Exception {
        try {
            HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(AuthURL)).newBuilder();
            urlBuilder.addQueryParameter("client_id", CLIENT_ID);
            urlBuilder.addQueryParameter("client_secret", CLIENT_SECRET);

            Request request = new Request.Builder()
                    .url(urlBuilder.build())
                    .build();
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful())
                throw new Exception("Failed to get access token");
            JSONObject jsonResponse = new JSONObject(Objects.requireNonNull(response.body()).string());
            return jsonResponse.getString("access_token");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    @FXML
    private void onSearch(ActionEvent event) {
        try {
            if (edtSearch.getText().isEmpty())
                throw new Exception("Поле 'Поиск иконок' не может быть пустым");
            HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(SearchURL)).newBuilder();
            urlBuilder.addQueryParameter("client_id", CLIENT_ID);
            urlBuilder.addQueryParameter("client_secret", CLIENT_SECRET);
            urlBuilder.addQueryParameter("query", edtSearch.getText());
            urlBuilder.addQueryParameter("count", "10");

            String access_token = Objects.requireNonNull(auth());
            Request searchRequest = new Request.Builder()
                    .url(urlBuilder.build())
                    .addHeader("Accept", "application/json, text/javascript, */*; q=0.01")
                    .addHeader("Origin", "http://iconfinder.github.io")
                    .addHeader("Referer", "http://iconfinder.github.io/api-demo/public/")
                    .addHeader("Authorization", "JWT " + access_token)
                    .build();

            Response response = client.newCall(searchRequest).execute();
            if (!response.isSuccessful())
                throw new Exception("Failed to get icons");
            JSONObject jsonResponse = new JSONObject(Objects.requireNonNull(response.body()).string());
            JSONArray icons = jsonResponse.getJSONArray("icons");
            List<String> previewURLs = new LinkedList<>();
            for (int nI = 0; nI < icons.length(); nI++) {
                JSONObject icon = icons.getJSONObject(nI);
                String previewUrl = icon.getJSONArray("raster_sizes").getJSONObject(3)
                        .getJSONArray("formats").getJSONObject(0)
                        .getString("preview_url");
                previewURLs.add(previewUrl);
            }
            String mainViewContent = "";
            mainViewContent += "<html><body><table>";
            int nI = 0;
            int nTD = 0;
            while (nI < previewURLs.size()) {
                if (nTD == 0) {
                    mainViewContent += "<tr>";
                }
                mainViewContent += "<td>";
                mainViewContent += "<img src=\""+previewURLs.get(nI)+"\">";
                mainViewContent += "</td>";
                nI += 1; nTD += 1;
                if (nTD == 5) {
                    mainViewContent += "</tr>";
                    nTD = 0;
                }
            }
            if (!mainViewContent.endsWith("</tr>"))
                mainViewContent += "</tr>";
            mainViewContent += "</table></body></html>";
            mainView.getEngine().loadContent(mainViewContent);
        } catch (Exception ex) {
            ex.printStackTrace();
            UIController.showErrorMessage("Error: " + ex.getMessage());
        }
    }
}
