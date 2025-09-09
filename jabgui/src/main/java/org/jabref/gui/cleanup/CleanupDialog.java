package org.jabref.gui.cleanup;

import java.util.EnumSet;

import javafx.fxml.FXML;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import org.jabref.gui.util.BaseDialog;
import org.jabref.logic.FilePreferences;
import org.jabref.logic.cleanup.CleanupPreferences;
import org.jabref.logic.cleanup.FieldFormatterCleanups;
import org.jabref.logic.l10n.Localization;
import org.jabref.model.database.BibDatabaseContext;

import com.airhacks.afterburner.views.ViewLoader;

public class CleanupDialog extends BaseDialog<CleanupPreferences> {

    @FXML private TabPane tabPane;

    public CleanupDialog(BibDatabaseContext databaseContext, CleanupPreferences initialPreset, FilePreferences filePreferences) {
        setTitle(Localization.lang("Clean up entries"));

        // Load FXML
        ViewLoader.view(this)
                  .load()
                  .setAsDialogPane(this);

        CleanupSingleFieldPanel singleFieldPanel = new CleanupSingleFieldPanel(initialPreset);
        CleanupFileRelatedPanel fileRelatedPanel = new CleanupFileRelatedPanel(databaseContext, initialPreset, filePreferences);
        CleanupMultiFieldPanel multiFieldPanel = new CleanupMultiFieldPanel(initialPreset);

        tabPane.getTabs().addAll(
                new Tab(Localization.lang("Single field"), singleFieldPanel),
                new Tab(Localization.lang("File-related"), fileRelatedPanel),
                new Tab(Localization.lang("Multi-field"), multiFieldPanel)
        );

        setResultConverter(button -> {
            if (button.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                EnumSet<CleanupPreferences.CleanupStep> allActiveJobs = EnumSet.noneOf(CleanupPreferences.CleanupStep.class);

                allActiveJobs.addAll(fileRelatedPanel.getActiveJobs());
                allActiveJobs.addAll(multiFieldPanel.getActiveJobs());

                // Always include this step, as file links need to be fixed in every cleanup
                allActiveJobs.add(CleanupPreferences.CleanupStep.FIX_FILE_LINKS);

                FieldFormatterCleanups formatterCleanups = singleFieldPanel.getFieldFormatterCleanups();

                return new CleanupPreferences(allActiveJobs, formatterCleanups);
            } else {
                return null;
            }
        });
    }
}
