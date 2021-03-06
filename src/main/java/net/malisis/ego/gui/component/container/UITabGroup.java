/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Ordinastie
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.malisis.ego.gui.component.container;

import net.malisis.ego.gui.ComponentPosition;
import net.malisis.ego.gui.component.UIComponent;
import net.malisis.ego.gui.component.interaction.UITab;
import net.malisis.ego.gui.element.position.Position;
import net.malisis.ego.gui.element.position.Position.IPosition;
import net.malisis.ego.gui.element.size.Size;
import net.malisis.ego.gui.element.size.Size.ISize;
import net.malisis.ego.gui.event.GuiEvent;
import net.malisis.ego.gui.render.GuiIcon;

import java.util.LinkedHashMap;

/**
 * @author Ordinastie
 */
public class UITabGroup extends UIContainer
{
	public static enum Type
	{
		WINDOW, PANEL
	}

	/** The list of {@link UITab} added to this {@link UITabGroup}. */
	protected LinkedHashMap<UITab, UIContainer> listTabs = new LinkedHashMap<>();
	/** The currently active {@link UITab}. */
	protected UITab activeTab;
	/** The position of this {@link UITabGroup} relative to its {@link #attachedContainer}. */
	protected ComponentPosition tabPosition = ComponentPosition.TOP;
	/** The position of this {@link UITabGroup} relative to its {@link #attachedContainer}. */
	protected Type type = Type.WINDOW;
	/** The {@link UIContainer} this {@link UITabGroup} is attached to. */
	protected UIContainer attachedContainer;
	/** Number of pixels this {@link UITabGroup} is offset to the border of the {@link #attachedContainer}. */
	protected int offset = 3;
	/** Number of pixels between each tab. */
	protected int spacing = 0;

	/**
	 * Instantiates a new {@link UITabGroup}.
	 *
	 * @param tabPosition the tab position
	 */
	public UITabGroup(ComponentPosition tabPosition, Type type)
	{
		this.tabPosition = tabPosition;
		this.type = type;
		clipContent = false;
	}

	/**
	 * Gets the relative position of the tabs around their containers.
	 *
	 * @return the tab position
	 */
	public ComponentPosition getTabPosition()
	{
		return tabPosition;
	}

	/**
	 * Gets the icons for this {@link UITabGroup}
	 *
	 * @return the icons
	 */
	public GuiIcon getIcon()
	{
		if (type == Type.WINDOW)
		{
			switch (tabPosition)
			{
				case TOP:
					return GuiIcon.TAB_WINDOW_TOP;
				case BOTTOM:
					return GuiIcon.TAB_WINDOW_BOTTOM;
				case LEFT:
					return GuiIcon.TAB_WINDOW_LEFT;
				case RIGHT:
					return GuiIcon.TAB_WINDOW_RIGHT;
			}
		}
		if (type == Type.PANEL)
		{
			switch (tabPosition)
			{
				case TOP:
					return GuiIcon.TAB_PANEL_TOP;
				case BOTTOM:
					return GuiIcon.TAB_PANEL_BOTTOM;
				case LEFT:
					return GuiIcon.TAB_PANEL_LEFT;
				case RIGHT:
					return GuiIcon.TAB_PANEL_RIGHT;
			}
		}
		return GuiIcon.FULL;
	}

	/**
	 * Gets the attached parent for this {@link UITabGroup}.
	 *
	 * @return the attached parent
	 */
	public UIContainer getAttachedContainer()
	{
		return attachedContainer;
	}

	/**
	 * Gets the offset for this {@link UITabGroup}.
	 *
	 * @return the offset
	 */
	public int getOffset()
	{
		return offset;
	}

	/**
	 * Sets the offset for this {@link UITabGroup}.
	 *
	 * @param offset the offset
	 * @return this {@link UITabGroup}
	 */
	public UITabGroup setOffset(int offset)
	{
		this.offset = offset;
		return this;
	}

	/**
	 * Gets the spacing for this {@link UITabGroup}.
	 *
	 * @return the spacing
	 */
	public int getSpacing()
	{
		return spacing;
	}

	/**
	 * Sets the spacing for this {@link UITabGroup}.
	 *
	 * @param spacing the spacing
	 * @return this {@link UITabGroup}
	 */
	public UITabGroup setSpacing(int spacing)
	{
		this.spacing = spacing;
		return this;
	}

	/**
	 * Adds a {@link UITab} and its corresponding {@link UIContainer} to this {@link UITabGroup}.<br>
	 * Also sets the width of this {@code UITabGroup}.
	 *
	 * @param tab tab to add to the UITabGroup
	 * @param container {@link UIContainer} linked to the {@link UITab}
	 * @return this {@link UITab}
	 */
	public UITab addTab(UITab tab, UIContainer container)
	{
		if (tab.isActive())
			activeTab = tab;

		add(tab);
		tab.setContainer(container);
		tab.setActive(false);
		listTabs.put(tab, container);
		updateSize();

		if (attachedContainer != null)
		{
			setupTabContainer(container);
			calculateTabPosition();
		}
		return tab;
	}

	private void setupTabContainer(UIContainer container)
	{
		attachedContainer.add(container);
		container.setPosition(Position.topLeft(container));
		container.setSize(Size.inherited(container));
	}

