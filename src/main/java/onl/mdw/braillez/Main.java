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
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.jspecify.annotations.NonNull;

import java.nio.file.Path;

/**
 * <p>
 * Contains the main method.
 * </p>
 *
 * @author Mike Gray mgray@aph.org
 */
public final class Main {
    private final @NonNull Shell shell;
    private final @NonNull BZFile bzFile;
    private final @NonNull BZSettings bzSettings;

    public static void main(String... args) {
        new Main(args);
    }

    public Main(String... args) {
        //   must be before display is created (on Macs at least)
        Display.setAppName("BrailleZ");

        Display display = Display.getDefault();

        //   needed to catch Quit (Command-Q) on Macs
        display.addListener(SWT.Close, event -> event.doit = checkClosing());

        shell = new Shell(display);
        shell.setLayout(new FillLayout());
        shell.setText("BrailleZ");
        shell.addListener(SWT.Close, e -> e.doit = checkClosing());

        final BZStyledText bzStyledText = new BZStyledText(shell);
        bzFile = new BZFile(bzStyledText);
        bzSettings = new BZSettings(bzStyledText);
        new BZMenu(bzStyledText, bzFile, bzSettings);

        //   assume any argument is a file to open
        if (args.length > 0) {
            bzFile.openFile(Path.of(args[0]).normalize());
        }

        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }

        display.dispose();
    }

    private boolean checkClosing() {
        //   check if text has been modified
        boolean doit = bzFile.closeCurrentDocument();

        //   write settings file
        if (doit) {
            if (!bzSettings.writeSettings()) {
                MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL);
                messageBox.setMessage("Would you like to exit anyway?");
                doit = messageBox.open() == SWT.YES;
            }
        }

        return doit;
    }

}
