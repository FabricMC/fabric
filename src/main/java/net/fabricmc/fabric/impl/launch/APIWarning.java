/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
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

package net.fabricmc.fabric.impl.launch;

import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public final class APIWarning {
	public static void main(String[] args) {
		final ResourceBundle WarningAPI = ResourceBundle.getBundle("lang/WarningAPI", Locale.getDefault(), new ResourceBundle.Control() {
			@Override
			public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IllegalAccessException, InstantiationException, IOException {
				final String bundleName = toBundleName(baseName, locale);
				final String resourceName = toResourceName(bundleName, "properties");

				try (InputStream stream = loader.getResourceAsStream(resourceName)) {
					if (stream != null) {
						try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
							return new PropertyResourceBundle(reader);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

					return super.newBundle(baseName, locale, format, loader, reload);
			}
		});
		String message = WarningAPI.getString("api.warning");

		if (GraphicsEnvironment.isHeadless()) {
			System.err.println(message);
		} else {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (ReflectiveOperationException | UnsupportedLookAndFeelException ignored) {
				// Ignored
			}

			JOptionPane.showMessageDialog(null, message);
		}
	}
}
