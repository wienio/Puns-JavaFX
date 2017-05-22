package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Wienio on 2017-05-07.
 */
public class ServerConnection extends Thread {
    private final static int PORT = 1555;

    private ServerSocket serverSocket;
    private Socket socket;
    private DataInputStream din;
    private DataOutputStream dout;
//    private ByteArrayOutputStream baos;

    public ServerConnection(TextArea textArea, Canvas canvas) throws IOException {
        serverSocket = new ServerSocket(PORT);

        Thread t = new Thread() {
            public void run() {
//                try {
                try {
                    socket = serverSocket.accept();
                    din = new DataInputStream(socket.getInputStream());
                    dout = new DataOutputStream(socket.getOutputStream());
//                    baos = new ByteArrayOutputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                while (true) {
                    try {
                        BufferedImage bi = ImageIO.read(din);
                        if (bi != null) {
                            WritableImage image = SwingFXUtils.toFXImage(bi, null);
                            canvas.getGraphicsContext2D().drawImage(image, 0, 0);
                            socket.getOutputStream().flush();
                        }
//                        String message = din.readUTF();
//                        textArea.appendText("Klient> " + message + "\n");
                    } catch (IOException e) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setHeaderText("Błąd!");
                        alert.setContentText("Wystąpił błąd poczas próby utworzenia servera!");
                        alert.showAndWait();
                    }
                }

            }
        };

        t.setDaemon(true);
        t.start();
    }

    public DataOutputStream getDout() {
        return dout;
    }

    public Socket getSocket() {
        return socket;
    }

    public static int getPORT() {
        return PORT;
    }
}
