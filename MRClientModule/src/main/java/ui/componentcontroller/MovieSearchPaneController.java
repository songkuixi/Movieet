package ui.componentcontroller;

import component.sequencebutton.SequenceButton;
import component.sequencebutton.SequenceButton.SequenceButtonState;
import component.taglabel.TagLabel;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import ui.viewcontroller.MovieListViewController;
import util.MovieGenre;

import java.beans.EventHandler;

/**
 * Created by Sorumi on 17/3/27.
 */
public class MovieSearchPaneController {


    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private TilePane genreHBox;

    @FXML
    private HBox sortHBox;

    @FXML
    private HBox keywordHBox;

    @FXML
    private Label keywordLabel;

    @FXML
    private Label clearLabel;


    private MovieListViewController movieListViewController;

    private final char clearCode = '\uf00d';

    @FXML
    public void initialize() {
        clearLabel.setText(clearCode + "");
        for (Node node : sortHBox.getChildren()) {
            SequenceButton sequenceButton = (SequenceButton) node;
            sequenceButton.setOnMouseClicked(event -> onClickSequenceButton(sequenceButton));
        }
        onClickSequenceButton((SequenceButton) sortHBox.getChildren().get(0));

        for (MovieGenre genre : MovieGenre.values()) {
            TagLabel tagLabel = new TagLabel();
            tagLabel.setText(genre.getGenreName());
            genreHBox.getChildren().add(tagLabel);
        }

        ((TagLabel) genreHBox.getChildren().get(0)).setActive(true);
    }

    public void setMovieListViewController(MovieListViewController movieListViewController) {
        this.movieListViewController = movieListViewController;
    }

    public void showGenre(boolean show) {
        if (!show) {
            genreHBox.setManaged(false);
            genreHBox.setVisible(false);
            sortHBox.setManaged(false);
            sortHBox.setVisible(false);
            keywordHBox.setManaged(true);
            keywordHBox.setVisible(true);
        } else {
            genreHBox.setManaged(true);
            genreHBox.setVisible(true);
            sortHBox.setManaged(true);
            sortHBox.setVisible(true);
            keywordHBox.setManaged(false);
            keywordHBox.setVisible(false);
        }

    }

    private void onClickSequenceButton(SequenceButton sequenceButton) {
        for (Node node : sortHBox.getChildren()) {
            SequenceButton oldSequenceButton = (SequenceButton) node;
            if (oldSequenceButton == sequenceButton) {
                if (sequenceButton.getActive()) {
                    if (sequenceButton.getState() == SequenceButtonState.Ascending) {
                        sequenceButton.setState(SequenceButtonState.Descending);
                    } else {
                        sequenceButton.setState(SequenceButtonState.Ascending);
                    }
                } else {
                    sequenceButton.setActive(true);
                    sequenceButton.setState(SequenceButtonState.Descending);
                }

            } else {
                oldSequenceButton.setActive(false);
            }

            sequenceButton.setOnMouseClicked(event -> onClickSequenceButton(sequenceButton));
        }
    }

    private void onClickTagLabel(TagLabel tagLabel) {
        TagLabel allTagLabel = (TagLabel) genreHBox.getChildren().get(0);
        if (tagLabel != allTagLabel) {
            tagLabel.setActive(!tagLabel.getActive());
        }
    }

    @FXML
    private void clickSearchButton() {
        String keyword = searchField.getText();
        keywordLabel.setText(keyword);
        movieListViewController.showMovieSearchList(keyword);
    }

    @FXML
    private void clickClearButton() {
        searchField.setText("");
        movieListViewController.showMovieGenreList();
    }
}
