package net.fabricmc.fabric.test.base.client;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class ServerRunner {
	private final Path serverJar;
	private final StringBuffer out = new StringBuffer();

	public ServerRunner(Path serverJar) {
		this.serverJar = serverJar;
	}

	public void run() throws IOException {
		String javaExecutablePath = ProcessHandle.current()
				.info()
				.command()
				.orElseThrow();

		var processBuilder = new ProcessBuilder()
				.directory(FabricLoader.getInstance().getGameDir().toFile())
				.command(
						javaExecutablePath,
						"-Xmx1G",
						"-jar",
						serverJar.toAbsolutePath().toString(),
						"nogui"
				);

		Process start = processBuilder.start();


	}

	private void stop() {

	}

	private static class ForwardingAppendable implements Appendable {
		final List<Appendable> appendables;
		final Runnable onAppended;

		ForwardingAppendable(List<Appendable> appendables, Runnable onAppended) {
			this.appendables = appendables;
			this.onAppended = onAppended;
		}

		@Override
		public Appendable append(CharSequence csq) throws IOException {
			for (Appendable appendable : appendables) {
				appendable.append(csq);
			}

			onAppended.run();
			return this;
		}

		@Override
		public Appendable append(CharSequence csq, int start, int end) throws IOException {
			for (Appendable appendable : appendables) {
				appendable.append(csq, start, end);
			}

			onAppended.run();
			return this;
		}

		@Override
		public Appendable append(char c) throws IOException {
			for (Appendable appendable : appendables) {
				appendable.append(c);
			}

			onAppended.run();
			return this;
		}
	}
}
