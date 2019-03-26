import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import repository.RepositoryJdbcStaff;
import services.StaffServices;
import view.controllers.FXMLStartDocController;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class StartApp extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/FXMLStartDoc.fxml"));
        Parent root = loader.load();
        FXMLStartDocController ctrl = loader.getController();
        ctrl.setServices(getStaffServices());
        Scene scene = new Scene(root, 340, 340);
        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    static StaffServices getStaffServices() {
        Properties serverProps=new Properties();
        try {
            serverProps.load(new FileReader("sqlite_db.config"));
//            System.out.println("Properties set. ");
//            serverProps.list(System.out);
        } catch (IOException e) {
            System.out.println("Cannot find sqlite_db.config "+e);
        }
        RepositoryJdbcStaff repo = new RepositoryJdbcStaff(serverProps);
        return new StaffServices(repo);
    }
}
