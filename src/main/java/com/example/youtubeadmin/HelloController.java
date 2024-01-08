package com.example.youtubeadmin;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeRequestInitializer;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

public class HelloController implements Initializable {
    private static final String API_KEY = "AIzaSyCBLbjuluw4UVqlhGnwG7Jy42KJhcgiRb4"; //Llave  que sirve para conectarse a la API de YouTube

    //
    //AIzaSyAY7MRIY2o6lsvLA-9Cb6hufm3en-L_cUQ

    private static final JsonFactory jsonfactory = JacksonFactory.getDefaultInstance(); //Clase, que convierte las respuestas de la API
    // YouTube en Clases con atributos de JAVA

    @FXML //Relaciona el componente con el archivo hello-view.fxml
    private Label digitalClock, LblAbout;

    @FXML
    private Label LblDashboard, LblAcount, LblAnalytics, LblReactions, LblExit, LblStaff;

    @FXML
    public HBox contentMain, afterContentMain = new HBox();
    @FXML
    private Label lblMetadata;
    @FXML
    private TilePane contentVideo;
    private static WebView webView;
    public static final List<Integer> listMinutes = new ArrayList<>(), listSeconds = new ArrayList<>();
    public static final ArrayList<Double> viewCounts = new ArrayList<>();
    @FXML
    public AnchorPane anchorMain;
    private long MaxResults = 1L;
    private static Video videoMetaData;
    private static String regionCode = "None";
    private static String order = "relevance";
    private static FXMLLoader fxmlLoader;

    public void setMaxSearchResult(long result) {
        if (result < 100) {
            MaxResults = result;
        }
    }

    public static String getRegionCode() {
        return regionCode;
    }


    public static void setOrderVideo(String order2) {
        order = order2;
    }

