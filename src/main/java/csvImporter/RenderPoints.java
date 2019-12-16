package csvImporter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import ij.ImageJ;
import net.imglib2.KDTree;
import net.imglib2.RealPoint;

public class RenderPoints {

	
	
	public static double CheckMaster(List<RealPoint> TrueDots, List<RealPoint> QuestionDots, double distanceThreshold, int timeThreshold) {
		
		double accuracy = 0;
		
		double size = Math.min(TrueDots.size(), QuestionDots.size());
		
		for (RealPoint targetpoint: QuestionDots) {
		
		RealPoint Nearest = getNearestPoint(TrueDots, targetpoint);
		
		double GTX = Nearest.getDoublePosition(0);
		double GTY = Nearest.getDoublePosition(1);
		int GTtime = (int) Nearest.getDoublePosition(2); 
		
		double [] Truespace = new double[] {GTX, GTY};
		
		double [] Queryspace = new double[] {targetpoint.getDoublePosition(0), targetpoint.getDoublePosition(1)};
		
		int Querytime = (int)targetpoint.getDoublePosition(2);
		
		if (Math.abs(Querytime - GTtime) <= timeThreshold) {
		
			double distance = GetSpaceDistance(Truespace, Queryspace);
				if (distance < distanceThreshold ) {
					
					accuracy+= 1;
				}
		}
		
		}
		return accuracy/size;
		
	}
	
	public static double GetSpaceDistance(double[] source, double[] target) {
		
		double distance = 0;
		
		for (int dim = 0; dim < source.length; dim++) {
			
			distance += (source[dim] - target[dim]) * (source[dim] - target[dim]);
			
		}
		
		return Math.sqrt(distance);
	}
	
	public static List<RealPoint> loadPoints3dCsv( String csvPath ) throws IOException
	{
		List< String > lines = Files.readAllLines( Paths.get( csvPath ) );
		ArrayList<RealPoint> pts = new ArrayList<>();

		for( String line : lines )
		{
			String[] elems = line.split( "," );
			RealPoint p = new RealPoint( Double.parseDouble( elems[ 0 ] ), Double.parseDouble( elems[ 1 ] ), Double.parseDouble( elems[ 2 ] ) ) ;
			pts.add( p );
		}
		return pts;
	}
	public static RealPoint getNearestPoint(List<RealPoint> Allrois, RealPoint Clickedpoint) {

		RealPoint KDtreeroi = null;

		final List<RealPoint> targetCoords = new ArrayList<RealPoint>(Allrois.size());
		final List<FlagNode<RealPoint>> targetNodes = new ArrayList<FlagNode<RealPoint>>(Allrois.size());
		for (int index = 0; index < Allrois.size(); ++index) {

			RealPoint r = Allrois.get(index);
			 
			 targetCoords.add( r );
			 

			targetNodes.add(new FlagNode<RealPoint>(Allrois.get(index)));

		}

		if (targetNodes.size() > 0 && targetCoords.size() > 0) {

			final KDTree<FlagNode<RealPoint>> Tree = new KDTree<FlagNode<RealPoint>>(targetNodes, targetCoords);

			final NNFlagsearchKDtree<RealPoint> Search = new NNFlagsearchKDtree<RealPoint>(Tree);


				final RealPoint source = Clickedpoint;
				final RealPoint sourceCoords = new RealPoint(source);
				Search.search(sourceCoords);
				final FlagNode<RealPoint> targetNode = Search.getSampler().get();

				KDtreeroi = targetNode.getValue();

		}

		return KDtreeroi;
	}
	
	
	public static void main(String[] args) throws IOException {
		
		new ImageJ();
		
		String HumanCSVFile = new String ("/Users/aimachine/Documents/VicData/HumanApoptosisMovie2.csv");
		String AICSVFile = new String ("/Users/aimachine/Documents/VicData/ONETApoptosisMovie2.csv");
		
		// Read files
		
		List<RealPoint> HumanDots = loadPoints3dCsv(HumanCSVFile);
		List<RealPoint> AIDots = loadPoints3dCsv(AICSVFile);
		
		double distanceThreshold = 40;
		
		int timeThreshold = 3;
				
		// Assume Human did a better job, check for AI accuracy:
		
		double AIaccuracy = CheckMaster(HumanDots, AIDots, distanceThreshold, timeThreshold);
		
		System.out.println("If Human was right AI accuracy is: " + AIaccuracy);
		
		// Assume Human did a better job, check for AI accuracy:
		
		double Humanaccuracy = CheckMaster(AIDots, HumanDots, distanceThreshold, timeThreshold);
				
		System.out.println("If AI was right Human accuracy is: " + Humanaccuracy);
		
	}
	
}
