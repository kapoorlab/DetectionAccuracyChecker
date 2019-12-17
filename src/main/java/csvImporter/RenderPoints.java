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
		
		double size = QuestionDots.size();
		
		for (RealPoint targetpoint: QuestionDots) {
		
			int Querytime = (int)targetpoint.getDoublePosition(2);
			RealPoint Queryspace = new RealPoint(targetpoint.getDoublePosition(0), targetpoint.getDoublePosition(1));
			
			
			List<RealPoint> TimeList = new ArrayList<RealPoint>();
			
			for(RealPoint sourcepoint: TrueDots) {
				
				int GTtime = (int) sourcepoint.getDoublePosition(2);
				if (Math.abs(Querytime - GTtime) <= timeThreshold) {
					
					RealPoint GT = new RealPoint(sourcepoint.getDoublePosition(0), sourcepoint.getDoublePosition(1));
					TimeList.add(GT);
					
				}
			}
			
		if (TimeList.size() > 0) {	
		RealPoint Nearest = getNearestPoint(TimeList, Queryspace);
		
		
	
		
			double distance = GetSpaceDistance(Nearest, Queryspace);
				if (distance < distanceThreshold ) {
					
					accuracy++;
		}
		
		}
		}
		System.out.println(accuracy);
		return accuracy/180;
		
	}
	
	public static double GetSpaceDistance(RealPoint source, RealPoint target) {
		
		double distance = 0;
		
		for (int dim = 0; dim < source.numDimensions(); dim++) {
			
			distance += (source.getDoublePosition(dim) - target.getDoublePosition(dim)) * (source.getDoublePosition(dim) - target.getDoublePosition(dim));
			
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
		
		
		String HumanCSVFile = new String ("/Users/aimachine/Documents/VicData/HumanApoptosisMovie2.csv");
		String AICSVFile = new String ("/Users/aimachine/Documents/VicData/ONETApoptosisLocationEventCountsMovie2.csv");
		
		// Read files
		
		List<RealPoint> HumanDots = loadPoints3dCsv(HumanCSVFile);
		List<RealPoint> AIDots = loadPoints3dCsv(AICSVFile);
		
		double distanceThreshold = 20;
		
		int timeThreshold = 6;
				
		
		
		
		double TruePositive = CheckMaster(AIDots, HumanDots, distanceThreshold, timeThreshold);
		double FalsePositive = 1.0 - CheckMaster(HumanDots,AIDots, distanceThreshold, timeThreshold);
		System.out.println(HumanDots.size()+ " "+ AIDots.size());
		System.out.println("True Positive: " + TruePositive);
		System.out.println("False Positive: " + FalsePositive);
		
		
		
	}
	
}