    /*
    * 1. Recibe una cantidad N de parametros (String... filter)
    *
    * 2. Evalua el primer argumento, y mira a ver si la variable regionCode guarda el valor 'None' y si lo guarda
    * Significa que la aplicacion se esta ejecutando por primera vez
    * 3. Retorna el identificador del Video (return YbJOTdZBX1g;)
    * 4. En caso que evalue el else significa que el usuario ya ajusto los filtros de busqueda segun su conveniencia
    * 5. Con el objeto Date obtenemos la fecha actual y restamos un mes para luego utilizar ese objeto para decirle a YouTube
    * Que me haga una busqueda con los videos cuya fecha de publicacion sean despues del mes anterior a la fecha actual

    * 6. Accedo a la clase Interna List que es la clase indicada para
    * Indicarle a YouTube que tipo de informacion es la que va a consultar
    * Nota: Al tratarse de una clase interna debemos acceder a la misma de  colocando primero la clase que la envuelve
    * Seguido del nombre de la clase envolvida (YouTube.Videos.List) el orden seria el siguiente
    * YouTube -> Videos -> List

    * * 7. Con los metodos setRegionCode, setOrder, setType le indico que filtros de busqueda va a aplicar
    * 8. La cantidad maxima de resultados

    * * 9. con el metodo list.execute(); le doy el 'ok' para hacer la consultas a la API de YouTube de acuerdo con los parametros
    * establecidos en el list() y los filtros de busqueda anteriormente aplicados en los metodos setOrder, setRegionCode etc.

    * 10. Con el metodo getItems(); obtengo un VECTOR que almacena  una cantidad N de valores (Que va a depender del valor que le pusimos
    * en el metodo setMaxResults) y cada valor almacenado en ese vector va a representar un VIDEO de YouTube

    * * 11. Con el metodo getFirst() estoy obteniendo el primer elemento del vector es decir el elemento que esta en la posicion 0
    * Que viene siendo el Video que mejor cumple con los parametros y filtros de busqueda anteriormente aplicados debido a que en el Vector
    * Estos estan guardados segun un orden preestablecido
    *
    * 12. Con el metodo getId() le indico que voy a obtener informacion acerca del identificador del Video mas no el ID como tal
    * para ello debo utilizar el metodo getVideoId para entonces sí obtener el identificador del video en especifico
    * */
    private static String showVideoMostPopular(String... filter) throws IOException {
        if (filter[0].equalsIgnoreCase("None")) {
            YouTube.Videos.List videoList = getService().videos().list("snippet,Statistics, contentDetails");
            videoList.setId("YbJOTdZBX1g");
            videoMetaData = videoList.execute().getItems().getFirst();
            return "YbJOTdZBX1g";
        } else {
            Date now = new Date();
            now.setMonth(now.getMonth()-1);
            YouTube.Search.List list = getService().search().list("snippet, id");
            if (DashboardContent.getSelectedCategory() != null)
                list.setVideoCategoryId(DashboardContent.getCategoryAndId().get(DashboardContent.getSelectedCategory()));
            list.setRegionCode(filter[0]);
            list.setOrder(filter[1]);
            list.setType("video");
            list.setPublishedAfter(new DateTime(now));
            if (DashboardContent.getDefinition() != null) list.setVideoDefinition(DashboardContent.getDefinition());
            list.setMaxResults(50L);
            List<SearchResult> resultList = list.execute().getItems();
            YouTube.Videos.List videoList = getService().videos().list("snippet,Statistics, contentDetails");
            videoList.setId(resultList.getFirst().getId().getVideoId());
            videoMetaData = videoList.execute().getItems().getFirst();
            return resultList.getFirst().getId().getVideoId();
        }
    }
    /*
    * 1. Metodo SETTER que establece el valor a una propiedad de la clase HelloController
    * En este caso al atributo o variable 'regionCode' Que es la encargada de almacenar el filtro de busqueda
    * Correspondiente al PAIS
    * */
    public static void setVideoRegionCode(String code) {
        regionCode = code;
    }
    /*
    * 1. Este metodo initComponent() Tal como su nombre lo indica es un metodo que se llama justo despues que se
    * llama al metodo Initialize que es el metodo principal que se ejecuta cuando se invoca al controlador
    * Este metodo initComponent va a  inicializar todos los componentes que deben mostrarse cuando la aplicacion se
    * Utilice por primera vez (O cada vez que se ejecute)
    *
    * 2. La palabra reservada throws indica que este metodo va a lanzar o va a producir una excepcion
    * */
    private void initComponent() throws IOException {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateClock()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
        if (webView != null) webView.getEngine().load(null);
        webView = new WebView();
        webView.setMaxHeight(265);
        webView.setMaxWidth(460);
        webView.getEngine().load("https://www.youtube.com/watch?v=" + showVideoMostPopular(getRegionCode(), getOrder()));
        contentVideo.getChildren().add(getWebView());
        setLblMetadataListener();
    }

    public static WebView getWebView() {
        return webView;
    }

