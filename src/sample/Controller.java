package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class Controller {
    @FXML
    private Canvas canvas;
    @FXML
    private Button serverButton;
    @FXML
    private Button clientButton;
    @FXML
    private Button clearButton;
    @FXML
    private ColorPicker colorPicker;
    @FXML
    private ComboBox<Integer> comboBox;
    @FXML
    private TextArea textArea;
    @FXML
    private TextField textField;

    private BufferedImage bi;

    private static ArrayList<Client> clientsList = new ArrayList<>();
    private static ArrayList<ServerConnection> serverList = new ArrayList<>();

    @FXML
    public void initialize() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        for (int i = 1; i < 42; ++i) {
            comboBox.getItems().add(i);
        }
        comboBox.setValue(3);

        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                gc.setStroke(colorPicker.getValue());
                gc.setLineWidth(comboBox.getValue());
                gc.beginPath();
                gc.moveTo(event.getX(), event.getY());
                gc.stroke();
            }
        });

        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                gc.setStroke(colorPicker.getValue());
                gc.setLineWidth(comboBox.getValue());
                gc.lineTo(event.getX(), event.getY());
                gc.stroke();
            }
        });

        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                WritableImage image = canvas.snapshot(null, null);
                bi = SwingFXUtils.fromFXImage(image, null);
                try {
                    if (serverList.size() > 0) {
                        ImageIO.write(bi, "PNG", serverList.get(0).getSocket().getOutputStream());
                    }
                    if (clientsList.size() > 0) {
                        ImageIO.write(bi, "PNG", clientsList.get(0).getSocket().getOutputStream());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    private void clearCanvas() {
        canvas.getGraphicsContext2D().setFill(Color.BLACK);
        canvas.getGraphicsContext2D().fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        WritableImage image = canvas.snapshot(null, null);
        bi = SwingFXUtils.fromFXImage(image, null);
        try {
            if (serverList.size() > 0) {
                ImageIO.write(bi, "PNG", serverList.get(0).getSocket().getOutputStream());
            }
            if (clientsList.size() > 0) {
                ImageIO.write(bi, "PNG", clientsList.get(0).getSocket().getOutputStream());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void sendMessage() throws IOException {
        if (textField.getText() != "") {
            textArea.appendText("Ty> " + textField.getText() + "\n");
            for (int i = 0; i < clientsList.size(); ++i) {
                clientsList.get(i).getDout().writeUTF(textField.getText());
            }
            for (int i = 0; i < serverList.size(); ++i) {
                serverList.get(i).getDout().writeUTF(textField.getText());
            }
            textField.clear();
        }
    }

    @FXML
    private void launchServer() {
        try {
            ServerConnection server = new ServerConnection(textArea,canvas);
            serverList.add(server);
            serverButton.setText("Serwer pracuje...");
            serverButton.setDisable(true);
            clientButton.setDisable(true);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd!");
            alert.setHeaderText("Bład przy tworzeniu serwera!");
            alert.showAndWait();
        }
    }

    @FXML
    private void clientJoin() {
        Client client = new Client("localhost", serverButton, clientButton, textArea, clearButton, canvas);
        clientsList.add(client);
    }
}
