package com.example.demo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class AddMTPCQController {
    @FXML
    private VBox showChoices;
    private int currentChoice = 0;

    private DataModel dataModel;
    private BreadCrumbBarModel breadCrumbBarModel;

    public void initDataModel(DataModel dataModel) {
        if (this.dataModel != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.dataModel = dataModel;
    }

    public void initModel(BreadCrumbBarModel breadCrumbBarModel) {
        if (this.breadCrumbBarModel != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.breadCrumbBarModel = breadCrumbBarModel;
    }

    @FXML
    private void initialize() {
        initDataModel(DataModel.getInstance());
        initModel(BreadCrumbBarModel.getInstance());


    }

    @FXML
    private void btnAddThreeMoreChoices() throws IOException {
        ArrayList<Parent> views = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("choice-box.fxml"));
            Parent parent = fxmlLoader.load();
            ChoiceBoxController view = fxmlLoader.getController();
            view.setChoice("Choice " + (i + currentChoice));
            views.add(parent);
        }
        currentChoice += 3;
        showChoices.getChildren().addAll(views);
    }

    @FXML
    private void btnSaveAndContinueEditing() {

    }

    @FXML
    private void btnSaveChanges() {

    }

    @FXML
    private void btnCancel() {

    }
}
