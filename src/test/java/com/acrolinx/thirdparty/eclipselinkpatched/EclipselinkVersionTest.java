/*******************************************************************************
 * (C) 2017 Acrolinx GmbH. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 ******************************************************************************/

package com.acrolinx.thirdparty.eclipselinkpatched;

import static org.junit.Assert.assertEquals;

import org.eclipse.persistence.Version;
import org.junit.Test;

public class EclipselinkVersionTest
{

	@Test
	public void versionTest() {
		final String assertMsg = "If this test fails you probably updated EclipseLink. Please make sure to review the patched classes.";
		assertEquals(assertMsg, "2.6.3", Version.getVersion());
		assertEquals(assertMsg, "20160428", Version.getBuildDate());
	}
}
