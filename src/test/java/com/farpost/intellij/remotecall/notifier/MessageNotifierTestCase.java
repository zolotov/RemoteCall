package com.farpost.intellij.remotecall.notifier;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public abstract class MessageNotifierTestCase {

	private MessageNotifier notifier;

	@BeforeMethod
	public void setUp() {
		notifier = createNotifier();
	}

	@AfterMethod
	public void tearDown() {
		disposeNotifier();
	}


	@Test
	public void notifierShouldCallHandlerOnMessageReceived() {
		assertTrue(true);

	}

	abstract protected MessageNotifier createNotifier();
	abstract protected void disposeNotifier();

}
