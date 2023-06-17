package com.example.demo;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class QuestionBankController {
    private BreadCrumbBarModel breadCrumbBarModel;
    @FXML
    private TabPane tabPane;
    @FXML
    private TreeView<String> root;
    @FXML
    private Label label;
    @FXML
    private TreeView<String> root1;
    @FXML
    private Label label1;
    @FXML
    private VBox dropZone;
    @FXML
    private TextField catogeryName;
    @FXML
    private CheckBox showQuesFromCate;
    @FXML
    private VBox showInfor;
    @FXML
    private VBox showQuestion;
    private DataModel dataModel;
    private List<File> files;
    private boolean isAllTextFile = false;

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
        showQuesFromCate.selectedProperty().addListener(e -> {
            if (showQuesFromCate.isSelected()) {
                showInfor.setVisible(true);
                showQuestion();
            } else {
                showQuestion.getChildren().clear();
                showInfor.setVisible(false);

            }
        });
        root.setRoot(dataModel.getRoot());
        root.getSelectionModel().selectedItemProperty().addListener(e -> {
            if (showQuesFromCate.isSelected()) {
                showQuestion.getChildren().clear();
                showQuestion();
            }
            label.setText(root.getSelectionModel().getSelectedItem().getValue());
        });
        root1.setRoot(dataModel.getRoot());
        root1.getSelectionModel().selectedItemProperty().addListener(e -> {
            label1.setText(root1.getSelectionModel().getSelectedItem().getValue());
        });

        if (Objects.equals(breadCrumbBarModel.getCurrentView(), "questionbank.fxml")) {
            tabPane.getSelectionModel().select(0);
        } else if (!Objects.equals(breadCrumbBarModel.getCurrentView(), "questionbank.fxml"))
            tabPane.getSelectionModel().select(Integer.parseInt(breadCrumbBarModel.getCurrentView()));
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, b, a) -> {
            if (a != b) {
                switch (tabPane.getSelectionModel().getSelectedIndex()) {
                    case 1 -> {
                        breadCrumbBarModel.setTabCheck(true);
                        breadCrumbBarModel.setCurrentView("1");
                    }
                    case 2 -> {
                        breadCrumbBarModel.setTabCheck(true);
                        breadCrumbBarModel.setCurrentView("2");

                    }
                    case 3 -> {
                        breadCrumbBarModel.setTabCheck(true);
                        breadCrumbBarModel.setCurrentView("3");

                    }
                    default -> {
                        breadCrumbBarModel.setTabCheck(true);
                        breadCrumbBarModel.setCurrentView("questionbank.fxml");

                    }
                }

            }
        });
        dropZone.setOnDragOver(event -> {
            if (event.getGestureSource() != dropZone && event.getDragboard().hasFiles()) {
                // Kiểm tra tất cả các file được kéo qua

                event.acceptTransferModes(TransferMode.COPY);
                event.consume();
            }

        });
