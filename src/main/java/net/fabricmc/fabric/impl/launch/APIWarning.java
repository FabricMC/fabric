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
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public final class APIWarning {
	public static void main(String[] args) {
		Locale defaultLocale = Locale.getDefault();
		String message = ResourceBundle.getBundle("lang/WarningAPI", defaultLocale).getString("api.warning");
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
