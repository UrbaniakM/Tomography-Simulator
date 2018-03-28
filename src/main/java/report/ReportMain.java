package report;

public class ReportMain {

    public static void main(String[] args) {
        //ReportWriter reportSquares = new ReportWriter("squares.jpg", "outSquares.csv");
        ReportWriter reportPhantom = new ReportWriter("phantom.png", "outPhantom.csv");
        //reportSquares.generateResults();
        reportPhantom.generateResults();
    }
}
