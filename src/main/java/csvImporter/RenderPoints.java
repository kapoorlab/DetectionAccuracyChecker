package csvImporter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import ij.ImageJ;
import net.imglib2.RealPoint;

public class RenderPoints {

	
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

	
	public static void main(String[] args) throws IOException {
		
		new ImageJ();
		
		String HumanCSVFile = new String ("/Users/aimachine/DocumentsVicData/DivisionMovie2.csv");
		String AICSVFile = new String ("/Users/aimachine/DocumentsVicData/ONETDivisionMovie2.csv");
		
		// Read files
		
		List<RealPoint> HumanDots = loadPoints3dCsv(HumanCSVFile);
		List<RealPoint> AIDots = loadPoints3dCsv(AICSVFile);
		
		
	}
	
}
