/**
 *      Tricode Blog module
 *      Is a Blog module for Magnolia CMS.
 *      Copyright (C) 2015  Tricode Business Integrators B.V.
 *
 * 	  This program is free software: you can redistribute it and/or modify
 *		  it under the terms of the GNU General Public License as published by
 *		  the Free Software Foundation, either version 3 of the License, or
 *		  (at your option) any later version.
 *
 *		  This program is distributed in the hope that it will be useful,
 *		  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *		  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *		  GNU General Public License for more details.
 *
 *		  You should have received a copy of the GNU General Public License
 *		  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package nl.tricode.magnolia.blogs.column;

import com.vaadin.ui.Table;
import info.magnolia.context.MgnlContext;
import info.magnolia.context.WebContext;
import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.ui.vaadin.integration.jcr.JcrItemAdapter;
import info.magnolia.ui.workbench.column.definition.PropertyColumnDefinition;
import nl.tricode.magnolia.blogs.BlogsNodeTypes;

import nl.tricode.magnolia.blogs.util.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.jcr.Node;
import javax.jcr.Property;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class BlogNameColumnFormatterTest {
    private BlogNameColumnFormatter formatter;

    @Mock
    private WebContext mockWebContext;
    @Mock
    private Table mockTable;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        formatter = new BlogNameColumnFormatter(new PropertyColumnDefinition());

        MgnlContext.setInstance(mockWebContext);
    }

    @Test
    public void testGenerateCellWithUnknownId() throws Exception {
        Object itemId = "1";

        Object result = formatter.generateCell(mockTable, itemId, null);
        Assert.assertEquals(StringUtils.EMPTY, result);
    }

    @Test
    public void testGenerateCellWithProperty() throws Exception {
        Object itemId = "1";

        JcrItemAdapter mockItem = mock(JcrItemAdapter.class);
        Property mockProperty = mock(Property.class);

        doReturn(mockItem).when(mockTable).getItem(itemId);
        doReturn(mockProperty).when(mockItem).getJcrItem();
        doReturn(false).when(mockProperty).isNode();

        Object result = formatter.generateCell(mockTable, itemId, null);
        Assert.assertEquals(StringUtils.EMPTY, result);
    }

    @Test
    public void testGenerateCellWithoutBlogOrFolderNode() throws Exception {
        Object itemId = "1";

        JcrItemAdapter mockItem = mock(JcrItemAdapter.class);
        Node mockNode = mock(Node.class);

        doReturn(mockItem).when(mockTable).getItem(itemId);
        doReturn(mockNode).when(mockItem).getJcrItem();
	     doReturn(true).when(mockItem).isNode();
        doReturn(true).when(mockNode).isNode();
        doReturnIsNodeType(mockNode, NodeTypes.Page.NAME);

        Object result = formatter.generateCell(mockTable, itemId, null);
        Assert.assertEquals(StringUtils.EMPTY, result);
    }

    @Test
    public void testGenerateCellWithFolderNode() throws Exception {
        Object itemId = "1";
        String folderName = "folder1";

        JcrItemAdapter mockItem = mock(JcrItemAdapter.class);
        Node mockNode = mock(Node.class);

        doReturn(mockItem).when(mockTable).getItem(itemId);
        doReturn(mockNode).when(mockItem).getJcrItem();
	     doReturn(true).when(mockItem).isNode();
        doReturn(true).when(mockNode).isNode();
        doReturnIsNodeType(mockNode, NodeTypes.Folder.NAME);
        doReturn(folderName).when(mockNode).getName();

        Object result = formatter.generateCell(mockTable, itemId, null);
        Assert.assertEquals(folderName, result);
    }

    @Test
    public void testGenerateCellWithBlogNode() throws Exception {
        Object itemId = "1";
        String blogTitle = "blog title";
		String blogWPPostId = "123";

        JcrItemAdapter mockItem = mock(JcrItemAdapter.class);
        Node mockNode = mock(Node.class);

        doReturn(mockItem).when(mockTable).getItem(itemId);
        doReturn(mockNode).when(mockItem).getJcrItem();
        doReturn(true).when(mockItem).isNode();
        doReturn(true).when(mockNode).isNode();
        doReturnIsNodeType(mockNode, BlogsNodeTypes.Blog.NAME);
		doReturnProperty(mockNode, BlogsNodeTypes.Blog.PROPERTY_WORDPRESS_POST_ID, blogWPPostId);
        doReturnProperty(mockNode, BlogsNodeTypes.Blog.PROPERTY_TITLE, blogTitle);

        Object result = formatter.generateCell(mockTable, itemId, null);
        Assert.assertEquals(blogTitle, result);
    }

    private static void doReturnIsNodeType(Node node, String nodeType) throws Exception {
        Property mockProperty = mock(Property.class);

        doReturn(mockProperty).when(node).getProperty(JcrConstants.JCR_PRIMARYTYPE);
        doReturn(nodeType).when(mockProperty).getString();
        doReturn(true).when(node).isNodeType(nodeType);
    }

    private static void doReturnProperty(Node node, String propertyName, String value) throws Exception {
        Property mockProperty = mock(Property.class);

        doReturn(true).when(node).hasProperty(propertyName);
        doReturn(mockProperty).when(node).getProperty(propertyName);
        doReturn(value).when(mockProperty).getString();
    }
}