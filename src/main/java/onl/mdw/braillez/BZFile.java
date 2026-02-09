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
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * <p>
 * This class handles the file operations for BZStyledText.
 * </p>
 *
 * @author Mike Gray mgray@aph.org
 */
public final class BZFile extends BZBase {
    private @Nullable String fileName;

    /**
     * <p>
     * Creates a new <code>BZFile</code> object.
     * </p>
     *
     * @param bzStyledText the bzStyledText object to operate on (cannot be null)
     */
    public BZFile(@NonNull BZStyledText bzStyledText) {
        super(bzStyledText);
    }

    @Nullable String getFileName() {
        return fileName;
    }

    boolean newFile() {
        if (!closeCurrentDocument()) {
            return false;
        }

        bzStyledText.setText("");
        fileName = null;
        parentShell.setText("BrailleZ");
        return true;
    }

    boolean openFile(@NonNull Path path) {
        Charset charset = path.toString().endsWith("bzy") ? StandardCharsets.UTF_8 : StandardCharsets.US_ASCII;
        try (BufferedReader fileReader = Files.newBufferedReader(path, charset)) {
            if (path.toString().endsWith("bzy")) {
                bzStyledText.readBZY(fileReader);
            } else {
                bzStyledText.readBRF(fileReader);
            }
            parentShell.setText(path.getFileName().toString() + " - BrailleZ");
            this.fileName = path.toString();
            return true;
        } catch (FileNotFoundException exception) {
            logError("Unable to open file", exception);
        } catch (IOException exception) {
            logError("Unable to read file", exception);
        } catch (BZException exception) {
            logError("Unable to read file", fileName + ":  " + exception.getMessage());
        }

        return false;
    }

    boolean openFile() {
        if (!closeCurrentDocument()) {
            return false;
        }

        FileDialog fileDialog = new FileDialog(parentShell, SWT.OPEN);
        fileDialog.setFilterExtensions("*.brf", "*.bzy", "*.brf;*.bzy", "*.*");
        fileDialog.setFilterNames("Braille Ready Format File", "BrailleZephyr File", "Braille Files", "All Files");
        fileDialog.setFilterIndex(2);
        String fileName = fileDialog.open();
        if (fileName == null)
            return false;

        return openFile(Path.of(fileName));
    }

    public boolean closeCurrentDocument() {
        //   check if text has been modified
        if (bzStyledText.getModified()) {
            MessageBox messageBox = new MessageBox(parentShell, SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL);
            messageBox.setMessage("Would you like to save your changes?");
            int result = messageBox.open();
            if (result == SWT.CANCEL) {
                return false;
            } else if (result == SWT.YES) {
                return saveFile();
            }
        }
        return true;
    }

    boolean saveFile() {
        String fileName;

        //   check if file name is set
        if (this.fileName == null) {
            FileDialog fileDialog = new FileDialog(parentShell, SWT.SAVE);
            fileDialog.setFileName(this.fileName);
            fileDialog.setFilterExtensions("*.brf", "*.bzy", "*.brf;*.bzy", "*.*");
            fileDialog.setFilterNames("Braille Ready Format File", "BrailleZephyr File", "Braille Files", "All Files");
            fileDialog.setFilterIndex(2);
            fileName = fileDialog.open();
            if (fileName == null)
                return false;

            File file = new File(fileName);
            if (file.exists()) {
                if (!file.isFile()) {
                    MessageBox messageBox = new MessageBox(parentShell, SWT.ICON_ERROR | SWT.OK);
                    messageBox.setMessage("Invalid file:  " + fileName);
                    messageBox.open();
                    return false;
                }

                MessageBox messageBox = new MessageBox(parentShell, SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL);
                messageBox.setMessage("Would you like to overwrite " + fileName + '?');
                int result = messageBox.open();
                if (result != SWT.YES)
                    return false;
            }
        } else
            fileName = this.fileName;

        Charset charset = fileName.endsWith("bzy") ? StandardCharsets.UTF_8 : StandardCharsets.US_ASCII;
        try(BufferedWriter writer = Files.newBufferedWriter(Path.of(fileName), charset)) {
            if (fileName.endsWith("bzy")) {
                bzStyledText.writeBZY(writer);
            } else {
                bzStyledText.writeBRF(writer);
            }

            parentShell.setText(new File(fileName).getName() + " - BrailleZ");
            this.fileName = fileName;
            return true;
        } catch (FileNotFoundException exception) {
            logError("Unable to open file", exception);
        } catch (IOException exception) {
            logError("Unable to write file", exception);
        }

        return false;
    }

    boolean saveAsFile() {
        //   set fileName to null so saveFile will ask for a new file name
        String fileName = this.fileName;
        this.fileName = null;
        if (saveFile())
            return true;

        //   saveFile didn't save, reset fileName
        this.fileName = fileName;
        return false;
    }
}
