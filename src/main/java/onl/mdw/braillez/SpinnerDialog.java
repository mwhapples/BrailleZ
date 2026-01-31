/* Copyright (C) 2025-2026 Michael Whapples.
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
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.jspecify.annotations.NonNull;

import java.util.function.IntConsumer;

final class SpinnerDialog implements SelectionListener, KeyListener {
    private final Shell shell;
    private final @NonNull Button okButton;
    private final @NonNull Spinner spinner;
    private final @NonNull IntConsumer onChange;

    public SpinnerDialog(Shell parentShell, String title, int value, int minimum, int maximum, IntConsumer onChange) {
        this(parentShell, title, value, minimum, maximum, 0, 1, 10, onChange);
    }

    public SpinnerDialog(Shell parentShell, String title, int value, int minimum, int maximum, int digits, int increment, int pageIncrement, @NonNull IntConsumer onChange) {
        this.onChange = onChange;
        shell = new Shell(parentShell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        shell.setText(title);
        shell.setLayout(new GridLayout(3, true));

        spinner = new Spinner(shell, 0);
        spinner.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
        spinner.setValues(value, minimum, maximum, digits, increment, pageIncrement);
        spinner.addKeyListener(this);

        okButton = new Button(shell, SWT.PUSH);
        okButton.setText("OK");
        okButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
        okButton.addSelectionListener(this);

        Button cancelButton = new Button(shell, SWT.PUSH);
        cancelButton.setText("Cancel");
        cancelButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
        cancelButton.addSelectionListener(this);

        shell.pack();
        shell.open();
    }

    @Override
    public void widgetSelected(SelectionEvent event) {
        if (event.widget == okButton) {
            onChange.accept(spinner.getSelection());
        }
        shell.dispose();
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent ignored) {
    }

    @Override
    public void keyPressed(KeyEvent event) {
        if (event.keyCode == '\r' || event.keyCode == '\n') {
            onChange.accept(spinner.getSelection());
            shell.dispose();
        }
    }

    @Override
    public void keyReleased(KeyEvent ignored) {
    }
}
