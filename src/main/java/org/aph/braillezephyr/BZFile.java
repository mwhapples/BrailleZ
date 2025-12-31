/* Copyright (C) 2015 American Printing House for the Blind Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.aph.braillezephyr;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.jspecify.annotations.Nullable;

import java.io.*;
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
    public BZFile(BZStyledText bzStyledText) {
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
        parentShell.setText("BrailleZephyr");
        return true;
    }

    boolean openFile(Path path) {
        try (BufferedReader fileReader = Files.newBufferedReader(path)) {
            if (path.toString().endsWith("bzy")) {
                bzStyledText.readBZY(fileReader);
            } else {
                bzStyledText.readBRF(fileReader);
            }
            parentShell.setText(path.getFileName().toString() + " - BrailleZephyr");
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

        try {
            OutputStreamWriter writer;

            if (fileName.endsWith("brf")) {
                writer = new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.US_ASCII);
                bzStyledText.writeBRF(writer);
            } else if (fileName.endsWith("bzy")) {
                writer = new OutputStreamWriter(new FileOutputStream(fileName));
                bzStyledText.writeBZY(writer);
            } else {
                writer = new OutputStreamWriter(new FileOutputStream(fileName));
                bzStyledText.writeBRF(writer);
            }

            writer.close();
            parentShell.setText(new File(fileName).getName() + " - BrailleZephyr");
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
