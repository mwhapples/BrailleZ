/* Copyright (C) 2026 Michael Whapples.
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

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.BiConsumer;

public class Icons {
    public static @NonNull Image loadIcon(Display display, BiConsumer<String, Exception> logger) {
        try (InputStream imageStream = Main.class.getResourceAsStream("/icons/BrailleZ-icon-192.png")) {
            return new Image(display, imageStream);
        } catch (IOException e) {
            logger.accept("Unable to load icon", e);
            throw new RuntimeException("Unable to load icon", e);
        }
    }
}
