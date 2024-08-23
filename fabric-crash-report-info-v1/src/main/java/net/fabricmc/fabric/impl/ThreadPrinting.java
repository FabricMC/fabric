package net.fabricmc.fabric.impl;

import java.lang.management.LockInfo;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;

public class ThreadPrinting {

	public static String fullThreadInfoToString(ThreadInfo threadInfo) {
		StringBuilder sb = new StringBuilder("\"" + threadInfo.getThreadName() + "\"" +
				(threadInfo.isDaemon() ? " daemon" : "") +
				" prio=" + threadInfo.getPriority() +
				" Id=" + threadInfo.getThreadId() + " " +
				threadInfo.getThreadState());

		if (threadInfo.getLockName() != null) {
			sb.append(" on ").append(threadInfo.getLockName());
		}

		if (threadInfo.getLockOwnerName() != null) {
			sb.append(" owned by \"").append(threadInfo.getLockOwnerName())
					.append("\" Id=").append(threadInfo.getLockOwnerId());
		}

		if (threadInfo.isSuspended()) {
			sb.append(" (suspended)");
		}

		if (threadInfo.isInNative()) {
			sb.append(" (in native)");
		}

		sb.append('\n');

		StackTraceElement[] stackTraceElements = threadInfo.getStackTrace();
		for (int i = 0; i < stackTraceElements.length; i++) {
			StackTraceElement ste = stackTraceElements[i];
			sb.append("\tat ").append(ste.toString());
			sb.append('\n');
			if (i == 0 && threadInfo.getLockInfo() != null) {
				Thread.State ts = threadInfo.getThreadState();
				switch (ts) {
					case BLOCKED -> {
						sb.append("\t-  blocked on ").append(threadInfo.getLockInfo());
						sb.append('\n');
					}
					case WAITING, TIMED_WAITING -> {
						sb.append("\t-  waiting on ").append(threadInfo.getLockInfo());
						sb.append('\n');
					}
					default -> {
					}
				}
			}

			for (MonitorInfo mi : threadInfo.getLockedMonitors()) {
				if (mi.getLockedStackDepth() == i) {
					sb.append("\t-  locked ").append(mi);
					sb.append('\n');
				}
			}
		}

		LockInfo[] locks = threadInfo.getLockedSynchronizers();
		if (locks.length > 0) {
			sb.append("\n\tNumber of locked synchronizers = ").append(locks.length);
			sb.append('\n');
			for (LockInfo li : locks) {
				sb.append("\t- ").append(li);
				sb.append('\n');
			}
		}

		sb.append('\n');
		return sb.toString();
	}
}
