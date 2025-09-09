package org.jabref.gui.cleanup;

import java.util.EnumSet;

import org.jabref.logic.cleanup.CleanupPreferences;

public interface CleanupPanel {
    CleanupPreferences getCleanupPreferences();
    EnumSet<CleanupPreferences.CleanupStep> getTabSteps();
}
