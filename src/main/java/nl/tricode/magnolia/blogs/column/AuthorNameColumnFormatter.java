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
import info.magnolia.contacts.app.ContactsNodeTypes;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.ui.workbench.column.AbstractColumnFormatter;
import info.magnolia.ui.workbench.column.definition.PropertyColumnDefinition;
import nl.tricode.magnolia.blogs.BlogsNodeTypes;
import nl.tricode.magnolia.blogs.util.BlogWorkspaceUtil;

import nl.tricode.magnolia.blogs.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

/**
 * Column formatter that displays either the name of a contact or a folder.
 *
 * @see info.magnolia.contacts.app.column.ContactNameColumnFormatter
 */
public class AuthorNameColumnFormatter extends AbstractColumnFormatter<PropertyColumnDefinition> {
    private static final Logger log = LoggerFactory.getLogger(AuthorNameColumnFormatter.class);

    public AuthorNameColumnFormatter(PropertyColumnDefinition definition) {
        super(definition);
    }

    public Object generateCell(Table source, Object itemId, Object columnId) {
        final Item jcrItem = getJcrItem(source, itemId);
        if (jcrItem != null && jcrItem.isNode()) {
            final Node node = (Node) jcrItem;

            try {
                if (NodeUtil.isNodeType(node, BlogsNodeTypes.Blog.NAME)) {
                    // Get identifier from author
                    final String authorId = PropertyUtil.getString(node, BlogsNodeTypes.Blog.PROPERTY_AUTHOR, StringUtils.EMPTY);
                    // Find author in contacts and return first name and last name
                    if (StringUtils.isNotEmpty(authorId)) {
                        final Node author = NodeUtil.getNodeByIdentifier(BlogWorkspaceUtil.CONTACTS, authorId);

                        final StringBuilder nameBuilder = new StringBuilder();
                        nameBuilder.append(PropertyUtil.getString(author, ContactsNodeTypes.Contact.PROPERTY_FIRST_NAME, StringUtils.EMPTY));
                        nameBuilder.append(StringUtils.SPACE);
                        nameBuilder.append(PropertyUtil.getString(author, ContactsNodeTypes.Contact.PROPERTY_LAST_NAME, StringUtils.EMPTY));
                        return nameBuilder.toString().trim();
                    }
                }
            } catch (RepositoryException e) {
                log.warn("Unable to get name of contact for column", e);
            }
        }
        return StringUtils.EMPTY;
    }
}