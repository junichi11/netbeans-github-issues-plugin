/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2015 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2015 Sun Microsystems, Inc.
 */
package com.junichi11.netbeans.modules.github.issues.utils;

import java.util.Arrays;
import java.util.Collections;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author junichi11
 */
public class StringUtilsTest {

    public StringUtilsTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of isEmpty method, of class StringUtils.
     */
    @Test
    public void testIsEmpty() {
        assertTrue(StringUtils.isEmpty(null));
        assertTrue(StringUtils.isEmpty(""));

        assertFalse(StringUtils.isEmpty("test"));
        assertFalse(StringUtils.isEmpty(" "));
        assertFalse(StringUtils.isEmpty("apple orange"));
    }

    /**
     * Test of join method, of class StringUtils.
     */
    @Test
    public void testJoin() {
        assertEquals("one,two,three,four,five", StringUtils.join(Arrays.asList("one", "two", "three", "four", "five"), ","));
        assertEquals("one:two:three:four:five", StringUtils.join(Arrays.asList("one", "two", "three", "four", "five"), ":"));
        assertEquals("onetwothreefourfive", StringUtils.join(Arrays.asList("one", "two", "three", "four", "five"), ""));

        assertEquals("", StringUtils.join(Collections.<String>emptyList(), ":"));
        assertEquals("", StringUtils.join(null, ":"));
        assertEquals("", StringUtils.join(null, null));
        assertEquals("", StringUtils.join(Arrays.asList("one", "two"), null));
    }

    /**
     * Test of toQuoteComment method, of class StringUtils.
     */
    @Test
    public void testToQuoteComment() {
        assertEquals("> test\n", StringUtils.toQuoteComment("test"));
        assertEquals("> test\n", StringUtils.toQuoteComment("test"));
        assertEquals("> one\n> two\n> three\n", StringUtils.toQuoteComment("one\ntwo\nthree"));

        assertEquals("", StringUtils.toQuoteComment(""));
        assertEquals("", StringUtils.toQuoteComment(null));
    }

}
