package org.jabref.gui.cleanup;

import java.util.EnumSet;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import org.jabref.gui.util.BaseDialog;
import org.jabref.logic.FilePreferences;
import org.jabref.logic.cleanup.CleanupPreferences;
import org.jabref.logic.cleanup.FieldFormatterCleanups;
import org.jabref.logic.l10n.Localization;
import org.jabref.model.database.BibDatabaseContext;

public class CleanupDialog extends BaseDialog<CleanupPreferences> {
    public CleanupDialog(BibDatabaseContext databaseContext, CleanupPreferences initialPreset, FilePreferences filePreferences) {
        setTitle(Localization.lang("Clean up entries"));
        getDialogPane().setPrefSize(600, 650);
        getDialogPane().getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        System.out.println("running cleanup panel");

        CleanupSingleFieldPanel singleFieldPanel = new CleanupSingleFieldPanel(initialPreset);
        CleanupFileRelatedPanel fileRelatedPanel = new CleanupFileRelatedPanel(databaseContext, initialPreset, filePreferences);
        CleanupMultiFieldPanel multiFieldPanel = new CleanupMultiFieldPanel(initialPreset);

        // placing the content of the presetPanel in a tab pane
        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(
                new Tab("Single field", singleFieldPanel),
                new Tab("File-related", fileRelatedPanel),
                new Tab("Multi-field", multiFieldPanel)
        );
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        getDialogPane().setContent(tabPane);

        setResultConverter(button -> {
            if (button == ButtonType.OK) {
                EnumSet<CleanupPreferences.CleanupStep> allActiveJobs = EnumSet.noneOf(CleanupPreferences.CleanupStep.class);
                allActiveJobs.addAll(fileRelatedPanel.getActiveJobs());
                allActiveJobs.addAll(multiFieldPanel.getActiveJobs());
                allActiveJobs.add(CleanupPreferences.CleanupStep.FIX_FILE_LINKS);

                FieldFormatterCleanups formatterCleanups = singleFieldPanel.getFieldFormatterCleanups();

                return new CleanupPreferences(allActiveJobs, formatterCleanups);
            } else {
                return null;
            }
        });
    }
}
