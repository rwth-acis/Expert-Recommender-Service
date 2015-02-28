package i5.las2peer.services.servicePackage.visualization;

import java.io.File;
import java.io.IOException;

import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.io.exporter.api.ExportController;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.layout.plugin.force.StepDisplacement;
import org.gephi.layout.plugin.force.yifanHu.YifanHuLayout;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;

/**
 * @author sathvik
 *
 */
public class GraphMl2GEXFConverter {
	GraphModel graphModel;

	public GraphMl2GEXFConverter() {

	}

	public void convert(String srcpath) throws IOException {
		File graphmlFile = new File(srcpath);

		// Init a project - and therefore a workspace
		ProjectController pc = Lookup.getDefault().lookup(
				ProjectController.class);
		pc.newProject();
		Workspace workspace = pc.getCurrentWorkspace();

		// get import controller
		ImportController importController = Lookup.getDefault().lookup(
				ImportController.class);

		// Import file
		Container container = importController.importFile(graphmlFile);

		// Append imported data to GraphAPI
		importController.process(container, new DefaultProcessor(), workspace);

		graphModel = Lookup.getDefault().lookup(GraphController.class)
				.getModel();

	}

	public void applyLayout() {
		YifanHuLayout layout = new YifanHuLayout(null, new StepDisplacement(1f));
		layout.setGraphModel(graphModel);
		layout.initAlgo();
		layout.resetPropertiesValues();
		layout.setOptimalDistance(200f);

		for (int i = 0; i < 100 && layout.canAlgo(); i++) {
			layout.goAlgo();
		}
		layout.endAlgo();
	}

	public void export(String destpath) throws IOException {
		// Export graph to PDF
		ExportController ec = Lookup.getDefault()
				.lookup(ExportController.class);
		ec.exportFile(new File(destpath + ".gexf"));
	}
}
