package com.redcanari.ui;

import com.redcanari.util.HttpUtils;
import com.sun.webkit.dom.HTMLImageElementImpl;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.shape.Circle;
import javafx.scene.web.WebEngine;
import javafx.util.Callback;
import netscape.javascript.JSObject;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;
import org.controlsfx.control.cell.ImageGridCell;


/**
 * Created by ndouba on 15-01-02.
 */
public class ImagesTab extends Tab {

    private final GridView<Image> gridView = new GridView<>();
    private final WebEngine webEngine;
    private static final String DOCUMENT_GET_ELEMENTS_BY_TAG_NAME_IMG = "document.getElementsByTagName('img');";
    private static final ObservableList<Image> dataSource = FXCollections.observableArrayList();

    public ImagesTab(WebEngine webEngine) {
        setText("Images");
        this.webEngine = webEngine;
        gridView.setCellFactory(param -> new ImageGridCell(true));
        gridView.setItems(dataSource);
        webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) {
                if (newValue == Worker.State.SCHEDULED) {
                    gridView.setShape(new Circle(50));
                    dataSource.clear();
                } else if (newValue == Worker.State.SUCCEEDED) {
                    getImages();
                }
            }
        });
        setContent(gridView);
    }


    public void getImages() {
        JSObject result = (JSObject) webEngine.executeScript(DOCUMENT_GET_ELEMENTS_BY_TAG_NAME_IMG);
        int length = (int) result.getMember("length");

        for (int i = 0; i < length; i++) {
            Object o = result.getSlot(i);
            if (o instanceof HTMLImageElementImpl) {
                String src = ((HTMLImageElementImpl) o).getSrc();
                if (HttpUtils.isHttpURL(src)) {
                    dataSource.add(new Image(src.trim()));
                }
            }
        }
    }


}
