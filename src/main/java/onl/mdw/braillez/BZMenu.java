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
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.jspecify.annotations.Nullable;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * <p>
 * This class creates and handles the menu for BZStyledText.
 * </p>
 *
 * @author Mike Gray mgray@aph.org
 */
public final class BZMenu extends BZBase {
    private final BZSettings bzSettings;

    /**
     * <p>
     * Creates a new <code>BZMenu</code> object.
     * </p>
     *
     * <p>
     * If <code>bzSettings</code> is null, then there will be no Open Recent
     * menu item.
     * </p>
     *
     * @param bzStyledText the bzStyledText object to operate on (cannot be null)
     * @param bzFile       the bzFile object for file operations (cannot be null)
     * @param bzSettings   the bzSettings object for recent files.
     */
    public BZMenu(BZStyledText bzStyledText, BZFile bzFile, BZSettings bzSettings) {
        super(bzStyledText);

        this.bzSettings = bzSettings;

        Menu menuBar = new Menu(parentShell, SWT.BAR);
        parentShell.setMenuBar(menuBar);

        Menu menu;
        MenuItem item;

        String mod1KeyName = "Ctrl+";
        String mod2KeyName = "Shift+";
        if (System.getProperty("os.name").toLowerCase().startsWith("mac")) {
            mod1KeyName = "⌘";
            mod2KeyName = "⇧";
        }

        //   file menu
        menu = new Menu(menuBar);
        item = new MenuItem(menuBar, SWT.CASCADE);
        item.setText("&File");
        item.setMenu(menu);

        addMenuItemTo(menu, "&New", e -> bzFile.newFile());
        addMenuItemTo(menu, "&Open\t" + mod1KeyName + "O", SWT.MOD1 | 'o', e -> {
            if (bzFile.openFile()) {
                addRecentFile(bzFile.getFileName());
            }
        });

        if (bzSettings != null) {
            Menu recentFilesMenu = createRecentFilesMenu(bzFile, bzSettings, menu);
            addSubMenuItemTo(menu, "Open Recent", recentFilesMenu);
        }

        addMenuItemTo(menu, "&Save\t" + mod1KeyName + "S", SWT.MOD1 | 's', e -> bzFile.saveFile());
        addMenuItemTo(menu, "Save As\t" + mod2KeyName + mod1KeyName + "O", SWT.MOD1 | SWT.MOD2 | 's', e -> {
            if (bzFile.saveAsFile()) {
                addRecentFile(bzFile.getFileName());
            }
        });
        addMenuItemTo(menu, "Quit\t" + mod1KeyName + "Q", SWT.MOD1 | 'q', e -> parentShell.close());
        new MenuItem(menu, SWT.SEPARATOR);
        addMenuItemTo(menu, "Load Line Margin Bell", e -> {
            FileDialog fileDialog = new FileDialog(parentShell, SWT.OPEN);
            fileDialog.setFileName(bzStyledText.getLineMarginFileName());
            String fileName = fileDialog.open();
            if (fileName == null)
                return;
            try {
                bzStyledText.loadLineMarginFileName(fileName);
            } catch (FileNotFoundException exception) {
                logError("Unable to open file", exception);
            } catch (IOException exception) {
                logError("Unable to read file", exception);
            } catch (UnsupportedAudioFileException ignore) {
                logError("Sound file unsupported for line margin bell", fileName);
            } catch (LineUnavailableException ignore) {
                logError("Line unavailable for line margin bell", fileName);
            }
        });
        addMenuItemTo(menu, "Load Page Margin Bell", e -> {
            FileDialog fileDialog = new FileDialog(parentShell, SWT.OPEN);
            fileDialog.setFileName(bzStyledText.getPageMarginFileName());
            String fileName = fileDialog.open();
            if (fileName == null)
                return;
            try {
                bzStyledText.loadPageMarginFileName(fileName);
            } catch (FileNotFoundException exception) {
                logError("Unable to open file", exception);
            } catch (IOException exception) {
                logError("Unable to read file", exception);
            } catch (UnsupportedAudioFileException ignore) {
                logError("Sound file unsupported for page margin bell", fileName);
            } catch (LineUnavailableException ignore) {
                logError("Line unavailable for page margin bell", fileName);
            }
        });
        addMenuItemTo(menu, "Load Line End Bell", e -> {
            FileDialog fileDialog = new FileDialog(parentShell, SWT.OPEN);
            fileDialog.setFileName(bzStyledText.getLineEndFileName());
            String fileName = fileDialog.open();
            if (fileName == null)
                return;
            try {
                bzStyledText.loadLineEndFileName(fileName);
            } catch (FileNotFoundException exception) {
                logError("Unable to open file", exception);
            } catch (IOException exception) {
                logError("Unable to read file", exception);
            } catch (UnsupportedAudioFileException ignore) {
                logError("Sound file unsupported for page margin bell", fileName);
            } catch (LineUnavailableException ignore) {
                logError("Line unavailable for line end bell", fileName);
            }
        });

        //   edit menu
        menu = new Menu(menuBar);
        item = new MenuItem(menuBar, SWT.CASCADE);
        item.setText("&Edit");
        item.setMenu(menu);

        //   cut, copy, and paste accelerators are handled by StyledText.
        addMenuItemTo(menu, "Cut\t" + mod1KeyName + "X", e -> bzStyledText.cut());
        addMenuItemTo(menu, "Copy\t" + mod1KeyName + "C", e -> bzStyledText.copy());
        addMenuItemTo(menu, "Paste\t" + mod1KeyName + "V", e -> bzStyledText.paste());
        new MenuItem(menu, SWT.SEPARATOR);
        addMenuItemTo(menu, "Undo\t" + mod1KeyName + "Z", SWT.MOD1 | 'z', e -> bzStyledText.undo());
        addMenuItemTo(menu, "Redo\t" + mod2KeyName + mod1KeyName + "Z", SWT.MOD1 | SWT.MOD2 | 'z', e -> bzStyledText.redo());

        //   view menu
        menu = new Menu(menuBar);
        item = new MenuItem(menuBar, SWT.CASCADE);
        item.setText("&View");
        item.setMenu(menu);

        new VisibleHandler(menu);
        addMenuItemTo(menu, "Braille Font", e -> {
            FontDialog fontDialog = new FontDialog(parentShell, SWT.OPEN);
            fontDialog.setFontList(bzStyledText.getBrailleFont().getFontData());
            FontData fontData = fontDialog.open();
            if (fontData == null)
                return;
            bzStyledText.setBrailleFont(new Font(parentShell.getDisplay(), fontData));
        });
        addMenuItemTo(menu, "ASCII Font", e -> {
            FontDialog fontDialog = new FontDialog(parentShell, SWT.OPEN);
            fontDialog.setFontList(bzStyledText.getAsciiFont().getFontData());
            FontData fontData = fontDialog.open();
            if (fontData == null)
                return;
            bzStyledText.setAsciiFont(new Font(parentShell.getDisplay(), fontData));
        });

        //   format menu
        menu = new Menu(menuBar);
        item = new MenuItem(menuBar, SWT.CASCADE);
        item.setText("F&ormat");
        item.setMenu(menu);

        addMenuItemTo(menu, "Lines Per Page", e -> new SpinnerDialog(parentShell, "Lines per Page", bzStyledText.getLinesPerPage(), 1, 255, i -> {
            bzStyledText.setLinesPerPage(i);
            bzStyledText.redraw();
        }));
        addMenuItemTo(menu, "Chars Per Line", e -> new SpinnerDialog(parentShell, "Characters Per Line", bzStyledText.getCharsPerLine(), 1, 27720, i -> {
            bzStyledText.setCharsPerLine(i);
            bzStyledText.redraw();
        }));
        addMenuItemTo(menu, "Line Margin Bell", bzStyledText.getLineMarginBell() != -1, e -> new SpinnerDialog(parentShell, "Bell Margin", bzStyledText.getLineMarginBell(), 1, 27720, bzStyledText::setLineMarginBell));
        addMenuItemTo(menu, "Page Margin Bell", bzStyledText.getPageMarginBell() != -1, e -> new SpinnerDialog(parentShell, "Bell Page", bzStyledText.getPageMarginBell(), 1, 27720, bzStyledText::setPageMarginBell));
        addMenuItemTo(menu, "Rewrap From Cursor\t" + mod1KeyName + "F", SWT.MOD1 | 'F', e -> bzStyledText.rewrapFromCaret());

        //   help menu
        menu = new Menu(menuBar);
        item = new MenuItem(menuBar, SWT.CASCADE);
        item.setText("&Help");
        item.setMenu(menu);

        addMenuItemTo(menu, "About", e -> showAboutDialog(parentShell));
        //TODO:  hide on non-development version
        addMenuItemTo(menu, "View Log", e -> showLogViewerDialog(parentShell));
    }

