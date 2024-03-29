package com.example.demo;


import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class ThiCuoiKyController {
    @FXML
    private VBox contentArea;
    DataModel dataModel;
    private BreadCrumbBarModel breadCrumbBarModel;


    public void initModel(BreadCrumbBarModel breadCrumbBarModel) {
        if (this.breadCrumbBarModel != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.breadCrumbBarModel = breadCrumbBarModel;
    }

    private void initDataModel(DataModel dataModel) {
        if (this.dataModel != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.dataModel = dataModel;
    }

    @FXML
    private void initialize() {
        initModel(BreadCrumbBarModel.getInstance());
        initDataModel(DataModel.getInstance());
        for (Quiz quiz : dataModel.getQuiz()) {
            QuizCheckBox quizCheckBox = new QuizCheckBox(quiz.getName());
            quizCheckBox.getButton().setOnAction(e -> {
                breadCrumbBarModel.removeQuizView();
                dataModel.setCurrentQuiz(quiz);
                breadCrumbBarModel.insertQuizView(quiz.getName());
                breadCrumbBarModel.getBreadCrumbBar().setSelectedCrumb(breadCrumbBarModel.getBreadConnection().get("quiz.fxml"));
            });
            contentArea.getChildren().add(quizCheckBox);
        }
    }
}
