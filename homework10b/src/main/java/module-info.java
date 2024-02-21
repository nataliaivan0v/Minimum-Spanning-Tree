module fundies2.mst {
    requires javafx.controls;
    requires javafx.fxml;


    opens fundies2.mst to javafx.fxml;
    exports fundies2.mst;
}