    public static void stopVideo() {
        webView.getEngine().load(null);
    }
    /*
    * 1. Este metodo reproduce el video en el componente webView
    * con el metodo webView.getEngine().load() el cual este ultimo recibe por parametro el link o la URL del video
    * el Identificador del video lo obtenemos del metodo showVideoMostPopular() el cual retorna un valor de tipo String que es
    * el ID de un video de YouTube mas popular, (Tomando en cuenta los parametros de busqueda especificados en la ventana
    * DashboardContent
    *
    * */
    public void setReplayVideo(WebView webView) {
        try {
            System.out.println(getOrder());
            webView.getEngine().load("https://www.youtube.com/watch?v=" + showVideoMostPopular(regionCode, getOrder()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getOrder() {
        return order;
    }
    /*
    *
    * Metodo que actualiza la hora del Label 'digitalClock' con la hora actual
    * 1. Crea un objeto Date  el cual se inicializa con un constructor vacio  le da un estado inicial que viene siendo
    * la hora actual
    * 2. Con la clase SimpleDateFormat aplicamos un formato de horas minutos y segundos y por ultimo
    * utilizamos el metodo setText del Label para ir actualizando la hora
    * */

    private void updateClock(   ) {
        // Obtener la hora actual
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        // Actualizar el texto del reloj digital
        digitalClock.setText(sdf.format(now));
    }
    /*
    * Metodo que devuelve un objeto de la API de YouTube el cual permite realizar las consultas a la API
    * 1. Se utiliza el return para indicar que vamos a devolver un objeto utilizamos el metodo estatico  Builder de la clase
    * YouTube para construir el objeto
    * 2. Este metodo recibe en el primer argumento un objeto de la clase GoogleNetHttptTransport que servira como medio de
    * Transporte para hacer las solicitudes HTTP al servidor
    * 3. El segundo argumento del metodo Builder recibe un objeto jsonFactory el cual es el encargado de realizar las conversiones
    * que recibe de la API de YouTube en formato json a clases de java para posteriormente llamar a  los metodos correspondientes
    * getter
    *
    * 4. El tercer argumento del metodo build nos sirve para indicar si deseamos realizar una peticion inicial a la API (En este caso no lo haremos
    * por lo tanto se le deja el valor null)
    *
    * 5. Posteriormente se aplican conceptos de programacion fluida para modificar los atributos del objeto YouTube
    * sin perder la 'instancia' y que podamos seguir llamando a los metodos de esta clase como se ve a continuacion:
    *
    * 6. El metodo setApplicationName establece el nombre de nuestra aplicacion
    *
    * 7. En el segundo metodo creamos un objeto YouTubeRequestInitializer el cual nos va a permitir realizar las consultas a la API
    * El constructor de este objeto recibe por parametro la Llave de la API de YouTube
    *
    * 8. Finalmente con el metodo build le damos el 'ok' a nuestro objeto YouTube o en otras palabras (Construye el objeto)
    * y lo devolvemos ya con todos los parametros anteriormente configurados
    * */

    public static YouTube getService() {
        try {
            return new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(), jsonfactory, null)
                    .setApplicationName("mi aplicacion")
                    .setYouTubeRequestInitializer(new YouTubeRequestInitializer(API_KEY))
                    .build();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }
    /*
    * 1. El metodo initialize es un metodo que pertenece a la interfaz Initializable por lo cual obliga a que cualquier clase
    * Que implemente dicha interfaz se vea obligado a sobreescribir el metodo initialize
    *
    * 2.  Este metodo se invoca cada vez que el usuario interactua sobre algun componente de la interfaz de usuario que pertenezca
    * al nuestro archivo 'Hello-view.fxml'
    * Por ej (Cuando el usuario da clic sobre el menu Main-content este lo que tiene que hacer es llamar la clase controlador de Hello-view.fxml
    * y por consecuente llama al metodo initialize para que inicie y cargue todos los componentes que estan en la Interfaz de Usuario
    * (Que lo haciamos con el metodo initComponent()
    *
    * 3. En su interior llama al metodo initComponent, y al metodo mouseClickedOnMenu
    * Que basicamente es un metodo el cual coloca a la ESCUCHA de un evento las etiquetas de los MENUS
    * Es decir a la etiqueta "LblDashboard, LblContentMain, etc de tal forma que cada vez que el usuario clickee sobre
    * alguna de ellas estás ejecutaran el codigo que se encuentre  en su interior
    *
    * */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            initComponent();
            mouseClickedOnMenu();
            setViewCounts();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    /*
    * Pone a la escucha el Label 'Metadata' que aparece justo debajo del webView (que es el recuadro que muestra
    * El video de YouTube)
    *
    * 1. Con el metodo lblMetadata.setOnMouseClicked() decimos que cada vez que se haga clic sobre el elemento 'Metadata'
    * 2. Creamos un objeto 'Parent' que representa el panel que contiene todos los elementos graficos o visuales que ve el usuario en la ventana
    * 3. Obtenemos la clase controlador asociada al archivo fxml de Metadata (Que representa la vista  o ventana que aparece
    * Cuando clickamos sobre el elemento)
    * 4. Con el metodo setMeta le pasamos el video de YouTube que esta reproduciendose en la ventana Main-content
    * para despues utilizar sus respectivos metodos de acceso para asi mostrar informacion en los Textfields de la ventana
    * Metadata
    * */
    private void setLblMetadataListener() {
        lblMetadata.setOnMouseClicked(e -> {
            FXMLLoader fxmlLoader1 = new FXMLLoader(getClass().getResource("Metadata.fxml"));
            try {
                //showVideoMostPopular(regionCode, getOrder());
                Parent parent = fxmlLoader1.load();
                Metadata metadata = fxmlLoader1.getController();
                metadata.setMeta(videoMetaData);
                Stage stage = new Stage();
                Scene scene = new Scene(parent);
                stage.setScene(scene);
                stage.setTitle("Metadata");
                stage.show();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    public void setViewCounts() throws IOException {
        YouTube.Videos.List videos = getService().videos().list("Statistics");
        videos.setRegionCode(DashboardContent.getRegion());
        videos.setMaxResults((DashboardContent.getResults() == 0L) ? 30L : DashboardContent.getResults());
        videos.setVideoCategoryId((DashboardContent.getSelectedCategory() != null) ? DashboardContent.getCategoryAndId()
                .get(DashboardContent.getSelectedCategory()) : "24");
        if(DashboardContent.getSelectedCategory() != null && !DashboardContent.getSelectedCategory().equalsIgnoreCase("Anime/Animation")){
            videos.setChart("mostPopular");
        }else if (DashboardContent.getSelectedCategory() != null && DashboardContent.getSelectedCategory().equalsIgnoreCase("Anime/Animation")){
            videos.setId("P_S7WOZtx9Y");
        }else{
            videos.setChart("mostPopular");
        }
        VideoListResponse videoListResponse = videos.execute();
        for (int i = 0; i < videoListResponse.getItems().size(); i++) {
            viewCounts.add(videoListResponse.getItems().get(i).getStatistics().getViewCount().doubleValue());
        }
    }

    /*
     * 1. Este metodo coloca a la escucha del evento Click a los Componentes del MENU que aparecen en la parte izquierda
     * de la ventana del proyecto el cual permite moverse entre las diferentes 'ventanas' del mismo
     *
     * 2. Para cada vez que el usuario clickee sobre algun componente del menu este inmediatamente ira a evaluar los diferentes ifs
     *  que estan en su interior, la funcion de estos if  nos permite saber desde que menu estamos dando click y a que menu de la aplicacion
     * Deseamos ir con el objetivo de obtener el panel que contiene todos los elementos visuales y cargarlos en el panel del menu
     * que esta visualizando el usuario en ese momento de este modo se consigue un efecto de transicion entre ventanas.
     *
     * Unicamente modificando o cambiando la informacion que aparece en el recuadro gris de nuestra aplicacio
     * Sin modificar o cambiar la parte visual del resto de la aplicacion
     *
     * */
    private void mouseClickedOnMenu() throws IOException {
        webView.getEngine().load(null);
        LblDashboard.setOnMouseClicked(e -> {
            if (HelloApplication.getLoader() != null && HelloApplication.getLoader().getController() instanceof HelloController) {
                webView.getEngine().load(null);
                fxmlLoader = new FXMLLoader(getClass().getResource("DashboardContent.fxml"));
                try {
                    fxmlLoader.load();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                afterContentMain.getChildren().addAll(contentMain.getChildren());
                DashboardContent content = fxmlLoader.getController();
                contentMain.getChildren().clear();
                contentMain.getChildren().add(content.contentMain);
            }
        });
        LblReactions.setOnMouseClicked(e -> {
            if (fxmlLoader != null && fxmlLoader.getController() instanceof DashboardContent) {
                DashboardContent content = fxmlLoader.getController();
                fxmlLoader = new FXMLLoader(getClass().getResource("Reactions.fxml"));
                try {
                    fxmlLoader.load();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                Reactions reactions = fxmlLoader.getController();
                content.contentMain.getChildren().clear();
                content.contentMain.getChildren().addAll(reactions.contentMain);

            } else if (fxmlLoader != null && fxmlLoader.getController() instanceof Analytics) {
                Analytics content = fxmlLoader.getController();
                fxmlLoader = new FXMLLoader(getClass().getResource("Reactions.fxml"));
                try {
                    fxmlLoader.load();
                } catch (IOException ex) {
                   ex.printStackTrace();
                }
                Reactions suscribers = fxmlLoader.getController();
                content.contentMain.getChildren().clear();
                content.contentMain.getChildren().addAll(suscribers.contentMain);


            } else if (fxmlLoader != null && fxmlLoader.getController() instanceof HelloController) {
                webView.getEngine().load(null);
                fxmlLoader = new FXMLLoader(getClass().getResource("Reactions.fxml"));
                try {
                    fxmlLoader.load();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                afterContentMain.getChildren().addAll(contentMain.getChildren());
                Reactions content = fxmlLoader.getController();
                contentMain.getChildren().clear();
                contentMain.getChildren().add(content.contentMain);
            } else if (HelloApplication.getLoader() != null && HelloApplication.getLoader().getController() instanceof HelloController) {
                webView.getEngine().load(null);
                fxmlLoader = new FXMLLoader(getClass().getResource("Reactions.fxml"));
                try {
                    fxmlLoader.load();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                afterContentMain.getChildren().addAll(contentMain.getChildren());
                Reactions content = fxmlLoader.getController();
                contentMain.getChildren().clear();
                contentMain.getChildren().add(content.contentMain);
            } else if (fxmlLoader != null && fxmlLoader.getController() instanceof Staff) {
                Staff content = fxmlLoader.getController();
                fxmlLoader = new FXMLLoader(getClass().getResource("Reactions.fxml"));
                try {
                    fxmlLoader.load();
                } catch (IOException ex) {
                   ex.printStackTrace();
                }
                Reactions suscribers = fxmlLoader.getController();
                content.contentMain.getChildren().clear();
                content.contentMain.getChildren().addAll(suscribers.contentMain);


            }
        });
        LblAcount.setOnMouseClicked(e -> {
            if (HelloApplication.getLoader() != null && HelloApplication.getLoader().getController() instanceof HelloController) {
                webView.getEngine().load(null);
                fxmlLoader = new FXMLLoader(getClass().getResource("Account.fxml"));
                try {
                    fxmlLoader.load();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                afterContentMain.getChildren().addAll(contentMain.getChildren());
                Account content = fxmlLoader.getController();
                contentMain.getChildren().clear();
                contentMain.getChildren().add(content.contentMain);
            } else if (fxmlLoader != null && fxmlLoader.getController() instanceof DashboardContent) {
                webView.getEngine().load(null);
                fxmlLoader = new FXMLLoader(getClass().getResource("Account.fxml"));
                try {
                    fxmlLoader.load();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                afterContentMain.getChildren().addAll(contentMain.getChildren());
                Account content = fxmlLoader.getController();
                contentMain.getChildren().clear();
                contentMain.getChildren().add(content.contentMain);
            } else if (fxmlLoader != null && fxmlLoader.getController() instanceof Reactions) {
                webView.getEngine().load(null);
                fxmlLoader = new FXMLLoader(getClass().getResource("Account.fxml"));
                try {
                    fxmlLoader.load();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                afterContentMain.getChildren().addAll(contentMain.getChildren());
                Account content = fxmlLoader.getController();
                contentMain.getChildren().clear();
                contentMain.getChildren().add(content.contentMain);
            } else if (fxmlLoader != null && fxmlLoader.getController() instanceof Analytics) {
                webView.getEngine().load(null);
                fxmlLoader = new FXMLLoader(getClass().getResource("Account.fxml"));
                try {
                    fxmlLoader.load();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                afterContentMain.getChildren().addAll(contentMain.getChildren());
                Account content = fxmlLoader.getController();
                contentMain.getChildren().clear();
                contentMain.getChildren().add(content.contentMain);
            } else if (fxmlLoader != null && fxmlLoader.getController() instanceof HelloController) {
                webView.getEngine().load(null);
                fxmlLoader = new FXMLLoader(getClass().getResource("Account.fxml"));
                try {
                    fxmlLoader.load();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                afterContentMain.getChildren().addAll(contentMain.getChildren());
                Account content = fxmlLoader.getController();
                contentMain.getChildren().clear();
                contentMain.getChildren().add(content.contentMain);
            } else if (fxmlLoader != null && fxmlLoader.getController() instanceof Staff) {
                webView.getEngine().load(null);
                fxmlLoader = new FXMLLoader(getClass().getResource("Account.fxml"));
                try {
                    fxmlLoader.load();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                afterContentMain.getChildren().addAll(contentMain.getChildren());
                Account content = fxmlLoader.getController();
                contentMain.getChildren().clear();
                contentMain.getChildren().add(content.contentMain);
            }
        });
        LblAbout.setOnMouseClicked(e -> {
            if (fxmlLoader != null && fxmlLoader.getController() instanceof DashboardContent) {

                ((DashboardContent) fxmlLoader.getController()).contentMain.getChildren().clear();

                ((DashboardContent) fxmlLoader.getController()).contentMain.getChildren().addAll(afterContentMain.getChildren());
                if (tilePane == null) {
                    AnchorPane anchorPane = (AnchorPane) ((DashboardContent) fxmlLoader.getController()).contentMain.getChildren().getFirst();
                    for (int i = 0; i < anchorPane.getChildren().size(); i++) {
                        if (anchorPane.getChildren().get(i) instanceof TilePane) {
                            tilePane = (TilePane) anchorPane.getChildren().get(i);
                        }
                    }
                }
                webView.getEngine().load(null);

                tilePane.getChildren().clear();

                tilePane.getChildren().addAll(webView);

                setReplayVideo(webView);

                fxmlLoader = HelloApplication.getLoader();
            } else if (fxmlLoader != null && fxmlLoader.getController() instanceof Analytics) {

                ((Analytics) fxmlLoader.getController()).contentMain.getChildren().clear();

                ((Analytics) fxmlLoader.getController()).contentMain.getChildren().addAll(afterContentMain.getChildren());
                if (tilePane == null) {
                    AnchorPane anchorPane = (AnchorPane) ((Analytics) fxmlLoader.getController()).contentMain.getChildren().getFirst();
                    for (int i = 0; i < anchorPane.getChildren().size(); i++) {
                        if (anchorPane.getChildren().get(i) instanceof TilePane) {
                            tilePane = (TilePane) anchorPane.getChildren().get(i);
                        }
                    }
                }
                webView.getEngine().load(null);

                tilePane.getChildren().clear();

                tilePane.getChildren().addAll(webView);

                setReplayVideo(webView);

                fxmlLoader = HelloApplication.getLoader();
            } else if (fxmlLoader != null && fxmlLoader.getController() instanceof Reactions) {

                ((Reactions) fxmlLoader.getController()).contentMain.getChildren().clear();

                ((Reactions) fxmlLoader.getController()).contentMain.getChildren().addAll(afterContentMain.getChildren());
                if (tilePane == null) {
                    AnchorPane anchorPane = (AnchorPane) ((Reactions) fxmlLoader.getController()).contentMain.getChildren().getFirst();
                    for (int i = 0; i < anchorPane.getChildren().size(); i++) {
                        if (anchorPane.getChildren().get(i) instanceof TilePane) {
                            tilePane = (TilePane) anchorPane.getChildren().get(i);
                        }
                    }
                }
                webView.getEngine().load(null);

                tilePane.getChildren().clear();

                tilePane.getChildren().addAll(webView);

                setReplayVideo(webView);

                fxmlLoader = HelloApplication.getLoader();
            } else if (fxmlLoader != null && fxmlLoader.getController() instanceof Account) {

                ((Account) fxmlLoader.getController()).contentMain.getChildren().clear();

                ((Account) fxmlLoader.getController()).contentMain.getChildren().addAll(afterContentMain.getChildren());
                if (tilePane == null) {
                    AnchorPane anchorPane = (AnchorPane) ((Account) fxmlLoader.getController()).contentMain.getChildren().getFirst();
                    for (int i = 0; i < anchorPane.getChildren().size(); i++) {
                        if (anchorPane.getChildren().get(i) instanceof TilePane) {
                            tilePane = (TilePane) anchorPane.getChildren().get(i);
                        }
                    }
                }
                webView.getEngine().load(null);

                tilePane.getChildren().clear();

                tilePane.getChildren().addAll(webView);

                setReplayVideo(webView);

                fxmlLoader = HelloApplication.getLoader();
            } else if (fxmlLoader != null && fxmlLoader.getController() instanceof Staff) {

                ((Staff) fxmlLoader.getController()).contentMain.getChildren().clear();

                ((Staff) fxmlLoader.getController()).contentMain.getChildren().addAll(afterContentMain.getChildren());
                if (tilePane == null) {
                    AnchorPane anchorPane = (AnchorPane) ((Staff) fxmlLoader.getController()).contentMain.getChildren().getFirst();
                    for (int i = 0; i < anchorPane.getChildren().size(); i++) {
                        if (anchorPane.getChildren().get(i) instanceof TilePane) {
                            tilePane = (TilePane) anchorPane.getChildren().get(i);
                        }
                    }
                }
                webView.getEngine().load(null);

                tilePane.getChildren().clear();

                tilePane.getChildren().addAll(webView);

                setReplayVideo(webView);

                fxmlLoader = HelloApplication.getLoader();
            }
        });


        LblExit.setOnMouseClicked(e -> {
            System.exit(0);
        });
        LblAnalytics.setOnMouseClicked(e -> {
            if (fxmlLoader != null && fxmlLoader.getController() instanceof DashboardContent) {
                DashboardContent content = fxmlLoader.getController();
                fxmlLoader = new FXMLLoader(getClass().getResource("Analytics.fxml"));
                try {
                    fxmlLoader.load();
                } catch (IOException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("BAD REQUEST 404");
                    alert.setHeaderText("Ha ocurrido un error inesperado");
                    alert.setContentText("Al parecer la categoria de videos seleccionada no se encuentra disponible" +
                            "en tu region por favor intenta mas tarde");
                    alert.show();
                }
                Analytics analytics = fxmlLoader.getController();
                content.contentMain.getChildren().clear();
                content.contentMain.getChildren().addAll(analytics.contentMain);
            } else if (fxmlLoader != null && fxmlLoader.getController() instanceof HelloController) {
                webView.getEngine().load(null);
                fxmlLoader = new FXMLLoader(getClass().getResource("Analytics.fxml"));
                try {
                    fxmlLoader.load();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                afterContentMain.getChildren().addAll(contentMain.getChildren());
                Analytics content = fxmlLoader.getController();
                contentMain.getChildren().clear();
                contentMain.getChildren().add(content.contentMain);
            } else if (HelloApplication.getLoader() != null && HelloApplication.getLoader().getController() instanceof HelloController) {
                webView.getEngine().load(null);
                fxmlLoader = new FXMLLoader(getClass().getResource("Analytics.fxml"));
                try {
                    fxmlLoader.load();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                afterContentMain.getChildren().addAll(contentMain.getChildren());
                Analytics content = fxmlLoader.getController();
                contentMain.getChildren().clear();
                contentMain.getChildren().add(content.contentMain);
            } else if (fxmlLoader != null && fxmlLoader.getController() instanceof Staff) {
                Staff content = fxmlLoader.getController();
                fxmlLoader = new FXMLLoader(getClass().getResource("Analytics.fxml"));
                try {
                    fxmlLoader.load();
                } catch (IOException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("BAD REQUEST 404");
                    alert.setHeaderText("Ha ocurrido un error inesperado");
                    alert.setContentText("Al parecer la categoria de videos seleccionada no se encuentra disponible" +
                            "en tu region por favor intenta mas tarde");
                    alert.show();
                }
                Analytics analytics = fxmlLoader.getController();
                content.contentMain.getChildren().clear();
                content.contentMain.getChildren().addAll(analytics.contentMain);
            }
        });
        LblStaff.setOnMouseClicked(ex -> {
            if (fxmlLoader != null && fxmlLoader.getController() instanceof DashboardContent) {
                DashboardContent content = fxmlLoader.getController();
                fxmlLoader = new FXMLLoader(getClass().getResource("Staff.fxml"));
                try {
                    fxmlLoader.load();
                } catch (IOException exception) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("BAD REQUEST 404");
                    alert.setHeaderText("Ha ocurrido un error inesperado");
                    alert.setContentText("Al parecer la categoria de videos seleccionada no se encuentra disponible" +
                            "en tu region por favor intenta mas tarde");
                    alert.show();
                }
                Staff staff = fxmlLoader.getController();
                content.contentMain.getChildren().clear();
                content.contentMain.getChildren().addAll(staff.contentMain);
            } else if (fxmlLoader != null && fxmlLoader.getController() instanceof HelloController) {
                webView.getEngine().load(null);
                fxmlLoader = new FXMLLoader(getClass().getResource("Staff.fxml"));
                try {
                    fxmlLoader.load();
                } catch (IOException exception) {
                    throw new RuntimeException(exception);
                }
                afterContentMain.getChildren().addAll(contentMain.getChildren());
                Staff content = fxmlLoader.getController();
                contentMain.getChildren().clear();
                contentMain.getChildren().add(content.contentMain);
            } else if (HelloApplication.getLoader() != null && HelloApplication.getLoader().getController() instanceof HelloController) {
                webView.getEngine().load(null);
                fxmlLoader = new FXMLLoader(getClass().getResource("Staff.fxml"));
                try {
                    fxmlLoader.load();
                } catch (IOException exception) {
                    throw new RuntimeException(exception);
                }
                afterContentMain.getChildren().addAll(contentMain.getChildren());
                Staff content = fxmlLoader.getController();
                contentMain.getChildren().clear();
                contentMain.getChildren().add(content.contentMain);
            }
        });

    }

    TilePane tilePane = null;

    public static void formatMinutes(String duration) {
        if (!duration.contains("H")) {
            int Minutes = duration.indexOf('T') + 1;
            int limitMinutes = duration.indexOf('M');
            StringBuilder formatMinutes = new StringBuilder("0");
            for (int j = Minutes; j < limitMinutes; j++) {
                formatMinutes.append(duration.charAt(j));
                //formatMinutes = new StringBuilder(formatMinutes.toString().trim());
            }
            listMinutes.add(Integer.parseInt(formatMinutes.toString()));
        }
    }
    /*
    * 1. Este metodo recibe por parametro un String que viene siendo la duracion de un video de YouTube
    * Por lo general los videos de YouTube  este nos retorna
    * el tiempo que dura el video pero en formato ISO Por lo tanto si deseamos calcular el promedio de segundos
    * minutos o horas que dura un video de YouTube requerimos transformarlo en un formato numerico para  luego
    * usar el metodo Integer.parseInt() y asi utilizar esos valores para realizar calculos
    *
    * */
    public static void formatSeconds(String duration) {
        if (!duration.contains("H")) {
            int Seconds = duration.indexOf('M') + 1;
            int limitSeconds = duration.indexOf('S');
            StringBuilder formatSeconds = new StringBuilder("0");
            for (int j = Seconds; j < limitSeconds; j++) {
                formatSeconds.append(duration.charAt(j));
                formatSeconds = new StringBuilder(formatSeconds.toString().trim());
            }
            try {
                listSeconds.add(Integer.parseInt(formatSeconds.toString()));
            } catch (NumberFormatException ignored) {
            }
        }
    }

    public static int getAverageMinutes() throws IOException {
        YouTube.Videos.List videos = getService().videos().list("ContentDetails");
        if (DashboardContent.getSelectedCategory() != null)
            videos.setVideoCategoryId(DashboardContent.getCategoryAndId().get(DashboardContent.getSelectedCategory()));
        videos.setMaxResults((DashboardContent.getResults() == 0L) ? 30L : DashboardContent.getResults());
        videos.setChart("mostPopular");
        videos.setRegionCode(DashboardContent.getRegion());
        VideoListResponse response = videos.execute();

        for (int i = 0; i < response.getItems().size(); i++) {
            String duration = response.getItems().get(i).getContentDetails().getDuration();
            formatMinutes(duration);
        }
        AtomicInteger sum = new AtomicInteger();
        listMinutes.forEach(sum::addAndGet);
        return sum.get() / listMinutes.size();
    }

    public static int getAverageSeconds() throws IOException {
        YouTube.Videos.List videos = getService().videos().list("ContentDetails");
        if (DashboardContent.getSelectedCategory() != null)
            videos.setVideoCategoryId(DashboardContent.getCategoryAndId().get(DashboardContent.getSelectedCategory()));
        videos.setMaxResults((DashboardContent.getResults() == 0L) ? 30L : DashboardContent.getResults());
        videos.setChart("mostPopular");
        videos.setRegionCode(DashboardContent.getRegion());

        VideoListResponse response = videos.execute();
        for (int j = 0; j < response.getItems().size(); j++) {
            String duration = response.getItems().get(j).getContentDetails().getDuration();
            formatSeconds(duration);
        }

        AtomicInteger sum = new AtomicInteger();

        listSeconds.forEach(sum::addAndGet);

        return sum.get() / listMinutes.size();
    }
}