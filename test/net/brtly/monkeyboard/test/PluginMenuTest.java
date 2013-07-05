package net.brtly.monkeyboard.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

import net.brtly.monkeyboard.api.plugin.IPluginCommandHandler;
import net.brtly.monkeyboard.api.plugin.PluginCommand;
import net.brtly.monkeyboard.api.plugin.action.ActionButton;
import net.brtly.monkeyboard.api.plugin.action.ActionMenu;
import net.brtly.monkeyboard.api.plugin.action.ActionNode;

import org.junit.Test;

public class PluginMenuTest {

	class TreeModelEventCounter implements TreeModelListener {

		private CountDownLatch _latch = null;
		private int _events = 0;

		@Override
		public void treeNodesChanged(TreeModelEvent arg0) {
			System.out.println("treeNodesChanged:"
					+ String.valueOf(arg0.getSource()));
			fire();
		}

		@Override
		public void treeNodesInserted(TreeModelEvent arg0) {
			System.out.println("treeNodesInserted:"
					+ String.valueOf(arg0.getSource()));
			fire();
		}

		@Override
		public void treeNodesRemoved(TreeModelEvent arg0) {
			System.out.println("treeNodesRemoved:"
					+ String.valueOf(arg0.getSource()));
			fire();

		}

		@Override
		public void treeStructureChanged(TreeModelEvent arg0) {
			System.out.println("treeStructureChanged:"
					+ String.valueOf(arg0.getSource()));
			fire();

		}

		private void fire() {
			_events += 1;
			if (_latch != null) {
				_latch.countDown();
			}
		}

		public int getEventCount() {
			return _events;
		}

		@Override
		public String toString() {
			return "#(" + String.valueOf(_events) + ")";
		}

	}

	private ActionMenu createRoot() {
		ActionMenu r = new ActionMenu("root");

		ActionMenu g = new ActionMenu("group");
		g.add(new ActionButton("action1"));

		ActionMenu g2 = new ActionMenu("group2");
		ActionButton b = new ActionButton("TEST");
		g2.add(b);

		List<ActionButton> bl = new ArrayList<ActionButton>();
		bl.add(new ActionButton("action2"));
		bl.add(new ActionButton("action3"));

		g.addAll(bl);
		g.add(g2);

		r.add(g);
		return r;
	}

	@Test
	public void testPluginMenuEventPropagation() {

		System.out.println("--------------");
		ActionMenu r = new ActionMenu("ROOT");
		r.addTreeModelListener(new TreeModelEventCounter());

		r.add(new ActionButton("action1"));
		ActionMenu g = new ActionMenu("groupie");

		r.add(g);
		g.add(new ActionButton("action2"));
		g.add(new ActionButton("action3"));

		ActionMenu g2 = new ActionMenu("group2");
		r.add(g2);
		g2.add(new ActionButton("action4"));

		ActionButton b = new ActionButton("button");
		g2.add(b);
		// g2.remove(b);
		// r.remove(g2);
		System.out.println("--------------");
		treeOut(r);
	}

	@Test
	public void testCommands() {
		PluginCommand c = PluginCommand.makeCommand("org.plugin.derp")
				.withName("Test Command")
				.withHandler(new IPluginCommandHandler() {
					@Override
					public void handle(Object source, PluginCommand command) {
						System.out.println(command.getId());

					}


				}).build();
		ActionButton b = new ActionButton(c);
		System.out.println(b.getText());
		c.handle(this);
	}

	public void treeOut(ActionNode root) {
		List<ActionNode> l = root.preOrderTraversal();
		for (ActionNode b : l) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < b.getLevel(); i++) {
				sb.append("-");
			}
			sb.append(b);
			System.out.println(sb);
		}
	}
}
