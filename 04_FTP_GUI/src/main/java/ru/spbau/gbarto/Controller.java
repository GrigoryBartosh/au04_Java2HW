package ru.spbau.gbarto;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Controller of scene.
 */
public class Controller {
    private static final String START_PATH = ".";

    private static String hostName;
    private static int portNumber;

    @FXML private TableView tableView;

    private String currentPath = START_PATH;

    /**
     * Contents information about file in single line.
     */
    public static class TableFile {
        public final SimpleStringProperty name;
        public final SimpleStringProperty type;

        public TableFile(String name, boolean isFolder) {
            this.name = new SimpleStringProperty(name);
            this.type = new SimpleStringProperty(isFolder ? "folder" : "file");
        }

        public String getName() {
            return name.get();
        }

        public String getType() {
            return type.get();
        }
    }

    /**
     * Updates current path by selected folder.
     *
     * @param pressedName name of the selected folder
     */
    private void updateCurrentPath(String pressedName) {
        currentPath = Paths.get(currentPath, pressedName).normalize().toString();
        if (currentPath.equals("")) {
            currentPath = ".";
        }
    }

    /**
     * Sends request to the server.
     *
     * @param request string with request content
     * @return ByteArrayOutputStream with response
     */
    private ByteArrayOutputStream processRequest(String request) {
        ByteArrayInputStream input = new ByteArrayInputStream(request.getBytes());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        new Client(hostName, portNumber, input, output).run();
        return output;
    }

    /**
     * constructs TableFile's from ByteArrayOutputStream.
     *
     * @param output ByteArrayOutputStream with response from the server
     * @return array of TableFile
     */
    private TableFile[] constructTableFiles(ByteArrayOutputStream output) {
        String[] result = output.toString().trim().split(System.lineSeparator());
        String[] data = Arrays.copyOfRange(result, 1, result.length);

        TableFile[] files = new TableFile[data.length];
        for (int i = 0; i < data.length; i++) {
            String[] line = data[i].split(" ");
            String name = Paths.get(line[0]).getFileName().toString();
            files[i] = new TableFile(name, Boolean.parseBoolean(line[1]));
        }
        return files;
    }

    /**
     * Updates table view by new array of files.
     *
     * @param files array of TableFile
     */
    private void updateTableView(TableFile[] files) {
        ObservableList<TableFile> lines = tableView.getItems();
        lines.clear();

        if (!currentPath.equals(".")) {
            lines.setAll(new TableFile("..", true));
        }

        lines.addAll(files);
    }

    /**
     * Processes a list request.
     *
     * @param pressedName name of selected folder
     */
    private void getList(String pressedName) {
        updateCurrentPath(pressedName);

        ByteArrayOutputStream output = processRequest("1 " + currentPath + "\nexit");
        TableFile[] files = constructTableFiles(output);
        updateTableView(files);

        Main.updateTitle(currentPath);
    }

    /**
     * Process a file request.
     *
     * @param pressedName name of selected file
     */
    private void getFile(String pressedName) {
        String name = Paths.get(currentPath, pressedName).toString();
        processRequest("2 " + name + "\nexit");
        Main.updateTitle("file " + name + " was downloaded");
    }

    /**
     * Initializes controller.
     */
    @FXML
    public void initialize() {
        hostName = Main.getHostName();
        portNumber = Main.getPortNumber();

        getList(currentPath);
    }


    /**
     * Processes double mouse click on the line in the table.
     *
     * @param mouseEvent an event happened.
     */
    @FXML
    public void mouseClick(MouseEvent mouseEvent) {
        TableFile file = (TableFile) tableView.getSelectionModel().getSelectedItem();

        if (mouseEvent.getClickCount() != 2) {
            return;
        }

        String name = file.getName();
        switch (file.getType()) {
            case "file":
                getFile(name);
                break;

            case "folder":
                getList(name);
                break;
        }
    }
}