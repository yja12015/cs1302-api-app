package cs1302.api;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.Priority;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.geometry.Pos;
import javafx.scene.text.TextAlignment;

import java.util.Random;

import javafx.scene.control.TextArea;
import java.util.ArrayList;
import java.util.HashSet;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX app utillizing Imgflip Api and Joke Api to create a meme builder using templates and jokes
 * provided by the API.
 */
public class ApiApp extends Application {

    private Stage stage;
    private Scene scene;
    private VBox root;
    private HBox selectBarCategory;
    private HBox selectBarSearch;
    private HBox memeBarMiddle;
    private HBox memeBarBottom;

    private Label jokeCreate;
    private Label category;
    private Label language;
    private Label search;
    private ComboBox<String> languageSelect;
    private ComboBox<Integer> jokeSelect;
    private TextField searchField;
    private TextField memeField;
    private Label memeLabel;
    private Button jokeFind;
    private Label loadTemplate;
    private Button loadTemplateButton;
    private TextArea jokeMessageBar;
    private Label messageBar;
    private TilePane memeTemplates;
    private Button createMeme;
    private ImageView meme;
    private VBox categoriesVertical;
    private CheckBox anyCheck;
    private CheckBox programmingCheck;
    private CheckBox miscCheck;
    private CheckBox darkCheck;
    private CheckBox punCheck;
    private CheckBox spookyCheck;
    private CheckBox christmasCheck;
    private CheckBox[] categoriesCheck;
    private HBox categoriesHorizontal;
    private GridPane memeGrid;
    private HBox languageBar;
    private HBox loadTemplateSection;
    private HBox createMemeSection;
    private HBox selectBar;
    private ArrayList<ImageView> templateListPanel;
    private ArrayList<ImgflipApi.Template> templateList;
    private ArrayList<ImgflipApi.Template> templatesAll;
    private JokeApi.JokeApiResult jokeApiResult;
    private int templateIndex;

    /**
     * Constructs an {@code ApiApp} object. This default (i.e., no argument)
     * constructor is executed in Step 2 of the JavaFX Application Life-Cycle.
     */
    public ApiApp() {
        root = new VBox();
        selectBarCategory = new HBox();
        selectBarSearch = new HBox();
        memeBarMiddle = new HBox();
        memeBarBottom = new HBox();
        jokeCreate = new Label("Joke Selection:");
        category = new Label("Category:");
        language = new Label("Language:");
        search = new Label("Search phrase:");
        searchField = new TextField();
        searchField.setPromptText("(Optional)");
        searchField.setFocusTraversable(false);
        jokeFind = new Button("Find Jokes");

        categoriesVertical = new VBox();
        categoriesHorizontal = new HBox();
        anyCheck = new CheckBox("Any");
        programmingCheck = new CheckBox("Programming");
        miscCheck = new CheckBox("Misc");
        darkCheck = new CheckBox("Dark");
        punCheck = new CheckBox("Pun");
        spookyCheck = new CheckBox("Spooky");
        christmasCheck = new CheckBox("Christmas");
        categoriesCheck = new CheckBox[]{anyCheck, programmingCheck, miscCheck, darkCheck,
                                         punCheck, spookyCheck, christmasCheck};
        languageSelect = new ComboBox<String>();
        jokeSelect = new ComboBox<Integer>();
        loadTemplate = new Label("Meme Templates:");
        loadTemplateButton = new Button("Load");
        messageBar = new Label("Use the selection features to search for jokes and choose a " +
        "meme template.");
        jokeMessageBar = new TextArea();
        memeLabel = new Label("Create meme:");
        createMeme = new Button("Create");
        memeField = new TextField("Meme Link:");

        loadTemplateSection = new HBox();
        createMemeSection = new HBox();

        languageBar = new HBox();
        memeGrid = new GridPane();
        meme = new ImageView();
        memeTemplates = new TilePane();
        templateListPanel = new ArrayList<ImageView>(10);
        templateList = new ArrayList<ImgflipApi.Template>(5);
        selectBar = new HBox();
        templateIndex = -1;
    } // ApiApp