	private void updateSize()
	{
		int width = offset;
		int height = offset;
		for (UITab tab : listTabs.keySet())
		{
			if (tabPosition == ComponentPosition.TOP || tabPosition == ComponentPosition.BOTTOM)
			{
				width += tab.contentSize().width() + spacing;
				height = Math.max(height, tab.contentSize().height());
			}
			else
			{
				width = Math.max(width, tab.contentSize().width());
				height += tab.contentSize().height() + spacing;
			}
		}
		setSize(Size.of(width, height));
	}

	/**
	 * Calculates the {@link UITab} position.<br>
	 * Sets the width and height of this {@link UITabGroup}.
	 */
	protected void calculateTabPosition()
	{
		boolean isHorizontal = tabPosition == ComponentPosition.TOP || tabPosition == ComponentPosition.BOTTOM;
		UITab lastTab = null;

		for (UITab tab : listTabs.keySet())
		{
			if (isHorizontal)
			{
				IPosition p = lastTab != null ? Position.rightOf(this, lastTab, spacing) : Position.of(offset, 0);
				tab.setPosition(p);
			}
			else
			{
				IPosition p = lastTab != null ? Position.below(this, lastTab, spacing) : Position.of(0, offset);
				tab.setPosition(p);
			}
			lastTab = tab;
		}
	}

	public void setActiveTab(String tabName)
	{
		UIComponent comp = getComponent(tabName);
		if (comp instanceof UITab)
			setActiveTab((UITab) comp);
	}

	/**
	 * Activates the {@link UITab} and deactivates currently active one.
	 *
	 * @param tab the new active tab
	 */
	public void setActiveTab(UITab tab)
	{
		if (attachedContainer == null)
		{
			activeTab = tab;
			return;
		}

		if (activeTab == tab)
			return;

		if (activeTab != null)
			activeTab.setActive(false);

		activeTab = tab;
		if (tab == null)
			return;

		tab.setActive(true);
	}

	/**
	 * Attach this {@link UITabGroup} to a {@link UIContainer}.
	 *
	 * @param container the parent to attach to.
	 * @param displace if true, moves and resize the UIContainer to make place for the UITabGroup
	 * @return this {@link UITab}
	 */
	public UITabGroup attachTo(UIContainer container, boolean displace)
	{
		attachedContainer = container;
		if (activeTab != null)
			activeTab.setActive(true);

		switch (tabPosition)
		{
			case TOP:
				setPosition(Position.above(this, container, -2));
				break;
			case BOTTOM:
				setPosition(Position.below(this, container, -2));
				break;
			case LEFT:
				setPosition(Position.leftOf(this, container, -2));
				break;
			case RIGHT:
				setPosition(Position.rightOf(this, container, -2));
				break;
		}

		for (UIContainer tabContainer : listTabs.values())
		{
			setupTabContainer(tabContainer);
		}

		calculateTabPosition();

		if (activeTab != null)
		{
			UITab tab = activeTab;
			activeTab = null;
			setActiveTab(tab);
		}

		if (displace)
		{
			attachedContainer.setPosition(new AttachedContainerPosition(attachedContainer.position()));
			attachedContainer.setSize(new AttachedContainerSize(attachedContainer.size()));
		}

		return this;
	}

	private class AttachedContainerPosition implements IPosition
	{
		private final IPosition originalPosition;

		public AttachedContainerPosition(IPosition position)
		{
			originalPosition = position;
		}

		@Override
		public int x()
		{
			return originalPosition.x() + (tabPosition == ComponentPosition.LEFT ? size().width() : 0);
		}

		@Override
		public int y()
		{
			return originalPosition.y() + (tabPosition == ComponentPosition.TOP ? size().height() : 0);
		}

		@Override
		public String toString()
		{
			return x() + "," + y();
		}
	}

	private class AttachedContainerSize implements ISize
	{
		private final ISize originalSize;

		public AttachedContainerSize(ISize size)
		{
			originalSize = size;
		}

		@Override
		public int width()
		{
			if (tabPosition == ComponentPosition.TOP || tabPosition == ComponentPosition.BOTTOM)
				return originalSize.width();

			return originalSize.width() - size().width();
		}

		@Override
		public int height()
		{
			if (tabPosition == ComponentPosition.LEFT || tabPosition == ComponentPosition.RIGHT)
				return originalSize.height();

			return originalSize.height() - size().height();
		}

		@Override
		public String toString()
		{
			return width() + "x" + height();
		}
	}

	/**
	 * Event fired when an inactive {@link UITab} is clicked.<br>
	 * Canceling the event will keep the old tab active.
	 *
	 * @author Ordinastie
	 */
	public static class TabChangeEvent extends GuiEvent<UITabGroup>
	{
		private UITab newTab;

		public TabChangeEvent(UITabGroup component, UITab newTab)
		{
			super(component);
			this.newTab = newTab;
		}

		/**
		 * @return the {@link UITab} deactivated
		 */
		public UITab getOldTab()
		{
			return component.activeTab;
		}

		/**
		 * @return the {@link UITab} activated
		 */
		public UITab getNewTab()
		{
			return newTab;
		}

	}
}