    private static Menu createRecentFilesMenu(BZFile bzFile, BZSettings bzSettings, Menu menu) {
        Menu recentFilesMenu = new Menu(menu);
        recentFilesMenu.addListener(SWT.Show, event -> {
            Menu m = (Menu)event.widget;
            MenuItem[] items = m.getItems();
            for (MenuItem i : items) {
                i.dispose();
            }
            List<String> recentFiles = bzSettings.getRecentFiles();
            for (String fileName : recentFiles) {
                addMenuItemTo(m, fileName, e -> {
                    if (bzFile.openFile(Path.of(fileName))) {
                        bzSettings.addRecentFile(fileName);
                    } else {
                        bzSettings.removeRecentFile(fileName);
                    }
                });
            }
        });
        return recentFilesMenu;
    }

    private void addRecentFile(@Nullable String fileName) {
        if (bzSettings != null && fileName != null) {
            bzSettings.addRecentFile(fileName);
        }
    }

    private final class VisibleHandler extends SelectionAdapter {
        private final MenuItem brailleItem;
        private final MenuItem asciiItem;

        private VisibleHandler(Menu menu) {
            brailleItem = new MenuItem(menu, SWT.PUSH);
            if (bzStyledText.getBrailleVisible())
                brailleItem.setText("Hide Braille");
            else
                brailleItem.setText("Show Braille");
            brailleItem.addSelectionListener(this);

            asciiItem = new MenuItem(menu, SWT.PUSH);
            if (bzStyledText.getAsciiVisible())
                asciiItem.setText("Hide ASCII");
            else
                asciiItem.setText("Show ASCII");
            asciiItem.addSelectionListener(this);
        }

