/* Copyright (C) 2025-2026 Michael Whapples.
 * Copyright (C) 2015 American Printing House for the Blind Inc.
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package onl.mdw.braillez;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.jspecify.annotations.NonNull;

/**
 * <p>
 * This class is the base class for the other BZ* classes.  It contains the
 * common logging methods used by the other classes.
 * </p>
 *
 * @author Mike Gray mgray@aph.org
 */
public class BZBase {
    protected final @NonNull BZStyledText bzStyledText;
    protected final @NonNull Shell parentShell;

    public BZBase(@NonNull BZStyledText bzStyledText) {
        this.bzStyledText = bzStyledText;
        parentShell = bzStyledText.getParentShell();
    }

    protected void logError(String message, String info, boolean showMessage) {
        String string;

        if (info == null)
            string = "ERROR:  " + message;
        else
            string = "ERROR:  " + message + ":  " + info;
        System.err.println(string);
        System.err.flush();
        bzStyledText.getLogWriter().println(string);
        bzStyledText.getLogWriter().flush();

        if (showMessage) {
            if (info == null)
                string = "ERROR:  " + message;
            else
                string = "ERROR:  " + message + ":\n" + info;
            MessageBox messageBox = new MessageBox(parentShell, SWT.ICON_ERROR | SWT.OK);
            messageBox.setMessage(string);
            messageBox.open();
        }
    }

    protected void logError(String message, Exception exception, boolean showMessage) {
        logError(message, exception.getMessage(), showMessage);
    }

    protected void logError(String message, Exception exception) {
        logError(message, exception, true);
    }

    protected void logError(String message, String info) {
        logError(message, info, true);
    }

    protected void logError(String message, boolean showMessage) {
        logError(message, (String) null, showMessage);
    }

    protected void logError(String message) {
        logError(message, true);
    }

    protected void logMessage(String string) {
        System.out.println(string);
        bzStyledText.getLogWriter().println(string);
        bzStyledText.getLogWriter().flush();
    }
}
