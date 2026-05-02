// TwoWheeler.java
public class TwoWheeler extends Vehicle {

    public TwoWheeler(String vehicleNumber, String ownerName) {
        super(vehicleNumber, ownerName, "TWO_WHEELER");
    }

    @Override
    public double getBaseRate() { return 10.0; }  // ₹10 for first hour

    @Override
    public double getExtraRatePerHour() { return 5.0; } // ₹5 each extra hour
}