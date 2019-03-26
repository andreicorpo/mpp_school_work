package utils;

import javafx.scene.control.Alert;

public class AlertError {
    public AlertError() {
    }

    public void alertError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        alert.showAndWait();
    }
}
