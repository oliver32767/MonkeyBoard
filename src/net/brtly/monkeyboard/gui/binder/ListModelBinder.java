package net.brtly.monkeyboard.gui.binder;

import javax.swing.JComponent;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public class ListModelBinder implements ListDataListener {

	private JComponent component;
	private IComponentProvider provider;
	private ListModel model;

	public ListModelBinder(JComponent component, ListModel model, IComponentProvider provider) {
		this.component = component;
		this.provider = provider;
		this.model = model;

		model.addListDataListener(this);

		intervalAdded(new ListDataEvent(model, ListDataEvent.INTERVAL_ADDED, 0, model.getSize()));
	}

	public void intervalAdded(ListDataEvent e) {
		for(int i = e.getIndex0(); i < e.getIndex1(); i++) {
			component.add(provider.createComponent(model.getElementAt(i)), i);
		}
	}

	public void intervalRemoved(ListDataEvent e) {
		for(int i = e.getIndex1()-1; i >= e.getIndex0(); i--) {
			component.remove(i);
		}
	}

	public void contentsChanged(ListDataEvent e) {
		component.removeAll();
		intervalAdded(new ListDataEvent(model, ListDataEvent.INTERVAL_ADDED, 0, model.getSize()));
	}
}