// Sự kiện thả vào VBox
        dropZone.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            boolean success = false;
            if (dragboard.hasFiles()) {
                files = dragboard.getFiles();

                for (File file : files) {
                    if (!isTextFile(file)) {
                        break;
                    }
                    isAllTextFile = true;
                }
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    private boolean isTextFile(File file) {
        String fileName = file.getName();
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
        return fileExtension.equalsIgnoreCase("txt") || fileExtension.equalsIgnoreCase("doc") || fileExtension.equalsIgnoreCase("docx");
    }

    @FXML
    private void btnAddQuestion() {
        breadCrumbBarModel.setCurrentView("add-MTPCQ.fxml");
    }

    @FXML
    private void textAction() {
        dataModel.insertCategory(root1.getSelectionModel().getSelectedItem(), catogeryName.getText());
    }

    public List<AikenQuestion> readAikenQuestions(File file) {
        List<AikenQuestion> questions = new ArrayList<>();
        boolean start = false;
        boolean hasContent = false;
        boolean hasAnswer = false;
        int countLine = 0;
        int contentLine = 0;
        int countOption = 0;
        AikenQuestion question = new AikenQuestion();
        if (file.getName().substring(file.getName().lastIndexOf(".") + 1).equalsIgnoreCase("docx")) {
            try (FileInputStream fis = new FileInputStream(file);
                 XWPFDocument document = new XWPFDocument(fis)) {

                List<XWPFParagraph> paragraphs = document.getParagraphs();
                /*for (XWPFPictureData pictureData : document.getAllPictures()) {
                    // Lấy dữ liệu hình ảnh dạng byte[]
                    byte[] imageData = pictureData.getData();

                    // Lưu hình ảnh thành file (tuỳ ý)
                    String imageFileName = pictureData.getFileName();
                    String imageFileExtension = pictureData.suggestFileExtension();
                    String imageFilePath = "path/to/save/image/" + imageFileName + "." + imageFileExtension;
                    IOUtils.write(imageData, new FileOutputStream(imageFilePath));
                }*/

                for (XWPFParagraph paragraph : paragraphs) {
                    String text = paragraph.getText().trim();
                    countLine++;
                    if (hasContent && text.isEmpty()) {
                        System.out.println("Error 1 at line: " + countLine);
                        return null;
                    }
                    if (hasContent && text.matches("[A-Z]\\.\\s.*[^.]$")) {
                        if (!text.startsWith(String.valueOf((char) (countOption + 65)))) {
                            System.out.println("Error 2 at line: " + countLine);
                            return null;
                        }
                        question.addOption(text);
                        countOption++;
                    } else if (hasContent && countOption >= 2 && text.matches("ANSWER:\\s[A-Z]")) {
                        question.setAnswer(text.substring("ANSWER:".length()).trim());
                        hasAnswer = true;
                    } else if (hasContent && countOption < 2 && text.matches("ANSWER:\\s[A-Z]")) {
                        System.out.println("Error 3 at line: " + countLine);
                        return null;
                    } else if (hasContent && countOption > 0) {
                        System.out.println("Error 4 at line: " + countOption + " " + countLine);
                        return null;
                    }
                    if (hasAnswer) {
                        questions.add(question);
                        question = new AikenQuestion();
                        countOption = 0;
                        start = false;
                        hasContent = false;
                        hasAnswer = false;
                    } else if (countOption == 0 && !text.isEmpty()) {
                        if (!start) {
                            contentLine = countLine;
                            start = true;
                        }
                        question.addTitle(text);
                        hasContent = true;
                    }
                }
                if (hasContent) {
                    System.out.println("Error 5 at line: " + contentLine);
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String text = line.trim();
                    countLine++;
                    if (hasContent && text.isEmpty()) {
                        System.out.println("Error 1 at line: " + countLine);
                        return null;
                    }
                    if (hasContent && text.matches("[A-Z]\\.\\s.*[^.]$")) {
                        if (!text.startsWith(String.valueOf((char) (countOption + 65)))) {
                            System.out.println("Error 2 at line: " + countLine);
                            return null;
                        }
                        question.addOption(text);
                        countOption++;
                    } else if (hasContent && countOption >= 2 && text.matches("ANSWER:\\s[A-Z]")) {
                        question.setAnswer(text.substring("ANSWER:".length()).trim());
                        hasAnswer = true;
                    } else if (hasContent && countOption < 2 && text.matches("ANSWER:\\s[A-Z]")) {
                        System.out.println("Error 3 at line: " + countLine);
                        return null;
                    } else if (hasContent && countOption > 0) {
                        System.out.println("Error 4 at line: " + countLine);
                        return null;
                    }
                    if (hasAnswer) {
                        questions.add(question);
                        question = new AikenQuestion();
                        countOption = 0;
                        start = false;
                        hasContent = false;
                        hasAnswer = false;
                    } else if (countOption == 0 && !text.isEmpty()) {
                        if (!start) {
                            contentLine = countLine;
                            start = true;
                        }
                        question.addTitle(text);
                        hasContent = true;
                    }
                }
                if (hasContent) {
                    System.out.println("Error 5 at line: " + contentLine);
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return questions;
    }

    public void btnImport() {
        try {
            if (isAllTextFile) {
                int i = 0;
                int count = 0;
                if (dataModel.getLastNode_id() != 0) {
                    Random random = new Random();
                    i = random.nextInt(dataModel.getLastNode_id()) + 1;
                } else {
                    LocalDate currentDate = LocalDate.now();

                    // Định dạng ngày tháng năm
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                    // Chuyển đổi thành chuỗi và in ra
                    String formattedDate = currentDate.format(formatter);
                    dataModel.insertCategory(dataModel.getCategoryMap().inverse().get(i), formattedDate);
                    i = 1;

                }
                for (File file : files) {
                    for (AikenQuestion question : readAikenQuestions(file)) {
                        dataModel.insertQuestion(dataModel.getCategoryMap().inverse().get(i), question);
                        count++;
                    }
                }
                dataModel.setCount(count);
                dataModel.updateCategory(dataModel.getCategoryMap().inverse().get(i));
                label1.setText(dataModel.getCategoryMap().inverse().get(i).getValue());
                dataModel.setCount(0);
                isAllTextFile = false;
                files = null;
            } else {
                System.out.println("Do some thing");
            }
        } catch (NullPointerException e) {
            System.out.println("No file selected");
        }
    }

    public void chooseFile() {
        // Tạo một đối tượng FileChooser
        FileChooser fileChooser = new FileChooser();

        // Đặt tiêu đề của hộp thoại chọn tệp
        fileChooser.setTitle("Chọn tệp");

        // Thêm bộ lọc tệp (tùy chọn)
        FileChooser.ExtensionFilter txtFilter = new FileChooser.ExtensionFilter("Tệp văn bản (.txt)", "*.txt");
        FileChooser.ExtensionFilter docFilter = new FileChooser.ExtensionFilter("Tệp Word (.doc)", "*.doc");
        FileChooser.ExtensionFilter docxFilter = new FileChooser.ExtensionFilter("Tệp Word (.docx)", "*.docx");
        fileChooser.getExtensionFilters().addAll(txtFilter, docFilter, docxFilter);

        // Lấy stage của scene hiện tại từ nút
        Stage stage = new Stage();

        // Hiển thị hộp thoại chọn tệp và lấy tệp được chọn
        File selectedFile = fileChooser.showOpenDialog(stage);


        if (selectedFile != null) {
            if (files == null) {
                files = new ArrayList<>();
            }
            files.add(selectedFile);
            isAllTextFile = true;
        }
    }

    private void showQuestion() {

        int i = 0;
        for (String title : dataModel.getQuestionTitle(dataModel.getCategoryMap().get(root.getSelectionModel().getSelectedItem()))) {

            CustomCheckBox customCheckBox = new CustomCheckBox(title);
            if (i % 2 == 0) {
                customCheckBox.setStyle("-fx-background-color: white");
            } else customCheckBox.setStyle("-fx-background-color: lightgray");
            showQuestion.getChildren().add(customCheckBox);
            i++;

        }
    }
}
