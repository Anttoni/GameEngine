package com.engine.core;

import com.ardor3d.annotation.MainThread;
import com.ardor3d.framework.CanvasRenderer;
import com.ardor3d.framework.DisplaySettings;
import com.ardor3d.framework.FrameHandler;
import com.ardor3d.framework.NativeCanvas;
import com.ardor3d.framework.Scene;
import com.ardor3d.framework.Updater;
import com.ardor3d.framework.lwjgl.LwjglCanvas;
import com.ardor3d.framework.lwjgl.LwjglCanvasRenderer;
import com.ardor3d.input.MouseManager;
import com.ardor3d.input.PhysicalLayer;
import com.ardor3d.input.logical.LogicalLayer;
import com.ardor3d.input.lwjgl.LwjglControllerWrapper;
import com.ardor3d.input.lwjgl.LwjglKeyboardWrapper;
import com.ardor3d.input.lwjgl.LwjglMouseWrapper;
import com.ardor3d.intersection.PickResults;
import com.ardor3d.math.Ray3;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.util.ContextGarbageCollector;
import com.ardor3d.util.ReadOnlyTimer;
import com.ardor3d.util.Timer;

public class EngineBase implements Scene, Updater, Runnable {

	protected final LogicalLayer logicalLayer = new LogicalLayer();
	protected final PhysicalLayer physicalLayer;
	
	protected final Timer timer = new Timer();
	protected final FrameHandler frameHandler = new FrameHandler(timer);
	
	protected NativeCanvas canvas;
	
	protected DisplaySettings settings;
	
	protected MouseManager mouseManager;
	
	protected boolean running = true;
	
	public EngineBase() {
		final LwjglCanvasRenderer canvasRenderer = new LwjglCanvasRenderer(this);
		canvas = new LwjglCanvas(settings, canvasRenderer);
		physicalLayer = new PhysicalLayer(new LwjglKeyboardWrapper(), new LwjglMouseWrapper(),
				new LwjglControllerWrapper(), (LwjglCanvas) canvas);
	}
	
	protected void registerInputTriggers() {
		logicalLayer.registerInput(canvas, physicalLayer);
	}

	@Override
	public PickResults doPick(Ray3 arg0) {
		return null;
	}

	@Override
	@MainThread
	public boolean renderUnto(Renderer arg0) {
		return false;
	}

	@Override
	@MainThread
	public void init() {
		
	}

	@Override
	@MainThread
	public void update(ReadOnlyTimer arg0) {
		if(canvas.isClosing()) {
			exit();
		}
	}
	
	protected void exit() {
		running = false;
	}

	@Override
	public void run() {
		try {
			frameHandler.init();
			
			while(running) {
				frameHandler.updateFrame();
				Thread.yield();
			}
			
			final CanvasRenderer cr = canvas.getCanvasRenderer();
			cr.makeCurrentContext();
			quit(canvas.getCanvasRenderer().getRenderer());
			
		} catch (final Throwable t) {
			
		}
	}
	
	protected void quit(final Renderer renderer) {
		ContextGarbageCollector.doFinalCleanup(renderer);
		canvas.close();
	}
	
}
