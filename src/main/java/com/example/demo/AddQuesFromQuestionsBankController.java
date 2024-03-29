package com.example.demo;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.ArrayList;
import java.util.List;

public class AddQuesFromQuestionsBankController {
    @FXML
    private Button button;

    @FXML
    private TreeView<String> root;
    @FXML
    private VBox showInfor;
    @FXML
    private CheckBox showQuesFromCate;
    @FXML
    private VBox showQuestion;
    @FXML
    private Label label;
    @FXML
    private Button btnADD;
    private DataModel dataModel;
    @FXML
    private ListView<CustomCheckBox> list;
    private final List<CustomCheckBox> quesFromSubcategories = new ArrayList<>();

    private final List<CustomCheckBox> questions = new ArrayList<>();

    private void initDataModel(DataModel dataModel) {
        if (this.dataModel != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.dataModel = dataModel;
    }

    @FXML
    private void initialize() {
        initDataModel(DataModel.getInstance());
        showInfor.setVisible(false);
        showQuesFromCate.selectedProperty().addListener(e -> {
            if (root.getSelectionModel().getSelectedItem() != null) {
                if (showQuesFromCate.isSelected()) {
                    traverseTreeView(root.getSelectionModel().getSelectedItem());
                    list.getItems().addAll(quesFromSubcategories);
                } else {
                    list.getItems().removeAll(quesFromSubcategories);
                    quesFromSubcategories.clear();
                }
                showInfor.setVisible(!list.getItems().isEmpty());
            }
        });
        root.setRoot(dataModel.getRoot());
        root.getSelectionModel().selectedItemProperty().addListener(e -> {
            list.getItems().clear();
            quesFromSubcategories.clear();
            showQuestion();
            showInfor.setVisible(!list.getItems().isEmpty());
            label.setText(root.getSelectionModel().getSelectedItem().getValue());
        });

    }

    public Button getButton() {
        return button;
    }

    private void showQuestion() {

        if (root.getSelectionModel().getSelectedItem() != null) {
            for (Question question : dataModel.getQuestionExcept(dataModel.getCategoryMap().get(root.getSelectionModel().getSelectedItem()), dataModel.getCurrentQuiz().getQuizID())) {
                CustomCheckBox customCheckBox = new CustomCheckBox(question);
                customCheckBox.getBox().getChildren().remove(customCheckBox.getButton());
                FontIcon icon = new FontIcon("fas-plus");
                icon.setIconColor(Color.valueOf("#00ACEA"));
                FontIcon icon2 = new FontIcon("fas-search-plus");
                icon2.setIconColor(Color.valueOf("#00ACEA"));
                customCheckBox.getBox().getChildren().add(0, icon);
                customCheckBox.getChildren().add(icon2);
                customCheckBox.getLabel().setMaxWidth(200);
                list.getItems().add(customCheckBox);
                customCheckBox.getCheckBox().selectedProperty().addListener(e -> {
                    if (customCheckBox.getCheckBox().isSelected()) {
                        questions.add(customCheckBox);
                    } else questions.remove(customCheckBox);
                });
            }
            if (showQuesFromCate.isSelected()) {
                traverseTreeView(root.getSelectionModel().getSelectedItem());
                list.getItems().addAll(quesFromSubcategories);
            }
            /*list.getSelectionModel().selectedItemProperty().addListener(e -> {
                list.getSelectionModel().getSelectedItem().getCheckBox().setSelected(true);
            });*/
        }
    }

    public ListView<CustomCheckBox> getList() {
        return list;
    }

    public List<CustomCheckBox> getQuestions() {
        return questions;
    }

    public Button getBtnADD() {
        return btnADD;
    }

    private void traverseTreeView(TreeItem<String> root) {
        if (root != null) {
            for (TreeItem<String> child : root.getChildren()) {
                traverseTreeView(child);
                for (Question question : dataModel.getQuestionExcept(dataModel.getCategoryMap().get(child), dataModel.getCurrentQuiz().getQuizID())) {
                    CustomCheckBox customCheckBox = new CustomCheckBox(question);
                    customCheckBox.getBox().getChildren().remove(customCheckBox.getButton());
                    FontIcon icon = new FontIcon("fas-plus");
                    icon.setIconColor(Color.valueOf("#00ACEA"));
                    FontIcon icon2 = new FontIcon("fas-search-plus");
                    icon2.setIconColor(Color.valueOf("#00ACEA"));
                    customCheckBox.getBox().getChildren().add(0, icon);
                    customCheckBox.getLabel().setMaxWidth(200);
                    customCheckBox.getChildren().add(icon2);
                    customCheckBox.getCheckBox().selectedProperty().addListener(e -> {
                        if (customCheckBox.getCheckBox().isSelected()) {
                            questions.add(customCheckBox);
                        } else questions.remove(customCheckBox);
                    });
                    quesFromSubcategories.add(customCheckBox);
                }
            }
        }
    }
}
