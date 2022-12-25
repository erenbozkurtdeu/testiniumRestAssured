import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Test;
import java.util.Random;

public class trello {

    private String id, boardId, cardIdOne;
    private String[] list;
    Random r = new Random();

    public static RequestSpecification base() {
        RestAssured.baseURI = "https://api.trello.com/1/";
        return RestAssured.given().
                    header("Content-Type", "application/json").contentType(ContentType.JSON).accept(ContentType.JSON).
                    queryParam("key", "caf37e07632ddd0af2670f0233f69602").
                    queryParam("token", "ATTAeaded948c2ebcdc23f716a4051e507602d637815bd5a43a15fb0ec343868e94bD7F60CA2");
    }

    @Test
    public void createBoard() {
        Response responseBoard = base().
                request().
                    basePath("boards/").
                    queryParam("name", "testinium").
                when().
                    post();
        id = responseBoard.jsonPath().get("id");
        System.out.println(id + " numaralı board oluşturuldu.");
    }

    @Test
    public void getIdList() {
        createBoard();
        Response responseIdList = base().
                request().
                    basePath("boards/" + id + "/lists").
                when().
                    get();
        boardId = responseIdList.jsonPath().get("id[0]");
    }

    @Test
    public void createCardNamedOne() {
        getIdList();
        Response response = base().
                request().
                    basePath("cards").
                    queryParam("name", "one").
                    queryParam("idList", boardId).
                when().
                    post();
        cardIdOne = response.jsonPath().get("id");
        System.out.println(cardIdOne + " numaralı ilk kart oluşturuldu.");
    }

    @Test
    public void createCardNamedTwo() {
        createCardNamedOne();
        Response response = base().
                request().
                    basePath("cards").
                    queryParam("name", "two").
                    queryParam("idList", boardId).
                when().
                    post();
        String cardIdTwo = response.jsonPath().get("id");
        System.out.println(cardIdTwo +  " numaralı ikinci kart oluşturuldu.");
        list = new String[]{cardIdOne, cardIdTwo};
    }

    @Test
    public void updateRandomCard() {
        createCardNamedTwo();
        base().request().
                    basePath("cards" + list[r.nextInt(list.length)]).
                    queryParam("name", "random").
                when().
                    post();
    }

    @Test
    public void deleteCards() {
        updateRandomCard();
        for (String s : list) {
            base().
                    request().
                        basePath("cards" + s).
                    when().
                        delete();
            System.out.println(s + " numaralı kart silindi.");
        }
    }

    @Test
    public void deleteBoard() {
        deleteCards();
        base().
                request().
                    basePath("boards/" + id).
                when().
                    delete();
        System.out.println(id + " numaralı board silindi.");
    }
}