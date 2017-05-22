package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * Created by Wienio on 2017-05-07.
 */
public class Client {
    private Socket socket;
    private DataInputStream din;
    private DataOutputStream dout;

    public Client(String serverName, Button serverButton, Button clientButton, TextArea textArea, Button clearButton, Canvas canvas) {
        try {
            socket = new Socket(serverName, ServerConnection.getPORT());
            din = new DataInputStream(socket.getInputStream());
            dout = new DataOutputStream(socket.getOutputStream());

            Thread t = new Thread() {
                public void run() {

                    while (true) {
                        try {
                            BufferedImage bi = ImageIO.read(din);
                            if(bi!=null) {
                                WritableImage image = SwingFXUtils.toFXImage(bi, null);
                                canvas.getGraphicsContext2D().drawImage(image, 0, 0);
                            }
//                            socket.getOutputStream().flush();
//                            String message = din.readUTF();
//                            textArea.appendText("Server> " + message + "\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };

            t.setDaemon(true);
            t.start();

            serverButton.setDisable(true);
            clientButton.setText("Klient został podłączony!");
            clientButton.setDisable(true);
            clearButton.setDisable(true);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Błąd!");
            alert.setHeaderText("Nie udało się połączyć z serverem!");
            alert.showAndWait();
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public DataOutputStream getDout() {
        return dout;
    }
}
