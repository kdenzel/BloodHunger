package de.kswmd.bloodhunger;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglCanvas;
import org.lwjgl.opengl.Display;
import org.reflections.Reflections;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ListTests extends JFrame implements MouseListener, WindowListener {

	private volatile JList<String> jList;
	private LwjglCanvas canvas;
	private ListTestAdapter listTestAdapter;
	private JScrollPane listScroller;

	public ListTests () {
		initComponents();
	}

	private void initComponents () {
		Reflections reflections = new Reflections("de.kswmd.bloodhunger");
		Set<Class<? extends BaseTest>> subTypes = reflections.getSubTypesOf(BaseTest.class);
		List<String> classNames = new ArrayList<>(subTypes.size());
		subTypes.forEach(c -> classNames.add(c.getName()));

		jList = new JList<>(classNames.toArray(new String[classNames.size()]));
		jList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		jList.addMouseListener(this);
		listScroller = new JScrollPane(jList);
		listScroller.setPreferredSize(new Dimension(250, 80));
		add(listScroller, BorderLayout.WEST);
		addWindowListener(this);
		listTestAdapter = new ListTestAdapter();
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.forceExit = false;
		config.width = 800;
		config.height = 600;
		config.resizable = false;
		canvas = new LwjglCanvas(listTestAdapter, config);
	}

	private void selectClass () {
		Class<?> c = null;
		try {
			c = Class.forName(jList.getSelectedValue());
			System.out.println(c.getName());
			Screen selectedScreen = (Screen)c.getDeclaredConstructor().newInstance();
			listTestAdapter.setScreen(selectedScreen);
		} catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void mouseClicked (MouseEvent mouseEvent) {

	}

	@Override
	public void mousePressed (MouseEvent mouseEvent) {

	}

	@Override
	public void mouseReleased (MouseEvent mouseEvent) {
		System.out.println("MOUSE RELEASED");
		selectClass();
	}

	@Override
	public void mouseEntered (MouseEvent mouseEvent) {

	}

	@Override
	public void mouseExited (MouseEvent mouseEvent) {

	}

	@Override
	public void dispose () {
		super.dispose();
	}

	@Override
	public void windowOpened (WindowEvent windowEvent) {
		add(canvas.getCanvas());
		canvas.getCanvas().revalidate();
		pack();
		setLocationRelativeTo(null);
	}

	@Override
	public void windowClosing (WindowEvent windowEvent) {
		Display.destroy();
	}

	@Override
	public void windowClosed (WindowEvent windowEvent) {

	}

	@Override
	public void windowIconified (WindowEvent windowEvent) {

	}

	@Override
	public void windowDeiconified (WindowEvent windowEvent) {

	}

	@Override
	public void windowActivated (WindowEvent windowEvent) {

	}

	@Override
	public void windowDeactivated (WindowEvent windowEvent) {

	}

	public static void main (String[] args) {
		JFrame listTest = new ListTests();
		listTest.setSize(800, 600);
		listTest.setDefaultCloseOperation(EXIT_ON_CLOSE);
		listTest.setVisible(true);

	}

}
