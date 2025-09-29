package it.unipi.memorizer.app;

import it.unipi.memorizer.app.ServiceRequest.HttpMethod;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.application.Platform;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.stage.Stage;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WordListController
{
    private static final Logger Log = LogManager.getLogger(WordListController.class);
    @FXML private TableView<WordWithInfo> wordTable = new TableView<>();
    private ObservableList<WordWithInfo> ol;
    private Instant lastClick;
    private WordWithInfo lastClicked;
    private ContextMenu contextMenu;
    private ContextMenu contextMenuAdd;
    @FXML private ProgressIndicator loader;
    @FXML private VBox root;
    private final EventHandler<KeyEvent> keyEventHandler = event ->
    {
        if(event.getCode() == KeyCode.ENTER)
        {
            WordWithInfo selectedWord = wordTable.getSelectionModel().getSelectedItem();
            openWord(selectedWord);
        }
        if(event.getCode() == KeyCode.DELETE)
        {
            WordWithInfo selectedWord = wordTable.getSelectionModel().getSelectedItem();
            deleteWord(selectedWord);
        }
        if(event.getCode() == KeyCode.PLUS)
        {
            addWord();
        }
    };
    
    public void setWordList(WordWithInfo[] wordList)
    {
        ol = FXCollections.observableArrayList();
        ol.addAll(Arrays.asList(wordList));
        wordTable.setItems(ol);
    }
    
    @FXML private void goBack()
    {
        App.getMainWindow().setRoot("home", null);
    }
    
    @FXML private void initialize()
    {
        loader.setVisible(false);
        
        TableColumn<WordWithInfo, String> wordColumn = new TableColumn("Word");
        wordColumn.setCellValueFactory(new PropertyValueFactory<>("word"));
        wordColumn.setPrefWidth(100);
        TableColumn<WordWithInfo, String> descriptionColumn = new TableColumn("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionColumn.setPrefWidth(200);
        TableColumn<WordWithInfo, String> translationColumn = new TableColumn("Translation");
        translationColumn.setCellValueFactory(new PropertyValueFactory<>("translation"));
        translationColumn.setPrefWidth(90);
        TableColumn<WordWithInfo, Double> confidenceColumn = new TableColumn("Confidence");
        confidenceColumn.setCellValueFactory(new PropertyValueFactory<>("confidence"));
        confidenceColumn.setCellFactory(c -> new TableCell<>()
        {
            @Override
            protected void updateItem(Double confidence, boolean empty)
            {
                super.updateItem(confidence, empty);
                if (confidence == null || empty)
                {
                    setText(null);
                }
                else
                {
                    setText(String.format("%.1f%%", confidence * 100));
                }
            }
        });
        confidenceColumn.setPrefWidth(90);
        TableColumn<WordWithInfo, Integer> quizzedColumn = new TableColumn("Quizzed");
        quizzedColumn.setCellValueFactory(new PropertyValueFactory<>("quizzed"));
        quizzedColumn.setPrefWidth(80);
        TableColumn<WordWithInfo, Integer> guessedColumn = new TableColumn("Guessed");
        guessedColumn.setCellValueFactory(new PropertyValueFactory<>("guessed"));
        guessedColumn.setPrefWidth(80);
        
        wordTable.getColumns().addAll(wordColumn, descriptionColumn, translationColumn, confidenceColumn, quizzedColumn, guessedColumn);
        
        addContextMenu();
        
        wordTable.addEventHandler(KeyEvent.KEY_PRESSED, keyEventHandler);
        
        wordTable.setRowFactory(tableView -> 
        {
            TableRow<WordWithInfo> row = new TableRow<>();
            row.setOnMouseClicked(event ->
            {
                if (row.isEmpty())
                {
                    wordTable.getSelectionModel().clearSelection();
                }
            });
            return row;
        });
        
        wordTable.addEventHandler(MouseEvent.MOUSE_CLICKED, event ->
        {
            contextMenu.hide();
            contextMenuAdd.hide();
            
            Node clickedNode = event.getPickResult().getIntersectedNode();
            while (clickedNode != null && clickedNode != wordTable && !(clickedNode instanceof TableRow))
            {
                clickedNode = clickedNode.getParent();
            }
            if (!(clickedNode instanceof TableRow))
            {
                return;
            }
            WordWithInfo selectedWord = wordTable.getSelectionModel().getSelectedItem();
            if(selectedWord == null)
            {
                if(event.getButton() == MouseButton.SECONDARY)
                    contextMenuAdd.show(wordTable, event.getScreenX(), event.getScreenY());
                return;
            }
            if (event.getButton() == MouseButton.SECONDARY)
            {
                contextMenu.show(wordTable, event.getScreenX(), event.getScreenY());
            }
            else 
            {
                contextMenu.hide();
                if (event.getButton() == MouseButton.PRIMARY)
                {
                    if(selectedWord == lastClicked && Duration.between(lastClick, Instant.now()).toMillis() < 500)
                    {
                        editWord(selectedWord);
                        lastClick = null;
                    }
                    lastClicked = selectedWord;
                    lastClick = Instant.now();
                }
            }
        });
    }
    
    private void addContextMenu()
    {
        contextMenu = new ContextMenu(); 
        contextMenuAdd = new ContextMenu();
        
        MenuItem openItem = new MenuItem("View Details");
        MenuItem editItem = new MenuItem("Edit");
        MenuItem deleteItem = new MenuItem("Delete");
        MenuItem addItem = new MenuItem("Add New Word");
        MenuItem addItem2 = new MenuItem("Add New Word");
        
        contextMenu.getItems().addAll(openItem, editItem, deleteItem, addItem);
        contextMenuAdd.getItems().addAll(addItem2);

        editItem.setOnAction(ae -> editWord(wordTable.getSelectionModel().getSelectedItem()));
        deleteItem.setOnAction(ae -> deleteWord(wordTable.getSelectionModel().getSelectedItem()));
        openItem.setOnAction(ae -> openWord(wordTable.getSelectionModel().getSelectedItem()));
        addItem.setOnAction(ae -> addWord());
        addItem2.setOnAction(ae -> addWord());
    }
    
    public void editWord(WordWithInfo selectedWord)
    {
        if (selectedWord != null)
        {
            new Window(new Stage(), "editWord", 400, 450, "Memorizer: Edit \"" + selectedWord.getWord() + "\"", controller ->
            {
                ((EditWordController)controller).setWord(selectedWord);
            }, true);
            wordTable.refresh();
        }
    }
    
    public void addWord()
    {
        new Window(new Stage(), "addWord", 400, 450, "Memorizer: Add New Word", controller ->
        {
            ((AddWordController)controller).setExternal(ol);
        }, true);
    }
    
    public void openWord(Word selectedWord)
    {
        if (selectedWord != null)
        {
            new Window(new Stage(), "openWord", 400, 400, "Memorizer: Details for \"" + selectedWord.getWord() + "\"", controller ->
            {
                ((OpenWordController)controller).setWord(selectedWord);
            }, false);
        }
    }
    
    public void deleteWord(WordWithInfo selectedWord)
    {
        if (selectedWord != null)
        {
            String deletedWord = selectedWord.getWord();
            if(Settings.getConfirmationOnDelete() && !Window.createConfirmationBox("Are you sure you want to delete the word \"" + deletedWord + "\""))
                return;
            showLoader();
            Task task = new Task<Void>()
            {
                @Override public Void call()
                {
                    ServiceRequest r = new ServiceRequest("word", HttpMethod.DELETE, new Parameter[]{new Parameter("word", deletedWord)});
                    String response;
                    try
                    {
                        response = r.send();
                    }
                    catch (DisconnectedException ex)
                    {
                        Log.warn("It looks like you're disconnected!");
                        Platform.runLater(() -> 
                        {
                            hideLoader();
                            Window.createAlertBox("It looks like you're disconnected!");
                        });
                        return null;
                    }
                    if(response.equals("ok"))
                    {
                        Log.info("Word deleted");
                        Platform.runLater(() -> 
                        {
                            ol.remove(selectedWord);
                            hideLoader();
                        });
                    }
                    else
                    {
                        Log.error("Unexpected response");
                        Platform.runLater(() -> 
                        {
                            hideLoader();
                            Window.createAlertBox("An error occurred");
                        });
                    }
                    return null;
                }
            };
            new Thread(task).start();
        }
    }
    
    void showLoader()
    {
        loader.setVisible(true);
        root.setOpacity(0.3);
        root.mouseTransparentProperty().setValue(true);
        root.removeEventHandler(KeyEvent.KEY_PRESSED, keyEventHandler);
    }
    
    void hideLoader()
    {
        loader.setVisible(false);
        root.setOpacity(1);
        root.mouseTransparentProperty().setValue(false);
        root.addEventHandler(KeyEvent.KEY_PRESSED, keyEventHandler);
    }
}
