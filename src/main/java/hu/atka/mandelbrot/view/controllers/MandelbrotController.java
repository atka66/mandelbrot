package hu.atka.mandelbrot.view.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;
import hu.atka.mandelbrot.logic.Util;
import hu.atka.mandelbrot.logic.MandelbrotGenerator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

public class MandelbrotController implements Initializable {

	private static Logger logger = LoggerFactory.getLogger(MandelbrotController.class);

	@FXML
	private AnchorPane anchorPane;

	@FXML
	private Canvas canvasMandelbrot;

	private GraphicsContext gcMandelbrot;

	private boolean canvasClickShouldRender;
	private double[] canvasClickPosition;

	private double[] currentOffset;
	private double currentScale;

	public void initialize(URL location, ResourceBundle resources) {
		anchorPane.setPrefHeight(Util.CANVAS_HEIGHT);
		anchorPane.setPrefWidth(Util.CANVAS_WIDTH);
		canvasMandelbrot.setHeight(Util.CANVAS_HEIGHT);
		canvasMandelbrot.setWidth(Util.CANVAS_WIDTH);

		canvasClickShouldRender = false;
		canvasClickPosition = new double[2];

		currentOffset = new double[]{Util.INITIAL_OFFSETX, Util.INITIAL_OFFSETY};
		currentScale = Util.INITIAL_SCALE;

		gcMandelbrot = canvasMandelbrot.getGraphicsContext2D();

		canvasMandelbrot.setOnMouseClicked(event -> {
			logger.info("Click registered");
			if (!canvasClickShouldRender) {
				canvasClickPosition[0] = event.getSceneX();
				canvasClickPosition[1] = event.getSceneY();
				canvasClickShouldRender = true;
			} else {
				double newScale = 1 / ((event.getSceneX() - canvasClickPosition[0]) / Util.CANVAS_WIDTH);
				currentOffset[0] = (currentOffset[0] + canvasClickPosition[0]) * newScale;
				currentOffset[1] = (currentOffset[1] + canvasClickPosition[1]) * newScale;
				currentScale *= newScale;
				this.renderMap();
				canvasClickShouldRender = false;
			}
		});

		this.renderMap();
	}

	private void clearAll() {
		gcMandelbrot.setFill(Color.BLACK);
		gcMandelbrot.fillRect(0, 0, Util.CANVAS_WIDTH, Util.CANVAS_HEIGHT);
		logger.info("Canvas cleared");
	}

	private void renderMap() {
		double renderStartTime = System.currentTimeMillis();
		this.clearAll();

		boolean[][] canvasMapFilled = new boolean[Util.CANVAS_WIDTH][Util.CANVAS_HEIGHT];
		logger.info("Rendering map...");
		MandelbrotGenerator generator = new MandelbrotGenerator();

		int maxIt = 100;

		for (int it = maxIt; it >= 1; it--) {
			double itStartTime = System.currentTimeMillis();
			for (int i = 0; i < Util.CANVAS_HEIGHT; i++) {
				for (int j = 0; j < Util.CANVAS_WIDTH; j++) {
					if (!canvasMapFilled[j][i] && generator.isSequenceBoundedForPosition(
						j, i, it, currentOffset[0], currentOffset[1], currentScale
					)) {
						gcMandelbrot.setFill(Color.hsb(
							0,
							(1.0 - (maxIt / (double) it < 2 ? it / (double) maxIt : 0.5)) * 2.0,
							(maxIt / (double) it > 2 ? (it / (double) maxIt) * 2.0 : 1.0)
						));
						gcMandelbrot.fillRect(j, i, 1, 1);
						canvasMapFilled[j][i] = true;
					}
				}
			}
			logger.info("Rendering {}. iteration took {} seconds", it, (System.currentTimeMillis() - itStartTime) / 1000);
		}
		logger.info(
			"Overall rendering took {} seconds, [OffsetX:{} - OffsetY:{} - Scale:{}]",
			(System.currentTimeMillis() - renderStartTime) / 1000,
			currentOffset[0],
			currentOffset[1],
			currentScale
		);
	}
}
