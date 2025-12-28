/* Copyright (C) 2025 Michael Whapples.
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
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

import java.util.function.IntConsumer;

final class SpinnerDialog implements SelectionListener, KeyListener {
    private final Shell shell;
    private final Button okButton;
    private final Spinner spinner;
    private final IntConsumer onChange;

    public SpinnerDialog(Shell parentShell, String title, int value, int minimum, int maximum, IntConsumer onChange) {
        this(parentShell, title, value, minimum, maximum, 0, 1, 10, onChange);
    }

    public SpinnerDialog(Shell parentShell, String title, int value, int minimum, int maximum, int digits, int increment, int pageIncrement, IntConsumer onChange) {
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