        @Override
        public void widgetSelected(SelectionEvent event) {
            if (event.widget == brailleItem) {
                if (bzStyledText.getBrailleVisible()) {
                    bzStyledText.setBrailleVisible(false);
                    brailleItem.setText("Show Braille");
                } else {
                    bzStyledText.setBrailleVisible(true);
                    brailleItem.setText("Hide Braille");
                }
            } else {
                if (bzStyledText.getAsciiVisible()) {
                    bzStyledText.setAsciiVisible(false);
                    asciiItem.setText("Show ASCII");
                } else {
                    bzStyledText.setAsciiVisible(true);
                    asciiItem.setText("Hide ASCII");
                }
            }
        }
    }

    private void showAboutDialog(Shell parentShell) {
        Shell dialog = new Shell(parentShell, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
        dialog.setLayout(new GridLayout(1, true));
        dialog.setText("About BrailleZ");

        String versionString = bzStyledText.getVersionString();
        if (versionString == null) {
            versionString = "dev";
        }

        Label label;

        label = new Label(dialog, SWT.CENTER);
        label.setLayoutData(new GridData(GridData.FILL_BOTH));
        Image image = Icons.loadIcon(parentShell.getDisplay(), this::logError);
        label.setImage(image);
        new Label(dialog, SWT.SEPARATOR | SWT.HORIZONTAL).setLayoutData(new GridData(GridData.FILL_BOTH));

        label = new Label(dialog, SWT.CENTER);
        label.setLayoutData(new GridData(GridData.FILL_BOTH));
        label.setFont(new Font(parentShell.getDisplay(), "Sans", 14, SWT.BOLD));
        label.setText("BrailleZ " + versionString);

        label = new Label(dialog, SWT.CENTER);
        label.setLayoutData(new GridData(GridData.FILL_BOTH));
        label.setFont(new Font(parentShell.getDisplay(), "Sans", 10, SWT.NORMAL));
        label.setText("Editor for BRF documents");

        label = new Label(dialog, SWT.CENTER);
        label.setLayoutData(new GridData(GridData.FILL_BOTH));
        label.setFont(new Font(parentShell.getDisplay(), "Sans", 10, SWT.NORMAL));
        label.setText("Copyright © 2025-2026 Michael Whapples.\nCopyright © 2015 American Printing House for the Blind Inc.");

        label = new Label(dialog, SWT.CENTER);
        label.setLayoutData(new GridData(GridData.FILL_BOTH));
        label.setFont(new Font(parentShell.getDisplay(), "Sans", 10, SWT.NORMAL));
        label.setText("""
                This program is free software: you can redistribute it and/or modify
                it under the terms of the GNU General Public License as published by
                the Free Software Foundation, version 3 of the License.
                
                This program is distributed in the hope that it will be useful,
                but WITHOUT ANY WARRANTY; without even the implied warranty of
                MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
                GNU General Public License for more details.
                
                You should have received a copy of the GNU General Public License
                along with this program.  If not, see <https://www.gnu.org/licenses/>.""");

        label = new Label(dialog, SWT.CENTER);
        label.setLayoutData(new GridData(GridData.FILL_BOTH));
        label.setFont(new Font(parentShell.getDisplay(), "Sans", 10, SWT.NORMAL));
        label.setText("BrailleZ is based on BrailleZephyr from American Printing House for the Blind which was published under the Apache 2.0 license.");

        dialog.pack();
        dialog.open();
    }

    private void showLogViewerDialog(Shell parentShell) {
        Shell dialog = new Shell(parentShell, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
        dialog.setLayout(new FillLayout());
        dialog.setText("Log Messages");

        Text text = new Text(dialog, SWT.READ_ONLY | SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        text.setText(bzStyledText.getLogString());

        dialog.open();
    }

    private static MenuItem addMenuItemTo(
            Menu menu,
            String tag,
            int accelerator,
            boolean enabled,
            Listener onSelection
    ) {
        MenuItem item = new MenuItem(menu, SWT.PUSH);
        item.setText(tag);
        if (accelerator != 0)
            item.setAccelerator(accelerator);
        item.addListener(SWT.Selection, onSelection);
        item.setEnabled(enabled);
        return item;
    }

    private static MenuItem addMenuItemTo(Menu menu, String tag, int accelerator, Listener onSelection) {
        return addMenuItemTo(menu, tag, accelerator, true, onSelection);
    }

    private static MenuItem addMenuItemTo(Menu menu, String tag, boolean enabled, Listener onSelection) {
        return addMenuItemTo(menu, tag, 0, enabled, onSelection);
    }

    private static MenuItem addMenuItemTo(Menu menu, String tag, Listener onSelection) {
        return addMenuItemTo(menu, tag, 0, true, onSelection);
    }

    private static MenuItem addSubMenuItemTo(
            Menu menu,
            String tag,
            boolean enabled,
            Menu subMenu
    ) {
        MenuItem item = new MenuItem(menu, SWT.CASCADE);
        item.setText(tag);
        item.setMenu(subMenu);
        item.setEnabled(enabled);
        return item;
    }

    private static MenuItem addSubMenuItemTo(Menu menu, String tag, Menu subMenu) {
        return addSubMenuItemTo(menu, tag, true, subMenu);
    }
}