    /** {@inheritDoc} */
    @Override
    public void start(Stage stage) {
        this.stage = stage;

        sceneSetup();

        anyCheck.setOnMouseClicked(e -> anyCheck());
        jokeFind.setOnAction(e -> findJoke());
        loadTemplateButton.setOnAction(e -> loadTemplate());
        createMeme.setOnAction(e -> createMeme());
        jokeSelect.setOnAction(e -> changeJoke());
        jokeSelect.setDisable(true);

        languageBar.setAlignment(Pos.CENTER_LEFT);
        selectBar.setAlignment(Pos.CENTER_LEFT);
        loadTemplateSection.setAlignment(Pos.CENTER_LEFT);
        createMemeSection.setAlignment(Pos.CENTER_LEFT);
        memeBarMiddle.setAlignment(Pos.CENTER);
        messageBar.setTextAlignment(TextAlignment.CENTER);
        root.setAlignment(Pos.TOP_CENTER);

        languageSelect.getItems().addAll("English", "Spanish", "French", "German", "Portuguese",
            "Czech");
        languageSelect.getSelectionModel().selectFirst();

        memeTemplates.setPrefRows(3);
        memeTemplates.setPrefColumns(3);
        memeTemplates.setMaxWidth(300);
        memeTemplates.setMaxHeight(300);
        for (int i = 0; i < 9; i++) {
            ImageView imgView = new ImageView(new Image("file:resources/default.png"));
            templateListPanel.add(imgView);
            memeTemplates.getChildren().add(imgView);
            imgView.setPreserveRatio(true);
            final ImageView imgView1 = imgView;
            final int index = i;
            imgView.setOnMouseClicked(e -> templateChosen(imgView1, index));
        }
        meme.setImage(new Image("file:resources/default.png"));

        memeGrid.add(memeTemplates,1,0);
        meme.setPreserveRatio(true);
        memeGrid.add(meme,2,0);
        meme.setFitWidth(300);
        memeGrid.setHgap(30);
        memeField.setEditable(false);
        memeField.setPrefWidth(220);
        jokeMessageBar.setEditable(false);
        jokeMessageBar.setWrapText(true);
        jokeMessageBar.setPrefRowCount(2);

        scene = new Scene(root,690,600);

        // setup stage
        stage.setTitle("MemeBuidlerApiApp");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> Platform.exit());
        stage.sizeToScene();
        stage.show();

    } // start

    /**
     * Scene graph setup and spacing.
     */
    public void sceneSetup() {
        root.getChildren().addAll(jokeCreate, selectBarCategory, selectBarSearch,
            messageBar, jokeMessageBar, memeBarMiddle, memeGrid);
        root.setSpacing(5);

        categoriesHorizontal.getChildren().addAll(categoriesCheck);
        categoriesHorizontal.setSpacing(10);

        selectBarCategory.getChildren().addAll(category, categoriesHorizontal);
        selectBarCategory.setSpacing(15);

        languageBar.getChildren().addAll(language,languageSelect);
        languageBar.setSpacing(10);
        selectBar.getChildren().addAll(search, searchField, jokeFind);
        selectBar.setSpacing(10);

        selectBarSearch.getChildren().addAll(languageBar,selectBar, jokeSelect);
        selectBarSearch.setSpacing(10);

        loadTemplateSection.getChildren().addAll(loadTemplate, loadTemplateButton);
        loadTemplateSection.setSpacing(10);
        createMemeSection.getChildren().addAll(createMeme, memeField);
        createMemeSection.setSpacing(10);

        memeBarMiddle.getChildren().addAll(loadTemplateSection, createMemeSection);
        memeBarMiddle.setSpacing(140);

    }

    /**
     * When anyCheck is ticked to true the other categories will be changed to true and
     * set to disabled so they cannot be changed while any is checked. When unchecked all
     * category checks are enabled again.
     */
    public void anyCheck() {
        if (anyCheck.isSelected()) {
            for (int i = 1; i < categoriesCheck.length; i++) {
                categoriesCheck[i].setSelected(true);
                categoriesCheck[i].setDisable(true);
            }
        } else {
            for (int i = 1; i < categoriesCheck.length; i++) {
                categoriesCheck[i].setDisable(false);
            }
        }
    }

    /**
     * When findJoke button is pressed it calls {@link JokeApi#getJoke} to return a
     * JokeApiResult object.
     *
     */
    public void findJoke() {
        int checks = 0;
        for (CheckBox category: categoriesCheck) {
            if (category.isSelected()) {
                checks += 1;
            }
        }
        if (checks == 0) {
            messageBar.setText("Error please choose a category to search.");
            return;
        }
        String[] categories = new String[checks];
        int i = 0;
        for (CheckBox category: categoriesCheck) {
            if (category.isSelected()) {
                categories[i] = category.getText();
                i++;
            }
        }

        String search = searchField.getText();
        searchField.clear();

        String lang = languageSelect.getValue();

        JokeApi.JokeApiResult jokeApiResultTemp = JokeApi.getJoke(categories, lang, search);
        if (jokeApiResultTemp == null) {
            messageBar.setText("Error was unable to find any jokes. Please try different search.");
            return;
        } else {
            jokeSelect.setDisable(false);
            jokeApiResult = jokeApiResultTemp;

            selectBarSearch.getChildren().remove(jokeSelect);
            ComboBox<Integer> jokeSelectTemp = new ComboBox<Integer>();

            for (int j = 0; j < jokeApiResult.amount; j++) {
                jokeSelectTemp.getItems().add(j + 1);
            }
            jokeSelect = jokeSelectTemp;
            jokeSelect.setOnAction(e -> changeJoke());
            selectBarSearch.getChildren().add(jokeSelect);
            jokeSelect.getSelectionModel().selectFirst();

            jokeMessageBar.setPrefRowCount(3);
            changeJoke();
            messageBar.setText(jokeApiResult.amount + " jokes loaded. Select different jokes " +
                "using the numbered box.");
            return;
        }
    }

    /**
     * Uses jokeSelectionBox to choose a joke in the range selected and be printed out for the user.
     */
    public void changeJoke() {
        int index = jokeSelect.getValue() - 1;
        String setup = jokeApiResult.jokes[index].setup;
        String punchline = jokeApiResult.jokes[index].delivery;
        jokeMessageBar.setText(String.format("Joke %d:\nSetup: %s\nPunchline: %s",
            index + 1, setup, punchline));

    }

    /**
     * Pressing load template uses {@link ImgflipApi#getTemplates} to get arraylist of templates.
     */
    public void loadTemplate() {
        if (templatesAll == null) {
            templatesAll = ImgflipApi.getTemplates();
            if (templatesAll.size() == 0) {
                messageBar.setText("Error. No templates were found.");
                return;
            }
        }
        Random random = new Random();

        templateList.clear();

        HashSet<Integer> distinctIndexes = new HashSet<Integer>();

        for (int i = 0; i < 9; i++) {
            int rand_index = random.nextInt(templatesAll.size());

            while (distinctIndexes.contains(rand_index)) {
                rand_index = random.nextInt(templatesAll.size());
            }
            distinctIndexes.add(rand_index);

            templateListPanel.get(i).setImage(new Image(templatesAll.get(rand_index).url,100,100,
                false, true));
            templateList.add(templatesAll.get(rand_index));
        }
        messageBar.setText("Loaded Templates. Please select a template to create a meme.");
    }

    /**
     * When clicking on a template image it will be chosen as the image to be used when creating
     * the meme.
     *
     * @param imgView contains the image selected for the meme
     * @param index the index of the imgView in templateList.
     */
    public void templateChosen(ImageView imgView, int index) {
        meme.setImage(new Image(templateList.get(index).url));
        templateIndex = index;
        messageBar.setText("Template selected. Press 'Create' to make the meme.");
    }

    /**
     * When clicking the create meme button it calls {@link ImgflipApi#createImg} to create
     * the meme in the form of a jpg and link.
     */
    public void createMeme() {
        if (templateIndex == -1) {
            messageBar.setText("Select a template.");
            return;
        } else if (templateList.size() == 0 ) {
            messageBar.setText("Load templates before generating a meme.");
            return;
        } else if (jokeApiResult == null) {
            messageBar.setText("Find jokes before creating a meme!");
            return;
        }


        int jokeIndex = jokeSelect.getValue() - 1;
        String setup = jokeApiResult.jokes[jokeIndex].setup;
        String punchline = jokeApiResult.jokes[jokeIndex].delivery;
        String templateID = templateList.get(templateIndex).id;

        String memeLink = ImgflipApi.createImg(setup, punchline, templateID);
        messageBar.setText("Created Meme! Link to meme below.");
        memeField.setText(memeLink);
        meme.setImage(new Image(memeLink));
    }
} // ApiApp
